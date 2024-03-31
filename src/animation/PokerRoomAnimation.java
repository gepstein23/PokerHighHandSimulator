package animation;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import tables.PokerTable;

public class PokerRoomAnimation {
    private List<PokerTablePanel> tablePanels = new ArrayList<>();
    private JFrame frame;
    private JButton showResultsButton;
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

        showResultsButton = new JButton("Show Next Hand");
        showResultsButton.addActionListener(e -> displayNextHand());
        frame.add(tablesPanel, BorderLayout.CENTER);
        frame.add(showResultsButton, BorderLayout.SOUTH);

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
            showResultsButton.setEnabled(false); // Disable button when no more hands to show
        }
    }
}

