package lc.minelc.hg.inventory.types;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import lc.minelc.hg.inventory.internal.InventoryCreator.Item;
import lc.minelc.hg.others.kits.KitStorage;

public final class SpawnShopInventory {

    private final Item kitItem;
    private final Inventory inventory;

    public SpawnShopInventory(Item kitItem, Inventory inventory) {
        this.kitItem = kitItem;
        this.inventory = inventory;
    }

    public void handle(InventoryClickEvent event) {
        event.setCancelled(true);
        final int slot = event.getSlot();

        if (slot == kitItem.slot()) {
            event.getWhoClicked().openInventory(KitStorage.getStorage().inventory().getInventory());
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}