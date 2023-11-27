package main;

import playingcards.PokerHand;

import java.time.Duration;

/**
 * Represents a high hand promotion.
 */
public class HighHand {
    private PokerHand minimumQualifyingHand;
    private Duration highHandPeriod;
    // TODO implement price incentive

    public HighHand(PokerHand minimumQualifyingHand, Duration highHandPeriod) {
        this.minimumQualifyingHand = minimumQualifyingHand;
        this.highHandPeriod = highHandPeriod;
    }

    public PokerHand getMinimumQualifyingHand() {
        return minimumQualifyingHand;
    }

    @Override
    public String toString() {
        return String.format("[HighHand: minimumQualifyingHand=%s, highHandPeriod=%s]",
                minimumQualifyingHand, highHandPeriod);
    }
}
