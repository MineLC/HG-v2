package lc.minelc.hg.listeners.pvp;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.game.GameDeath;
import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.game.PlayerInGame;
import lc.minelc.hg.others.events.GameEventType;

public final class PlayerRespawnListener implements EventListener {

    private final ArenaHGPlugin plugin;

    public PlayerRespawnListener(ArenaHGPlugin plugin) {
        this.plugin = plugin;
    }

    @ListenerData(
        priority = EventPriority.HIGHEST,
        event = PlayerRespawnEvent.class
    )
    public void handle(Event defaultEvent) {
        final PlayerRespawnEvent event = (PlayerRespawnEvent)defaultEvent;
        final PlayerInGame playerInGame = GameStorage.getStorage().getPlayerInGame(event.getPlayer().getUniqueId());

        if (playerInGame == null) {
            return;
        }
        final GameInProgress game = playerInGame.getGame();
        final Player player = event.getPlayer();

        event.setRespawnLocation(player.getWorld().getSpawnLocation());
        player.setGameMode(GameMode.SPECTATOR);

        if (player.getKiller() != null) {
            if (game.getCurrentEvent() != null && game.getCurrentEvent().eventType() == GameEventType.FULLHEALTH) {
                player.getKiller().setHealth(20.0D);
                player.getKiller().setFoodLevel(20);
                player.getKiller().playSound(player.getLocation(), Sound.EAT, 1.0f, 1.0f);       
            } else {
                player.getKiller().playSound(player.getLocation(), Sound.BAT_DEATH, 1.0f, 1.0f);       
            }
        }
        
        new GameDeath(plugin).death(playerInGame, player, false);
    }
}