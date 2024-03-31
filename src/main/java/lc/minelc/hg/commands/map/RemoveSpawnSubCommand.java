package lc.minelc.hg.commands.map;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import lc.minelc.hg.mapsystem.CreatorData;
import lc.minelc.hg.utils.BlockLocation;
import lc.minelc.hg.utils.EntityLocation;

final class RemoveSpawnSubCommand implements MapSubCommand {

    @Override
    public void handle(Player player, String[] args, CreatorData data) {
        final BlockLocation targetBlock = getBlock(player, Material.DIAMOND_BLOCK);

        if (targetBlock == null) {
            sendWithColor(player, "&cTo remove a spawn, you need view a diamond block");
            return;
        };

        final EntityLocation entityLocation = new EntityLocation(targetBlock.x(), targetBlock.y(), targetBlock.z(), 0.F, 0.F);
        if (!data.getSpawns().remove(entityLocation)) {
            sendWithColor(player, "&cThis isn't a spawn for any team");
            return;
        }
        sendWithColor(player, "&aSpawn removed!");
    }
}