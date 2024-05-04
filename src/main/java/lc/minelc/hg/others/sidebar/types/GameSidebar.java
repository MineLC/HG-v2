package lc.minelc.hg.others.sidebar.types;

import java.util.Collection;

import io.github.ichocomilk.lightsidebar.LightSidebarLib;
import lc.minelc.hg.database.mongodb.HGPlayerData;
import lc.minelc.hg.database.mongodb.PlayerDataStorage;
import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameStorage;
import org.bukkit.entity.Player;

import io.github.ichocomilk.lightsidebar.Sidebar;
import lc.minelc.hg.others.sidebar.HgSidebar;

public final class GameSidebar implements HgSidebar {

    private final String[] lines;
    private final String title;

    public GameSidebar(String[] lines, String title) {
        this.lines = lines;
        this.title = title;
    }

    @Override
    public void send(Player player) {
        final GameInProgress game = GameStorage.getStorage().getGame(player.getUniqueId());
        if (game == null) {
            return;
        }

        final HGPlayerData data = PlayerDataStorage.getStorage().get(player.getUniqueId());

        final String wins =  String.valueOf(data.wins);
        final String deaths = String.valueOf(data.deaths);
        final String kills = String.valueOf(data.kills);
        final String level = String.valueOf(data.level);
        final String kdr =  (data.deaths == 0) ? String.valueOf(data.kills) : String.valueOf((float)(data.kills / data.deaths));
        final String[] parsedLines = new String[lines.length];

        for (int i = 0; i < lines.length; i++) {
            parsedLines[i] = lines[i].isEmpty() ? "" : lines[i]
                    .replace("%deaths%",deaths)
                    .replace("%wins%", wins)
                    .replace("%kills%", kills)
                    .replace("%level%", level)
                    .replace("%kdr%", kdr);
        }
        final Sidebar sidebar = new LightSidebarLib().createSidebar();
        final Object[] lines = sidebar.createLines(parsedLines);

        sidebar.setTitle(title);
        sidebar.setLines(lines);
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