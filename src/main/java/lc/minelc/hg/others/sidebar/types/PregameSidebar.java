package lc.minelc.hg.others.sidebar.types;

import java.util.Collection;

import lc.minelc.hg.others.kits.Kit;
import lc.minelc.hg.others.kits.KitStorage;
import org.bukkit.entity.Player;

import io.github.ichocomilk.lightsidebar.LightSidebarLib;
import io.github.ichocomilk.lightsidebar.Sidebar;
import lc.minelc.hg.database.mongodb.HGPlayerData;
import lc.minelc.hg.database.mongodb.PlayerDataStorage;
import lc.minelc.hg.game.GameInProgress;
import lc.minelc.hg.game.GameState;
import lc.minelc.hg.game.GameStorage;
import lc.minelc.hg.others.sidebar.HgSidebar;

public final class PregameSidebar implements HgSidebar {

    private final String[] lines;
    private final String title;

    public PregameSidebar(String[] lines, String title) {
        this.lines = lines;
        this.title = title;
    }

    @Override
    public void send(Player player) {
        final GameInProgress game = GameStorage.getStorage().getGame(player.getUniqueId());
        if (game == null || game.getState() != GameState.PREGAME) {
            return;
        }

        final HGPlayerData data = PlayerDataStorage.getStorage().get(player.getUniqueId());

        final Kit selectedKit = KitStorage.getStorage().kitsPerId().get(data.kitSelected);
        final String selectedKitName = (selectedKit == null) ? "Sin Kit" : selectedKit.name();
    
        final String lcoins = String.valueOf(data.coins);
        final String players = String.valueOf(game.getPlayers().size());
        final String maxPlayers = String.valueOf(game.getMapData().getMaxPlayers());
        final String[] parsedLines = new String[lines.length];
        final String level = String.valueOf(data.level);

        for (int i = 0; i < lines.length; i++) {
            parsedLines[i] = lines[i].isEmpty() ? "" : lines[i]
                    .replace("%selectedkit%", selectedKitName)
                    .replace("%coin%", lcoins)
                    .replace("%players%", players)
                    .replace("%max%",maxPlayers)
                    .replace("%level%", level)
                    .replace("%map%", game.getMapData().getName());
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