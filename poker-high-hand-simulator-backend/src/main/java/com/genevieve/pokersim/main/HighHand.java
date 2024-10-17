package com.genevieve.pokersim.main;

import com.genevieve.pokersim.playingcards.PokerHand;

import java.time.Duration;

/**
 * Represents a high hand promotion.
 */
public class HighHand {

    private final PokerHand nlhMinimumQualifyingHand;
    private PokerHand ploMinimumQualifyingHand;
    private Duration highHandPeriod;
    // TODO implement price incentive

    public HighHand(PokerHand nlhMinimumQualifyingHand, PokerHand ploMinimumQualifyingHand, Duration highHandPeriod) {
        this.nlhMinimumQualifyingHand = nlhMinimumQualifyingHand;
        this.ploMinimumQualifyingHand = ploMinimumQualifyingHand;
        this.highHandPeriod = highHandPeriod;
    }
    public PokerHand getNlhMinimumQualifyingHand() {
        return nlhMinimumQualifyingHand;
    }

    public PokerHand getPloMinimumQualifyingHand() {
        return ploMinimumQualifyingHand;
    }

    @Override
    public String toString() {
        return String.format("[HighHand: nlhMinimumQualifyingHand=%s, ploMinimumQualifyingHand=%s, highHandPeriod=%s]",
                nlhMinimumQualifyingHand, ploMinimumQualifyingHand, highHandPeriod);
    }
}
