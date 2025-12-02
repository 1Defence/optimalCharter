package com.optimalcharter;

import java.util.ArrayList;
import java.util.List;
import net.runelite.api.coords.WorldPoint;
import static com.optimalcharter.PortData.PortAreaData;

/**
 * searches all valid pickups + deliveries
 * allows deliveries between pickups if cargo has been picked up
 * first visited port can be teled to, so no initial travel cost
 * We use a straight A-B distance calc rather than the actual route which can differ greatly but is too computationally expensive otherwise.
 * this may cause some routes to be slightly inoptimal but overall the lack of checking/routing yourself will outweigh this.
 */
public class CharterOptimalRoute {

    public static class Cargo {
        public final PortAreaData pickup;
        public final PortAreaData delivery;
        public final String name;

        public Cargo(PortAreaData pickup, PortAreaData delivery, String name) {
            this.pickup = pickup;
            this.delivery = delivery;
            this.name = name;
        }
    }

    public static class RouteStep {
        public final String cargoName;
        public final String action;
        public final PortAreaData port;

        public RouteStep(String cargoName, String action, PortAreaData port) {
            this.cargoName = cargoName;
            this.action = action;
            this.port = port;
        }
    }

    public static class Result {
        public final List<RouteStep> steps;
        public final List<PortAreaData> uniquePortsInOrder;

        public Result(List<RouteStep> steps, List<PortAreaData> uniquePortsInOrder) {
            this.steps = steps;
            this.uniquePortsInOrder = uniquePortsInOrder;
        }
    }

    private enum ActionType { PICKUP, DELIVERY }

    private static class Action {
        final int cargoIndex;
        final ActionType type;

        Action(int cargoIndex, ActionType type) {
            this.cargoIndex = cargoIndex;
            this.type = type;
        }
    }

    /**
     * Sq dist between 2 ports
     */
    private static long distBetweenPorts(WorldPoint portA, WorldPoint portB) {
        long dx = portA.getX() - portB.getX();
        long dy = portA.getY() - portB.getY();
        return dx * dx + dy * dy;
    }

    /**
     * finds optimal route through all 5 pickups and deliveries
     */
    public static Result findOptimalRoute(List<Cargo> cargoes) {
        int n = cargoes.size();
        if (n == 0 || n > 5) throw new IllegalArgumentException("1–5 cargoes");

        //Generate all valid orders
        List<List<Action>> allOrders = generateAllValidOrders(n);

        long bestDist = Long.MAX_VALUE;
        List<Action> bestOrder = null;

        //Sort each order
        for (List<Action> order : allOrders) {
            long dist = calculateOrderDistance(cargoes, order);
            if (dist < bestDist) {
                bestDist = dist;
                bestOrder = order;
            }
        }

        return buildRouteFromActions(cargoes, bestOrder);
    }

    /**
     * Calc distance for a set of actions
     */
    private static long calculateOrderDistance(List<Cargo> cargoes, List<Action> order) {
        WorldPoint pos = null;
        long dist = 0;

        for (Action a : order) {
            Cargo c = cargoes.get(a.cargoIndex);
            WorldPoint target = (a.type == ActionType.PICKUP) ? c.pickup.dock : c.delivery.dock;

            if (pos == null) {
                pos = target;
            } else {
                dist += distBetweenPorts(pos, target);
                pos = target;
            }
        }
        return dist;
    }

    /**
     * Builds route from given actions
     */
    private static Result buildRouteFromActions(List<Cargo> cargoes, List<Action> actions) {
        List<RouteStep> steps = new ArrayList<>();
        List<PortAreaData> uniquePorts = new ArrayList<>();

        for (Action a : actions) {
            Cargo c = cargoes.get(a.cargoIndex);
            boolean isPickup = (a.type == ActionType.PICKUP);
            PortAreaData port = isPickup ? c.pickup : c.delivery;
            String actionStr = isPickup ? "Pickup" : "Deliver";

            steps.add(new RouteStep(c.name, actionStr, port));

            //Track uniquePorts no consecutive duplicates for later usage
            if (uniquePorts.isEmpty() || uniquePorts.get(uniquePorts.size() - 1) != port) {
                uniquePorts.add(port);
            }
        }

        return new Result(steps, uniquePorts);
    }

    /**
     * Generate all valid actions of pickups and deliveries
     */
    private static List<List<Action>> generateAllValidOrders(int orderCount) {
        List<List<Action>> results = new ArrayList<>();
        boolean[] picked = new boolean[orderCount];
        boolean[] delivered = new boolean[orderCount];
        List<Action> current = new ArrayList<>(2 * orderCount);

        backtrackGenerate(orderCount, picked, delivered, current, results);
        return results;
    }

    private static void backtrackGenerate(int orderCount,
                                          boolean[] picked,
                                          boolean[] delivered,
                                          List<Action> current,
                                          List<List<Action>> results) {
        if (current.size() == 2 * orderCount) {
            results.add(new ArrayList<>(current));
            return;
        }

        //Try pickup if not yet picked, then try delivery if picked but not delivered.
        for (int i = 0; i < orderCount; i++) {
            // Option: pick up cargo i
            if (!picked[i]) {
                picked[i] = true;
                current.add(new Action(i, ActionType.PICKUP));

                backtrackGenerate(orderCount, picked, delivered, current, results);

                current.remove(current.size() - 1);
                picked[i] = false;
            }

            //deliver cargo if picked up and hasn't been delivered
            if (picked[i] && !delivered[i]) {
                delivered[i] = true;
                current.add(new Action(i, ActionType.DELIVERY));

                backtrackGenerate(orderCount, picked, delivered, current, results);

                current.remove(current.size() - 1);
                delivered[i] = false;
            }
        }
    }

}
