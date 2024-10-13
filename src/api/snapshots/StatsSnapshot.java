package api.snapshots;

import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
public class StatsSnapshot {
    int currNumHighHands;
    int numPloWins;
    int numHoldEmWins;

    public StatsSnapshot() {
        this.currNumHighHands = 0;
        this.numPloWins = 0;
        this.numHoldEmWins = 0;
    }

    public int getNumHighHands() {
        return currNumHighHands;
    }

    public void setNumHighHands(int numHighHands) {
        this.currNumHighHands = numHighHands;
    }

    public int getNumPloWins() {
        return numPloWins;
    }

    public void setNumPloWins(int numPloWins) {
        this.numPloWins = numPloWins;
    }

    public int getNumHoldEmWins() {
        return numHoldEmWins;
    }

    public void setNumHoldEmWins(int numHoldEmWins) {
        this.numHoldEmWins = numHoldEmWins;
    }

    public void addHour(boolean isPloWin, boolean isNlhWin) {
        currNumHighHands++;
        if (isPloWin) {
            numPloWins++;
        } else if (isNlhWin) {
            numHoldEmWins++;
        }
    }
}
