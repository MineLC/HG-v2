package lc.minelc.hg.others.abilities;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import lc.minelc.hg.messages.Messages;

public final class InteractAbilities {
    
    public void soup(final PlayerInteractEvent event, final Material type, final int hearts) {
        if (type != Material.MUSHROOM_SOUP || event.getPlayer().getHealth() == 20) {
            return;
        }
        final double newHealth = event.getPlayer().getHealth() + (hearts << 1);
        if (newHealth >= 20) {
            event.getPlayer().setHealth(20);
            return;
        }
        event.getPlayer().setHealth(newHealth);
        deleteOneItem(event.getPlayer().getInventory(), event.getItem());
    }

    public void cookie(final PlayerInteractEvent event, final Material type) {
        if (type != Material.COOKIE) {
            return;
        }
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1));
        Messages.send(event.getPlayer(), "abilities.cookie");
        deleteOneItem(event.getPlayer().getInventory(), event.getItem());
    }

    public void fastGoldenApple(final PlayerInteractEvent event, final Material type) {
        if (type != Material.GOLDEN_APPLE) {
            return;
        }
        final Player player = event.getPlayer();
        if (event.getItem().getDurability() == 1) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 3));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 400, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 6000, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 6000, 0));
        }
        deleteOneItem(player.getInventory(), event.getItem());
    }

    private void deleteOneItem(final PlayerInventory inventory, final ItemStack item) {
        if (item.getAmount() == 1) {
            inventory.setItemInHand(null);
            return;
        }
        item.setAmount(item.getAmount() - 1);
        inventory.setItemInHand(item);
    }
}