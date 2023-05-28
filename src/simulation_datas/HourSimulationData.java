package simulation_datas;

import playingcards.PokerHand;

public class HourSimulationData {
    private long hour;
    private PokerHand highHand;
    private Boolean isPlo;

    public HourSimulationData(long hour, PokerHand highHand, Boolean isPlo) {
        this.hour = hour;
        this.highHand = highHand;
        this.isPlo = isPlo;
    }

    public Boolean getPlo() {
        return isPlo;
    }

    public PokerHand getHighHand() {
        return highHand;
    }

    @Override
    public String toString() {
        return String.format("[Hour=%s, Hand=%s, isPlo=%s]", hour, getHighHand(), getPlo());
    }
}
