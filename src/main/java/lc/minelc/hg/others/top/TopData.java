package lc.minelc.hg.others.top;

public final class TopData {
    private PlayerInTop[] tops;
    private final String inventoryName;

    TopData(String inventoryName) {
        this.inventoryName = inventoryName;
    }

    public String getInventoryName() {
        return inventoryName;
    }

    void setTops(PlayerInTop[] tops) {
        this.tops = tops;
    }

    public PlayerInTop[] getTops() {
        return tops;
    }
}