package lc.minelc.hg.commands.map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.mapsystem.CreatorData;
import lc.minelc.hg.mapsystem.MapCreatorData;

import lc.lcspigot.commands.Command;

import net.md_5.bungee.api.ChatColor;

public final class MapCreatorCommand implements Command {

    private final EditorSubCommand editor;
    private final SetSpawnSubCommand setspawn;
    private final RemoveSpawnSubCommand removeSpawn;
    private final SaveSubCommand save;

    private final MapCreatorData data;

    public MapCreatorCommand(ArenaHGPlugin plugin, MapCreatorData data) {
        this.editor = new EditorSubCommand(data);
        this.setspawn = new SetSpawnSubCommand();
        this.removeSpawn = new RemoveSpawnSubCommand();
        this.save = new SaveSubCommand(plugin, data);
        this.data = data;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            send(sender, "You need be a player to use map creator");
            return;
        } 

        if (args.length < 1) {
            sender.sendMessage(format());
            return;
        }
        final String subcommand = args[0].toLowerCase();

        if (subcommand.equals("editor")) {
            editor.handle(player, args);
            return;
        }
        if (subcommand.equals("save")) {
            save.handle(player, args);
            return;
        }

        final CreatorData creatorData = data.getData(player.getUniqueId());

        if (creatorData == null) {
            sendWithColor(sender, "&cTo use this command enable editor mode. /map editor on");
            return;
        }

        switch (subcommand) {
            case "setspawn":
                setspawn.handle(player, args, creatorData);
                break;
            case "removespawn":
                removeSpawn.handle(player, args, creatorData);
                break;
            default:
                sendWithColor(sender, format());
                break;
        }
        return;
    }

    @Override
    public String[] tab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return new String[] { "editor", "addgenerator", "removegenerator", "setspawn", "removespawn", "setegg", "removeegg", "addshop", "removeshopspawn", "save", "setmax", "info"};
        }
        switch (args[0].toLowerCase()) {
            case "editor": return editor.tab(sender, args);
            case "setspawn": return setspawn.tab(sender, args);
            default: return none();
        }
    }

    private String format() {
        return """
              &6&lMap creator &7(MineLC)
              &r
              &e/map &7->
                &6editor &7(on-off) - &fActivate/Disable the editor mode
                &r
                &6setspawn &7(team) - &fSet spawn
                &6removespawn &7(team) - &fRemove spawn
                &r
                &6setmax &7(amount)- &fSet max players per team
                &r
                &6save &7- &fSave all settings in the world
            """.replace('&', ChatColor.COLOR_CHAR);
    }
}