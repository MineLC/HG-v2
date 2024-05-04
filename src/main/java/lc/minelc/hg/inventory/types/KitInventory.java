package lc.minelc.hg.inventory.types;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import io.netty.util.collection.IntObjectHashMap;
import lc.minelc.hg.database.mongodb.HGPlayerData;
import lc.minelc.hg.database.mongodb.PlayerDataStorage;
import lc.minelc.hg.messages.Messages;
import lc.minelc.hg.others.kits.Kit;

public final class KitInventory {

    private final IntObjectHashMap<Kit> inventoryItems;
    private final Inventory inventory;

    public KitInventory(IntObjectHashMap<Kit> kits, Inventory inventory) {
        this.inventoryItems = kits;
        this.inventory = inventory;
    }

    public void handle(InventoryClickEvent event) {
        event.setCancelled(true);
        final Kit clickedKit = inventoryItems.get(event.getSlot());

        if (clickedKit == null) {
            return;
        }
        final HGPlayerData data = PlayerDataStorage.getStorage().get(event.getWhoClicked().getUniqueId());
        if (clickedKit.permission() != null && !event.getWhoClicked().hasPermission(clickedKit.permission())) {
            Messages.send(event.getWhoClicked(), "kit.no-permission");
            return;
        }
        if (clickedKit.cost() <= 0) {
            data.kitSelected = clickedKit.id();
            event.getWhoClicked().sendMessage(Messages.get("kit.selected").replace("%name%", clickedKit.name()));
            return;
        }
        if (data.coins < clickedKit.cost()) {
            Messages.send(event.getWhoClicked(), "kit.no-money");
            return;
        }
        data.kitSelected = clickedKit.id();
        data.coins -= clickedKit.cost();
        event.getWhoClicked().sendMessage(Messages.get("kit.selected").replace("%name%", clickedKit.name()));
    }

    public Inventory getInventory() {
        return inventory;
    }
}