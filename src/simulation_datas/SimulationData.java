package simulation_datas;

import java.util.Collection;

public class SimulationData {
    private Collection<HourSimulationData> hourSimulationDatas;

    public SimulationData(Collection<HourSimulationData> hourSimulationDatas) {
        this.hourSimulationDatas = hourSimulationDatas;
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
}
