package lc.minelc.hg.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import lc.minelc.hg.database.mongodb.PlayerDataStorage;
import lc.minelc.hg.game.countdown.invencibility.InvencibilityCountdown;
import lc.minelc.hg.others.abilities.GameAbility;
import lc.minelc.hg.others.kits.KitStorage;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.game.countdown.endgame.EndgameCountdown;
import lc.minelc.hg.game.countdown.pregame.PreGameCountdown;

public final class GameStorage {

    private static GameStorage storage;

    private final ArenaHGPlugin plugin;
    private final PreGameCountdown.Data pregameData;
    private final InvencibilityCountdown.Data invencibilityData;
    private final Map<UUID, PlayerInGame> playersInGame = new HashMap<>();

    GameStorage(ArenaHGPlugin plugin, PreGameCountdown.Data data, InvencibilityCountdown.Data invencibilityData) {
        this.plugin = plugin;
        this.pregameData = data;
        this.invencibilityData = invencibilityData;
    }

    public void join(final String world, final GameInProgress game, final Player player) {
        int kitSelected = PlayerDataStorage.getStorage().get(player.getUniqueId()).kitSelected;
        GameAbility[] gameAbilities = KitStorage.getStorage().kitsPerId().get(kitSelected).gameAbilities();
        PlayerInGame playerInGame = new PlayerInGame(game);
        playerInGame.setGameAbilities(gameAbilities);
        playersInGame.put(player.getUniqueId(), playerInGame);

        game.getPlayers().add(player);

        if (game.getState() != GameState.NONE) {
            return;
        }

        final PreGameCountdown waitToStartCountdown = new PreGameCountdown(
            pregameData,
            game.getPlayers(),
            () -> new GameStartAndStop().start(plugin, game, invencibilityData, world)
        );

        final int id = plugin.getServer().getScheduler().runTaskTimer(plugin, waitToStartCountdown, 0, 20).getTaskId();
        waitToStartCountdown.setId(id);

        game.setCountdown(waitToStartCountdown);
        game.setState(GameState.PREGAME);
    }

    public void stop(final GameInProgress game) {
        System.out.println("se detuvo el juego: " + game.getWorld().getName());
        new GameStartAndStop().stop(game);
    }

    public void leave(final GameInProgress game, final Player player) {
        final PlayerInGame playerInGame = playersInGame.remove(player.getUniqueId());
        final Set<Player> players = game.getPlayers();
        players.remove(player);
        playerInGame.setGameAbilities(null);
        if (game.getCountdown() instanceof EndgameCountdown) {
            if (players.isEmpty()) {
                stop(game);
                return;
            }
            return;
        }
        if (game.getCountdown() instanceof PreGameCountdown) {
            if (players.isEmpty()) {
                game.getMapData().setGame(null);
                plugin.getServer().getScheduler().cancelTask(game.getCountdown().getId());
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