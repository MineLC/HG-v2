package lc.minelc.hg.others.top;

import java.util.UUID;

import org.bukkit.inventory.Inventory;

import gnu.trove.map.hash.TIntObjectHashMap;

public class TopStorage {

    private static TopStorage storage;
    private final Inventory topInventory;
    private final TIntObjectHashMap<TopData> tops;

    private final PlayerInTop[] kill, death, win, levels;
    private final int size, amountTops;

    TopStorage(int topAmount, int size, PlayerInTop[] kills, PlayerInTop[] death, PlayerInTop[] wins, PlayerInTop[] levels, Inventory inventory, TIntObjectHashMap<TopData> tops) {
        this.amountTops = topAmount;
        this.size = size;
        this.kill = kills;
        this.death = death;
        this.win = wins;
        this.levels = levels;
        this.tops = tops;
        this.topInventory = inventory;
    }

    public void updatePosition(final String playerName, UUID uuid, final int stat, final PlayerInTop[] data) {
        for (int i = 0; i < data.length; i++) {
            final PlayerInTop playerInTop = data[i];

            if (playerInTop == null) {
                data[i] = new PlayerInTop(playerName, stat, uuid);
                return;
            }
            if (playerInTop.getUUID().equals(uuid)) {
                playerInTop.setScore(stat);
                return;
            }
            if (playerInTop.getScore() <= stat) {
                final UUID oldPlayerUUID = playerInTop.getUUID();
                final String oldPlayerName = playerInTop.getPlayerName();
                final int oldPlayerStat = playerInTop.getScore();
                playerInTop.setPlayerName(playerName);
                playerInTop.setUUID(uuid);
                playerInTop.setScore(stat);
                updatePosition(oldPlayerName, oldPlayerUUID, oldPlayerStat, data);
                return;
            }
        }
    }

    public Inventory getInventory() {
        return topInventory;
    }

    public TopData getTopData(final int clickedSlot) {
        return tops.get(clickedSlot);
    }

    public int getInventorySize() {
        return size;
    }

    public PlayerInTop[] getKills() {
        return kill;
    }

    public PlayerInTop[] getDeaths() {
        return death;
    }

    public PlayerInTop[] getWins() {
        return win;
    }

    public PlayerInTop[] getLevels() {
        return levels;
    }

    public int getAmountTops() {
        return amountTops;
    }

    public static TopStorage getStorage() {
        return storage;
    }

    static void update(TopStorage newStorage) {
        storage = newStorage;
    }
}
