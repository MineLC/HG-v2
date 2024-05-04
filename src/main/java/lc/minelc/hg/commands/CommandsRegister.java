package lc.minelc.hg.commands;

import org.bukkit.entity.Player;

import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.commands.game.LeaveCommand;
import lc.minelc.hg.commands.game.StartCommand;
import lc.minelc.hg.commands.map.MapCreatorCommand;
import lc.minelc.hg.database.mongodb.PlayerDataStorage;
import lc.minelc.hg.mapsystem.MapCreatorData;
import lc.minelc.hg.messages.Messages;
import lc.minelc.hg.others.top.TopStorage;
import lc.lcspigot.commands.CommandStorage;

public final class CommandsRegister {

    public void registerCommands(ArenaHGPlugin plugin) {
        CommandStorage.register((sender, args) -> {
            if (sender instanceof Player player) {
                final String format = Messages.get("commands.level");
                sender.sendMessage(format.replace("%level%", String.valueOf(PlayerDataStorage.getStorage().get(player.getUniqueId()).level)));
            }
        }, "level");
    
        CommandStorage.register((sender, args) -> {
            if (sender instanceof Player player) {
                player.openInventory(TopStorage.getStorage().getInventory());
            }
        }, "top");

        CommandStorage.register(new GiveCoin(), "givecoin");
        CommandStorage.register(new InfoCommand(), "inf", "in", "i");
        CommandStorage.register(new MapCreatorCommand(plugin, new MapCreatorData()), "map");
        CommandStorage.register(new LeaveCommand(), "leave", "salir");
        CommandStorage.register(new StartCommand(), "iniciar", "start");

        CommandStorage.register((sender, args) -> Messages.send(sender, "commands.help"), "help", "ayuda");
        CommandStorage.register((sender, args) -> sender.sendMessage(" verhentai.top §e9/10 §7(Ofrece preview y una detallada sinopsis) \n §fhentaila.com §e7/10 §7(Comunidad activa, pero muy rara) \n §fnhentai.com §e9-10 §7(God pero ingles) \n §fhentaird.com §e7-10 §7(Muchos hentais viejos, con buena historia) \n §fmuchohentai.com §e8-10 §7(Ofrece episodios RAW y en otros idiomas) \n §fchochox.com §e10-10 §7(De hecho, hay un comic en chochox que lo explica) \n \n §8by iChocoMilk (Lector de la biblia)"), "hentai");
    }
}
