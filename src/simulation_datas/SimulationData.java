package simulation_datas;

import java.util.Collection;
import java.util.List;

public class SimulationData {
    private final List<TableSimulationData> tableSimulationDatas;
    private final long totalNumHH;
    private Collection<HourSimulationData> hourSimulationDatas;

    public SimulationData(Collection<HourSimulationData> hourSimulationDatas, List<TableSimulationData> tableSimulationDatas, long totalNumHH) {
        this.hourSimulationDatas = hourSimulationDatas;
        this.tableSimulationDatas = tableSimulationDatas;
        this.totalNumHH = totalNumHH;
    }

    public long getNumPloWins() {
        return hourSimulationDatas.stream().filter(data -> data.getPlo() != null && data.getPlo()).count();
    }

    public long getNumNlhWins() {
        return hourSimulationDatas.stream().filter(data -> data.getPlo() != null && !data.getPlo()).count();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (HourSimulationData hourSimulationData : hourSimulationDatas) {
            builder.append(hourSimulationData).append("\n");
        }
        return String.format("=============== SIMULATION RESULTS ===============\n"
                + builder.toString() + "\n"
                + "numPloWins=%s\n"
                + "numNlhWins=%s\n", getNumPloWins(), getNumNlhWins());
    }

    public double getNlhWinPercent() {
        return (getNumNlhWins() * 1.0 / totalNumHH * 1.0) * 100.0;
    }

    public double getPloWinPercent() {
        return (getNumPloWins() * 1.0 / totalNumHH * 1.0) * 100.0;
    }

    public List<TableSimulationData> getTableSimulationDatas() {
        return tableSimulationDatas;
    }

    public Collection<HourSimulationData> getHourSimulationDatas() {
        return hourSimulationDatas;
    }

}
