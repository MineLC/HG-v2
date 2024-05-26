package lc.minelc.hg.others.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class HitAbilities {

    public void canibal(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) {
            return;
        }
        final double newHealth = damager.getHealth() + (event.getDamage() * 0.2D);
        if (newHealth >= 20) {
            damager.setHealth(20.0D);
            return;
        }
        damager.setHealth(newHealth);
    }
}