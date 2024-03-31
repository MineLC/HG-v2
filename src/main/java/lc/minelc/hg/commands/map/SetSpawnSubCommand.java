package lc.minelc.hg.commands.map;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import lc.minelc.hg.mapsystem.CreatorData;
import lc.minelc.hg.utils.BlockLocation;
import lc.minelc.hg.utils.EntityLocation;

final class SetSpawnSubCommand implements MapSubCommand {

    @Override
    public void handle(Player player, String[] args, CreatorData data) {
        final BlockLocation location = getBlock(player, Material.DIAMOND_BLOCK);
        if (location == null) {
            sendWithColor(player, "&cTo set a new spawn, you need view a diamond block");
            return;
        };
        final EntityLocation entityLocation = new EntityLocation(location.x(), location.y(), location.z(), player.getLocation().getYaw(), player.getLocation().getPitch());
        if (!data.getSpawns().add(entityLocation)) {
            sendWithColor(player, "&cThis site is already a spawn");
            return;
        }
        sendWithColor(player, "&aSpawn added. Cords: " + location.toString());
    }
}