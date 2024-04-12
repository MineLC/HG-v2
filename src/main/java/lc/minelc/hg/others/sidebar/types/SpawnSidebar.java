package lc.minelc.hg.others.sidebar.types;

import java.util.Collection;

import lc.minelc.hg.others.kits.KitStorage;
import org.bukkit.entity.Player;

import io.github.ichocomilk.lightsidebar.LightSidebarLib;
import io.github.ichocomilk.lightsidebar.Sidebar;
import lc.minelc.hg.database.mongodb.HGPlayerData;
import lc.minelc.hg.database.mongodb.PlayerDataStorage;
import lc.minelc.hg.others.sidebar.HgSidebar;

public final class SpawnSidebar implements HgSidebar {

    private final String[] lines;
    private final String title;

    public SpawnSidebar(String[] lines, String title) {
        this.lines = lines;
        this.title = title;
    }

    @Override
    public void send(Player player) {
        final Sidebar sidebar = new LightSidebarLib().createSidebar();
        final String[] parsedLines = new String[lines.length];
        final HGPlayerData data = PlayerDataStorage.getStorage().get(player.getUniqueId());

        final String selectedKit = KitStorage.getStorage().kitsPerId().get(data.kitSelected).name();
        final String coins = String.valueOf(data.coins);
        final String level = String.valueOf(data.level);
        final String wins =  String.valueOf(data.wins);
        final String deaths = String.valueOf(data.deaths);
        final String kills = String.valueOf(data.kills);
        final String kdr =  (data.deaths == 0) ? String.valueOf(data.kills) : String.valueOf((float)(data.kills / data.deaths));

        for (int i = 0; i < lines.length; i++) {
            parsedLines[i] = lines[i].isEmpty() ? "" : lines[i]
                    .replace("%deaths%",deaths)
                    .replace("%coin%", coins)
                    .replace("%level%", level)
                    .replace("%wins%", wins)
                    .replace("%kills%", kills)
                    .replace("%kdr%", kdr);
        }
        sidebar.setTitle(title);
        sidebar.setLines(sidebar.createLines(parsedLines));
        sidebar.sendLines(player);
        sidebar.sendTitle(player);
    }

    @Override
    public void send(Collection<Player> players) {
        for (final Player player : players) {
            send(player);
        }
    }
}