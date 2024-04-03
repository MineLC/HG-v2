package lc.minelc.hg.others.selectgame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.game.GameState;
import lc.minelc.hg.messages.Messages;

public final class StartMapInventories {

    public MapInventoryBuilder load(final ArenaHGPlugin plugin) {
        final FileConfiguration config = plugin.loadConfig("inventories/gameselector");

        return new MapInventoryBuilder(
            getStateItems(config),
            getLore(config),
            Messages.color(config.getString("title")),
            Messages.color(config.getString("games.time")));
    }

    private StateItem[] getStateItems(final FileConfiguration config) {
        final GameState[] gameStates = GameState.values();
        final StateItem[] states = new StateItem[gameStates.length];

        int index = 0;
        for (final GameState state : gameStates) {
            final String stateName = state.name().toLowerCase();
            final String path = "states." + stateName;
            final String suffix = Messages.color(config.getString(path + ".suffix"));
            Material material = Material.getMaterial(config.getString(path + ".item"));
            if (material == null) {
                material = Material.STONE;
            }
            states[index++] = new StateItem(suffix, material);
        }
        return states;
    }

    private List<String> getLore(final FileConfiguration config) {
        final List<String> lore = config.getStringList("games.lore");
        final List<String> newLore = new ArrayList<>(lore.size());

        for (final String line : lore) {
            newLore.add(Messages.color(line));
        }
        return newLore;
    }
}