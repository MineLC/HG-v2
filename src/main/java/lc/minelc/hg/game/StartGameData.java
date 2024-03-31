package lc.minelc.hg.game;

import org.bukkit.configuration.file.FileConfiguration;

import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.game.countdown.pregame.PreGameCountdown;

public class StartGameData {

    public void load(ArenaHGPlugin plugin) {
        final FileConfiguration config = plugin.loadConfig("countdowns");
        final PreGameCountdown.Data preGameData = new PreGameCountdown.Data(
            config.getInt("sidebar-update-delay"),
            config.getInt("waiting-to-start"),
            config.getInt("game-starting-in"),
            config.getInt("sound-starting"),
            config.getInt("spam-message"),
            config.getInt("minimum-players-to-start"));

        GameStorage.update(new GameStorage(plugin, preGameData));
    }
}