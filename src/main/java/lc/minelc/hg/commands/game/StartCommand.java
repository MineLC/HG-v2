package lc.minelc.hg.commands.game;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.lcspigot.commands.Command;
import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.game.countdown.pregame.PreGameCountdown;
import lc.minelc.hg.messages.Messages;

public final class StartCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players");
            return;
        }
        if (!player.hasPermission("minelc.vip")) {
            Messages.send(player, "pregame.no-perms-force-start");
            return;
        }
        final GameInProgress game = GameStorage.getStorage().getGame(player.getUniqueId());
        if (game == null) {
            sendWithColor(player, "&eActualmente no estás en ningun juego");
            return;
        }

        if (!(game.getCountdown() instanceof PreGameCountdown preGameCountdown)) {
            sendWithColor(player, "&cEl juego no está en etapa de pregame");
            return;
        }
        if (game.getPlayers().size() < 2) {
            Messages.send(player, "pregame.waiting-players");
            return;
        }

        preGameCountdown.forceStart();
        Messages.send(game.getPlayers(), "pregame.force-start");
    }
}