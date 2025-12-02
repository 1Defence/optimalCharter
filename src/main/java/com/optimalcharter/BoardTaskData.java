package com.optimalcharter;
import net.runelite.api.coords.WorldPoint;

import java.util.Map;

public final class BoardTaskData {
    public final WorldPoint[] boardInteractLocs;
    public final int boardDestinationModelId;

    public BoardTaskData(WorldPoint[] boardLocs, int boardDestinationModelId) {
        this.boardInteractLocs = boardLocs.clone();
        this.boardDestinationModelId = boardDestinationModelId;
    }

    public WorldPoint[] getBoardInteractLocs() {
        return boardInteractLocs.clone();
    }

    public int getBoardDestinationModelId() {
        return boardDestinationModelId;
    }

    public static final Map<String, BoardTaskData> BOARD_TASK_DATA = Map.ofEntries(
            Map.entry("Ecteria", new BoardTaskData(
                    new WorldPoint[] {
                            new WorldPoint(2616, 3851, 0),
                            new WorldPoint(2616, 3850, 0),
                            new WorldPoint(2616, 3849, 0)
                    },
                    57818
            ))

            ,Map.entry("Releka", new BoardTaskData(
                    new WorldPoint[] {
                            new WorldPoint(2630, 3685, 0),
                            new WorldPoint(2630, 3686, 0),
                            new WorldPoint(2630, 3687, 0)
                    },
                    57834
            ))

            ,Map.entry("Lunar", new BoardTaskData(
                    new WorldPoint[] {
                            new WorldPoint(2139, 3883, 0),
                            new WorldPoint(2140, 3883, 0),
                            new WorldPoint(2141, 3883, 0)
                    },
                    57822
            ))

            ,Map.entry("Aldarin", new BoardTaskData(
                    new WorldPoint[] {
                            new WorldPoint(1438, 2968, 0),
                            new WorldPoint(1439, 2968, 0),
                            new WorldPoint(1440, 2968, 0)
                    },
                    57809
            ))

            ,Map.entry("Port robert", new BoardTaskData(
                    new WorldPoint[] {
                            new WorldPoint(1871, 3305, 0),
                            new WorldPoint(1871, 3304, 0),
                            new WorldPoint(1871, 3303, 0)
                    },
                    57829
            ))

            ,Map.entry("Deepfin", new BoardTaskData(
                    new WorldPoint[] {
                            new WorldPoint(1931, 2760, 0),
                            new WorldPoint(1932, 2760, 0),
                            new WorldPoint(1933, 2760, 0)
                    },
                    57816
            ))

            ,Map.entry("Prif", new BoardTaskData(
                    new WorldPoint[] {
                            new WorldPoint(2163, 3325, 0),
                            new WorldPoint(2164, 3325, 0),
                            new WorldPoint(2165, 3325, 0)
                    },
                    57832
            ))

            ,Map.entry("Tyras", new BoardTaskData(
                    new WorldPoint[] {
                            new WorldPoint(2146, 3122, 0),
                            new WorldPoint(2147, 3122, 0),
                            new WorldPoint(2148, 3122, 0)
                    },
                    57831
            ))

            ,Map.entry("Civi", new BoardTaskData(
                    new WorldPoint[] {
                            new WorldPoint(1781, 3144, 0),
                            new WorldPoint(1781, 3143, 0),
                            new WorldPoint(1781, 3142, 0)
                    },
                    57814
            ))

            ,Map.entry("Lands end", new BoardTaskData(
                    new WorldPoint[] {
                            new WorldPoint(1503, 3407, 0),
                            new WorldPoint(1503, 3408, 0),
                            new WorldPoint(1503, 3409, 0)
                    },
                    57821
            ))
    );


    public static int GetModelIdForLocation(WorldPoint worldPoint) {
        if (worldPoint == null) {
            return -1;
        }

        for (Map.Entry<String, BoardTaskData> entry : BOARD_TASK_DATA.entrySet()) {
            BoardTaskData data = entry.getValue();
            for (WorldPoint point : data.getBoardInteractLocs()) {
                if (point.equals(worldPoint)) {
                    return data.getBoardDestinationModelId();
                }
            }
        }

        return -1;
    }


}