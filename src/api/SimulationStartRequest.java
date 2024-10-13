package api;

import java.util.UUID;

public class SimulationStartRequest extends SimulationRequest {
    private int numNlhTables;

    private int numPloTables;
    private int numPlayersPerTable;
    private int simulationDuration;
    private int numHandsPerHour;
    private String nlhMinimumQualifyingHand;
    private String ploMinimumQualifyingHand;
    private boolean noPloFlopRestriction;

    protected SimulationStartRequest() {
        this(UUID.randomUUID());
    }

    protected SimulationStartRequest(UUID simulationId) {
        super(simulationId);
    }

    public int getNumNlhTables() {
        return numNlhTables;
    }

    public void setNumNlhTables(int numNlhTables) {
        this.numNlhTables = numNlhTables;
    }

    public int getNumPloTables() {
        return numPloTables;
    }

    public void setNumPloTables(int numPloTables) {
        this.numPloTables = numPloTables;
    }

    public int getNumPlayersPerTable() {
        return numPlayersPerTable;
    }

    public void setNumPlayersPerTable(int numPlayersPerTable) {
        this.numPlayersPerTable = numPlayersPerTable;
    }

    public int getSimulationDuration() {
        return simulationDuration;
    }

    public void setSimulationDuration(int simulationDuration) {
        this.simulationDuration = simulationDuration;
    }

    public String getNlhMinimumQualifyingHand() {
        return nlhMinimumQualifyingHand;
    }

    public void setNlhMinimumQualifyingHand(String nlhMinimumQualifyingHand) {
        this.nlhMinimumQualifyingHand = nlhMinimumQualifyingHand;
    }

    public String getPloMinimumQualifyingHand() {
        return ploMinimumQualifyingHand;
    }

    public void setPloMinimumQualifyingHand(String ploMinimumQualifyingHand) {
        this.ploMinimumQualifyingHand = ploMinimumQualifyingHand;
    }

    public boolean isNoPloFlopRestriction() {
        return noPloFlopRestriction;
    }

    public void setNoPloFlopRestriction(boolean noPloFlopRestriction) {
        this.noPloFlopRestriction = noPloFlopRestriction;
    }

    public int getNumHandsPerHour() {
        return numHandsPerHour;
    }

    public void setNumHandsPerHour(int numHandsPerHour) {
        this.numHandsPerHour = numHandsPerHour;
    }
}
