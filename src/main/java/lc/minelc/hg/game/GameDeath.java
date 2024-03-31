package lc.minelc.hg.game;

import java.util.Set;

import org.bukkit.entity.Player;

import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.game.countdown.GameCountdown;
import lc.minelc.hg.game.countdown.endgame.EndgameCountdown; 
import lc.minelc.hg.messages.Messages;
import lc.minelc.hg.others.deaths.DeathStorage;
import lc.minelc.hg.others.levels.LevelStorage;

public final class GameDeath {

    private final ArenaHGPlugin plugin;

    public GameDeath(ArenaHGPlugin plugin) {
        this.plugin = plugin;
    }

    public void death(final PlayerInGame playerInGame,final Player player, final boolean leaveFromGame) {
        final GameInProgress game = playerInGame.getGame();
        if (leaveFromGame) {
            game.getPlayers().remove(player);
            if (game.getPlayers().isEmpty()) {
                new GameStartAndStop().stop(game);
                return;
            }
        }
        LevelStorage.getStorage().onDeath(player);
        DeathStorage.getStorage().onDeath(playerInGame, game.getPlayers(), player);

        final Player finalPlayer = getLastPlayerAlive(game.getPlayers());
        if (finalPlayer == null) {
            return;
        }
        
        LevelStorage.getStorage().win(finalPlayer);

        final EndgameCountdown endgameCountdown = new EndgameCountdown(game);

        Messages.sendNoGet(game.getPlayers(), Messages.get("team.win")
            .replace("%player%", finalPlayer.getName())
            .replace("%time%", GameCountdown.parseTime((System.currentTimeMillis() - game.getStartedTime()) / 1000)));

        game.setState(GameState.END_GAME);

        int id = plugin.getServer().getScheduler().runTaskLater(
            plugin,
            endgameCountdown,
            plugin.getConfig().getInt("win-celebration-duration-in-seconds") * 20).getTaskId();

        endgameCountdown.setId(id);
        game.setCountdown(endgameCountdown);
    }

    private Player getLastPlayerAlive(final Set<Player> players) {
        Player finalPlayer = null;
        for (final Player otherPlayer : players) {
            if (finalPlayer == null) {
                finalPlayer = otherPlayer;
                continue;
            }
            return null;
        }
        return finalPlayer;
    }
}