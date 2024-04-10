package lc.minelc.hg.game;

import lc.minelc.hg.game.countdown.invencibility.InvencibilityCountdown;
import org.bukkit.configuration.file.FileConfiguration;

import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.game.countdown.pregame.PreGameCountdown;

public class StartGameData {

    public void load(ArenaHGPlugin plugin) {
        final FileConfiguration config = plugin.loadConfig("countdowns");
        final PreGameCountdown.Data preGameData = new PreGameCountdown.Data(
            config.getInt("sidebar-update-delay"),
            config.getInt("starting.waiting-to-start"),
            config.getInt("starting.game-starting-in"),
            config.getInt("starting.sound-starting"),
            config.getInt("starting.spam-message"),
            config.getInt("minimum-players-to-start"));
        final InvencibilityCountdown.Data invencibilityData = new InvencibilityCountdown.Data(
                config.getInt("invencibility.waiting-to-invencibility"),
                config.getInt("invencibility.finish-invencibility-in"),
                config.getInt("invencibility.sound-finishing"),
                config.getInt("invencibility.spam-message"));
        GameStorage.update(new GameStorage(plugin, preGameData, invencibilityData));
    }
}