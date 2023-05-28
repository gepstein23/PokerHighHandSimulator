package tables;

import main.Card;
import main.Deck;
import main.HighHand;
import main.PokerHand;
import players.PLOPokerPlayer;
import players.PokerPlayer;

import java.util.ArrayList;
import java.util.Collection;

public class PLOTable extends PokerTable {
    public PLOTable(int numPlayers, double tableHandsPerHour) {
        super(numPlayers, tableHandsPerHour);
    }

    @Override
    protected boolean isPloTable() {
        return true;
    }

    @Override
    protected boolean isQualifyingHighHand(PokerHand winner, HighHand highHand) {
        return winner.compare(highHand.getMinimumQualifyingHand()) > 0 && winner.isFlopped();
    }

    @Override
    protected Collection<PokerPlayer> dealPlayers(Deck deck) {
        final Collection<PokerPlayer> dealtPlayers = new ArrayList<>();
        for (int i = 0; i < this.numPlayers; i++) {
            final int holeCard1Index = i;
            final int holeCard2Index = i + numPlayers;
            final int holeCard3Index = i + (numPlayers * 2);
            final int holeCard4Index = i + (numPlayers * 3);
            final Card holeCard1 = deck.get(holeCard1Index);
            final Card holeCard2 = deck.get(holeCard2Index);
            final Card holeCard3 = deck.get(holeCard3Index);
            final Card holeCard4 = deck.get(holeCard4Index);
            final PLOPokerPlayer ploPokerPlayer =
                    new PLOPokerPlayer(new Card[] { holeCard1, holeCard2, holeCard3, holeCard4});
            dealtPlayers.add(ploPokerPlayer);
        }
        return dealtPlayers;
    }
}
