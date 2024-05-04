package lc.minelc.hg.others.top;

import lc.minelc.hg.game.PlayerInGame;

public final class KillsTop {
    private final PlayerInGame[] topKills = new PlayerInGame[3];

    public void checkPosition(final PlayerInGame player) {
        for (int i = 0; i < 3; i++) {
            final PlayerInGame top = topKills[i];
            if (top == null) {
                topKills[i] = player;
                return;
            }
            if (top.kills < player.kills) {
                topKills[i] = player;
                if (i != 3) {
                    checkPosition(topKills[i]);
                }
                return;
            }
        }
    }

    public String getPosition(final int top) {
        final PlayerInGame player = topKills[top - 1];
        if (player == null) {
            return "Nadie";
        }
        return player.getPlayer().getCustomName() + " - " + player.kills;
    }
}
