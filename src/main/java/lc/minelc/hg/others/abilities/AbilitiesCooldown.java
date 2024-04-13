package lc.minelc.hg.others.abilities;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Timer;
import java.util.TimerTask;

public class AbilitiesCooldown {
    public static void monkCooldown(final Player player, Configuration config) {
        TimerTask action = new TimerTask() {
            public void run() {
                AbilitiesFunctions.cooldown.remove(player);
            }
        };
        Timer timer = new Timer();
        timer.schedule(action, (config.getInt("AB.12.Cooldown") * 1000L));
    }

    public static void thiefCooldown(final Player player, FileConfiguration config) {
        TimerTask action = new TimerTask() {
            public void run() {
                AbilitiesFunctions.cooldown.remove(player);
            }
        };
        Timer timer = new Timer();
        timer.schedule(action, (config.getInt("AB.ITEM_THEFT.cooldown") * 1000));
    }

    public static void ghostCooldown(final Player player, FileConfiguration config) {
        TimerTask action = new TimerTask() {
            public void run() {
                AbilitiesFunctions.cooldown.remove(player);
            }
        };
        Timer timer = new Timer();
        timer.schedule(action, (config.getInt("AB.INVISIBLE_WHEN_EATING_APPLE.cooldown") * 1000));
    }

    public static void viperCooldown(final Player player, FileConfiguration config) {
        TimerTask action = new TimerTask() {
            public void run() {
                AbilitiesFunctions.cooldown.remove(player);
            }
        };
        Timer timer = new Timer();
        timer.schedule(action, (config.getInt("AB.POISON_PLAYERS.duration") * 1000));
    }

    public static void orcoCooldown(final Player player, FileConfiguration config) {
        TimerTask action = new TimerTask() {
            public void run() {
                AbilitiesFunctions.cooldown.remove(player);
            }
        };
        Timer timer = new Timer();
        timer.schedule(action, (config.getInt("AB.WEAKEN_PLAYERS.Duration") * 1000));
    }

    public static void trollCooldown(final Player player, FileConfiguration config) {
        TimerTask action = new TimerTask() {
            public void run() {
                AbilitiesFunctions.cooldown.remove(player);
            }
        };
        Timer timer = new Timer();
        timer.schedule(action, (config.getInt("AB.BLIND_PLAYERS.duration") * 1000));
    }

    public static void thorCooldown(final Player player, FileConfiguration config) {
        TimerTask action = new TimerTask() {
            public void run() {
                AbilitiesFunctions.cooldown.remove(player);
            }
        };
        Timer timer = new Timer();
        timer.schedule(action, (config.getInt("AB.THUNDER_WITH_AXE.cooldown") * 1000));
    }

    public static void flashCooldown(final Player player, FileConfiguration config) {
        TimerTask action = new TimerTask() {
            public void run() {
                AbilitiesFunctions.cooldown.remove(player);
                player.sendMessage(ChatColor.GREEN + "Ahora puedes volver a teletransportarte!");
            }
        };
        Timer timer = new Timer();
        timer.schedule(action, (config.getInt("AB.TELEPORT_WITH_TORCH.cooldown") * 1000L));
    }

    public static void timeCooldown(final Player player, FileConfiguration config) {
        TimerTask action = new TimerTask() {
            public void run() {
                AbilitiesFunctions.cooldown.remove(player);
            }
        };
        Timer timer = new Timer();
        timer.schedule(action, (config.getInt("AB.FREEZE_PLAYERS.cooldown") * 1000));
    }

    public static void freezeCooldown(final Player player, FileConfiguration config) {
        TimerTask action = new TimerTask() {
            public void run() {
                sendWithColor(player, config.getString("AB.FREEZE_PLAYERS.unfrozen"));
                AbilitiesFunctions.frezee.remove(player);
            }
        };
        Timer timer = new Timer();
        timer.schedule(action, (config.getInt("AB.FREEZE_PLAYERS.duration") * 1000));
    }
    private static void sendWithColor(Player player, String message) {
        player.sendMessage(message.replace('&', '§'));
    }
}