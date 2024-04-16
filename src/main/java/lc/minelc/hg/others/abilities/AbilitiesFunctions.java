package lc.minelc.hg.others.abilities;

import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.game.PlayerInGame;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
                } else {
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

    public static void thunderWithAxe(PlayerInteractEvent event){
        Player p = event.getPlayer();
        Action a = event.getAction();

        if (a == Action.RIGHT_CLICK_BLOCK && p.getItemInHand()
                .getType() == Material.DIAMOND_AXE)
            if (!cooldown.contains(p)) {
                cooldown.add(p);
                AbilitiesCooldown.thorCooldown(p, abilities);
                Block block = event.getClickedBlock();
                Location loc = block.getLocation();
                World world = Bukkit.getServer().getWorlds().get(0);
                if (event.getClickedBlock().getType() != Material.BEDROCK)
                    event.getClickedBlock().setType(Material.NETHERRACK);
                event.getClickedBlock().getRelative(BlockFace.UP).setType(Material.FIRE);
                world.strikeLightning(loc);
            } else {
                sendWithColor(p, abilities.getString("AB.THUNDER_WITH_AXE.expired"));
            }
    }

    public static void arrowExplode(ProjectileHitEvent event, PlayerInGame playerInGame){
        Projectile entity = event.getEntity();
        if (entity.getType() == EntityType.ARROW) {
            Arrow arrow = (Arrow)entity;
            LivingEntity shooter = (LivingEntity)arrow.getShooter();
            if (shooter.getType() == EntityType.PLAYER) {
                Player player = (Player)shooter;
                if (player.getGameMode() != GameMode.SPECTATOR){
                    return;
                }
                playerInGame.getGame().getWorld().createExplosion(arrow.getLocation(), 2.0F, false);
                arrow.remove();
            }
        }
    }
    public static void snowballToCobwebs(ProjectileHitEvent event, PlayerInGame playerInGame){
        Projectile proj = event.getEntity();
        if (proj instanceof Snowball snow) {
            LivingEntity shooter = (LivingEntity)snow.getShooter();
            if (shooter instanceof Player) {
                Location loc = snow.getLocation();
                playerInGame.getGame().getWorld().getBlockAt(loc).setType(Material.WEB);
            }
        }
    }

    public static void blidingPlayers(EntityDamageByEntityEvent event){
        Player dam = (Player)event.getDamager();

        Entity defender = event.getEntity();
        if (defender.getType() == EntityType.PLAYER) {
            Player def = (Player)defender;
            int random = (int)(Math.random() * (abilities.getInt("AB.BLIND_PLAYERS.chance") - 1) + 1.0D);
            if (random == 1 && !cooldown.contains(def)) {
                def.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, abilities.getInt("AB.BLIND_PLAYERS.duration") * 20, 0));
                def.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, abilities.getInt("AB.BLIND_PLAYERS.duration") * 20, 0));
                def.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, abilities.getInt("AB.BLIND_PLAYERS.duration") * 20, 0));
                cooldown.add(def);
                sendWithColor(dam, abilities.getString("AB.BLIND_PLAYERS.damager"));
                sendWithColor(def, abilities.getString("AB.BLIND_PLAYERS.defender"));
                AbilitiesCooldown.trollCooldown(def, abilities);
                dam.playSound(dam.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
            }
        }
    }
    public static void weakenPlayers(EntityDamageByEntityEvent event){
        Player dam = (Player)event.getDamager();

        Entity defender = event.getEntity();
        if (defender.getType() == EntityType.PLAYER) {
            Player def = (Player)defender;
            int random = (int)(Math.random() * (abilities.getInt("AB.WEAKEN_PLAYERS.chance") - 1) + 1.0D);
            if (random == 1 && !cooldown.contains(def)) {
                def.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, abilities.getInt("AB.WEAKEN_PLAYERS.curation") * 20, 0));
                cooldown.add(def);
                sendWithColor(dam, abilities.getString("AB.WEAKEN_PLAYERS.damager"));
                sendWithColor(def, abilities.getString("AB.WEAKEN_PLAYERS.defender"));
                AbilitiesCooldown.orcoCooldown(def, abilities);
                dam.playSound(dam.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
            }
        }
    }
    public static void stealItemsWithStick(EntityDamageByEntityEvent event){
        Player dam = (Player)event.getDamager();

        Entity defender = event.getEntity();
        if (defender.getType() == EntityType.PLAYER) {
            Player def = (Player)defender;
            if (dam.getItemInHand().getType() == Material.STICK && def.getItemInHand() != null)
                if (!cooldown.contains(dam)) {
                    int random = (int)(Math.random() * (abilities.getInt("AB.ITEM_THEFT.chance") - 1) + 1.0D);
                    if (random == 1) {
                        cooldown.add(dam);
                        AbilitiesCooldown.thiefCooldown(dam, abilities);
                        dam.getInventory().clear(dam.getInventory().getHeldItemSlot());
                        dam.getInventory().addItem(def.getItemInHand());
                        def.getInventory().clear(def.getInventory().getHeldItemSlot());
                        sendWithColor(dam, abilities.getString("AB.ITEM_THEFT.success"));
                        sendWithColor(def, abilities.getString("AB.ITEM_THEFT.success"));
                        dam.playSound(dam.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                    }
                } else {
                    sendWithColor(dam, abilities.getString("AB.ITEM_THEFT.expired"));
                }
        }
    }
    public static void stealItems(EntityDamageByEntityEvent event){
        Player dam = (Player)event.getDamager();

        Entity defender = event.getEntity();
        if (defender.getType() == EntityType.PLAYER) {
            Player def = (Player)defender;
            if (!cooldown.contains(dam)) {
                int random = (int)(Math.random() * (abilities.getInt("AB.ITEM_THEFT_2.chance") - 1) + 1.0D);
                if (random == 1) {
                    cooldown.add(dam);
                    AbilitiesCooldown.thiefCooldown(dam, abilities);
                    dam.getInventory().clear(dam.getInventory().getHeldItemSlot());
                    dam.getInventory().addItem(def.getItemInHand());
                    def.getInventory().clear(def.getInventory().getHeldItemSlot());
                    sendWithColor(dam, abilities.getString("AB.ITEM_THEFT_2.success"));
                    sendWithColor(def, abilities.getString("AB.ITEM_THEFT_2.success"));
                    dam.playSound(dam.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                }
            } else {
                sendWithColor(dam, abilities.getString("AB.ITEM_THEFT_2.expired"));
            }

        }
    }
    public static void headShot(EntityDamageByEntityEvent event, PlayerInGame playerInGame){
        if (event.getDamager() instanceof Arrow arrow) {
            if (arrow.getShooter() instanceof Player p) {
                LivingEntity victim = (LivingEntity)event.getEntity();
                if (event.getEntity() instanceof LivingEntity &&
                        victim instanceof Player v) {
                    sendWithColor(p, "&aAhora &e" + v.getName() + " &atiene &4" + (Math.round(v.getHealth() * 100.0D) / 100L) + "HP");
                    if (p.getLocation().distance(event.getEntity().getLocation()) >= abilities.getInt("AB.INSTANT_DEATH.distance")) {
                        ItemStack helmet = v.getInventory().getHelmet();
                        if (helmet == null) {
                            sendForAll(playerInGame.getGame().getPlayers(), "<victim> fue headshotted por <player>.".replace("<victim>", v.getName()).replace("<player>", p.getName()));
                            p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                            sendForAll(playerInGame.getGame().getPlayers(), "<amount> jugadores restantes.".replace("<amount>", String.valueOf(playerInGame.getGame().getPlayers().size())));
                            sendWithColor(v,"");
                            Location light = v.getLocation();
                            playerInGame.getGame().getWorld().strikeLightningEffect(light.add(0.0D, 100.0D, 0.0D));
                            v.setHealth(0.0D);
                        } else {
                            helmet.setDurability((short)(helmet.getDurability() + 20));
                            v.setHealth(2.0D);
                            v.getInventory().setHelmet(helmet);
                        }
                        sendWithColor(p, "&aHeadshot!");
                    }
                }
            }
        }
    }
    public static void fastBreakTree(BlockBreakEvent event, World w){
        Player p = event.getPlayer();
        Block b = event.getBlock();
        if (b.getType() == Material.LOG) {
            double y = b.getLocation().getY() + 1.0D;
            Location l = new Location(w, b.getLocation().getX(), y, b
                    .getLocation().getZ());
            while (l.getBlock().getType() == Material.LOG) {
                l.getBlock().breakNaturally();
                y = y + 1.0D;
                l.setY(y);
            }
            p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
        }
    }

    private static void sendForAll(Set<Player> players, String message){
        players.forEach((player) -> sendWithColor(player,message));
    }
    private static void sendWithColor(Player player, String message) {
        player.sendMessage(message.replace('&', '§'));
    }
}
