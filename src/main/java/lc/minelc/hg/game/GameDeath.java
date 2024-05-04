package lc.minelc.hg.game;

import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.database.mongodb.HGPlayerData;
import lc.minelc.hg.database.mongodb.PlayerDataStorage;
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

        if (player.getKiller() != null) {
            final HGPlayerData killerData = PlayerDataStorage.getStorage().get(player.getKiller().getUniqueId());
            final PlayerInGame killer = GameStorage.getStorage().getPlayerInGame(player.getKiller().getUniqueId());
            killer.kills++;
            killerData.kills++;
            game.getTop().checkPosition(killer);
        }

        final Player finalPlayer = getLastPlayerAlive(game.getPlayers());
        if (finalPlayer == null) {
            return;
        }
        final HGPlayerData finalPlayerData = PlayerDataStorage.getStorage().get(finalPlayer.getUniqueId());
        finalPlayerData.wins++;

        LevelStorage.getStorage().win(finalPlayer);

        final EndgameCountdown endgameCountdown = new EndgameCountdown(game);
        
        Messages.sendNoGet(game.getPlayers(), Messages.get("game.win")
            .replace("%player%", finalPlayer.getCustomName())
            .replace("%1%", game.getTop().getPosition(1))
            .replace("%2%", game.getTop().getPosition(2))
            .replace("%3%", game.getTop().getPosition(3))
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
            if (otherPlayer.getGameMode() == GameMode.SPECTATOR) {
                continue;
            }
            if (finalPlayer == null) {
                finalPlayer = otherPlayer;
                continue;
            }
            return null;
        }
        return finalPlayer;
    }
}