package lc.minelc.hg.listeners.pvp;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import lc.minelc.hg.ArenaHGPlugin;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public final class PlayerDeathListener implements EventListener {

    private final ArenaHGPlugin plugin;

    public PlayerDeathListener(ArenaHGPlugin plugin) {
        this.plugin = plugin;
    }

    @ListenerData(
        priority = EventPriority.HIGHEST,
        event = PlayerDeathEvent.class
    )
    public void handle(Event defaultEvent) {
        if (!(defaultEvent instanceof PlayerDeathEvent)) {
            return;
        }
        final PlayerDeathEvent event = ((PlayerDeathEvent)defaultEvent);
        plugin.getServer().getScheduler().runTask(plugin, () -> event.getEntity().spigot().respawn());
        event.setDeathMessage(null);
    }
}