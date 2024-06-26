package lc.minelc.hg.others.sidebar;

public final class SidebarStorage {
    private static SidebarStorage storage;

    private final HgSidebar[] sidebars;

    SidebarStorage(HgSidebar[] sidebars) {
        this.sidebars = sidebars;
    }

    public HgSidebar getSidebar(final SidebarType type){
        return sidebars[type.ordinal()];
    }

    public static SidebarStorage getStorage() {
        return storage;
    }

    static void update(final SidebarStorage newStorage) {
        storage = newStorage;
    }
}
