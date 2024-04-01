package animation;

import players.PokerPlayer;
import playingcards.Card;
import playingcards.CardSuit;
import playingcards.PokerHand;
import simulation_datas.TableSimulationData;
import tables.PokerTable;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PokerTablePanel extends JPanel {
    private final TableSimulationData tableSimulationData;
    private final List<PlayedHandData> playedHands;
    private final UUID tableId;
    private PlayedHandData currentHandData;

    private List<PlayedHandData> tableHHQualifiers = new ArrayList<>();
    private int currentHandIndex = 0;

    public PokerTablePanel(TableSimulationData tableSimulationData, PokerTable table) {
        Collections.reverse(table.getPlayedHands());
        this.playedHands = table.getPlayedHands();
        this.tableSimulationData = tableSimulationData;
        setPreferredSize(new Dimension(650, 400));
        this.tableId = table.getTableID();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawTable(g);
        if (currentHandData != null) {
            drawSeats(g);
            drawCommunityCards(g);
            drawTableID(g);
            drawPlayersCards(g);
            drawWinningHand(g);
        }

        drawPanelOutline(g);
    }

    private void drawTableID(Graphics g) {
        g.setColor(Color.BLACK); // Set text color
        g.setFont(new Font("SansSerif", Font.BOLD, 9)); // Set font

        // Format the table ID string
        String tableIDString = "Table ID: " + this.tableId.toString();

        // Calculate position (you might want to adjust these values)
        int x = 400; // Horizontal position from the left
        int y = 20; // Vertical position from the top

        g.drawString(tableIDString, x, y);
    }


    private void drawPanelOutline(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }

    private void drawTable(Graphics g) {
        g.setColor(new Color(0, 128, 0));
        g.fillOval(100, 50, this.getWidth() - 200, this.getHeight() - 100); // The oval table
        g.setColor(Color.BLACK);
        g.drawOval(100, 50, this.getWidth() - 200, this.getHeight() - 100); // Border
    }

    private void drawSeats(Graphics g) {
        if (currentHandData == null || currentHandData.getPlayers().isEmpty()) {
            return;
        }

        int numberOfPlayers = currentHandData.getPlayers().size();
        int seatSpacing = this.getWidth() / numberOfPlayers;
        g.setColor(Color.GRAY);

        for (int i = 0; i < numberOfPlayers; i++) {
            int seatSize = 50;
            int x = seatSpacing * i + (seatSpacing - seatSize) / 2;
            int y = getHeight() - 100;

            // Draw the seat with a filled oval
            g.fillOval(x, y, seatSize, seatSize);

            // Draw a darker grey border around the seat
            g.setColor(new Color(64, 64, 64));
            g.drawOval(x, y, seatSize, seatSize);

            g.setColor(Color.GRAY);
        }
    }

    private void drawCommunityCards(Graphics g) {
        final List<Card> communityCards = currentHandData.getCommunityCards();
        if (communityCards != null) {
            Dimension cardSize = getCardSize(); // Get dynamic card size
            int cardWidth = cardSize.width;
            int cardHeight = cardSize.height;
            int spaceBetweenCards = 10;
            int totalWidth = (cardWidth * communityCards.size()) + (spaceBetweenCards * (communityCards.size() - 1));
            int xStart = (getWidth() / 2) - (totalWidth / 2);
            int yStart = getHeight() / 2 - cardHeight / 2;
            g.setColor(Color.BLUE);

            for (int i = 0; i < communityCards.size(); i++) {
                int x = xStart + (cardWidth + spaceBetweenCards) * i;
                drawCard(g, communityCards.get(i), x, yStart, cardWidth, cardHeight); // Adjust drawCard accordingly
            }
        }
    }

    private void drawPlayersCards(Graphics g) {
        List<PokerPlayer> players = currentHandData.getPlayers();
        if (players != null) {
            Dimension cardSize = getCardSize(); // Use the dynamic card size
            int cardWidth = cardSize.width * 3/7;
            int cardHeight = cardSize.height * 3/7;
            int gap = 5; // Gap between cards

            for (PokerPlayer player : players) {
                Card[] holeCards = player.getHoleCards();
                // Determine the base positioning for the cards
                int seatSpacing = this.getWidth() / players.size();
                int xBase = seatSpacing * players.indexOf(player) + (seatSpacing - cardWidth) / 2;
                int yBase = this.getHeight() - cardHeight - 50; // Adjust as necessary

                if (holeCards.length == 4) {
                    // Display in a 2x2 grid
                    for (int i = 0; i < holeCards.length; i++) {
                        int x = xBase + (i % 2) * (cardWidth + gap);
                        int y = yBase + (i / 2) * (cardHeight + gap);
                        drawCard(g, holeCards[i], x, y, cardWidth, cardHeight);
                    }
                } else {
                    // Display in a single row
                    for (int i = 0; i < holeCards.length; i++) {
                        int x = xBase + i * (cardWidth + gap);
                        drawCard(g, holeCards[i], x, yBase, cardWidth, cardHeight);
                    }
                }
            }
        }
    }


    private Dimension getCardSize() {
        // Adjust these values based on your layout needs
        int baseWidth = getWidth() / 20; // Example proportion
        int baseHeight = (int) (baseWidth * 1.4); // Maintain card aspect ratio

        // Ensure a minimum size
        baseWidth = Math.max(baseWidth, 50);
        baseHeight = Math.max(baseHeight, 70);

        return new Dimension(baseWidth, baseHeight);
    }


    public static void drawCard(Graphics g, Card card, int x, int y, int width, int height) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
        if (card.getSuit() == CardSuit.HEARTS || card.getSuit() == CardSuit.DIAMONDS) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.BLACK);
        }
        Font valueFont = new Font("SansSerif", Font.BOLD, height / 3);
        g.setFont(valueFont);
        String valueString = card.getValue().getFriendlyName();
        g.drawString(valueString, x + 5, y + g.getFontMetrics().getAscent());

        int suitFontSize = width > 50 ? height / 2 : height / 3;
        Font suitFont = new Font("SansSerif", Font.PLAIN, suitFontSize);
        g.setFont(suitFont);
        String suitSymbol = getSuitSymbol(card.getSuit());
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int suitX = centerX - g.getFontMetrics().stringWidth(suitSymbol) / 2;
        int suitY = centerY + g.getFontMetrics().getAscent() / 2;
        g.drawString(suitSymbol, suitX, suitY);
    }

    public static String getSuitSymbol(CardSuit suit) {
        switch (suit) {
            case HEARTS:
                return "♥";
            case DIAMONDS:
                return "♦";
            case CLUBS:
                return "♣";
            case SPADES:
                return "♠";
            default:
                return ""; // Handle unknown suit
        }
    }

    private void drawWinningHand(Graphics g) {
        if (currentHandData == null || currentHandData.getWinningHand() == null) {
            return;
        }

        final PokerHand winningHand = currentHandData.getWinningHand();
        int cardWidth = 50;
        int cardHeight = 70;
        int cardMargin = 5;
        int xStart = 20; // Top left
        int yStart = 20;
        int circlePadding = 10; // Padding for purple circle around winning hand

        // Draw purple circle
        g.setColor(new Color(153, 102, 255));
        int circleDiameter = (cardWidth + cardMargin) * winningHand.getFiveHandCards().length;
        g.fillOval(xStart - circlePadding, yStart - circlePadding, circleDiameter + circlePadding * 2 - cardMargin, cardHeight + circlePadding * 2);

        for (int i = 0; i < winningHand.getFiveHandCards().length; i++) {
            int x = xStart + (cardWidth + cardMargin) * i;
            drawCard(g, winningHand.getFiveHandCards()[i], x, yStart, cardWidth, cardHeight);
        }

        // Draw "WINNER"
        g.setColor(Color.BLACK);
        g.drawString("WINNER", xStart, yStart + cardHeight + 20);

        if (currentHandData.isQualifiesForHighHand()) {
            Graphics2D g2 = (Graphics2D) g.create();
            int highHandCirclePadding = 20;
            int highHandCircleDiameter = circleDiameter + highHandCirclePadding * 2;

            g2.setStroke(new BasicStroke(6));
            g2.setColor(Color.RED);
            g2.drawOval(xStart - highHandCirclePadding, yStart - highHandCirclePadding, highHandCircleDiameter, cardHeight + highHandCirclePadding * 2);

            // Draw "HIGH HAND QUALIFIER"
            Font qualifierFont = new Font("SansSerif", Font.BOLD, 14);
            g2.setFont(qualifierFont);
            String qualifierText = "HIGH HAND QUALIFIER";
            int qualifierTextWidth = g2.getFontMetrics().stringWidth(qualifierText);
            g2.drawString(qualifierText, xStart + (highHandCircleDiameter - qualifierTextWidth) / 2, yStart + cardHeight + 35);

            g2.dispose(); // Properly dispose of the Graphics2D object
        }
        if (winningHand.getHandType().rank() <= PokerHand.HandType.FLUSH.rank()) {
            return;
        }

        int winningPlayerIndex = findWinningPlayerIndex();
        if (winningPlayerIndex != -1) {
            circleWinningSeat(g, winningPlayerIndex);
        }
        
        if (currentHandData.isQualifiesForHighHand()) {
            tableHHQualifiers.add(currentHandData);
        }
    }

    private int findWinningPlayerIndex() {
        final PokerHand winningHand = currentHandData.getWinningHand();
        final List<Card> winningCards = List.of(winningHand.getFiveHandCards());
        int winningPlayerIndex = -1;

        for (int i = 0; i < currentHandData.getPlayers().size(); i++) {
            PokerPlayer player = currentHandData.getPlayers().get(i);
            if (Arrays.stream(player.getHoleCards()).anyMatch(winningCards::contains)) {
                winningPlayerIndex = i;
                break;
            }
        }
        return winningPlayerIndex;
    }

    private void circleWinningSeat(Graphics g, int winningPlayerIndex) {
        if (winningPlayerIndex < 0 || currentHandData.getPlayers() == null) return;

        Graphics2D g2 = (Graphics2D) g.create();

        int numberOfPlayers = currentHandData.getPlayers().size();
        int seatSpacing = getWidth() / (numberOfPlayers > 0 ? numberOfPlayers : 1);
        int seatSize = 60; // Assuming this is the size used for seats
        int x = seatSpacing * winningPlayerIndex + (seatSpacing - seatSize) / 2;
        int y = getHeight() - 120; // Y-position of the seat

        // Set the color for the "WINNER" sign
        g2.setColor(new Color(153, 102, 255)); // Purple color

        // Set the font for the text
        Font winnerFont = new Font("SansSerif", Font.BOLD, 20); // Adjust font size as needed
        g2.setFont(winnerFont);

        // Calculate the width of the "WINNER" text to center it above the seat
        String winnerText = "WINNER";
        int textWidth = g2.getFontMetrics().stringWidth(winnerText);
        int textX = x + (seatSize / 2) - (textWidth / 2); // Center text over the seat
        int textY = y + seatSize + 20; // Adjust the offset as needed for visual appeal


        // Draw the "WINNER" text
        g2.drawString(winnerText, textX, textY);

        g2.dispose();
    }




    public PokerHand displayHand() {
        return displayHand(playedHands.get(currentHandIndex));
    }

    public PokerHand displayHand(PlayedHandData currentHandData) {
        this.currentHandData = currentHandData;
        // Clear current content
        this.removeAll();

        for (Card card : currentHandData.getCommunityCards()) {
            JLabel cardLabel = new JLabel();
            this.add(cardLabel);
        }

        // refresh the panel to show updates
        this.revalidate();
        this.repaint();
        this.currentHandIndex++;

        if (currentHandData.isQualifiesForHighHand()) {
            return currentHandData.getWinningHand();
        }
        return null;
    }

    public List<PlayedHandData> getTableHHQualifiers() {
        return tableHHQualifiers;
    }

    public boolean hasHand() {
        return currentHandIndex < playedHands.size();
    }

    public PokerHand displayLastHand() {
        return displayHand(playedHands.get(playedHands.size() - 1));
    }

    public UUID getTableID() {
        return tableId;
    }

    public void resetPanel() {
        this.currentHandIndex = 0;
        this.currentHandData = null;
        this.tableHHQualifiers = new ArrayList<>();
        repaint();
    }

    public int getCurrHandIndex() {
        return currentHandIndex;
    }
}