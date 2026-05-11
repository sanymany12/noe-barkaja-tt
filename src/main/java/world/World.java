package world;

import engine.BuildManager;
import world.building.*;
import world.resources.AnimalType;
import world.tile.*;
import world.tile.road.Bridge;
import world.tile.road.RoadDirection;
import world.vehicle.*;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class World {
    private final int rows;
    private final int cols;

    private final Tile[][] grid;

    private int money;
    private int elapsedTime;

    private int vehiclesUpkeep;
    private final int DAYS_PER_VEHICLES_UPKEEP = 14;

    private List<Vehicle> vehicles;
    private ArrayList<BusStop> busStops;

    private BusStop start;
    private BusStop stop;
    private int daysSinceBusRoute;

    private int tickCounter = 0;
    private final static int TICKS_PER_DAY = 100;
    private final int DAYS_UNTIL_NEW_BUS_ROUTE = 15;

    private final int COST_TO_CUT_TREE = 5;
    private final int COST_TO_DESTROY = 200;

    private final int STARTING_FUNDS = 13000;

    public World(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        grid = new Tile[cols][rows];

        this.money = this.STARTING_FUNDS;

        this.elapsedTime = 0;

        this.busStops = new ArrayList<BusStop>();
        this.vehicles = new ArrayList<Vehicle>();

        this.vehiclesUpkeep = 0;

        this.start = null;
        this.stop = null;

        this.daysSinceBusRoute = DAYS_UNTIL_NEW_BUS_ROUTE;

        initWorld();
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getCostToCutTree() {
        return this.COST_TO_CUT_TREE;
    }

    public int getCostToDestroy() {
        return this.COST_TO_DESTROY;
    }

    public void increaseTickCounter() throws Exception {
        this.tickCounter++;
        this.moveVehicles();
        if (this.tickCounter == this.TICKS_PER_DAY) {
            this.newDay();
            this.tickCounter = 0;
        }
    }

    public int getTicksPerDay() {
        return this.TICKS_PER_DAY;
    }

    public void initWorld(){
        BuildManager setupBuilder = new BuildManager(this);

        for(int i = 0; i < cols; i++)
        {
            for(int j = 0; j < rows; j++)
            {
                grid[i][j] = new Tile(new Point(i,j), TerrainType.LAND, 0, null, null, false);
            }
        }

        int centerX = cols / 2;
        int centerY = rows / 2;
        for (int x = 0; x < cols; x++) {
            int riverY = (int) (Math.sin(x * 0.25) * 4) + centerY;
            for (int w = -2; w <= 2; w++) {
                int wy = riverY + w;
                if (wy >= 0 && wy < rows) {
                    grid[x][wy].setTerrainType(TerrainType.WATER);
                }
            }
            for (int y = 0; y < rows; y++) {
                if (Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= 64) { // Sugár ~8
                    grid[x][y].setTerrainType(TerrainType.WATER);
                }
            }
        }

        setupBuilder.placeBuilding(grid[4][4], BuildingType.CITY);
        setupBuilder.placeBuilding(grid[7][4], BuildingType.CITY);
        setupBuilder.placeBuilding(grid[4][7], BuildingType.CITY);
        setupBuilder.placeBuilding(grid[7][7], BuildingType.CITY);

        for(int x = 4; x <= 8; x++) { setupBuilder.buildRoad(grid[x][6], true); }
        for(int y = 4; y <= 8; y++) { setupBuilder.buildRoad(grid[6][y], true); }

        setupBuilder.placeBusStop(grid[5][3], RoadDirection.WEST);
        setupBuilder.buildRoad(grid[6][3], true);

        setupBuilder.placeBusStop(grid[9][5], RoadDirection.NORTH);
        setupBuilder.buildRoad(grid[9][6], true);

        setupBuilder.placeBusStop(grid[5][9], RoadDirection.WEST);
        setupBuilder.buildRoad(grid[6][9], true);

        setupBuilder.placeBuilding(grid[2][10], BuildingType.FARM);
        setupBuilder.placeBuilding(grid[6][10], BuildingType.FARM);

        setupBuilder.placeBuilding(grid[15][4], BuildingType.AGRICULTURALPLANT);
        setupBuilder.placeBuilding(grid[15][7], BuildingType.AGRICULTURALPLANT);

        setupBuilder.placeBuilding(grid[34][4], BuildingType.RESEARCHLAB);
        setupBuilder.placeBuilding(grid[34][8], BuildingType.CLONINGFACILITY);

        setupBuilder.placeBuilding(grid[24][4], BuildingType.SILO);
        setupBuilder.placeEnclosure(grid[26][4], grid[24][4]);

        setupBuilder.placeBuilding(grid[24][7], BuildingType.SILO);
        setupBuilder.placeEnclosure(grid[26][7], grid[24][7]);

        setupBuilder.placeBuilding(grid[24][10], BuildingType.SILO);
        setupBuilder.placeEnclosure(grid[26][10], grid[24][10]);

        setupBuilder.placeBuilding(grid[32][11], BuildingType.SILO);
        setupBuilder.placeEnclosure(grid[34][11], grid[32][11]);

        if (grid[26][4].getBuilding() instanceof Enclosure) {
            Enclosure enc = (Enclosure) grid[26][4].getBuilding();
            enc.newSpeciesArrives(AnimalType.CAT);
            enc.receiveAnimal(); enc.receiveAnimal();
        }
        if (grid[26][7].getBuilding() instanceof Enclosure) {
            Enclosure enc = (Enclosure) grid[26][7].getBuilding();
            enc.newSpeciesArrives(AnimalType.HORSE);
            enc.receiveAnimal();
        }
        if (grid[26][10].getBuilding() instanceof Enclosure) {
            Enclosure enc = (Enclosure) grid[26][10].getBuilding();
            enc.newSpeciesArrives(AnimalType.BEAR);
            enc.receiveAnimal(); enc.receiveAnimal(); enc.receiveAnimal();
        }

        setupBuilder.placeBuilding(grid[30][30], BuildingType.CITY);
        setupBuilder.placeBuilding(grid[33][30], BuildingType.CITY);
        setupBuilder.placeBuilding(grid[30][33], BuildingType.CITY);
        setupBuilder.placeBuilding(grid[33][33], BuildingType.CITY);

        for(int x = 30; x <= 34; x++) { setupBuilder.buildRoad(grid[x][32], true); }
        for(int y = 30; y <= 34; y++) { setupBuilder.buildRoad(grid[32][y], true); }

        setupBuilder.placeBusStop(grid[31][29], RoadDirection.WEST);
        setupBuilder.buildRoad(grid[32][29], true);

        setupBuilder.placeBusStop(grid[35][31], RoadDirection.NORTH);
        setupBuilder.buildRoad(grid[35][32], true);

        setupBuilder.placeBusStop(grid[31][35], RoadDirection.WEST);
        setupBuilder.buildRoad(grid[32][35], true);

        setupBuilder.placeBuilding(grid[26][36], BuildingType.FARM);
        setupBuilder.placeBuilding(grid[30][36], BuildingType.FARM);
        setupBuilder.placeBuilding(grid[34][36], BuildingType.FARM);

        setupBuilder.placeBuilding(grid[10][32], BuildingType.AGRICULTURALPLANT);
        setupBuilder.placeBuilding(grid[10][35], BuildingType.AGRICULTURALPLANT);

        setupBuilder.placeBuilding(grid[4][28], BuildingType.SILO);
        setupBuilder.placeEnclosure(grid[6][28], grid[4][28]);

        setupBuilder.placeBuilding(grid[4][31], BuildingType.SILO);
        setupBuilder.placeEnclosure(grid[6][31], grid[4][31]);

        setupBuilder.placeBuilding(grid[12][28], BuildingType.SILO);
        setupBuilder.placeEnclosure(grid[14][28], grid[12][28]);

        for(int x = 0; x < cols; x++) {
            for(int y = 0; y < rows; y++) {
                if (grid[x][y].isEmpty() && grid[x][y].getTerrainType() == TerrainType.LAND) {
                    int distToCenter = (int) Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                    int treeChance = distToCenter < 14 ? 10 : 4;

                    if (ThreadLocalRandom.current().nextInt(100) < treeChance) {
                        grid[x][y].setTreeCount(ThreadLocalRandom.current().nextInt(1, 4));
                    }
                }
            }
        }

//        try{
//            Vehicle testVehicle = new FoodTruck(this, new Point(grid[6][3].getCoordinate().x, grid[6][3].getCoordinate().y));
//            grid[6][3].getRoad().vehicleEnters(testVehicle, testVehicle.getCurrentDirection());
//            vehicles.add(testVehicle);
//        }catch (Exception e){System.err.println("error: " + e.getMessage());}
    }

    public Tile get(int x, int y) {
        if (x < 0 || x > rows - 1 || y < 0 || y > cols - 1) {
            return null;
        }
        return grid[x][y];
    }

    public int getMoney() {
        return this.money;
    }

    public int getElapsedTime() { return this.elapsedTime; }

    public int getAnnualCostOfVehicles() {
        int cost = 0;
        for (int i = 0; i < this.vehicles.size(); i++) {
            cost = cost + this.vehicles.get(i).getCostToOperate();
        }
        return cost;
    }

    public void sellVehicle(Vehicle vehicle) {
        this.receiveMoney(vehicle.getCostToSell());
        this.vehicles.remove(vehicle);
    }

    public void receiveMoney(int income) {
        this.money = this.money + income;
    }

    public void spendMoney(int spending) {
        this.money = this.money - spending;
    }

    public void moveVehicles() throws Exception {
        for (int i = 0; i < this.vehicles.size(); i++) {
            this.vehicles.get(i).increaseTickCount();
        }
    }

    public void newDay() throws Exception {
        this.elapsedTime = this.elapsedTime + 1;

        Set<Building> updatedBuildings = new HashSet<>();
        for(int i = 0; i < cols; i++)
        {
            for(int j = 0; j < rows; j++)
            {
                Tile t = grid[i][j];
                if(t != null && t.getBuilding() != null)
                {
                    Building b = t.getBuilding();
                    if(!updatedBuildings.contains(b))
                    {
                        b.newDay();
                        updatedBuildings.add(b);
                    }
                }
            }
        }

        this.vehiclesUpkeep++;
        if (this.vehiclesUpkeep == this.DAYS_PER_VEHICLES_UPKEEP) {
            this.vehiclesUpkeep = 0;
            this.spendMoney(this.getAnnualCostOfVehicles());
        }

        this.daysSinceBusRoute++;
        if (this.daysSinceBusRoute >= this.DAYS_UNTIL_NEW_BUS_ROUTE && this.start == null && this.stop == null) {
            try {
                this.setBusRoute();
                this.daysSinceBusRoute = 0;
            } catch (Exception e) {
                System.err.println("Nem sikerült buszjáratot generálni: " + e.getMessage());
            }
        }
    }

    // ÚTKERESÉS ÚT -> ÚT
    public List<Point> findPathRoad(Tile start, Tile stop) throws Exception {
        // Ellenőrizzük, hogy útra akarunk-e menni
        if (stop.getTerrainType() != TerrainType.ROAD && stop.getTerrainType() != TerrainType.BRIDGE) {
            throw new Exception("The destination isn't on the road!");
        }
        else {
            List<Point> path = new ArrayList<Point>();
            ArrayList<Tile> queue = new ArrayList<Tile>();
            HashMap<Tile, PathHelper> pathfinder = new HashMap<Tile, PathHelper>();
            pathfinder.put(start, new PathHelper(0, start));
            queue.add(start);
            while (!queue.isEmpty()) {
                for (int i = 0; i < queue.size(); i++) {
                    Tile current = queue.removeFirst();
                    List<Tile> neighbours = getNeighbourRoads(current);
                    for (int j = 0; j < neighbours.size(); j++) {
                        int value = pathfinder.get(current).currentMinDistance + 1;
                        if (!pathfinder.containsKey(neighbours.get(j))) {
                            pathfinder.put(neighbours.get(j), new PathHelper(value, current));
                            queue.add(neighbours.get(j));
                        } else if (pathfinder.get(neighbours.get(j)).currentMinDistance > value) {
                            pathfinder.remove(neighbours.get(j));
                            pathfinder.put(neighbours.get(j), new PathHelper(value, current));
                            if (!queue.contains(neighbours.get(j))) {
                                queue.add(neighbours.get(j));
                            }
                        }
                    }
                }
            }
            if (pathfinder.containsKey(stop)) {
                Tile currentKey = stop;
                while (currentKey != start) {
                    path.add(new Point(currentKey.getCoordinate().x, currentKey.getCoordinate().y));
                    Tile newCurrentKey = pathfinder.get(currentKey).currentOrigin;
                    currentKey = newCurrentKey;
                }
                path.add(new Point(start.getCoordinate().x, start.getCoordinate().y));
                Collections.reverse(path);
                return path;
            } else {
                // No way to reach destination!
                return null;
            }
        }
    }

    // ÚJ ÚTKERESÉS: MEGÁLLÓ -> MEGÁLLÓ
    public List<Point> findPath(Tile start, Tile stop) throws Exception {
        if (stop.getTerrainType() != TerrainType.STOP) {
            throw new Exception("The destination isn't a stop!");
        }
        // Megállóból való indulás esetén
        else if (start.getBuilding() != null) {
            switch (start.getBuilding().getBuildingType()) {
                case BuildingType.BUSSTOP:
                    if (((BusStop) (start.getBuilding())).getConnectedRoad() == null) {
                        // Nincs út, amihez kapcsolódna
                        return null;
                    } else {
                        if (stop.getBuilding().getBuildingType() != BuildingType.BUSSTOP) {
                            throw new Exception("Can only go from bus stop to bus stop!");
                        } else if (((BusStop) (stop.getBuilding())).getConnectedRoad() == null) {
                            // Szintén nincs út, amihez kapcsolódna
                            return null;
                        } else {
                            Tile startRoad = (((BusStop) (start.getBuilding())).getConnectedRoad());
                            Tile stopRoad = (((BusStop) (stop.getBuilding())).getConnectedRoad());
                            List<Point> path = findPathRoad(startRoad, stopRoad);
                            // path.addFirst(new Point(start.getCoordinate().x, start.getCoordinate().y));
                            path.add(new Point(stop.getCoordinate().x, stop.getCoordinate().y));
                            return path;
                        }
                    }
                case BuildingType.STATION:
                    if (((Station) (start.getBuilding())).getConnectedRoad() == null) {
                        return null;
                    } else {
                        if (stop.getBuilding().getBuildingType() != BuildingType.STATION) {
                            throw new Exception("Can only go from station to station!");
                        } else if (((Station) (stop.getBuilding())).getConnectedRoad() == null) {
                            return null;
                        } else {
                            Tile startRoad = (((Station) (start.getBuilding())).getConnectedRoad());
                            Tile stopRoad = (((Station) (stop.getBuilding())).getConnectedRoad());
                            List<Point> path = findPathRoad(startRoad, stopRoad);
                            // path.addFirst(new Point(start.getCoordinate().x, start.getCoordinate().y));
                            path.add(new Point(stop.getCoordinate().x, stop.getCoordinate().y));
                            return path;
                        }
                    }
                default:
                    return null;
                }
        // Útról való indulás esetén
        } else if (start.getRoad() != null) {
            if (stop.getBuilding() != null) {
                switch (stop.getBuilding().getBuildingType()) {
                    case BuildingType.BUSSTOP:
                        if (((BusStop) (stop.getBuilding())).getConnectedRoad() == null) {
                            // Szintén nincs út, amihez kapcsolódna
                            return null;
                        } else {
                            Tile stopRoad = (((BusStop) (stop.getBuilding())).getConnectedRoad());
                            List<Point> path = findPathRoad(start, stopRoad);
                            // path.addFirst(new Point(start.getCoordinate().x, start.getCoordinate().y));
                            path.add(new Point(stop.getCoordinate().x, stop.getCoordinate().y));
                            path.removeFirst();
                            return path;
                        }
                    case BuildingType.STATION:
                        if (((Station) (stop.getBuilding())).getConnectedRoad() == null) {
                            // Szintén nincs út, amihez kapcsolódna
                            return null;
                        } else {
                            Tile stopRoad = (((Station) (stop.getBuilding())).getConnectedRoad());
                            List<Point> path = findPathRoad(start, stopRoad);
                            // path.addFirst(new Point(start.getCoordinate().x, start.getCoordinate().y));
                            path.add(new Point(stop.getCoordinate().x, stop.getCoordinate().y));
                            path.removeFirst();
                            return path;
                        }
                    default:
                        return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    // Betöltés után world referenciák visszaállítása
    public void restoreWorldReferences() {
        for (int y = 0; y < getRows(); y++) {
            for (int x = 0; x < getCols(); x++) {
                world.tile.Tile t = get(x, y);
                if (t != null) {
                    if (t.getBuilding() != null) {
                        t.getBuilding().setWorld(this);
                        if (t.getBuilding() instanceof BusStop bs) {
                            bs.initAfterLoad();
                        } else if (t.getBuilding() instanceof Station st) {
                            st.initAfterLoad();
                        } else if (t.getBuilding() instanceof Enclosure en) {
                            en.restoreSiloRef();
                        }
                    } else if (t.getRoad() != null && t.getRoad() instanceof Bridge bridge) {
                        Tile start = get(bridge.getStartTilePos().x, bridge.getStartTilePos().y);
                        Tile end = get(bridge.getEndTilePos().x, bridge.getEndTilePos().y);
                        bridge.initAfterLoad(end, start);
                    }
                }
            }
        }

        if (getVehicles() != null) {
            for (world.vehicle.Vehicle v : getVehicles()) {
                v.initAfterLoad(this);
            }
        }
    }

    private class PathHelper {
        public int currentMinDistance;
        public Tile currentOrigin;

        public PathHelper(int i, Tile t) {
            this.currentMinDistance = i;
            this.currentOrigin = t;
        }
    }

    public boolean isValidTile(int x, int y) {
        if (x < 0 || x >= grid.length || y < 0 || y >= grid[0].length) {
            return false;
        }
        return true;
    }

    private List<Tile> getNeighbourRoads(Tile t) {
        ArrayList<Tile> neighbourRoads = new ArrayList<Tile>();

        Tile northNeighbour = this.get(t.getCoordinate().x, t.getCoordinate().y - 1);
        Tile southNeighbour = this.get(t.getCoordinate().x, t.getCoordinate().y + 1);
        Tile eastNeighbour = this.get(t.getCoordinate().x + 1, t.getCoordinate().y);
        Tile westNeighbour = this.get(t.getCoordinate().x - 1, t.getCoordinate().y);

        if (northNeighbour != null && (northNeighbour.getTerrainType() == TerrainType.ROAD || northNeighbour.getTerrainType() == TerrainType.BRIDGE) && northNeighbour.getRoad() != null) {
            neighbourRoads.add(northNeighbour);
        }
        if (southNeighbour != null && (southNeighbour.getTerrainType() == TerrainType.ROAD || southNeighbour.getTerrainType() == TerrainType.BRIDGE) && southNeighbour.getRoad() != null) {
            neighbourRoads.add(southNeighbour);
        }
        if (eastNeighbour != null && (eastNeighbour.getTerrainType() == TerrainType.ROAD || eastNeighbour.getTerrainType() == TerrainType.BRIDGE) && eastNeighbour.getRoad() != null) {
            neighbourRoads.add(eastNeighbour);
        }
        if (westNeighbour != null && (westNeighbour.getTerrainType() == TerrainType.ROAD || westNeighbour.getTerrainType() == TerrainType.BRIDGE) && westNeighbour.getRoad() != null) {
            neighbourRoads.add(westNeighbour);
        }

        return neighbourRoads;
    }

    public void rerouteVehiclesStation(Tile t) {
        for (int i = 0; i < this.vehicles.size(); i++) {
            this.vehicles.get(i).rerouteStop(t);
        }
    }

    public void rerouteVehiclesRoad(Tile t) {
        for (int i = 0; i < this.vehicles.size(); i++) {
            this.vehicles.get(i).reroutePath(t);
        }
    }

    public void rerouteVehicles() {
        for (int i = 0; i < this.vehicles.size(); i++) {
            this.vehicles.get(i).reroutePath();
        }
    }

    public void startedBusRoute() {
        this.start = null;
    }

    public void setBusRoute() throws Exception {
        if (start == null && stop == null) {
            if (this.busStops.size() < 2) {
                throw new Exception("Not enough bus stops in world!");
            } else {
                List<Integer> listOfIndexes = new ArrayList<Integer>();
                for (int i = 0; i < busStops.size(); i++) {
                    listOfIndexes.add(i);
                }
                int startInd = ThreadLocalRandom.current().nextInt(0, listOfIndexes.size());
                int actualStartInd = listOfIndexes.remove(startInd);

                BusStop startStop = busStops.get(actualStartInd);
                startStop.setAsStart();
                this.start = startStop;

                int stopInd = ThreadLocalRandom.current().nextInt(0, listOfIndexes.size());
                int actualStopInd = listOfIndexes.get(stopInd);

                BusStop destinationStop = busStops.get(actualStopInd);
                destinationStop.setAsStop();
                this.stop = destinationStop;
            }
        }
    }

    public void finishedBusRoute() {
        this.stop = null;
    }

    public List<BusStop> getBusStops() { return this.busStops; }
}
