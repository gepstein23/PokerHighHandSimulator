package animation;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import simulation_datas.SimulationData;
import simulation_datas.TableSimulationData;
import tables.PokerTable;

public class PokerRoomAnimation {
    private final SimulationData data;
    private List<PokerTablePanel> tablePanels = new ArrayList<>();
    private JFrame frame;
    private JButton showNextHandsButton;
    private JButton showFinalResultsButton;
    private List<PokerTable> tables;

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

        JPanel tablesPanel = new JPanel(new GridLayout(0, 2)); // Adjust based on the number of tables
        for (PokerTable table : tables) {
            PokerTablePanel panel = new PokerTablePanel(tableUUIDToSimulationData.get(table.getTableID()), table);
            tablePanels.add(panel);
            tablesPanel.add(panel);
        }
        frame.add(tablesPanel, BorderLayout.CENTER);

        showNextHandsButton = new JButton("Show Next Hand");
        showNextHandsButton.addActionListener(e -> displayNextHand());
        frame.add(showNextHandsButton, BorderLayout.SOUTH);

        showFinalResultsButton = new JButton("Show Final Results");
        showFinalResultsButton.addActionListener(e -> showFinalResults());
        frame.add(showFinalResultsButton, BorderLayout.NORTH);

        frame.pack();
        frame.setVisible(true);
    }

    private void displayNextHand() {
        boolean handsRemaining = false;
        for (PokerTablePanel panel : tablePanels) {
            boolean thisHandsRemaining = panel.hasHand();
            handsRemaining |= thisHandsRemaining;
            if (thisHandsRemaining) {
                panel.displayHand();
            }
        }
        if (!handsRemaining) {
            showNextHandsButton.setEnabled(false);
            showFinalResultsButton.setEnabled(false);
            displaySimulationResultsPanel();
        }
    }

    private void showFinalResults() {
        for (PokerTablePanel panel : tablePanels) {
            panel.displayLastHand();
        }
        showNextHandsButton.setEnabled(false);
        showFinalResultsButton.setEnabled(false);
        displaySimulationResultsPanel();
    }

    private void displaySimulationResultsPanel() {
        for (PokerTablePanel panel: tablePanels) {
            // TODO
        }
    }
}

