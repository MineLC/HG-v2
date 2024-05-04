package lc.minelc.hg.listeners.pvp.damage;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameState;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.game.PlayerInGame;
import lc.minelc.hg.game.pregame.PregameStorage;
import lc.minelc.hg.others.abilities.GameAbility;
import lc.minelc.hg.others.spawn.SpawnStorage;
import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public final class EntityDamageListener implements EventListener {

    @ListenerData(
        priority = EventPriority.HIGHEST,
        event = EntityDamageEvent.class
    )
    public void handle(Event defaultEvent) {
        final EntityDamageEvent event = (EntityDamageEvent)defaultEvent;

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (SpawnStorage.getStorage().isInSpawn(player)) {
            event.setCancelled(true);
            event.setDamage(0);
            return;
        }

        final PlayerInGame playerInGame = GameStorage.getStorage().getPlayerInGame(player.getUniqueId());
    
        if (playerInGame == null) {
            return;
        }
    
        final GameInProgress game = playerInGame.getGame();

        if (game.getState() == GameState.PREGAME) {           
            event.setCancelled(true);
            event.setDamage(0);

            if (event.getCause() == DamageCause.FIRE) {
                event.getEntity().setFireTicks(0);
            }

            if (event.getCause() == DamageCause.VOID) {
                player.teleport(PregameStorage.getStorage().mapLocation());
            }
            return;
        }

        if (game.getState() == GameState.IN_GAME) {
            if (game.getInvincibility() || event.getCause() == DamageCause.FALL && playerInGame.containsAbility(GameAbility.NO_FALL_DAMAGE)) {
                event.setCancelled(true);
                event.setDamage(0);
            }
            return;
        }
        if (game.getState() == GameState.END_GAME) {
            event.setCancelled(true);
            if (event.getCause() == DamageCause.VOID) {
                player.teleport(game.getWorld().getSpawnLocation());
            }
            return;
        }

        if (event.getCause() == DamageCause.VOID) {
            player.setHealth(0);
            event.setCancelled(true);
        }
    }
}