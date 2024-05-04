package lc.minelc.hg.others.top;

import java.util.UUID;

public final class PlayerInTop {
    private String playerName;
    private UUID uuid;
    private int score;

    public PlayerInTop(String playerName, int score, final UUID uuid) {
        this.playerName = playerName;
        this.score = score;
        this.uuid = uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setScore(int newScore) {
        this.score = newScore;
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }
}
