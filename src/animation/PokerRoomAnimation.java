package animation;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import main.HighHand;
import playingcards.PokerHand;
import simulation_datas.HourSimulationData;
import simulation_datas.SimulationData;
import simulation_datas.TableSimulationData;
import tables.PLOTable;
import tables.PokerTable;

public class PokerRoomAnimation {
    private JFrame statsFrame;
    private JLabel nlhWinLabel;
    private JLabel ploWinLabel;
    private int nlhWins = 0;
    private int ploWins = 0;
    private final SimulationData data;
    private JFrame highHandSummaryFrame;
    private JTextArea highHandSummaryText;
    private List<PokerTablePanel> tablePanels = new ArrayList<>();
    private JFrame frame;
    private List<PokerTable> tables;
    private PokerHand currentHighHand = null;
    private UUID highHandTableUUID = null;
    private HighHandPanel highHandPanel;
    private JFrame highHandFrame;
    private Timer fastForwardTimer;
    private JButton pauseButton;
    private int numHours;
    private int numHandsPerHour;
    private List<String> highHandLogs = new ArrayList<>();
    private int hourIndex = 1; // Tracks the current hour



    public PokerRoomAnimation(List<PokerTable> tables, SimulationData data) {
        this.tables = tables;
        this.data = data;
    }

    public void initUI() {
        frame = new JFrame("Poker Hand Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        final Map<UUID, TableSimulationData> tableUUIDToSimulationData =
                data.getTableSimulationDatas().stream().collect(Collectors.toMap(TableSimulationData::getTableID, t -> t));

        // Determine the optimal number of rows and columns for the grid
        int totalTables = tables.size();
        int columns = (int) Math.ceil(Math.sqrt(totalTables));
        int rows = (int) Math.ceil((double) totalTables / columns);

        JPanel tablesPanel = new JPanel(new GridLayout(rows, columns)); // Adjust based on the number of tables dynamically
        for (PokerTable table : tables) {
            PokerTablePanel panel = new PokerTablePanel(tableUUIDToSimulationData.get(table.getTableID()), table);
            tablePanels.add(panel);
            tablesPanel.add(panel);
        }
        frame.add(tablesPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Play Button
        JButton playButton = new JButton("▶ Next Hand");
        playButton.addActionListener(e -> displayNextHand());
        buttonsPanel.add(playButton);
        // Pause Button
        pauseButton = new JButton("⏸ Pause");
        pauseButton.addActionListener(e -> {
            if (fastForwardTimer != null && fastForwardTimer.isRunning()) {
                fastForwardTimer.stop(); // Stop the fast forwarding
                playButton.setEnabled(true); // Optionally re-enable the Play button
                pauseButton.setEnabled(false); // Optionally disable the Pause button since we're not fast forwarding anymore
            }
        });
        pauseButton.setEnabled(false);
        int minSpeed = 1;
        int maxSpeed = 1000;
        int defaultSpeed = 500;

        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, minSpeed, maxSpeed, defaultSpeed);
        speedSlider.setInverted(false);
        speedSlider.setMajorTickSpacing(250);
        speedSlider.setMinorTickSpacing(50);
        speedSlider.setPaintTicks(true);
        speedSlider.addChangeListener(e -> {
            if (!speedSlider.getValueIsAdjusting()) {
                int newDelay = calculateDelayFromSlider(speedSlider.getValue(), minSpeed, maxSpeed);
                if (fastForwardTimer != null) {
                    fastForwardTimer.setDelay(newDelay);
                }
            }
        });


        // Fast Forward Button
        JButton fastForwardButton = new JButton("⏩ Fast Forward");
        fastForwardButton.addActionListener(e -> {
            if (fastForwardTimer != null) {
                fastForwardTimer.stop();
            }

            // Use the updated method to calculate the delay based on the current slider value
            int delay = calculateDelayFromSlider(speedSlider.getValue(), minSpeed, maxSpeed);

            ActionListener taskPerformer = evt -> {
                boolean handsRemaining = displayNextHand();
                if (!handsRemaining) {
                    fastForwardTimer.stop(); // Stop the timer when no hands remain
                    displaySimulationResultsPanel(); // Display the final results
                    pauseButton.setEnabled(false); // Disable pause since we're done
                }
            };

            fastForwardTimer = new Timer(delay, taskPerformer);
            fastForwardTimer.start();

            playButton.setEnabled(false); // Disable play during fast forwarding
            pauseButton.setEnabled(true); // Ensure pause is enabled when fast forwarding starts
        });
        buttonsPanel.add(fastForwardButton);

        // Create the Restart button
        JButton restartButton = new JButton("↺ Restart");
        restartButton.addActionListener(e -> {
            // Reset each PokerTablePanel
            for (PokerTablePanel panel : tablePanels) {
                panel.resetPanel();
            }

            // Reset the high hand display and summary
            resetHighHandDisplay();
            resetHighHandSummaryFrame();

            // Reset any other application state as necessary
            // Example: Resetting the deck, shuffling, re-dealing, etc.

            // Re-enable any controls as necessary
            fastForwardButton.setEnabled(true);
            playButton.setEnabled(true);
            pauseButton.setEnabled(false); // Ensure the pause button is disabled until needed again

            // Assuming the Timer is stopped as part of the Fast Forward or Play functionality
            // but if not, make sure to stop it here as well
            if (fastForwardTimer != null && fastForwardTimer.isRunning()) {
                fastForwardTimer.stop();
            }

            // Optionally, restart the game or simulation logic from the beginning
        });

// Add the Restart button to your layout
        buttonsPanel.add(restartButton);

        buttonsPanel.add(speedSlider);
        buttonsPanel.add(pauseButton);

        frame.add(buttonsPanel, BorderLayout.SOUTH);

        Collection<HourSimulationData> hourSimulationDatas = data.getHourSimulationDatas();
        this.numHandsPerHour = data.getNumHandsPerHour();
        this.numHours = hourSimulationDatas.size();

        initHighHandFrame();

        initHighHandFrameSummaryFrame();
        initStatsFrame();

        frame.pack();
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    private void resetHighHandDisplay() {
        this.currentHighHand = null;
        this.highHandTableUUID = null;
        if (highHandPanel != null) {
            highHandPanel.setCurrentHighHand(null); // Clear the display
            highHandPanel.setHighHandTableID(null);
            highHandFrame.repaint();
        }
    }

    private void resetHighHandSummaryFrame() {
        highHandLogs.clear(); // Clear the logs
        highHandSummaryText.setText(""); // Clear the summary text area
        hourIndex = 1; // Reset the hour index
        nlhWins = 0;
        ploWins = 0;
        updateStats();
    }

    private void initHighHandFrameSummaryFrame() {
        highHandSummaryFrame = new JFrame("All High Hands");
        highHandSummaryFrame.setSize(500, 350); // Slightly longer than highHandFrame
        highHandSummaryFrame.getContentPane().setBackground(Color.GRAY); // Set background color
        highHandSummaryFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Prevent closing

        highHandSummaryText = new JTextArea();
        highHandSummaryText.setEditable(false); // Make the text area non-editable
        highHandSummaryText.setFont(new Font("SansSerif", Font.BOLD, 12)); // Set font

        // Add a JScrollPane to make the text area scrollable
        JScrollPane scrollPane = new JScrollPane(highHandSummaryText);
        highHandSummaryFrame.add(scrollPane, BorderLayout.CENTER);

        JLabel label = new JLabel("All High Hands", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 16)); // Set label font
        label.setOpaque(true);
        label.setBackground(Color.GRAY);
        label.setForeground(Color.WHITE);
        highHandSummaryFrame.add(label, BorderLayout.NORTH); // Add the label at the top

        // Position the highHandSummaryFrame directly below the highHandFrame
        SwingUtilities.invokeLater(() -> {
            Point highHandFrameLocation = highHandFrame.getLocation();
            // Set the Y position to be directly below the highHandFrame by adding its height to the Y position
            highHandSummaryFrame.setLocation(highHandFrameLocation.x, highHandFrameLocation.y + highHandFrame.getHeight());
            highHandSummaryFrame.setVisible(true);
        });
    }

    private int calculateDelayFromSlider(int sliderValue, int minSpeed, int maxSpeed) {
        int maxDelay = 2000; // Slowest speed at leftmost slider position
        int minDelay = 0;    // Fastest speed at rightmost slider position

        // Linear interpolation from left (slow) to right (fast)
        int delay = maxDelay - ((sliderValue - minSpeed) * (maxDelay - minDelay) / (maxSpeed - minSpeed));

        return delay;
    }

    private void initHighHandFrame() {
        highHandFrame = new JFrame("Current High Hand");
        highHandPanel = new HighHandPanel();
        highHandFrame.add(highHandPanel);
        highHandFrame.setSize(500, 300); // Maintain the specified size
        highHandFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Prevent closing

        // Wait until the main frame is packed and displayed to calculate the position
        frame.pack();
        frame.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            // Calculate the position to the right of the main frame
            int xPosition = frame.getLocationOnScreen().x + frame.getWidth() + 5; // Adjusted for right side positioning
            int yPosition = frame.getLocationOnScreen().y;

            // Screen boundary check (Optional, requires getting screen dimensions)
            // Toolkit toolkit = Toolkit.getDefaultToolkit();
            // int screenWidth = toolkit.getScreenSize().width;
            // if (xPosition + highHandFrame.getWidth() > screenWidth) {
            //     xPosition = screenWidth - highHandFrame.getWidth() - 5; // Adjust to keep the highHandFrame on screen
            // }

            highHandFrame.setLocation(xPosition, yPosition);
            highHandFrame.setVisible(true); // Make it visible after setting the location
        });
    }


    private boolean displayNextHand() {
        PokerHand newHighHand = null;
        boolean handsRemaining = false;
        UUID newTableID = null;
        for (PokerTablePanel panel : tablePanels) {
            boolean thisHandsRemaining = panel.hasHand();
            handsRemaining |= thisHandsRemaining;
            if (thisHandsRemaining) {
                final PokerHand winningQualifyingHand = panel.displayHand();
                if (winningQualifyingHand != null) { // potentialQualifier
                    if (beatsCurrentHighHand(winningQualifyingHand)) {
                        newHighHand = winningQualifyingHand;
                        newTableID = panel.getTableID();
                    }
                }
            }
        }
        if (newHighHand != null) {
            this.currentHighHand = newHighHand;
            this.highHandTableUUID = newTableID;
            displayNewHighHand();
        }
        if (!handsRemaining) {
            displaySimulationResultsPanel();
        }
        int currHandIndex = tablePanels.iterator().next().getCurrHandIndex();
        if (currHandIndex % numHandsPerHour == 0 && handsRemaining) {
            updateHighHandSummaryBoard(); // Call the method when an hour of play is completed
            resetCurrentHighHand();
        }
        return handsRemaining;
    }

    private void resetCurrentHighHand() {
        this.currentHighHand = null;
      //  this.highHandTableUUID = null;
        // Optionally, update the high hand display to reflect the reset
        if (highHandPanel != null) {
            highHandPanel.setCurrentHighHand(null);
            highHandPanel.setHighHandTableID(null);
            highHandFrame.repaint();
        }
    }


    private void updateHighHandSummaryBoard() {
        final PokerTable table = highHandTableUUID  == null ? null : findTableById(highHandTableUUID);
        if (currentHighHand != null) {
            recordWin(findTableById(highHandTableUUID));
        }
        String tableType = table instanceof PLOTable ? "PLO" : "NLH";
        String logEntry;
        if (currentHighHand == null) {
            logEntry = "No winner for this hour";
        } else {
            logEntry = String.format("Hour %d: %s from %s Table %s", hourIndex, currentHighHand, tableType, highHandTableUUID);
        }
        highHandLogs.add(logEntry);
        highHandSummaryText.append(logEntry + "\n"); // Append the new log entry to the text area
        hourIndex++; // Increment the hour index for the next update
    }

    private PokerTable findTableById(UUID highHandTableUUID) {
        return tables.stream().filter(t -> t.getTableID().equals(highHandTableUUID)).findFirst().get();
    }

    private void displayNewHighHand() {
        // Update the High Hand on the board
        if (currentHighHand != null) {
            highHandPanel.setCurrentHighHand(currentHighHand); // Update the panel with the new high hand
            highHandPanel.setHighHandTableID(highHandTableUUID); // Set the table ID as well
            highHandFrame.repaint(); // Repaint to update the visual representation
        }
    }

    private boolean beatsCurrentHighHand(PokerHand winningQualifyingHand) {
        if (winningQualifyingHand == null) {
            return false;
        }
        if (currentHighHand == null) {
            return true;
        }
        return winningQualifyingHand.compare(currentHighHand) > 0;
    }

    private ActionListener showFinalResults() {
        pauseButton.setEnabled(true);
        // Define the action to be performed at each interval
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                boolean handsRemaining = displayNextHand();
                if (!handsRemaining) {
                    fastForwardTimer.stop(); // Stop the timer when no hands remain
                    displaySimulationResultsPanel(); // Display the final results
                }
            }
        };

        // Initialize the class member Timer instead of a local variable
        if (fastForwardTimer != null) {
            fastForwardTimer.stop(); // Ensure any existing timer is stopped before creating a new one
        }
        fastForwardTimer = new Timer(500, taskPerformer);
        fastForwardTimer.setRepeats(true); // Ensure the timer repeats
        fastForwardTimer.start(); // Start the timer
        return taskPerformer;
    }


    private void displaySimulationResultsPanel() {
        for (PokerTablePanel panel: tablePanels) {
            // TODO
        }
    }

    private void initStatsFrame() {
        statsFrame = new JFrame("Statistics");
        statsFrame.setSize(500, 200); // Adjust size as needed
        statsFrame.getContentPane().setBackground(Color.BLUE); // Set background color
        statsFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Prevent closing

        JPanel statsPanel = new JPanel();
        statsPanel.setBackground(Color.BLUE);
        statsPanel.setLayout(new GridLayout(2, 2)); // Grid layout for labels and values

        JLabel nlhLabel = new JLabel("NLH", SwingConstants.CENTER);
        nlhLabel.setForeground(Color.WHITE); // Set text color
        nlhLabel.setBackground(new Color(0, 0, 150)); // Lighter blue background
        nlhLabel.setOpaque(true);

        JLabel ploLabel = new JLabel("PLO", SwingConstants.CENTER);
        ploLabel.setForeground(Color.WHITE); // Set text color
        ploLabel.setBackground(new Color(0, 0, 150)); // Lighter blue background
        ploLabel.setOpaque(true);

        nlhWinLabel = new JLabel("0%", SwingConstants.CENTER);
        nlhWinLabel.setForeground(Color.WHITE);
        nlhWinLabel.setOpaque(true);
        nlhWinLabel.setBackground(new Color(0, 0, 180)); // Even lighter blue for contrast

        ploWinLabel = new JLabel("0%", SwingConstants.CENTER);
        ploWinLabel.setForeground(Color.WHITE);
        ploWinLabel.setOpaque(true);
        ploWinLabel.setBackground(new Color(0, 0, 180)); // Even lighter blue for contrast

        // Adding components to the panel
        statsPanel.add(nlhLabel);
        statsPanel.add(ploLabel);
        statsPanel.add(nlhWinLabel);
        statsPanel.add(ploWinLabel);

        // Add the panel to the frame
        statsFrame.add(statsPanel);

        // Position the statsFrame directly below the highHandSummaryFrame
        SwingUtilities.invokeLater(() -> {
            Point summaryFrameLocation = highHandSummaryFrame.getLocation();
            statsFrame.setLocation(summaryFrameLocation.x, summaryFrameLocation.y + highHandSummaryFrame.getHeight());
            statsFrame.setVisible(true);
        });
    }

    private void updateStats() {
        // Assuming this method is called every time a high hand win is recorded after the first hour
        int totalWins = nlhWins + ploWins;
        String nlhWinPercentage;
        String ploWinPercentage;
        if (totalWins != 0) {
            nlhWinPercentage = String.format("%d%%", (nlhWins * 100 / totalWins));
            ploWinPercentage = String.format("%d%%", (ploWins * 100 / totalWins));
        } else {
            nlhWinPercentage = "0%";
            ploWinPercentage = "0%";
        }
            nlhWinLabel.setText(nlhWinPercentage);
            ploWinLabel.setText(ploWinPercentage);

        statsFrame.repaint();
    }

    private void recordWin(PokerTable winningTable) {
        // Increment win counters based on table type
        if (winningTable instanceof PLOTable) {
            ploWins++;
        } else {
            nlhWins++;
        }
        updateStats(); // Update stats display after recording the win
    }


}

