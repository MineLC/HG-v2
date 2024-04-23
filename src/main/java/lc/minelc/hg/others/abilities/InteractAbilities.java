package lc.minelc.hg.others.abilities;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import lc.minelc.hg.messages.Messages;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.ItemFood;

public final class InteractAbilities {
    
    private static final ItemFood GOLDEN_APPLE = (ItemFood) Item.REGISTRY.a(322);

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
        GOLDEN_APPLE.b(CraftItemStack.asNMSCopy(event.getItem()), ((CraftWorld)event.getPlayer().getWorld()).getHandle(), ((CraftPlayer)event.getPlayer()).getHandle());
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