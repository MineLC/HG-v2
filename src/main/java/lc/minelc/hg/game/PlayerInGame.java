package lc.minelc.hg.game;


import lc.minelc.hg.others.abilities.GameAbility;
import lombok.Getter;
import lombok.Setter;

@Getter
public final class PlayerInGame {

    private final GameInProgress game;
    @Setter
    private GameAbility[] gameAbilities;

    PlayerInGame(GameInProgress game) {
        this.game = game;
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