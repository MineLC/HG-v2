package lc.minelc.hg.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.game.countdown.endgame.EndgameCountdown;
import lc.minelc.hg.game.countdown.pregame.PreGameCountdown;

public final class GameStorage {

    private static GameStorage storage;

    private final ArenaHGPlugin plugin;
    private final PreGameCountdown.Data pregameData;
    private final Map<UUID, PlayerInGame> playersInGame = new HashMap<>();

    GameStorage(ArenaHGPlugin plugin, PreGameCountdown.Data data) {
        this.plugin = plugin;
        this.pregameData = data;
    }

    public void join(final String world, final GameInProgress game, final Player player) {
        playersInGame.put(player.getUniqueId(), new PlayerInGame(game));
        game.getPlayers().add(player);

        if (game.getState() != GameState.NONE) {
            return;
        }

        final PreGameCountdown waitToStartCountdown = new PreGameCountdown(
            pregameData,
            game.getPlayers(),
            () -> new GameStartAndStop().start(plugin, game, world)
        );

        final int id = plugin.getServer().getScheduler().runTaskTimer(plugin, waitToStartCountdown, 0, 20).getTaskId();
        waitToStartCountdown.setId(id);

        game.setCountdown(waitToStartCountdown);
        game.setState(GameState.PREGAME);
    }

    public void stop(final GameInProgress game) {
        new GameStartAndStop().stop(game);
    }

    public void leave(final GameInProgress game, final Player player) {
        final PlayerInGame playerInGame = playersInGame.remove(player.getUniqueId());
        final Set<Player> players = game.getPlayers();
        players.remove(player);

        if (game.getCountdown() instanceof EndgameCountdown || game.getCountdown() instanceof PreGameCountdown) {
            if (game.getPlayers().isEmpty()) {
                stop(game);
                return;
            }
            return;
        }
        if (player.getGameMode() != GameMode.SPECTATOR) {
            new GameDeath(plugin).death(
                playerInGame,
                player,
                true);
        }
    }

    public void remove(final UUID uuid) {
        playersInGame.remove(uuid);
    }

    public PlayerInGame getPlayerInGame(final UUID uuid) {
        return playersInGame.get(uuid);
    }

    public GameInProgress getGame(UUID uuid) {
        final PlayerInGame playerInGame = playersInGame.get(uuid);
        return (playerInGame == null) ? null : playerInGame.getGame();
    }

    public static GameStorage getStorage() {
        return storage;
    }

    final static void update(final GameStorage newStorage) {
        storage = newStorage;
    }
}