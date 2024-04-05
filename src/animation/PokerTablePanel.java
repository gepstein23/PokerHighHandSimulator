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
       // setPreferredSize(new Dimension(650, 400));
        this.tableId = table.getTableID();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawTable(g);
        if (currentHandData != null) {
            drawSeatsAndCards(g);
            drawCommunityCards(g);
            drawTableID(g);
            drawWinningHand(g);
        }

        drawPanelOutline(g);
    }

    private void drawTableID(Graphics g) {
        g.setColor(Color.BLACK); // Set text color

        // Dynamically set font size based on panel width to ensure visibility
        int fontSize = Math.max(9, getWidth() / 50); // Ensure a minimum font size of 9
        g.setFont(new Font("SansSerif", Font.BOLD, fontSize));

        // Format the table ID string
        String tableIDString = "Table ID: " + this.tableId.toString();

        // Dynamically calculate position based on panel size
        int x = getWidth() * 4/7; // Position it relative to panel width for consistent padding
        int y = getHeight() / 20; // Similarly, position it relative to panel height

        // Draw the string. Adjust x and y if you need the text positioned differently.
        g.drawString(tableIDString, x, y);
    }



    private void drawPanelOutline(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }

    private void drawTable(Graphics g) {
        // Use relative dimensions for the table
        int ovalWidth = getWidth() - getWidth() / 10;
        int ovalHeight = getHeight() - getHeight() / 5;
        g.setColor(new Color(0, 128, 0));
        g.fillOval(getWidth() / 20, getHeight() / 10, ovalWidth, ovalHeight);
        g.setColor(Color.BLACK);
        g.drawOval(getWidth() / 20, getHeight() / 10, ovalWidth, ovalHeight);
    }




    private void drawCommunityCards(Graphics g) {
        final List<Card> communityCards = currentHandData.getCommunityCards();
        if (communityCards != null) {
            Dimension cardSize = getCardSize(); // Get dynamic card size
            int cardWidth = (int) ((Integer) cardSize.width * 1.5);
            int cardHeight = (int) ((int)cardSize.height * 1.5);
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

    private void drawSeatsAndCards(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        int numberOfPlayers = currentHandData.getPlayers().size();
        int panelWidth = getWidth();
        int seatDiameter = Math.min(panelWidth / numberOfPlayers, getHeight() / 5); // Adjust seat size based on panel width
        int cardWidth = seatDiameter / 4; // Adjust card size to fit within seats
        int cardHeight = cardWidth * 3 / 2; // Maintain card aspect ratio

        // Calculate horizontal space needed for all seats including margins
        int totalSpaceNeeded = numberOfPlayers * seatDiameter + (numberOfPlayers + 1) * (seatDiameter / 5);
        // If total space needed exceeds panel width, adjust seat diameter
        if (totalSpaceNeeded > panelWidth) {
            seatDiameter = (panelWidth / numberOfPlayers) - (seatDiameter / 5); // Reduce seat size to fit
            cardWidth = seatDiameter / 3; // Adjust card size accordingly
            cardHeight = cardWidth * 3 / 2;
        }

        for (int i = 0; i < numberOfPlayers; i++) {
            PokerPlayer player = currentHandData.getPlayers().get(i);
            Card[] holeCards = player.getHoleCards();
            // Space seats evenly, including margin at the start
            int seatX = (i * (seatDiameter + (seatDiameter / 5))) + (seatDiameter / 5);
            int seatY = getHeight() - seatDiameter - (seatDiameter / 5); // Position seats above the bottom edge

            // Draw seat
            g.setColor(Color.DARK_GRAY);
            g.fillOval(seatX, seatY, seatDiameter, seatDiameter);

            // Draw player's cards in a 2x2 grid for PLO within each seat
            for (int cardIndex = 0; cardIndex < holeCards.length; cardIndex++) {
                int x = seatX + (cardIndex % 2) * (cardWidth + (cardWidth / 4)) + seatDiameter/5;
                int y = seatY + (cardIndex / 2) * (cardHeight + (cardHeight / 10)) + seatDiameter/5;
                drawCard(g, holeCards[cardIndex], x, y, cardWidth, cardHeight);
            }
        }
    }



    private Dimension getCardSize() {
        // Scale card size based on panel width, maintaining aspect ratio
        int baseWidth = getWidth() / 20; // Dynamic scaling factor
        int baseHeight = (int) (baseWidth * 1.4); // Preserve aspect ratio

        return new Dimension(baseWidth, baseHeight);
    }


    public static void drawCard(Graphics g, Card card, int x, int y, int width, int height) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        // Adjust color for the suit symbols
        if (card.getSuit() == CardSuit.HEARTS || card.getSuit() == CardSuit.DIAMONDS) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.BLACK);
        }

        // Simplified font size calculation
        int fontSize = Math.min(width / 2, height / 2); // Ensure the font size is proportional to card size

        // Draw card value in the top half
        Font valueFont = new Font("Serif", Font.BOLD, fontSize);
        g.setFont(valueFont);
        String valueString = card.getValue().getFriendlyName();
        // Center text horizontally and position it within the top half vertically
        int valueTextWidth = g.getFontMetrics(valueFont).stringWidth(valueString);
        int valueX = x + (width - valueTextWidth) / 2;
        int valueY = y + fontSize + (height / 4 - fontSize / 2); // Adjust to vertically center in the top half
        g.drawString(valueString, valueX, valueY);

        // Draw suit symbol in the bottom half
        Font suitFont = new Font("Serif", Font.PLAIN, fontSize);
        g.setFont(suitFont);
        String suitSymbol = getSuitSymbol(card.getSuit());
        // Center text horizontally and position it within the bottom half vertically
        int suitTextWidth = g.getFontMetrics(suitFont).stringWidth(suitSymbol);
        int suitX = x + (width - suitTextWidth) / 2;
        int suitY = y + 3 * height / 4 + (fontSize / 2); // Adjust to vertically center in the bottom half
        g.drawString(suitSymbol, suitX, suitY);
    }

    private static String getSuitSymbol(CardSuit suit) {
        switch (suit) {
            case HEARTS: return "♥";
            case DIAMONDS: return "♦";
            case CLUBS: return "♣";
            case SPADES: return "♠";
            default: return ""; // Handle unknown suit
        }
    }




    private void drawWinningHand(Graphics g) {
        if (currentHandData == null || currentHandData.getWinningHand() == null) {
            return;
        }

        final PokerHand winningHand = currentHandData.getWinningHand();
        Dimension cardSize = getCardSize();
        int cardWidth = (int) (cardSize.width * 2); // Make winning hand cards slightly larger than the dynamic base size
        int cardHeight = (int) (cardSize.height * 2);

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
        // Dynamic calculation of seatSpacing and seatSize based on panel size and number of players
        int seatSpacing = getWidth() / numberOfPlayers;
        int seatSize = Math.min(seatSpacing, getHeight() / 4); // Ensure seats fit vertically and are not too large

        // Calculate x and y position for the seat circle
        int x = seatSpacing * winningPlayerIndex + (seatSpacing - seatSize) / 2;
        int y = getHeight() - seatSize - 20; // Adjust y position based on dynamic seatSize

        // Dynamic font size based on seatSize
        int fontSize = Math.max(12, seatSize / 8); // Ensure font is legible but proportional to seat size
        Font winnerFont = new Font("SansSerif", Font.BOLD, fontSize);
        g2.setFont(winnerFont);

        // Adjusted text positioning
        String winnerText = "WINNER";
        int textWidth = g2.getFontMetrics().stringWidth(winnerText);
        int textX = x + (seatSize / 2) - (textWidth / 2); // Center text over the seat
        int textY = y + seatSize + fontSize; // Position text below the seat circle, adjusted by fontSize

        g2.setColor(new Color(153, 102, 255)); // Purple color for "WINNER" text
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