package lc.minelc.hg.others.abilities;

import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;

import lc.minelc.hg.game.PlayerInGame;

public final class ArrowAbilities {

    public void arrowExplode(ProjectileHitEvent event, PlayerInGame playerInGame){
        Projectile entity = event.getEntity();
        Arrow arrow = (Arrow)entity;
        if (arrow.getShooter() instanceof Player shooter) {
            if (shooter.getGameMode() != GameMode.SPECTATOR){
                return;
            }
            playerInGame.getGame().getWorld().createExplosion(arrow.getLocation(), 2.0F, false);
            arrow.remove();
        }
    }
}