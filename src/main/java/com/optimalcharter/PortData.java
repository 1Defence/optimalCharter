package com.optimalcharter;

import net.runelite.api.coords.WorldPoint;

import java.util.Map;

import static java.util.Map.entry;

public class PortData
{


    public static class PortAreaData
    {
        public String name;
        public WorldPoint dock;
        public WorldPoint pickup;
        public PortAreaData(String name, WorldPoint dock, WorldPoint pickup) {
            this.name = name;
            this.dock = dock;
            this.pickup = pickup;
        }
    }


    public static class PortTaskData
    {
        public int taskId;
        public PortAreaData start;
        public PortAreaData end;
        public PortTaskData(int taskId, PortAreaData start, PortAreaData end) {
            this.taskId = taskId;
            this.start = start;
            this.end = end;
        }
    }




    public static final Map<String, PortAreaData> PORT_AREAS_DATA =
            Map.ofEntries(
                    entry("Lands end",     new PortAreaData("Lands end", new WorldPoint(1508, 3403, 0), new WorldPoint(1505, 3407, 0)))
                    ,entry("Prif",          new PortAreaData("Prif", new WorldPoint(2158, 3323, 0), new WorldPoint(2171, 3328, 0)))
                    ,entry("Tyras",         new PortAreaData("Tyras", new WorldPoint(2144, 3119, 0), new WorldPoint(2151, 3122, 0)))
                    ,entry("Civi",          new PortAreaData("Civi", new WorldPoint(1774, 3142, 0), new WorldPoint(1780, 3147, 0)))
                    ,entry("Lunar",         new PortAreaData("Lunar", new WorldPoint(2153, 3881, 0), new WorldPoint(2147, 3879, 0)))
                    ,entry("Port robert",   new PortAreaData("Port robert", new WorldPoint(1860, 3307, 0), new WorldPoint(1867, 3307, 0)))
                    ,entry("Aldarin",       new PortAreaData("Aldarin", new WorldPoint(1452, 2971, 0), new WorldPoint(1449, 2968, 0)))
                    ,entry("Piscatoris",       new PortAreaData("Piscatoris", new WorldPoint(2303, 3689, 0), new WorldPoint(2313, 3692, 0)))
                    ,entry("Port Piscarilus",       new PortAreaData("Port Piscarilus", new WorldPoint(1845, 3686, 0), new WorldPoint(1837, 3690, 0)))
                    ,entry("Sunset",       new PortAreaData("Sunset", new WorldPoint(1511, 2974, 0), new WorldPoint(1514, 2977, 0)))
                    ,entry("Releka",       new PortAreaData("Releka", new WorldPoint(2630, 3706, 0), new WorldPoint(2630, 3699, 0)))
                    ,entry("Neitiznot",       new PortAreaData("Neitiznot", new WorldPoint(2308, 3782, 0), new WorldPoint(2308, 3774, 0)))
                    ,entry("Ecteria",       new PortAreaData("Ecteria", new WorldPoint(2615, 3841, 0), new WorldPoint(2612, 3845, 0)))
                    ,entry("Deepfin",       new PortAreaData("Deepfin", new WorldPoint(1923, 2757, 0), new WorldPoint(1927, 2760, 0)))
                    ,entry("Hosidius",       new PortAreaData("Hosidius", new WorldPoint(1726, 3451, 0), new WorldPoint(1726, 3460, 0)))
                    ,entry("Ardougne",       new PortAreaData("Ardougne", new WorldPoint(2671, 3264, 0), new WorldPoint(2673, 3269, 0)))
                    ,entry("Jatizo",       new PortAreaData("Jatizo", new WorldPoint(2412, 3779, 0), new WorldPoint(2402, 3788, 0)))

            );


    public static final Map<Integer, PortTaskData> PORT_TASKS_DATA;

    static {
        var landsEnd = PORT_AREAS_DATA.get("Lands end");
        var prif = PORT_AREAS_DATA.get("Prif");
        var tyras = PORT_AREAS_DATA.get("Tyras");
        var civi = PORT_AREAS_DATA.get("Civi");
        var lunar = PORT_AREAS_DATA.get("Lunar");
        var portRobert = PORT_AREAS_DATA.get("Port robert");
        var aldarin = PORT_AREAS_DATA.get("Aldarin");
        var piscatoris = PORT_AREAS_DATA.get("Piscatoris");
        var portPiscarilus = PORT_AREAS_DATA.get("Port Piscarilus");
        var sunSet = PORT_AREAS_DATA.get("Sunset");
        var releka = PORT_AREAS_DATA.get("Releka");
        var neitiznot = PORT_AREAS_DATA.get("Neitiznot");
        var ecteria = PORT_AREAS_DATA.get("Ecteria");
        var deepfin = PORT_AREAS_DATA.get("Deepfin");
        var hosidius = PORT_AREAS_DATA.get("Hosidius");
        var ardougne = PORT_AREAS_DATA.get("Ardougne");
        var jatizo = PORT_AREAS_DATA.get("Jatizo");

        PORT_TASKS_DATA = Map.ofEntries(
                //Lunar Board
                entry(437, new PortTaskData(437, aldarin, lunar))
                ,entry(433, new PortTaskData(433, civi, lunar))
                ,entry(431, new PortTaskData(431, portRobert, lunar))
                ,entry(429, new PortTaskData(429, deepfin, lunar))
                ,entry(424, new PortTaskData(424, prif, lunar))
                ,entry(422, new PortTaskData(422, piscatoris, lunar))
                ,entry(420, new PortTaskData(420, portPiscarilus, lunar))

                //Lands End Board
                ,entry(246, new PortTaskData(246, landsEnd, lunar))

                //Prif Board
                ,entry(377, new PortTaskData(377, prif, lunar))
                ,entry(376, new PortTaskData(376, civi, prif))
                ,entry(367, new PortTaskData(367, ardougne, prif))
                ,entry(365, new PortTaskData(365, portRobert, prif))
                ,entry(363, new PortTaskData(363, tyras, prif))

                //Tyras Board
                ,entry(359, new PortTaskData(359, tyras, lunar))

                //Robert Board
                ,entry(316, new PortTaskData(316, portRobert, lunar))

                //Releka Board
                ,entry(399, new PortTaskData(399, tyras, releka))
                ,entry(397, new PortTaskData(397, jatizo, releka))
                ,entry(395, new PortTaskData(395, portPiscarilus, releka))
                ,entry(393, new PortTaskData(393, piscatoris, releka))
                ,entry(391, new PortTaskData(391, portRobert, releka))
                ,entry(386, new PortTaskData(386, sunSet, releka))
                ,entry(384, new PortTaskData(384, neitiznot, releka))
                ,entry(382, new PortTaskData(382, ecteria, releka))

                //Ecteria Board
                ,entry(418, new PortTaskData(418, hosidius, ecteria))
                ,entry(416, new PortTaskData(416, piscatoris, ecteria))
                ,entry(414, new PortTaskData(414, sunSet, ecteria))
                ,entry(412, new PortTaskData(412, neitiznot, ecteria))
                ,entry(410, new PortTaskData(410, portPiscarilus, ecteria))
                ,entry(405, new PortTaskData(405, portRobert, ecteria))
                ,entry(403, new PortTaskData(403, jatizo, ecteria))
                ,entry(401, new PortTaskData(401, releka, ecteria))
        );
    }

    public static final Map<Integer, String> PORT_CRATE_IDS =
            Map.ofEntries(
                    entry(57932,"Lunar"),
                    entry(57928,"Ecteria"),
                    entry(57943,"Releka"),
                    entry(57941,"Prif"),
                    entry(57940,"Tyras")
            );


}
