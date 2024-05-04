package lc.minelc.hg.game;


import org.bukkit.entity.Player;

import lc.minelc.hg.others.abilities.GameAbility;
import lombok.Getter;
import lombok.Setter;

@Getter
public final class PlayerInGame {

    private final GameInProgress game;
    private final Player player;

    @Setter
    private GameAbility[] gameAbilities;
    public int kills = 0;

    PlayerInGame(GameInProgress game, Player player) {
        this.game = game;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean containsAbility(final GameAbility ability) {
        if (gameAbilities == null) {
            return false;
        }
        for (final GameAbility gameAbility : gameAbilities) {
            if (gameAbility == ability) {
                return true;
            }
        }
        return false;
    }
}