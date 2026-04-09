package com.genevieve.pokersim.tables;

import com.genevieve.pokersim.main.HighHand;
import com.genevieve.pokersim.playingcards.CardValue;
import com.genevieve.pokersim.playingcards.PokerHand;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class PlaySingleHandTest {

    private final HighHand lowQualifier = new HighHand(
            new PokerHand(CardValue.TWO, CardValue.TWO, CardValue.TWO, CardValue.THREE, CardValue.THREE),
            new PokerHand(CardValue.TWO, CardValue.TWO, CardValue.TWO, CardValue.THREE, CardValue.THREE),
            Duration.ofHours(1));

    @Test
    void nlhReturnsHandResult() {
        NLHTable table = new NLHTable(6, 25, false);
        HandResult result = table.playSingleHand(0, lowQualifier);

        assertNotNull(result);
        assertNotNull(result.getPlayedHandData());
        assertNotNull(result.getTableId());
    }

    @Test
    void ploReturnsHandResult() {
        PLOTable table = new PLOTable(6, 25, false, false, false);
        HandResult result = table.playSingleHand(0, lowQualifier);

        assertNotNull(result);
        assertNotNull(result.getPlayedHandData());
        assertNotNull(result.getTableId());
    }

    @Test
    void handResultIsPloFlag() {
        NLHTable nlhTable = new NLHTable(6, 25, false);
        HandResult nlhResult = nlhTable.playSingleHand(0, lowQualifier);
        assertFalse(nlhResult.isPlo());

        PLOTable ploTable = new PLOTable(6, 25, false, false, false);
        HandResult ploResult = ploTable.playSingleHand(0, lowQualifier);
        assertTrue(ploResult.isPlo());
    }

    @Test
    void handResultTableIdMatches() {
        NLHTable table = new NLHTable(6, 25, false);
        HandResult result = table.playSingleHand(0, lowQualifier);

        assertEquals(table.getTableID(), result.getTableId());
    }

    @Test
    void playedHandsListGrows() {
        NLHTable table = new NLHTable(6, 25, false);
        assertEquals(0, table.getPlayedHands().size());

        table.playSingleHand(0, lowQualifier);
        table.playSingleHand(1, lowQualifier);
        table.playSingleHand(2, lowQualifier);

        assertEquals(3, table.getPlayedHands().size());
    }
}
