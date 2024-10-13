package api.snapshots;

import animation.PlayedHandData;
import org.springframework.web.bind.annotation.ResponseBody;
import playingcards.Card;

import java.util.*;

public class HandSnapShot {

    public int getHandNum() {
        return handNum;
    }

    int handNum;

    List<PlayedHandData> tableSnapshots;

    // Curr Stats at this time
    StatsSnapshot statsSnapshot;

    // Curr High Hand Board Snapshot
    HighHandSnapshot highHandSnapshot;

    public HandSnapShot(int handNum) {
        this.handNum = handNum;
        this.tableSnapshots = new ArrayList<>();
    }

    public List<PlayedHandData> getTableSnapshots() {
        return tableSnapshots;
    }

    public StatsSnapshot getStatsSnapshot() {
        return statsSnapshot;
    }

    public HighHandSnapshot getHighHandSnapshot() {
        return highHandSnapshot;
    }

    public void setStatsSnapshot(StatsSnapshot statsSnapshot) {
        this.statsSnapshot = statsSnapshot;
    }

    public void setHighHandSnapshot(HighHandSnapshot highHandSnapshot) {
        this.highHandSnapshot = highHandSnapshot;
    }

    public HandSnapshotApiModel transform() {
        return new HandSnapshotApiModel(this);
    }

    @ResponseBody
    public class HandSnapshotApiModel {
        int handNum;

        public int getHandNum() {
            return handNum;
        }

        public void setHandNum(int handNum) {
            this.handNum = handNum;
        }

        public Map<UUID, TableSnapshot> getTableIdToSnapshot() {
            return tableIdToSnapshot;
        }

        public void setTableIdToSnapshot(Map<UUID, TableSnapshot> tableIdToSnapshot) {
            this.tableIdToSnapshot = tableIdToSnapshot;
        }

        public StatsSnapshot getStatsSnapshot() {
            return statsSnapshot;
        }

        public void setStatsSnapshot(StatsSnapshot statsSnapshot) {
            this.statsSnapshot = statsSnapshot;
        }

        public HighHandSnapshot getHighHandSnapshot() {
            return highHandSnapshot;
        }

        public void setHighHandSnapshot(HighHandSnapshot highHandSnapshot) {
            this.highHandSnapshot = highHandSnapshot;
        }

        Map<UUID, TableSnapshot> tableIdToSnapshot;

        // Curr Stats at this time
        StatsSnapshot statsSnapshot;

        // Curr High Hand Board Snapshot
        HighHandSnapshot highHandSnapshot;

        public HandSnapshotApiModel(HandSnapShot handSnapShot) {
            this.handNum = handSnapShot.getHandNum();
            this.tableIdToSnapshot = new HashMap<>();
            handSnapShot.getTableSnapshots().forEach(table -> tableIdToSnapshot.put(table.getTableId(), new TableSnapshot(table)));
            this.statsSnapshot = handSnapShot.getStatsSnapshot();
            this.highHandSnapshot = handSnapShot.getHighHandSnapshot();
        }
    }

    @ResponseBody
    private class TableSnapshot {
        private final List<Card[]> playerCards;
        private final Card[] communityCards;
        private final Card[] winningHand;
        private final boolean qualifiesForHighHand;
        public boolean isPlo;

        public List<Card[]> getPlayerCards() {
            return playerCards;
        }

        public Card[] getCommunityCards() {
            return communityCards;
        }

        public Card[] getWinningHand() {
            return winningHand;
        }

        public boolean isQualifiesForHighHand() {
            return qualifiesForHighHand;
        }

        public boolean isPlo() {
            return isPlo;
        }

        public void setPlo(boolean plo) {
            isPlo = plo;
        }

        public UUID getTableId() {
            return tableId;
        }

        public void setTableId(UUID tableId) {
            this.tableId = tableId;
        }

        private UUID tableId;
        public TableSnapshot(PlayedHandData table) {
            this.playerCards = new ArrayList<>();
            table.getPlayers().forEach(player -> playerCards.add(player.getHoleCards()));
            this.communityCards = table.getCommunityCards().toArray(new Card[0]);
            this.winningHand = table.getWinningHand() == null ? null : table.getWinningHand().getFiveHandCards();
            this.qualifiesForHighHand = table.isQualifiesForHighHand();
            this.isPlo = table.isPlo;
            this.tableId = table.getTableId();
        }
    }
}