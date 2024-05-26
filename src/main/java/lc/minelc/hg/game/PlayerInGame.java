package lc.minelc.hg.game;


import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import lc.minelc.hg.others.abilities.GameAbility;
import lombok.Getter;
import lombok.Setter;

@Getter
public final class PlayerInGame {

    private final GameInProgress game;
    private final Player player;
    private final Map<GameAbility, Long> cooldownAbilities = new HashMap<>();

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

    public boolean canUseAbility(final GameAbility ability, final int seconds) {
        final Long cooldown = cooldownAbilities.get(ability);
        if (cooldown == null) {
            return true;
        }
        if ((System.currentTimeMillis() - cooldown) / 1000 >= seconds) {
            cooldownAbilities.remove(ability);
            return true;
        }
        return false;
    }

    public void setCooldown(final GameAbility ability) {
        cooldownAbilities.put(ability, System.currentTimeMillis());
    }
}