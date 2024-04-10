package lc.minelc.hg.listeners;

import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.others.events.GameEventType;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import lc.minelc.hg.others.spawn.SpawnStorage;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

import java.util.UUID;

public class PlayerBreakListener implements EventListener {

    @ListenerData(
        priority = EventPriority.LOWEST,
        event = BlockBreakEvent.class
    )
    public void handle(Event defaultEvent) {
        final BlockBreakEvent event = (BlockBreakEvent)defaultEvent;
        final UUID playerUUID = ((BlockBreakEvent) defaultEvent).getPlayer().getUniqueId();
        final GameInProgress game = GameStorage.getStorage().getGame(playerUUID);
        if (SpawnStorage.getStorage().isInSpawn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}