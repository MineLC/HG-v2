package lc.minelc.hg.commands.game;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.lcspigot.commands.Command;
import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.others.sidebar.SidebarStorage;
import lc.minelc.hg.others.sidebar.SidebarType;
import lc.minelc.hg.others.spawn.SpawnStorage;

public final class LeaveCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        final Player player = (Player)sender; 
        final GameInProgress game = GameStorage.getStorage().getGame(player.getUniqueId());
        if (game == null) {
            send(player, "Actualmente no estás en ningun juego");
            return;
        }

        GameStorage.getStorage().leave(game, player);
        SpawnStorage.getStorage().sendToSpawn(player);
        SidebarStorage.getStorage().getSidebar(SidebarType.SPAWN).send(player);

        final Set<Player> players = game.getPlayers();
        for (final Player otherPlayer : players) {
            otherPlayer.hidePlayer(player);
            player.hidePlayer(otherPlayer);
        }
        player.getActivePotionEffects().forEach((potion) -> player.removePotionEffect(potion.getType()));
        send(player, "Has salido del juego");
    }
}