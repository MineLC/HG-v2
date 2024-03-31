package lc.minelc.hg.listeners.pvp.damage;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameState;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.others.events.GameEventType;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public final class PlayerDamageByPlayerListener implements EventListener {

    @ListenerData(
        priority = EventPriority.NORMAL,
        event = EntityDamageByEntityEvent.class
    )
    public void handle(final Event defaultEvent) {
        if (!(defaultEvent instanceof EntityDamageByEntityEvent event)) {
            return;
        }
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        final GameInProgress game = GameStorage.getStorage().getGame(event.getDamager().getUniqueId());
        if (game == null || game.getState() != GameState.IN_GAME) {
            return;
        }

        if (game.getCurrentEvent() != null && game.getCurrentEvent().eventType() == GameEventType.CRITICAL) {
            event.setDamage(event.getDamage() + (event.getDamage() / 100) * 25);
            return;
        }
    }
}