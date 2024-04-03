package lc.minelc.hg.others.selectgame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameState;
import lc.minelc.hg.game.countdown.GameCountdown;
import lc.minelc.hg.mapsystem.MapData;
import lc.minelc.hg.mapsystem.MapStorage;
import lc.minelc.hg.utils.NumberUtils;

import net.md_5.bungee.api.ChatColor;

public final class MapInventoryBuilder {

    private final List<String> gameLore;
    private final StateItem[] states;
    private final String inventoryName, timeLineLore;

    MapInventoryBuilder(StateItem[] states, List<String> gameLore, String inventoryName, String timeLineLore) {
        this.states = states;
        this.gameLore = gameLore;
        this.inventoryName = inventoryName;
        this.timeLineLore = timeLineLore;
    }

    public Inventory build() {
        final MapData[] maps = MapStorage.getStorage().getMaps();
        final int rows = NumberUtils.aproximate(maps.length, 9);
        final Inventory inventory = Bukkit.createInventory(
            new MapSelectorInventoryHolder(),
            rows * 9,
            inventoryName);

        int slot = 0;

        for (final MapData map : maps) {
            inventory.setItem(slot++, createMapItem(map));
        }
        return inventory;
    }

    private ItemStack createMapItem(final MapData map) {
        final GameInProgress game = map.getGameInProgress();
        final GameState gameState = (game == null) ? GameState.NONE : game.getState();
        final StateItem state = states[gameState.ordinal()];

        final ItemStack item = new ItemStack(state.material(), (game == null) ? 0 : game.getPlayers().size());
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.GREEN + map.toString() + state.suffix());
        if (game == null) {
            meta.setLore(parseLore(0, 0, map.getMaxPlayers(), gameState));
        } else {
            meta.setLore(parseLore(game.getPlayers().size(),  game.getStartedTime(), map.getMaxPlayers(), gameState));
        }
        item.setItemMeta(meta);

        return item;
    }

    private List<String> parseLore(final int amountPlayers, final long startedTime, final int maxPlayers, final GameState state) {
        final List<String> newLore = new ArrayList<>(gameLore.size());
        final String playersFormat = amountPlayers + "/" + maxPlayers;

        for (final String line : gameLore) {
            if (line.isEmpty()) {
                newLore.add("");
                continue;
            }
            newLore.add(line.replace("%players%", playersFormat));
        }

        if (state == GameState.IN_GAME || state == GameState.END_GAME) {
            newLore.add(timeLineLore.replace("%time%", GameCountdown.parseTime((System.currentTimeMillis() - startedTime) / 1000)));
        }
        return newLore;
    }
}