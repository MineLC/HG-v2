package lc.minelc.hg.others.abilities;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import lc.minelc.hg.game.PlayerInGame;
import lc.minelc.hg.messages.Messages;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.ItemFood;

public final class InteractAbilities {
    
    private static final ItemFood GOLDEN_APPLE = (ItemFood) Item.REGISTRY.a(322);

    public void soup(final PlayerInteractEvent event, final Material type, final float hearts) {
        if (type != Material.MUSHROOM_SOUP || (event.getPlayer().getHealth() >= 20 && event.getPlayer().getFoodLevel() >= 20)) {
            return;
        }
        final double newHealth = event.getPlayer().getHealth() + (hearts * 2);
        if (newHealth >= event.getPlayer().getMaxHealth()) {
            event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
        } else {
            event.getPlayer().setHealth(newHealth);
        }
        event.getPlayer().setFoodLevel(20);
        event.setCancelled(true);
        final PlayerInventory inventory = event.getPlayer().getInventory();
        if (event.getItem().getAmount() == 1) {
            inventory.setItemInHand(new ItemStack(Material.BOWL, 1));
            return;
        }
        event.getItem().setAmount(event.getItem().getAmount() - 1);
        inventory.setItemInHand(event.getItem());
        deleteOneItem(event.getPlayer().getInventory(), event.getItem());
    }

    public void teleportWithTorch(final PlayerInteractEvent e, final PlayerInGame playerInGame){
        Player p = e.getPlayer();
        if (p.getInventory().getItemInHand().getType() == Material.REDSTONE_TORCH_ON &&
                (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            e.setCancelled(true);
            if (playerInGame.canUseAbility(GameAbility.TELEPORT_WITH_TORCH, 5)) {
                p.sendMessage(ChatColor.RED + "Necesitas esperar para volver a usarlo!");
            } else {
                Location loc = e.getPlayer().getTargetBlock((Set<Material>) null, 500).getLocation();
                if (loc.getBlock().getType() == Material.AIR) {
                    p.sendMessage(ChatColor.RED + "Necesitas mirar un bloque para teletransportarte");
                    return;
                }
                if (loc.distance(e.getPlayer().getLocation()) > 25) {
                    p.sendMessage(ChatColor.RED + "No puedes teletransportarte tan lejos");
                    return;
                }
                playerInGame.setCooldown(GameAbility.TELEPORT_WITH_TORCH);
                p.setFallDistance(0.0F);
                loc.add(0.0D, 1.0D, 0.0D);
                p.teleport(loc);
                p.setFallDistance(0.0F);
                int distance = (int)(p.getLocation().distance(loc) / 2.0D);
                p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, distance, 0));
                p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, -1.0F);
            }
        }
    }

   public void jumpWithFireworks(final PlayerInteractEvent e){
        Player p = e.getPlayer();
        if (e.getPlayer().getItemInHand().getType() == Material.FIREWORK) {
            e.setCancelled(true);
            if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) && e.getMaterial() == Material.FIREWORK && !p.isSneaking()) {
                Block b = p.getLocation().getBlock();
                if (b.getType() != Material.AIR || b.getRelative(BlockFace.DOWN).getType() != Material.AIR) {
                    p.setFallDistance(-5.0F);
                    Vector vector = p.getEyeLocation().getDirection();
                    vector.multiply(0.6F);
                    vector.setY(1);
                    p.setVelocity(vector);
                }
            }
        }
    }

    public void firetrow(PlayerInteractEvent event){
        Player p = event.getPlayer();
        if (p.getItemInHand().getType() == Material.FIREBALL) {
            Vector lookat = p.getLocation().getDirection().multiply(10);
            Fireball fire = p.getWorld().spawn(p.getLocation().add(lookat), Fireball.class);
            fire.setShooter(p);
            p.playSound(p.getLocation(), Sound.FIRE, 1.0F, 1.5F);
            deleteOneItem(p.getInventory(), p.getItemInHand());
        }
    }

    public void cookie(final PlayerInteractEvent event, final Material type) {
        if (type != Material.COOKIE) {
            return;
        }
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1));
        Messages.send(event.getPlayer(), "abilities.cookie");
        event.setCancelled(true);
        deleteOneItem(event.getPlayer().getInventory(), event.getItem());
    }

    public void fastGoldenApple(final PlayerInteractEvent event, final Material type) {
        if (type != Material.GOLDEN_APPLE) {
            return;
        }
        final Player player = event.getPlayer();
        GOLDEN_APPLE.b(CraftItemStack.asNMSCopy(event.getItem()), ((CraftWorld)event.getPlayer().getWorld()).getHandle(), ((CraftPlayer)event.getPlayer()).getHandle());
        event.setCancelled(true);
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