package lc.minelc.hg.others.levels;

import org.bukkit.entity.Player;

import lc.minelc.hg.database.mongodb.HGPlayerData;
import lc.minelc.hg.database.mongodb.PlayerDataStorage;
import net.md_5.bungee.api.ChatColor;

public class LevelStorage {

    private static LevelStorage storage;
    private final LevelStat kill, death, wins;

    LevelStorage(LevelStat kill, LevelStat death, LevelStat wins) {
        this.kill = kill;
        this.death = death;
        this.wins = wins;
    }

    public void onDeath(final Player player) {
        final HGPlayerData victim = PlayerDataStorage.getStorage().get(player.getUniqueId());
        final HGPlayerData killer = (player.getKiller() != null)
            ? PlayerDataStorage.getStorage().get(player.getKiller().getUniqueId())
            : null;

        victim.deaths++;
        tryGainRewards(death, victim.deaths, victim, player);

        if (killer != null) {
            killer.kills++;
            tryGainRewards(kill, killer.kills, killer, player.getKiller());
        }
    }

    public void win(final Player player) {
        final HGPlayerData data = PlayerDataStorage.getStorage().get(player.getUniqueId());
        data.wins++;
        tryGainRewards(wins, data.wins, data, player);
    }

    private void tryGainRewards(final LevelStat levelStat, int stats, final HGPlayerData data, final Player player) {
        if (stats % levelStat.lcoinsEvery() == 0) {
            data.coins += levelStat.addlcoins();
            player.sendMessage(buildLcoinsMessage(levelStat));
        }

        if (levelStat.levelUpEvery() > 0 && stats % levelStat.levelUpEvery() == 0) {
            player.sendMessage(buildLevelUpMessage(levelStat, data.level, data.level++));
        }
    }

    public int getLevels(final HGPlayerData data) {
        int levels = 0;
        levels += (kill.levelUpEvery() > 0) ? data.kills % kill.levelUpEvery() : 0;
        levels += (death.levelUpEvery() > 0) ? data.deaths % death.levelUpEvery() : 0;
        levels += (wins.levelUpEvery() > 0) ? data.wins % wins.levelUpEvery() : 0;
        return levels;
    }

    private String buildLcoinsMessage(final LevelStat stat) {
        final StringBuilder builder = new StringBuilder();
        builder.append('\n');
        builder.append(stat.prefix());
        if (stat.addlcoins() != 0) {
            builder.append(ChatColor.GOLD);
            builder.append("    LCoins ");
            builder.append((stat.addlcoins() < 0) ? '-' : '+');
            builder.append(stat.addlcoins());
            builder.append('\n');
        }
        return builder.toString();
    }

    private String buildLevelUpMessage(final LevelStat stat, final int oldLevel, final int newLevel) {
        final StringBuilder builder = new StringBuilder();
        builder.append('\n');
        builder.append(stat.prefix());
        builder.append(ChatColor.GREEN);
        builder.append("    Nivel ");
        builder.append(oldLevel);
        builder.append(" -> ");
        builder.append(newLevel);
        builder.append('\n');

        return builder.toString();
    }

    public static LevelStorage getStorage() {
        return storage;
    }

    static void update(LevelStorage newStorage) {
        storage = newStorage;
    }
}