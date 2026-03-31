package world;

import world.building.BuildingType;
import world.building.Station;
import world.tile.Point;
import world.tile.TerrainType;
import world.tile.Tile;
import world.tile.road.Road;
import world.tile.road.RoadDirection;
import world.building.BusStop;

import java.nio.file.Path;
import java.util.*;

public class World {
    private Random random;

    private final int rows;
    private final int cols;

    private final Tile[][] grid;

    private int money;
    private int elapsedTime;

    private ArrayList<BusStop> busStops;
    private BusStop start;
    private BusStop stop;

    public World(int rows, int cols) {
        this.random = new Random();
        this.rows = rows;
        this.cols = cols;
        grid = new Tile[rows][cols];
        this.money = 10000;
        this.elapsedTime = 0;
        this.busStops = new ArrayList<BusStop>();
        initWorld();
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    //TODO itt kellene beolvasni a meghatározott világot
    public void initWorld(){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Tile(new Point(i,j), TerrainType.LAND, 0, null, null, false);
            }
        }

        grid[0][0].setTreeCount(1);

        grid[1][1].setTerrainType(TerrainType.WATER);
        grid[1][2].setTerrainType(TerrainType.WATER);
        grid[2][1].setTerrainType(TerrainType.WATER);
        grid[2][2].setTerrainType(TerrainType.WATER);

        //Egy 3x3as épület létrehozása
        grid[10][10].setTerrainType(TerrainType.BUILDING);
        grid[10][10].setAnchor(true);

        grid[11][10].setTerrainType(TerrainType.BUILDING);
        grid[12][10].setTerrainType(TerrainType.BUILDING);
        grid[10][11].setTerrainType(TerrainType.BUILDING);
        grid[10][12].setTerrainType(TerrainType.BUILDING);
        grid[11][11].setTerrainType(TerrainType.BUILDING);
        grid[12][12].setTerrainType(TerrainType.BUILDING);
        grid[11][12].setTerrainType(TerrainType.BUILDING);
        grid[12][11].setTerrainType(TerrainType.BUILDING);

        //Egy 3x3as épület létrehozása
        grid[8][8].setTerrainType(TerrainType.BUILDING);
        grid[8][8].setAnchor(true);

        grid[9][8].setTerrainType(TerrainType.BUILDING);
        grid[10][8].setTerrainType(TerrainType.BUILDING);
        grid[8][9].setTerrainType(TerrainType.BUILDING);
        grid[8][10].setTerrainType(TerrainType.BUILDING);
        grid[9][9].setTerrainType(TerrainType.BUILDING);
        grid[10][10].setTerrainType(TerrainType.BUILDING);
        grid[9][10].setTerrainType(TerrainType.BUILDING);
        grid[10][9].setTerrainType(TerrainType.BUILDING);


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

    public void newDay() {
        this.elapsedTime = this.elapsedTime + 1;
    }

    public List<Point> findPath(Tile start, Tile stop) throws Exception {
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
                    Tile current = queue.get(i);
                    List<Tile> neighbours = getNeighbourRoads(current);
                    for (int j = 0; j < neighbours.size(); j++) {
                        int value = pathfinder.get(current).currentMinDistance + 1;
                        if (!pathfinder.containsKey(neighbours.get(j))) {
                            pathfinder.put(neighbours.get(j), new PathHelper(value, current));
                            queue.add(neighbours.get(j));
                        } else if (pathfinder.get(neighbours.get(j)).currentMinDistance > value) {
                            pathfinder.remove(neighbours.get(j));
                            pathfinder.put(neighbours.get(j), new PathHelper(value, current));
                            queue.add(neighbours.get(j));
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

    public void setBusRoute() throws Exception {
        if (start == null && stop == null) {
            if (this.busStops.size() < 2) {
                throw new Exception("Not enough bus stops in world!");
            } else {
                List<Integer> listOfIndexes = new ArrayList<Integer>();
                for (int i = 0; i < busStops.size(); i++) {
                    listOfIndexes.add(i);
                }
                int startInd = random.nextInt(0, listOfIndexes.size());
                listOfIndexes.remove(startInd);
                busStops.get(startInd).setAsStart();
                int stopInd = random.nextInt(0, listOfIndexes.size());
                busStops.get(stopInd).setAsStop();
            }
        }
    }
}
