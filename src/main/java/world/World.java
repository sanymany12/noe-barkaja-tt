package world;

import world.building.*;
import world.tile.*;
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

    private List<Vehicle> vehicles;
    private ArrayList<BusStop> busStops;

    private BusStop start;
    private BusStop stop;
    private int daysSinceBusRoute;

    private final int DAYS_UNTIL_NEW_BUS_ROUTE = 7;

    public World(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        grid = new Tile[rows][cols];
        this.money = 10000;
        this.elapsedTime = 0;

        this.busStops = new ArrayList<BusStop>();
        this.vehicles = new ArrayList<Vehicle>();

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

    //TODO itt kellene beolvasni a meghatározott világot
    public void initWorld(){
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j] = new Tile(new Point(i,j), TerrainType.LAND, 0, null, null, false);
            }
        }

        grid[0][0].setTreeCount(1);

        grid[1][1].setTerrainType(TerrainType.WATER);
        grid[1][2].setTerrainType(TerrainType.WATER);
        grid[2][1].setTerrainType(TerrainType.WATER);
        grid[2][2].setTerrainType(TerrainType.WATER);

        try{
            Vehicle testVehicle = new Bus(this, new Point(5,5));
            vehicles.add(testVehicle);
        }catch (Exception e){System.err.println("error");}


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

    public void receiveMoney(int income) {
        this.money = this.money + income;
    }

    public void spendMoney(int spending) {
        this.money = this.money - spending;
    }

    public void newDay() throws Exception {
        this.elapsedTime = this.elapsedTime + 1;
        for (int i = 0; i < this.vehicles.size(); i++) {
            this.vehicles.get(i).move();
        }
        this.daysSinceBusRoute++;
        if (this.daysSinceBusRoute >= this.DAYS_UNTIL_NEW_BUS_ROUTE && !(this.start == null && this.stop != null)) {
            //this.setBusRoute();
        }
    }

    // ÚTKERESÉS ÚT -> ÚT
    public List<Point> findPathRoad(Tile start, Tile stop) throws Exception {
        // Ellenőrizzük, hogy útra akarunk-e menni
        if (stop.getTerrainType() != TerrainType.ROAD) {
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
        if (start.getTerrainType() != TerrainType.STOP || stop.getTerrainType() != TerrainType.STOP) {
            throw new Exception("The destination isn't a stop!");
        }
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
                            path.addFirst(new Point(start.getCoordinate().x, start.getCoordinate().y));
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
                            path.addFirst(new Point(start.getCoordinate().x, start.getCoordinate().y));
                            path.add(new Point(stop.getCoordinate().x, stop.getCoordinate().y));
                            return path;
                        }
                    }
                default:
                    return null;
                }
        } else {
            return null;
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
            return true;
        }
        return true;
    }

    private List<Tile> getNeighbourRoads(Tile t) {
        ArrayList<Tile> neighbourRoads = new ArrayList<Tile>();

        Tile northNeighbour = this.get(t.getCoordinate().x, t.getCoordinate().y - 1);
        Tile southNeighbour = this.get(t.getCoordinate().x, t.getCoordinate().y + 1);
        Tile eastNeighbour = this.get(t.getCoordinate().x + 1, t.getCoordinate().y);
        Tile westNeighbour = this.get(t.getCoordinate().x - 1, t.getCoordinate().y);

        if (northNeighbour != null && northNeighbour.getTerrainType() == TerrainType.ROAD && northNeighbour.getRoad() != null) {
            neighbourRoads.add(northNeighbour);
        }
        if (southNeighbour != null && southNeighbour.getTerrainType() == TerrainType.ROAD && southNeighbour.getRoad() != null) {
            neighbourRoads.add(southNeighbour);
        }
        if (eastNeighbour != null && eastNeighbour.getTerrainType() == TerrainType.ROAD && eastNeighbour.getRoad() != null) {
            neighbourRoads.add(eastNeighbour);
        }
        if (westNeighbour != null && westNeighbour.getTerrainType() == TerrainType.ROAD && westNeighbour.getRoad() != null) {
            neighbourRoads.add(westNeighbour);
        }

        return neighbourRoads;
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
                listOfIndexes.remove(startInd);
                busStops.get(startInd).setAsStart();
                int stopInd = ThreadLocalRandom.current().nextInt(0, listOfIndexes.size());
                busStops.get(stopInd).setAsStop();
            }
        }
    }
}
