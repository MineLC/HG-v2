package lc.minelc.hg.listeners.pvp.damage;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.game.PlayerInGame;
import lc.minelc.hg.others.abilities.ArrowAbilities;
import lc.minelc.hg.others.abilities.GameAbility;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ProjectilHitListener implements EventListener {
    private final ArrowAbilities arrowAbilities = new ArrowAbilities();

    @ListenerData(
        priority = EventPriority.HIGHEST,
        event = ProjectileHitEvent.class
    )
    @Override
    public void handle(Event defaultEvent) {
        final ProjectileHitEvent event = (ProjectileHitEvent) defaultEvent;
        if (event.getEntity() instanceof Player) {
            handleAbilitiesInteract(event);
        }
    }

    private void handleAbilitiesInteract(final ProjectileHitEvent event) {
        Arrow arrow = (Arrow)event.getEntity();
        LivingEntity shooter = (LivingEntity)arrow.getShooter();
        Player player = (Player) shooter;

        final PlayerInGame playerInGame = GameStorage.getStorage().getPlayerInGame(player.getUniqueId());
        if (playerInGame == null) {
            return;
        }
        GameAbility[] abilities = playerInGame.getGameAbilities();
        if (abilities == null) {
            return;
        }
        for (GameAbility ability : abilities) {
            switch (ability) {
                case EXPLODING_ARROWS:
                    arrowAbilities.arrowExplode(event, playerInGame);
                    break;
                default:
                    // No se requiere acción para otras habilidades
                    break;
            }
        }
    }
}
