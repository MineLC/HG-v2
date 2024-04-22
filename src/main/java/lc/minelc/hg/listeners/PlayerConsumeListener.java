package lc.minelc.hg.listeners;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import lc.minelc.hg.game.GameState;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.game.PlayerInGame;
import lc.minelc.hg.others.abilities.GameAbility;

public final class PlayerConsumeListener implements EventListener {

    @ListenerData(event = PlayerItemConsumeEvent.class)
    public void handle(Event defaultEvent) {
        final PlayerItemConsumeEvent event = (PlayerItemConsumeEvent)defaultEvent;
        final PlayerInGame playerInGame = GameStorage.getStorage().getPlayerInGame(event.getPlayer().getUniqueId());
        if (playerInGame == null || playerInGame.getGame().getState() != GameState.IN_GAME || !playerInGame.containsAbility(GameAbility.HEAL_ON_EAT)) {
            return;
        }
        final double newHealth = event.getPlayer().getHealth() + 4;
        if (newHealth >= 20) {
            event.getPlayer().setHealth(20);
            return;
        }
        event.getPlayer().setHealth(newHealth);
    }
}
