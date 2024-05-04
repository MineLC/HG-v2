package lc.minelc.hg.others.top;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lc.minelc.hg.inventory.internal.CustomInventoryHolder;

public final class TopInventoryBuilder {

    public Inventory build(final PlayerInTop[] data, final String inventoryName) {
        final Inventory inventory = Bukkit.createInventory(new CustomInventoryHolder("top"), TopStorage.getStorage().getInventorySize(), inventoryName);

        if (data.length == 0) {
            return inventory;
        }
        if (data.length >= 1 && data[0] != null) {
            inventory.setItem(0, createItem(Material.DIAMOND_BLOCK, data[0], 1));
        }       
        if (data.length >= 2 && data[1] != null) {
            inventory.setItem(1, createItem(Material.GOLD_BLOCK, data[1], 2));
        }
        if (data.length >= 3 && data[2] != null) {
            inventory.setItem(2, createItem(Material.REDSTONE_BLOCK, data[2], 3));
        }
        for (int i = 3; i < data.length; i++) {
            final PlayerInTop top = data[i];
            if (top == null) {
                return inventory;
            }
            final Material material = (i <= 10) ? Material.BRICK : (i <= 20) ? Material.ANVIL : Material.COBBLESTONE;
            inventory.setItem(i, createItem(material, top, i));
        }
        return inventory;
    }

    private ItemStack createItem(final Material material, final PlayerInTop playerInTop, final int top) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(playerInTop.getPlayerName() + " - " + playerInTop.getScore());
        item.setItemMeta(meta);
        item.setAmount(top);
        return item;
    }
}
