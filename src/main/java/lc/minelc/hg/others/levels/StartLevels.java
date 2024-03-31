package lc.minelc.hg.others.levels;

import org.bukkit.configuration.file.FileConfiguration;

import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.messages.Messages;

public final class StartLevels {

    private final FileConfiguration config;

    public StartLevels(ArenaHGPlugin plugin) {
        config = plugin.loadConfig("levels");
    }

    public void load() {
        LevelStorage.update(new LevelStorage(
            createStat("kills"),
            createStat("deaths"),
            createStat("wins")));
    }

    private LevelStat createStat(final String path) {
        return new LevelStat(
            Messages.color(config.getString(path + ".prefix")),
            config.getInt(path + ".add-lcoins"),
            config.getInt(path + ".every"),
            config.getInt(path + ".level-up")
        );
    }
}