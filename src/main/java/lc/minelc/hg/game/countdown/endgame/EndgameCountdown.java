package lc.minelc.hg.game.countdown.endgame;

import java.util.Set;

import org.bukkit.entity.Player;
import org.tinylog.Logger;

import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.game.countdown.GameCountdown;
import lc.minelc.hg.others.sidebar.SidebarStorage;
import lc.minelc.hg.others.sidebar.SidebarType;
import lc.minelc.hg.others.spawn.SpawnStorage;

public class EndgameCountdown extends GameCountdown  {

    private final GameInProgress game;

    public EndgameCountdown(GameInProgress game) {
        this.game = game;
    }

    @Override
    public void run() {
        try {
            final Set<Player> gamePlayers = game.getPlayers();
    
            for (final Player player : gamePlayers) {
                for (final Player otherPlayer : gamePlayers) {
                    otherPlayer.hidePlayer(player);
                    player.hidePlayer(otherPlayer);
                }
                SpawnStorage.getStorage().sendToSpawn(player);
                SidebarStorage.getStorage().getSidebar(SidebarType.SPAWN).send(player);
                GameStorage.getStorage().remove(player.getUniqueId());
                player.setHealth(20);
                player.setLevel(0);
                player.setFoodLevel(20);
                player.getActivePotionEffects().forEach((potion) -> player.removePotionEffect(potion.getType()));
            }
            game.setCountdown(null);
            GameStorage.getStorage().stop(game);
        } catch (Exception e) {
            Logger.error(e);
        }
    }
}