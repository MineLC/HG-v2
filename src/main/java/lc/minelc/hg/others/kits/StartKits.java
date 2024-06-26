package lc.minelc.hg.others.kits;

import java.io.File;
import java.util.List;

import lc.minelc.hg.others.abilities.GameAbility;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.tinylog.Logger;

import io.netty.util.collection.IntObjectHashMap;
import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.inventory.internal.InventoryCreator;
import lc.minelc.hg.inventory.types.KitInventory;
import lc.minelc.hg.utils.NumberUtils;
import net.minecraft.server.v1_8_R3.ItemStack;

public final class StartKits {

    final ArenaHGPlugin plugin;

    public StartKits(ArenaHGPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        final File kitsFolder = new File(plugin.getDataFolder(), "kits");
        try {
            tryCreateDefaultKits(kitsFolder);
            String defaultKit = plugin.getConfig().getString("default-kit");

            final FileConfiguration kitsInventory = plugin.loadConfig("inventories/kits");

            final InventoryCreator creator = new InventoryCreator(kitsInventory);
            final Inventory inventory = creator.create("kits", "inventory");

            final File[] kitsFiles = kitsFolder.listFiles();
            final IntObjectHashMap<Kit> kits = new IntObjectHashMap<>();
            final IntObjectHashMap<Kit> kitsPerId = new IntObjectHashMap<>();

            for (final File kitFile : kitsFiles) {
                try {
                    final Kit kit = createKit(YamlConfiguration.loadConfiguration(kitFile));
                    kitsPerId.put(kit.name().hashCode(), kit);
                    inventory.setItem(kit.inventoryItem().slot(), kit.inventoryItem().item());
                    kits.put(kit.inventoryItem().slot(), kit);
                } catch (Exception e) {
                    Logger.error("Error al cargar un kit: " + kitFile);
                    Logger.error(e);
                }
            }
            Kit kitPerDefault = (defaultKit != null) ? kitsPerId.get(defaultKit.hashCode()) : null;
            if (kitPerDefault == null && !kitsPerId.isEmpty()) {
                kitPerDefault = kitsPerId.values().iterator().next();
            }
            KitStorage.update(new KitStorage(new KitInventory(kits, inventory), kitsPerId, kitPerDefault));
        } catch (Exception e) {
            // Imprimir la excepción en la consola
            Logger.error("Error al cargar los kits");
            Logger.error(e);
        }
    }

    private void tryCreateDefaultKits(File kitsFolder) {
        if (kitsFolder.exists()) {
            return;
        }
        plugin.tryCreateFiles(
            "kits/adan.yml");
    }

    private Kit createKit(final FileConfiguration config) {
        final InventoryCreator creator = new InventoryCreator(config);
        final String name = config.getString("name");
        return new Kit(
            name.hashCode(),
            name,
            config.getString("permission"),
            creator.create("inventory-item"),
            createArmor(config, creator), createAbilities(config, name),
            createItems(config),
            createPotionEffects(config, "effects"),
            config.getInt("cost"));
    }

    private ItemStack[] createArmor(final FileConfiguration config, InventoryCreator creator) {
        return new ItemStack[] {
            createArmorPiece(config.getString("armor.boots")),
            createArmorPiece(config.getString("armor.leggings")),
            createArmorPiece(config.getString("armor.chestplate")),
            createArmorPiece(config.getString("armor.heltmet"))
        };
    }

    private ItemStack createArmorPiece(final String section) {
        if (section == null) {
            return null;
        }
        Material material = Material.getMaterial(NumberUtils.parsePositive(section));
        if (material == null) {
            return null;
        }
        return CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(material));
    }

    private ItemStack[] createItems(final FileConfiguration config) {
        final List<String> itemList = config.getStringList("items");

        if (itemList.isEmpty()) {
            return null;
        }
        final ItemStack[] items = new ItemStack[itemList.size()];
        int index = 0;

        for (final String item : itemList) {
            final String[] split = StringUtils.split(item, ':');
            Material material = null;
            if (isSplitable(split[0])){
                String materialPotion = split[0];
                String[] splitPotion = StringUtils.split(materialPotion, ",");
                Potion splash = new Potion(PotionType.getByEffect(PotionEffectType.getByName(splitPotion[1])), 1);
                if (splitPotion.length >= 3) {
                    splash.setSplash(Boolean.parseBoolean(splitPotion[2]));
                }
                int amount = 1;
                if (split.length >= 2) {
                    int newAmount = Integer.parseInt(split[1]);
                    amount = (newAmount == -1) ? 1 : newAmount;
                }
                org.bukkit.inventory.ItemStack itemStack = splash.toItemStack(amount);
                items[index++] = CraftItemStack.asNMSCopy(itemStack);
            }else{
                material = Material.getMaterial(Integer.parseInt(split[0]));
                if (material == null) {
                    material = Material.STONE;
                }
                int amount = 1;
                if (split.length >= 2) {
                    int newAmount = Integer.parseInt(split[1]);
                    amount = (newAmount == -1) ? 1 : newAmount;
                }
                org.bukkit.inventory.ItemStack itemStack = new org.bukkit.inventory.ItemStack(material, amount);

                if (split.length == 4) {
                    Enchantment enchantment = Enchantment.getByName(split[2]);
                    if (enchantment == null) {
                        Logger.warn("The enchant type: " + split[2] + " don't exist");
                    } else {
                        int level = Integer.parseInt(split[3]);
                        itemStack.addUnsafeEnchantment(enchantment, (level == 0) ? 1 : level);
                    }
                }
                items[index++] = CraftItemStack.asNMSCopy(itemStack);
            }


        }
        return items;
    }
    private GameAbility[] createAbilities(final FileConfiguration config, final String kitname) {
        final List<String> abilitiesList = config.getStringList("abilities");
        if (abilitiesList.isEmpty()) {
            return null;
        }
        final GameAbility[] abilities = new GameAbility[abilitiesList.size()];
        int index = 0;

        for (final String ability : abilitiesList) {
            final GameAbility gameAbility = GameAbility.valueOf(ability);
            if (gameAbility == null) {
                Logger.warn("The ability " + ability + " don't exist. Kit: " + kitname);
                return null;
            }
            abilities[index++] = gameAbility;
        }
        return abilities;
    }

    public PotionEffect[] createPotionEffects(final FileConfiguration config, final String section) {
        final List<String> effects = config.getStringList(section);
        if (effects.isEmpty()) {
            return null;
        }

        final PotionEffect[] potionEffects = new PotionEffect[effects.size()];
        int index = 0;
        for (final String effect : effects) {
            final String[] split = StringUtils.split(effect, ':');
            PotionEffectType type = PotionEffectType.getByName(split[0]);
            int level = 0;
            int duration = 0;

            if (type == null) {
                type = PotionEffectType.ABSORPTION;
                Logger.info("The potion type: " + split[0] + " don't exist");
            }
            if (split.length >= 2) {
                level = Integer.parseInt(split[1]) - 1;
            }
            if (split.length == 3) {
                duration = Integer.parseInt(split[2]);
            }
            potionEffects[index++] = new PotionEffect(type, duration, level);
        }
        return potionEffects;
    }
    public static boolean isSplitable(String str) {
        // Verifica si el string es nulo o vacío
        if (str == null || str.isEmpty()) {
            return false;
        }
        // Verifica si el string contiene al menos una coma
        for (char c : str.toCharArray()) {
            if (c == ',') {
                return true;
            }
        }
        return false;
    }
}
