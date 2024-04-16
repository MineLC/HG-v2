package lc.minelc.hg.listeners.pvp.damage;

import lc.minelc.hg.game.PlayerInGame;
import lc.minelc.hg.others.abilities.GameAbility;
import org.bukkit.Bukkit;
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
import org.bukkit.event.player.PlayerInteractEvent;

import static lc.minelc.hg.others.abilities.AbilitiesFunctions.*;

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
        if (game.getInvincibility()){
            event.setCancelled(true);
            event.setDamage(0);
        }

        if (game.getCurrentEvent() != null && game.getCurrentEvent().eventType() == GameEventType.CRITICAL) {
            event.setDamage(event.getDamage() + (event.getDamage() / 100) * 25);
        }

        if (event.getDamager() instanceof Player) {
            final PlayerInGame playerInGame = GameStorage.getStorage().getPlayerInGame(event.getDamager().getUniqueId());
            handleAbilitiesInteract(event,playerInGame);
        }
    }
    private void handleAbilitiesInteract(final EntityDamageByEntityEvent event, final PlayerInGame playerInGame) {
        GameAbility[] abilities = playerInGame.getGameAbilities();

        for (GameAbility ability : abilities) {
            switch (ability) {
                case BLIND_PLAYERS:
                    blidingPlayers(event);
                    break;
                case WEAKEN_PLAYERS:
                    weakenPlayers(event);
                    break;
                case ITEM_THEFT:
                    stealItemsWithStick(event);
                    break;
                case ITEM_THEFT_2:
                    stealItems(event);
                    break;
                case INSTANT_DEATH:
                    headShot(event, playerInGame);
                    // Agrega más casos según sea necesario para otras habilidades
                default:
                    // No se requiere acción para otras habilidades
                    break;
            }
        }
    }
}