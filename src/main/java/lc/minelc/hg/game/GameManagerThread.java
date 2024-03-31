package lc.minelc.hg.game;


import org.tinylog.Logger;

import lc.minelc.hg.game.threadtasks.EventTask;
import lc.minelc.hg.mapsystem.MapData;

public final class GameManagerThread extends Thread {

    private static final GameManagerThread THREAD = new GameManagerThread();

    private static final EventTask EVENT_TASK = new EventTask();

    private boolean run = false;
    private MapData[] maps;

    @Override
    public void run() {
        while (maps != null && run) {
            try {
                Thread.sleep(1000);
                executeTasks();
            } catch (Exception e) {
                Logger.error(e);
            }
        }
    }

    private void executeTasks() {
        final long time = System.currentTimeMillis();
        for (final MapData map : maps) {
            if (map != null && map.getGameInProgress() != null) {
                EVENT_TASK.execute(map, time);
            }
        }
    }

    public static void startThread() {
        if (!THREAD.run) {
            THREAD.run = true;
            THREAD.start();
        }
    }

    public static void stopThread() {
        THREAD.run = false;
    }

    public static void setMaps(final MapData[] maps) {
        THREAD.maps = maps;
    }
}