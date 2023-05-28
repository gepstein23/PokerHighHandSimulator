package simulation_datas;

import main.PokerHand;

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

    public void setPlo(Boolean plo) {
        isPlo = plo;
    }

    public PokerHand getHighHand() {
        return highHand;
    }

    public void setHighHand(PokerHand highHand) {
        this.highHand = highHand;
    }

    public long getHour() {
        return hour;
    }

    public void setHour(long hour) {
        this.hour = hour;
    }

    @Override
    public String toString() {
        return String.format("[Hour=%s, Hand=%s, isPlo=%s]", hour, getHighHand(), getPlo());
    }
}
