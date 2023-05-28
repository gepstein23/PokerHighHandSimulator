package main;

import java.time.Duration;

public class HighHand {
    private PokerHand minimumQualifyingHand;
    private Duration highHandPeriod;

    public Duration getHighHandPeriod() {
        return highHandPeriod;
    }

    public void setHighHandPeriod(Duration highHandPeriod) {
        this.highHandPeriod = highHandPeriod;
    }

    public HighHand(PokerHand minimumQualifyingHand, Duration highHandPeriod) {
        this.minimumQualifyingHand = minimumQualifyingHand;
        this.highHandPeriod = highHandPeriod;
    }

    public PokerHand getMinimumQualifyingHand() {
        return minimumQualifyingHand;
    }

    public void setMinimumQualifyingHand(PokerHand minimumQualifyingHand) {
        this.minimumQualifyingHand = minimumQualifyingHand;
    }

    @Override
    public String toString() {
        return String.format("[HighHand: minimumQualifyingHand=%s, highHandPeriod=%s]",
                minimumQualifyingHand, highHandPeriod);
    }
}
