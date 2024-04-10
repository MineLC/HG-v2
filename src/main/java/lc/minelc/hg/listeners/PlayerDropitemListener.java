package lc.minelc.hg.listeners;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;

import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameState;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.others.spawn.SpawnStorage;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public final class PlayerDropitemListener implements EventListener {

    @ListenerData(
        event = PlayerDropItemEvent.class,
        priority = EventPriority.LOWEST
    )
    public void handle(Event defaultEvent) {
        final PlayerDropItemEvent event = (PlayerDropItemEvent)defaultEvent;
        if (SpawnStorage.getStorage().isInSpawn(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }
        final GameInProgress gameInProgress = GameStorage.getStorage().getGame(event.getPlayer().getUniqueId());
        if (!(gameInProgress != null && gameInProgress.getState() == GameState.IN_GAME)) {
            event.setCancelled(true);
        }else{
            event.setCancelled(false);
        }
    }
}
