package lc.minelc.hg.listeners.pvp.damage;

import lc.minelc.hg.game.PlayerInGame;
import lc.minelc.hg.others.abilities.GameAbility;
import lc.minelc.hg.others.abilities.HitAbilities;

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
    private final HitAbilities pvpAbilities = new HitAbilities();

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
        if (game.getInvincibility()){
            event.setCancelled(true);
            event.setDamage(0);
        }

        if (game.getActiveEvent() != null && game.getActiveEvent().eventType() == GameEventType.CRITICAL) {
            event.setDamage(event.getDamage() + (event.getDamage() / 100) * 25);
        }

        if (event.getDamager() instanceof Player) {
            final PlayerInGame playerInGame = GameStorage.getStorage().getPlayerInGame(event.getDamager().getUniqueId());
            handleAbilitiesInteract(event, playerInGame);
        }
    }
    private void handleAbilitiesInteract(final EntityDamageByEntityEvent event, final PlayerInGame playerInGame) {
        final GameAbility[] abilities = playerInGame.getGameAbilities();

        for (final GameAbility ability : abilities) {
            switch (ability) {
                case CANIBAL:
                    pvpAbilities.canibal(event);
                    break;
                default:
                    break;
            }
        }
    }
}