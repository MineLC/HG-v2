package lc.minelc.hg.others.events.special;

import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.Title;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.messages.Messages;
import lc.minelc.hg.others.sidebar.SidebarStorage;
import lc.minelc.hg.others.sidebar.SidebarType;
import net.md_5.bungee.api.ChatColor;

public final class DeathMatchEvent implements Runnable {

    private final GameInProgress game;
    private final Data data;
    private final ArenaHGPlugin plugin;

    private int count;
    private boolean alreadyStarted = false;

    public DeathMatchEvent(GameInProgress game, Data data, ArenaHGPlugin plugin) {
        this.game = game;
        this.data = data;
        this.plugin = plugin;
        this.count = data.regresiveCount;
    }

    @Override
    public void run() {
        if (alreadyStarted) {
            if (count-- > 0) {
                return;
            }
            plugin.getServer().getScheduler().runTask(plugin, () -> addPotionEffects());
            count = data.secondsToAddPotions;
            return;
        }
        final Set<Player> players = game.getPlayers();
        if (count == data.regresiveCount) {
            final int effectDuraction = data.regresiveCount * 20;
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                for (final Player player : players) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, effectDuraction, 1000));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, effectDuraction, 1000));
                }
            });
        }

        if (count-- > 0) {
            final Title countdownTitle = new Title(ChatColor.RED.toString() + ChatColor.BOLD.toString() + count);
            for (final Player player : players) {
                player.sendTitle(countdownTitle);
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
            }
            return;
        }
        plugin.getServer().getScheduler().runTask(plugin, () -> startDeathMatch());
        this.count = data.secondsToAddPotions;
        this.alreadyStarted = true;
    }

    private void addPotionEffects() {
        final Set<Player> players = game.getPlayers();
        for (final Player player : players) {
            if (player.getGameMode() == GameMode.SPECTATOR) {
                continue;
            }
            for (final PotionEffect effect : data.effects) {
                player.addPotionEffect(effect);
            }
        }
    }

    private void startDeathMatch() {
        final Title startTitle = new Title(
            Messages.get("death-match.start-title.title"),
            Messages.get("death-match.start-title.subtitle"),
            0, 20, 0
        );

        final Set<Player> players = game.getPlayers();

        game.getWorld().getWorldBorder().setSize(data.worldBorderSize);

        for (final Player player : players) {
            if (player.getGameMode() != GameMode.SPECTATOR) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 1000));
            }

            player.sendTitle(startTitle);
            player.teleport(game.getWorld().getSpawnLocation());

            player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1.0f, 1.0f);
        }

        SidebarStorage.getStorage().getSidebar(SidebarType.IN_GAME).send(players);
    }

    public static record Data(
        int regresiveCount,
        int worldBorderSize,
        int secondsToAddPotions,
        PotionEffect[] effects
    ) {}
}