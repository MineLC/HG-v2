package lc.minelc.hg.others.abilities;

public enum GameAbility {
    EXPLODING_ARROWS("Arrows Explode"),
    FAST_TREE_BREAK("Quickly Break Trees"),
    DAMAGE_ENEMIES_WITH_SHOTS("Shots Damage Enemies"),
    FIRE_THROW("Fire Throwing"),
    EAT_COOKIE("Eat Cookies"),
    GAIN_STRENGTH_WITH_FIRE("Gain Strength with Fire"),
    PORK_CHOPS_FROM_PIGS("Pork Chops from Pigs"),
    FALL_DAMAGE("Fall Damage"),
    INSTANT_DEATH("Instant Death"),
    INSTANT_CROPS("Instant Crops"),
    THUNDER_WITH_AXE("Thunder with Axe"),
    LIFE_STEAL_ON_HIT("Life Steal on Hit"),
    ITEM_THEFT("Item Theft"),
    HUNGER_RECOVERY("Hunger Recovery"),
    ITEM_THEFT_2("Item Theft 2"),
    INVISIBLE_WHEN_EATING_APPLE("Invisible when Eating Apple"),
    MOB_TRANSFORMATION("Mob Transformation"),
    HIT_WITH_HANDS("Hit with Hands"),
    POISON_PLAYERS("Poison Players"),
    MOBS_DO_NOT_ATTACK("Mobs Do Not Attack"),
    NIGHT_VISION_WITH_POTATO("Night Vision with Potato"),
    FREEZE_PLAYERS("Freeze Players"),
    EXPLODE_ON_DEATH("Explode on Death"),
    GAIN_STRENGTH_2_WITH_FIRE("Gain Strength 2 with Fire"),
    TELEPORT_WITH_TORCH("Teleport with Torch"),
    HIGH_JUMPS_WITH_FIREWORK("High Jumps with Firework"),
    HEARTS_RECOVERY_WITH_SOUPS_2("Hearts Recovery with Soups 2"),
    HEARTS_RECOVERY_WITH_SOUPS_3("Hearts Recovery with Soups 3"),
    WEAKEN_PLAYERS("Weaken Players"),
    BLIND_PLAYERS("Blind Players"),
    GENERATE_COBWEBS_WITH_SNOWBALLS("Generate Cobwebs with Snowballs");

    private final String description;

    GameAbility(String s) {
        this.description = s;
    }
}