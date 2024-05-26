package lc.minelc.hg.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameState;
import lc.minelc.hg.game.GameStorage;

public final class PlayerInteractWithEntityListener implements EventListener {

    @ListenerData(event = PlayerInteractEntityEvent.class)
    public void handle(Event defaultEvent) {
        final PlayerInteractEntityEvent event = (PlayerInteractEntityEvent)defaultEvent;
        if (event.getRightClicked() instanceof Player) {
            return;
        }
        final GameInProgress game = GameStorage.getStorage().getGame(event.getPlayer().getUniqueId());
        if (game == null || game.getState() == GameState.PREGAME) {
            event.setCancelled(true);
        }
    }
}