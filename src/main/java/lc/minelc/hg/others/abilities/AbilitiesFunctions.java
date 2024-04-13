package lc.minelc.hg.others.abilities;

import lc.minelc.hg.ArenaHGPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Set;

public class AbilitiesFunctions {
    public static ArrayList<Player> cooldown = new ArrayList<>();
    public static ArrayList<Player> frezee = new ArrayList<>();
    private static FileConfiguration abilities;
    public AbilitiesFunctions(ArenaHGPlugin plugin){
        abilities = plugin.loadConfig("abilities");
    }

    public static void heartRecovery(Player player, double hearts) {
        if (player.getItemInHand().getType() == Material.MUSHROOM_SOUP){
            ItemStack bowl = new ItemStack(Material.BOWL, 1);

            double maxHealth = player.getMaxHealth();
            double health = player.getHealth();
            int foodLevel = player.getFoodLevel();

            double heal = hearts * 2.0; // Convertir corazones a puntos de salud
            double feed = hearts * 2.0; // Convertir corazones a puntos de comida

            if (health < maxHealth - 1.0) {
                if (health < maxHealth - heal + 1.0) {
                    player.setItemInHand(bowl);
                    player.setHealth(Math.min(health + heal, maxHealth)); // Asegurar que la salud no exceda el máximo
                } else if (health < maxHealth) {
                    player.setHealth(maxHealth);
                    player.setItemInHand(bowl);
                }
            } else if (health == maxHealth && foodLevel < 20) {
                if (foodLevel < 20 - feed + 1.0) {
                    player.setFoodLevel(Math.min(foodLevel + (int) feed, 20)); // Asegurar que la alimentación no exceda el máximo
                    player.setItemInHand(bowl);
                } else if (foodLevel < 20) {
                    player.setFoodLevel(20);
                    player.setItemInHand(bowl);
                }
            }
        }
    }

    public static void teleportWithTorch(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if (p.getInventory().getItemInHand().getType() == Material.REDSTONE_TORCH_ON &&
                (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            e.setCancelled(true);
            if (cooldown.contains(p)) {
                p.sendMessage(ChatColor.RED + "Necesitas esperar para volver a usarlo!");
            } else {
                Location loc = e.getPlayer().getTargetBlock((Set<Material>) null, 500).getLocation();
                if (loc.getBlock().getType() == Material.AIR) {
                    p.sendMessage(ChatColor.RED + "Necesitas mirar un bloque para teletransportarte");
                    return;
                }
                if (loc.distance(e.getPlayer().getLocation()) > abilities.getInt("AB.TELEPORT_WITH_TORCH.distance")) {
                    p.sendMessage(ChatColor.RED + "No puedes teletransportarte tan lejos");
                    return;
                }
                cooldown.add(p);
                AbilitiesCooldown.flashCooldown(p,abilities);
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
    public static void jumpWithFireworks(PlayerInteractEvent e){
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
    public static void eatCookie(PlayerInteractEvent event){
        Player p = event.getPlayer();
        Action a = event.getAction();
        if ((a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK)) {
            // Verificar si el jugador tiene una galleta en la mano
            ItemStack itemInHand = p.getItemInHand();
            if (itemInHand != null && itemInHand.getType() == Material.COOKIE) {
                p.addPotionEffect(new PotionEffect(
                        PotionEffectType.INCREASE_DAMAGE, abilities.getInt("AB.EAT_COOKIE.duration") * 20, 0));
                p.getInventory().removeItem(new ItemStack(Material.COOKIE, 1));
                p.playSound(p.getLocation(), Sound.BURP, 1.0F, 1.0F);
            }
        }
    }
    public static void firetrow(PlayerInteractEvent event){
        Player p = event.getPlayer();
        Action a = event.getAction();
        if ((a == Action.LEFT_CLICK_BLOCK || a == Action.LEFT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK || a == Action.RIGHT_CLICK_AIR) && p.getItemInHand() != null &&
                p.getItemInHand().getType().equals(Material.FIREBALL)) {
            Vector lookat = p.getLocation().getDirection().multiply(10);
            Fireball fire = p.getWorld().spawn(p.getLocation().add(lookat), Fireball.class);
            fire.setShooter(p);
            p.playSound(p.getLocation(), Sound.FIRE, 1.0F, 1.5F);
            p.getInventory().removeItem(new ItemStack(Material.FIREBALL, 1));
        }
    }
    public static void fastBreakTree(BlockBreakEvent event, World w){
        Player p = event.getPlayer();
        Block b = event.getBlock();
        if (b.getType() == Material.LOG) {
            Double y = Double.valueOf(b.getLocation().getY() + 1.0D);
            Location l = new Location(w, b.getLocation().getX(), y.doubleValue(), b
                    .getLocation().getZ());
            while (l.getBlock().getType() == Material.LOG) {
                l.getBlock().breakNaturally();
                y = Double.valueOf(y.doubleValue() + 1.0D);
                l.setY(y.doubleValue());
            }
            p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
        }
    }
}
