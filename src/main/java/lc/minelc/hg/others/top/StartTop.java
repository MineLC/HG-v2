package lc.minelc.hg.others.top;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.tinylog.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import gnu.trove.map.hash.TIntObjectHashMap;
import lc.minelc.hg.ArenaHGPlugin;
import lc.minelc.hg.inventory.internal.InventoryCreator;
import lc.minelc.hg.inventory.internal.InventoryCreator.Item;
import lc.minelc.hg.messages.Messages;
import lc.minelc.hg.utils.NumberUtils;

public final class StartTop {

    public void load(ArenaHGPlugin plugin) {
        final File topFile = new File(plugin.getDataFolder(), "tops/top.json");
        if (!topFile.exists()) {
            try {
                FileUtils.write(topFile, "{\"kills\":[],\"deaths\":[],\"wins\":[],\"levels\":[]}", Charset.defaultCharset(), false);
            } catch (IOException e) {
                Logger.error(e);
                return;
            }
        }
        try {
            final FileConfiguration config = plugin.loadConfig("tops/topConfig");
            final int amountTops = config.getInt("amount-tops");

            final Gson gson = new Gson();
            final TopJsonFormat data = gson.fromJson(new JsonReader(new BufferedReader(new FileReader(topFile))), TopJsonFormat.class);
            final int inventorySize = NumberUtils.aproximate(amountTops, 9) * 9;
            final TIntObjectHashMap<TopData> topData = new TIntObjectHashMap<>();
            final TopData[] arrayTopData = new TopData[4];
            final Inventory inventory = getInventory(config, topData, arrayTopData);

            TopStorage.update(new TopStorage(
                amountTops,
                inventorySize,
                deserializeData(data.kills(), amountTops, arrayTopData[0]),
                deserializeData(data.deaths(), amountTops, arrayTopData[1]),
                deserializeData(data.wins(), amountTops, arrayTopData[2]),
                deserializeData(data.levels(), amountTops, arrayTopData[3]),
                inventory,
                topData
            ));

        } catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
            Logger.error(e);
        }
    }

    private Inventory getInventory(final FileConfiguration config, final TIntObjectHashMap<TopData> topData, final TopData[] arrayTopData) {
        final InventoryCreator creator = new InventoryCreator(config);
        final Inventory inventory = creator.create("topSelect", "inventory");
        final Item killTop = creator.create("kill-top");
        final Item deathTop = creator.create("death-top");
        final Item winTop = creator.create("win-top");
        final Item levelTop = creator.create("level-top");

        inventory.setItem(killTop.slot(), killTop.item());
        inventory.setItem(deathTop.slot(), deathTop.item());
        inventory.setItem(winTop.slot(), winTop.item());
        inventory.setItem(levelTop.slot(), levelTop.item());

        final TopData killTopData = new TopData(Messages.color(config.getString("kill-top.inventory-name")));
        final TopData deathTopData = new TopData(Messages.color(config.getString("death-top.inventory-name")));
        final TopData winTopData = new TopData(Messages.color(config.getString("win-top.inventory-name")));
        final TopData levelTopData = new TopData(Messages.color(config.getString("level-top.inventory-name")));

        topData.put(killTop.slot(), killTopData);
        topData.put(deathTop.slot(), deathTopData);
        topData.put(winTop.slot(), winTopData);
        topData.put(levelTop.slot(), levelTopData);

        arrayTopData[0] = killTopData;
        arrayTopData[1] = deathTopData;
        arrayTopData[2] = winTopData;
        arrayTopData[3] = levelTopData;

        return inventory;
    }

    public void saveTops(final ArenaHGPlugin plugin) {
        if (TopStorage.getStorage().getKills()[0] == null &&
            TopStorage.getStorage().getDeaths()[0] == null &&
            TopStorage.getStorage().getWins()[0] == null) {
            return;
        }

        final StringBuilder builder = new StringBuilder();
        builder.append('{');
        serializeData(TopStorage.getStorage().getKills(), "kills", builder);
        builder.append(',');
        serializeData(TopStorage.getStorage().getDeaths(), "deaths", builder);
        builder.append(',');
        serializeData(TopStorage.getStorage().getWins(), "wins", builder);
        builder.append(',');
        serializeData(TopStorage.getStorage().getLevels(), "levels", builder);
        builder.append('}');

        try {
            FileUtils.write(new File(plugin.getDataFolder(), "tops/top.json"), builder.toString(), Charset.defaultCharset(), false);
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    private void serializeData(final PlayerInTop[] values, final String section, final StringBuilder builder) {
        builder.append('"');
        builder.append(section);
        builder.append("\": [");
        for (int i = 0; i < values.length; i++) {
            final PlayerInTop playerInTop = values[i];
            if (playerInTop == null) {
                break;
            }
            builder.append('"');
            builder.append(playerInTop.getPlayerName());
            builder.append(',');
            builder.append(playerInTop.getScore());
            builder.append(',');
            builder.append(playerInTop.getUUID());
            builder.append('"');
    
            if (i + 1 < values.length) {
                final PlayerInTop nextPlayerInTop = values[i + 1];
                if (nextPlayerInTop != null) {
                    builder.append(',');
                    continue;
                }
                break;
            }
        }
        builder.append(']');
    }

    private PlayerInTop[] deserializeData(final String[] values, final int amountTops, final TopData topData) {
        final PlayerInTop[] playersInTop = new PlayerInTop[amountTops];

        for (int i = 0; i < values.length; i++) {
            final String[] value = StringUtils.split(values[i], ',');
            final String playerName = value[0];
            final int score = NumberUtils.parsePositive(value[1]);
            final UUID uuid = UUID.fromString(value[2]);
            final PlayerInTop playerInTop = new PlayerInTop(playerName, score, uuid);
            playersInTop[i] = playerInTop;
        }
        topData.setTops(playersInTop);
        return playersInTop;
    }
}