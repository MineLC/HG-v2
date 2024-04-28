package lc.minelc.hg.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.minelc.hg.database.mongodb.HGPlayerData;
import lc.minelc.hg.database.mongodb.PlayerDataStorage;
import lc.minelc.hg.messages.Messages;
import lc.minelc.hg.others.kits.Kit;
import lc.minelc.hg.others.kits.KitStorage;
import lc.lcspigot.commands.Command;

public final class InfoCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) { 
        Player player;
        if (args.length == 1) {
            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sendWithColor(sender, "&cEste jugador no existe");
                return;
            }
        } else {
            if (!(sender instanceof Player)) {
                send(sender, "You need be a user to use this command");
                return;
            } else {
                player = (Player)sender;
            }
        }

        final HGPlayerData data = PlayerDataStorage.getStorage().get(player.getUniqueId());
        final Kit kit = KitStorage.getStorage().kitsPerId().get(data.kitSelected);
        final String kitName = (data.kitSelected == 0 || kit == null) ? "Ninguno" : kit.name();
        
        send(sender, Messages.get("commands.info")
            .replace("%name%", player.getName())
            .replace("%kills%", String.valueOf(data.kills))
            .replace("%deaths%", String.valueOf(data.deaths))
            .replace("%level%", String.valueOf(data.level))
            .replace("%kit%", kitName)
            .replace("%kdr%", (data.deaths == 0) ? String.valueOf(data.kills) : String.valueOf((float)(data.kills / data.deaths)))
            .replace("%coins%", String.valueOf(data.coins))
            .replace("%wins%", String.valueOf(data.wins))
        );
    }
}