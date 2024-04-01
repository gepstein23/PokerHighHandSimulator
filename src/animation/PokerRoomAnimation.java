package animation;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import tables.PokerTable;

public class PokerRoomAnimation {
    private List<PokerTablePanel> tablePanels = new ArrayList<>();
    private JFrame frame;
    private JButton showNextHandsButton;
    private JButton showFinalResultsButton;
    private int currentHandIndex = 0;
    private List<PokerTable> tables;

    public PokerRoomAnimation(List<PokerTable> tables) {
        this.tables = tables;
    }

    public void initUI() {
        frame = new JFrame("Poker Hand Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel tablesPanel = new JPanel(new GridLayout(0, 2)); // Adjust based on the number of tables
        for (PokerTable table : tables) {
            PokerTablePanel panel = new PokerTablePanel();
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
        if (!tables.isEmpty() && currentHandIndex < tables.get(0).getPlayedHands().size()) {
            for (int i = 0; i < tables.size(); i++) {
                PokerTable table = tables.get(i);
                PokerTablePanel panel = tablePanels.get(i);
                PlayedHandData handData = table.getPlayedHands().get(currentHandIndex);

                panel.displayHand(handData);
            }
            currentHandIndex++;
        } else {
            showNextHandsButton.setEnabled(false); // Disable button when no more hands to show
        }
        if (currentHandIndex == tables.get(0).getPlayedHands().size()) {
            displaySimulationResultsPanel();
        }
    }

    private void showFinalResults() {
        if (tables.isEmpty() || currentHandIndex >= tables.get(0).getPlayedHands().size()) {
            showFinalResultsButton.setEnabled(false);
            return;
        }
        currentHandIndex = tables.get(0).getPlayedHands().size() - 1;

        for (int i = 0; i < tables.size(); i++) {
                PokerTable table = tables.get(i);
                PokerTablePanel panel = tablePanels.get(i);
                PlayedHandData handData = table.getPlayedHands().get(currentHandIndex);
                panel.displayHand(handData);
        }
        displaySimulationResultsPanel();
    }

    private void displaySimulationResultsPanel() {
        for (PokerTablePanel panel: tablePanels) {
            // TODO
        }
    }
}

