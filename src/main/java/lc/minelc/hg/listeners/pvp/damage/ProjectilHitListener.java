package lc.minelc.hg.listeners.pvp.damage;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.game.PlayerInGame;
import lc.minelc.hg.others.abilities.GameAbility;
import lc.minelc.hg.others.spawn.SpawnStorage;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static lc.minelc.hg.others.abilities.AbilitiesFunctions.*;

public class ProjectilHitListener implements EventListener {

    @ListenerData(
            priority = EventPriority.HIGHEST,
            event = ProjectileHitEvent.class
    )
    @Override
    public void handle(Event defaultEvent) {
        ProjectileHitEvent event = (ProjectileHitEvent) defaultEvent;

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (SpawnStorage.getStorage().isInSpawn(player)) {
            return;
        }
        handleAbilitiesInteract(event);
    }
    private void handleAbilitiesInteract(final ProjectileHitEvent event) {
        Arrow arrow = (Arrow)event.getEntity();
        LivingEntity shooter = (LivingEntity)arrow.getShooter();
        Player player = (Player) shooter;

        final PlayerInGame playerInGame = GameStorage.getStorage().getPlayerInGame(player.getUniqueId());

        GameAbility[] abilities = playerInGame.getGameAbilities();
        for (GameAbility ability : abilities) {
            switch (ability) {
                case EXPLODING_ARROWS:
                    arrowExplode(event,  playerInGame);
                    break;
                case GENERATE_COBWEBS_WITH_SNOWBALLS:
                    snowballToCobwebs(event, playerInGame);
                default:
                    // No se requiere acción para otras habilidades
                    break;
            }
        }
    }
}
