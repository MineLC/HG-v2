package lc.minelc.hg.others.kits;

import lc.minelc.hg.others.abilities.GameAbility;
import org.bukkit.potion.PotionEffect;

import lc.minelc.hg.inventory.internal.InventoryCreator.Item;
import net.minecraft.server.v1_8_R3.ItemStack;

public final record Kit(
    int id,
    String name,
    String permission,
    Item inventoryItem,
    ItemStack[] armor,
    GameAbility[] gameAbilities,
    ItemStack[] items,
    PotionEffect[] potionEffects,
    int cost
) {
}