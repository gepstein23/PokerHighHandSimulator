package com.genevieve.pokersim.tables;

import com.genevieve.pokersim.players.PokerPlayer;
import com.genevieve.pokersim.playingcards.Card;
import com.genevieve.pokersim.playingcards.Deck;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DealingTest {

    // ========== NLH Dealing ==========

    @Nested
    class NLHDealingTests {

        @Test
        void nlhDeals2HoleCardsPerPlayer() {
            Deck deck = new Deck(new ArrayList<>(Deck.STANDARD_DECK));
            NLHTable table = new NLHTable(8, 25, false);
            Collection<PokerPlayer> players = table.dealPlayers(deck);

            assertEquals(8, players.size());
            for (PokerPlayer player : players) {
                assertEquals(2, player.getHoleCards().length);
            }
        }

        @Test
        void nlhHoleCardsFromCorrectDeckPositions() {
            List<Card> cards = new ArrayList<>(Deck.STANDARD_DECK);
            Deck deck = new Deck(cards);
            NLHTable table = new NLHTable(4, 25, false);
            List<PokerPlayer> players = new ArrayList<>(table.dealPlayers(deck));

            // NLH dealing: player i gets deck[i] and deck[i + numPlayers]
            for (int i = 0; i < 4; i++) {
                assertSame(cards.get(i), players.get(i).getHoleCards()[0],
                        "Player " + i + " first card should be deck[" + i + "]");
                assertSame(cards.get(i + 4), players.get(i).getHoleCards()[1],
                        "Player " + i + " second card should be deck[" + (i + 4) + "]");
            }
        }

        @Test
        void nlhAllDealtCardsAreUnique() {
            Deck deck = new Deck(new ArrayList<>(Deck.STANDARD_DECK));
            NLHTable table = new NLHTable(8, 25, false);
            List<PokerPlayer> players = new ArrayList<>(table.dealPlayers(deck));

            Set<String> allCards = new HashSet<>();
            for (PokerPlayer player : players) {
                for (Card card : player.getHoleCards()) {
                    assertTrue(allCards.add(card.toString()),
                            "Duplicate card dealt: " + card);
                }
            }
            assertEquals(16, allCards.size(), "8 players x 2 cards = 16 unique cards");
        }
    }

    // ========== PLO Dealing ==========

    @Nested
    class PLODealingTests {

        @Test
        void ploDeals4HoleCardsPerPlayer() {
            Deck deck = new Deck(new ArrayList<>(Deck.STANDARD_DECK));
            PLOTable table = new PLOTable(6, 25, false, false, false);
            Collection<PokerPlayer> players = table.dealPlayers(deck);

            assertEquals(6, players.size());
            for (PokerPlayer player : players) {
                assertEquals(4, player.getHoleCards().length);
            }
        }

        @Test
        void ploHoleCardsFromCorrectDeckPositions() {
            List<Card> cards = new ArrayList<>(Deck.STANDARD_DECK);
            Deck deck = new Deck(cards);
            PLOTable table = new PLOTable(3, 25, false, false, false);
            List<PokerPlayer> players = new ArrayList<>(table.dealPlayers(deck));

            // PLO dealing: player i gets deck[i], deck[i+N], deck[i+2N], deck[i+3N]
            int n = 3;
            for (int i = 0; i < n; i++) {
                assertSame(cards.get(i), players.get(i).getHoleCards()[0],
                        "Player " + i + " card 1");
                assertSame(cards.get(i + n), players.get(i).getHoleCards()[1],
                        "Player " + i + " card 2");
                assertSame(cards.get(i + 2 * n), players.get(i).getHoleCards()[2],
                        "Player " + i + " card 3");
                assertSame(cards.get(i + 3 * n), players.get(i).getHoleCards()[3],
                        "Player " + i + " card 4");
            }
        }

        @Test
        void ploAllDealtCardsAreUnique() {
            Deck deck = new Deck(new ArrayList<>(Deck.STANDARD_DECK));
            PLOTable table = new PLOTable(6, 25, false, false, false);
            List<PokerPlayer> players = new ArrayList<>(table.dealPlayers(deck));

            Set<String> allCards = new HashSet<>();
            for (PokerPlayer player : players) {
                for (Card card : player.getHoleCards()) {
                    assertTrue(allCards.add(card.toString()),
                            "Duplicate card dealt: " + card);
                }
            }
            assertEquals(24, allCards.size(), "6 players x 4 cards = 24 unique cards");
        }
    }

    // ========== Community Card Dealing ==========

    @Nested
    class CommunityCardTests {

        @Test
        void communityCardsReturn5Cards() {
            Deck deck = new Deck(new ArrayList<>(Deck.STANDARD_DECK));
            NLHTable table = new NLHTable(4, 25, false);
            List<Card> community = table.dealCommunityCards(deck);
            assertEquals(5, community.size());
        }

        @Test
        void nlhCommunityFromCorrectDeckPositionsWithBurnCards() {
            // NLH 4 players: hole cards use deck[0..7] (4 players x 2 cards)
            // Community starts at deck[8]:
            //   deck[8]  = flop burn (skipped)
            //   deck[9]  = flop 1
            //   deck[10] = flop 2
            //   deck[11] = flop 3
            //   deck[12] = turn burn (skipped)
            //   deck[13] = turn
            //   deck[14] = river burn (skipped)
            //   deck[15] = river
            List<Card> cards = new ArrayList<>(Deck.STANDARD_DECK);
            Deck deck = new Deck(cards);
            NLHTable table = new NLHTable(4, 25, false);
            List<Card> community = table.dealCommunityCards(deck);

            assertSame(cards.get(9), community.get(0), "Flop 1");
            assertSame(cards.get(10), community.get(1), "Flop 2");
            assertSame(cards.get(11), community.get(2), "Flop 3");
            assertSame(cards.get(13), community.get(3), "Turn");
            assertSame(cards.get(15), community.get(4), "River");
        }

        @Test
        void ploCommunityFromCorrectDeckPositionsWithBurnCards() {
            // PLO 4 players: hole cards use deck[0..15] (4 players x 4 cards)
            // Community starts at deck[16]:
            //   deck[16] = flop burn (skipped)
            //   deck[17] = flop 1
            //   deck[18] = flop 2
            //   deck[19] = flop 3
            //   deck[20] = turn burn (skipped)
            //   deck[21] = turn
            //   deck[22] = river burn (skipped)
            //   deck[23] = river
            List<Card> cards = new ArrayList<>(Deck.STANDARD_DECK);
            Deck deck = new Deck(cards);
            PLOTable table = new PLOTable(4, 25, false, false, false);
            List<Card> community = table.dealCommunityCards(deck);

            assertSame(cards.get(17), community.get(0), "Flop 1");
            assertSame(cards.get(18), community.get(1), "Flop 2");
            assertSame(cards.get(19), community.get(2), "Flop 3");
            assertSame(cards.get(21), community.get(3), "Turn");
            assertSame(cards.get(23), community.get(4), "River");
        }

        @Test
        void communityCardsDoNotOverlapWithNlhHoleCards() {
            List<Card> cards = new ArrayList<>(Deck.STANDARD_DECK);
            Deck deck = new Deck(cards);
            NLHTable table = new NLHTable(4, 25, false);
            Collection<PokerPlayer> players = table.dealPlayers(deck);
            List<Card> community = table.dealCommunityCards(deck);

            Set<String> holeCardStrings = new HashSet<>();
            for (PokerPlayer p : players) {
                for (Card c : p.getHoleCards()) {
                    holeCardStrings.add(c.toString());
                }
            }

            for (Card c : community) {
                assertFalse(holeCardStrings.contains(c.toString()),
                        "Community card " + c + " overlaps with a hole card");
            }
        }

        @Test
        void communityCardsDoNotOverlapWithPloHoleCards() {
            List<Card> cards = new ArrayList<>(Deck.STANDARD_DECK);
            Deck deck = new Deck(cards);
            PLOTable table = new PLOTable(4, 25, false, false, false);
            Collection<PokerPlayer> players = table.dealPlayers(deck);
            List<Card> community = table.dealCommunityCards(deck);

            Set<String> holeCardStrings = new HashSet<>();
            for (PokerPlayer p : players) {
                for (Card c : p.getHoleCards()) {
                    holeCardStrings.add(c.toString());
                }
            }

            for (Card c : community) {
                assertFalse(holeCardStrings.contains(c.toString()),
                        "Community card " + c + " overlaps with a hole card");
            }
        }
    }
}
