package animation;

import playingcards.Card;
import playingcards.PokerHand;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

import static animation.PokerTablePanel.drawCard;

public class HighHandPanel extends JPanel {
    private PokerHand currentHighHand;
    private UUID highHandTableID;
    private int highHandPeriod;


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Set the background to black
        setBackground(Color.BLACK);
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw "High Hand" in red at the top
        g.setColor(Color.RED);
        g.setFont(new Font("Serif", Font.BOLD, 36)); // Adjust the font as needed
        FontMetrics fm = g.getFontMetrics();
        String highHandText = "High Hand";
        int x = (getWidth() - fm.stringWidth(highHandText)) / 2;
        g.drawString(highHandText, x, fm.getAscent() + 20); // Adjust positioning as needed

        // Draw 5 grey boxes for cards
        int cardWidth = 75;
        int cardHeight = 120;
        int spaceBetween = 20; // Space between cards
        int startX = (getWidth() - (5 * cardWidth) - (4 * spaceBetween)) / 2; // To center the cards
        g.setColor(Color.GRAY);
        for (int i = 0; i < 5; i++) {
            g.fillRect(startX + i * (cardWidth + spaceBetween), fm.getHeight() + 40, cardWidth, cardHeight);
        }

        // Draw "Table ID:" text very small under the cards
        g.setColor(Color.WHITE);
        g.setFont(new Font("Serif", Font.PLAIN, 12)); // Smaller font for table ID
        String tableIDText = "Table ID: " + highHandTableID;
        int tableIDY = fm.getHeight() + 40 + cardHeight + 15; // Position below the cards
        g.drawString(tableIDText, 10, tableIDY); // Adjust positioning as needed

        if (currentHighHand != null) {
            for (int i = 0; i < 5; i++) {
                int cardX = startX + i * (cardWidth + spaceBetween);
                int cardY = fm.getHeight() + 40;
                g.fillRect(cardX, cardY, cardWidth, cardHeight);
                // Assuming you have a method to get a Card object or similar
                // For demonstration, you may use a placeholder Card object
                // You'll need to adjust this part to use actual card data
                Card card = currentHighHand.getFiveHandCards()[i]; // Implement this method based on your Card class
                drawCard(g, card, cardX, cardY, cardWidth, cardHeight); // Draw the actual card over the placeholder
            }
        }
    }

    public void setHighHandTableID(UUID highHandTableID) {
        this.highHandTableID = highHandTableID;
        repaint();
    }

    public void setCurrentHighHand(PokerHand currentHighHand) {
        this.currentHighHand = currentHighHand;
        repaint();
    }
}
