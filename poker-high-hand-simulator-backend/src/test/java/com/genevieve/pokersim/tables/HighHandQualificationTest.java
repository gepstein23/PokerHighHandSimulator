package com.genevieve.pokersim.tables;

import com.genevieve.pokersim.main.HighHand;
import com.genevieve.pokersim.playingcards.Card;
import com.genevieve.pokersim.playingcards.CardValue;
import com.genevieve.pokersim.playingcards.PokerHand;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests high hand qualification rules for NLH and PLO.
 *
 * NLH qualification:
 *   - Hand must be >= minimum qualifying hand
 *   - Must use both hole cards (i.e., 3 community cards in the hand)
 *
 * PLO qualification:
 *   - Hand must be >= minimum qualifying hand
 *   - Must be flopped (all 3 community from flop) unless flop restriction is waived
 *   - Must be turned (all 3 community from flop+turn) if turn restriction is active
 *   - Note: turn restriction auto-waives flop restriction (PLOTable constructor logic)
 *
 * Test cases in src/test/resources/testdata/nlh_qualification.csv
 * and src/test/resources/testdata/plo_qualification.csv
 */
class HighHandQualificationTest {

    @Nested
    class NLHQualificationTests {

        @ParameterizedTest(name = "{0}")
        @CsvFileSource(resources = "/testdata/nlh_qualification.csv", delimiter = '|', numLinesToSkip = 1)
        void testNlhQualification(String description, String handStr, String minimumStr,
                                   String usesBothStr, String expectedStr) {
            PokerHand winner = PokerHand.from(handStr.trim());
            PokerHand minimum = PokerHand.from(minimumStr.trim());
            boolean usesBoth = Boolean.parseBoolean(usesBothStr.trim());
            boolean expected = Boolean.parseBoolean(expectedStr.trim());

            HighHand highHand = new HighHand(minimum, minimum, Duration.ofHours(1));
            NLHTable table = new NLHTable(8, 25, false);

            boolean qualifies = table.isQualifyingHighHand(winner, highHand, usesBoth);
            assertEquals(expected, qualifies, description.trim());
        }
    }

    @Nested
    class PLOQualificationTests {

        @ParameterizedTest(name = "{0}")
        @CsvFileSource(resources = "/testdata/plo_qualification.csv", delimiter = '|', numLinesToSkip = 1)
        void testPloQualification(String description, String handStr, String minimumStr,
                                   String isFloppedStr, String isTurnedStr,
                                   String noFlopRestrStr, String turnRestrStr,
                                   String expectedStr) {
            boolean isFlopped = Boolean.parseBoolean(isFloppedStr.trim());
            boolean isTurned = Boolean.parseBoolean(isTurnedStr.trim());
            boolean noFlopRestr = Boolean.parseBoolean(noFlopRestrStr.trim());
            boolean turnRestr = Boolean.parseBoolean(turnRestrStr.trim());
            boolean expected = Boolean.parseBoolean(expectedStr.trim());

            // Create the hand with flopped/turned flags using suited cards
            PokerHand base = PokerHand.from(handStr.trim());
            Card[] baseCards = base.getFiveHandCards();
            PokerHand winner = new PokerHand(isFlopped, isTurned, baseCards);

            PokerHand minimum = PokerHand.from(minimumStr.trim());
            HighHand highHand = new HighHand(minimum, minimum, Duration.ofHours(1));

            // PLOTable constructor: noPloFlopRestriction = turnRestr || noFlopRestr
            PLOTable table = new PLOTable(8, 25, false, noFlopRestr, turnRestr);

            boolean qualifies = table.isQualifyingHighHand(winner, highHand, true);
            assertEquals(expected, qualifies, description.trim());
        }
    }
}
