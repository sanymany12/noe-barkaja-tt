package world;

import world.building.BuildingType;
import world.building.Station;
import world.tile.Point;
import world.tile.TerrainType;
import world.tile.Tile;
import world.tile.road.Road;
import world.tile.road.RoadDirection;
import world.building.BusStop;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

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
        if (stop.getTerrainType() != TerrainType.STOP || stop.getTerrainType() != TerrainType.ROAD) {
            throw new Exception("The destination isn't on the road!");
        }
        else {
            return null;
            // pathfinding algorithm needed
        }
    }

    public boolean isValidTile(int x, int y) {
        if (x < 0 || x >= grid.length || y < 0 || y >= grid[0].length) {
            return true;
        }
        return true;
    }

    // Ennek meghívásával frissülnek a környékén található utak és megépül az út a megadott mezőre
    public void buildRoad(Tile t) {
        t.setRoad(new Road());
        t.setTerrainType(TerrainType.ROAD);
        Tile neighbourNorth = this.get(t.getCoordinate().x, t.getCoordinate().y-1);
        Tile neighbourWest = this.get(t.getCoordinate().x-1, t.getCoordinate().y);
        Tile neighbourEast = this.get(t.getCoordinate().x+1, t.getCoordinate().y);
        Tile neighbourSouth = this.get(t.getCoordinate().x, t.getCoordinate().y+1);
        if (neighbourNorth != null) {
            if (neighbourNorth.getRoad() != null) {
                t.getRoad().setConnection(RoadDirection.NORTH);
                neighbourNorth.getRoad().setConnection(RoadDirection.NORTH.getOpposite());
            }
        }
        if (neighbourSouth != null) {
            if (neighbourSouth.getRoad() != null) {
                t.getRoad().setConnection(RoadDirection.SOUTH);
                neighbourSouth.getRoad().setConnection(RoadDirection.SOUTH.getOpposite());;
            }
        }
        if (neighbourEast != null) {
            if (neighbourEast.getRoad() != null) {
                t.getRoad().setConnection(RoadDirection.EAST);
                neighbourEast.getRoad().setConnection(RoadDirection.EAST.getOpposite());
            }
        }
        if (neighbourWest != null) {
            if (neighbourWest.getRoad() != null) {
                t.getRoad().setConnection(RoadDirection.WEST);
                neighbourWest.getRoad().setConnection(RoadDirection.WEST.getOpposite());;
            }
        }
    }

    public void buildStation(Tile t, RoadDirection dir) {
        Tile buildingTile = null;
        if (dir == RoadDirection.NORTH) {
            buildingTile = this.get(t.getCoordinate().x, t.getCoordinate().y - 1);
        } else if (dir == RoadDirection.SOUTH) {
            buildingTile = this.get(t.getCoordinate().x, t.getCoordinate().y + 1);
        } else if (dir == RoadDirection.WEST) {
            buildingTile = this.get(t.getCoordinate().x - 1, t.getCoordinate().y);
        } else if (dir == RoadDirection.EAST) {
            buildingTile = this.get(t.getCoordinate().x + 1, t.getCoordinate().y);
        }
        if (buildingTile != null && buildingTile.getTerrainType() == TerrainType.BUILDING && buildingTile.getBuilding() != null) {
            if (buildingTile.getBuilding().getBuildingType() != BuildingType.BUSSTOP && buildingTile.getBuilding().getBuildingType() != BuildingType.STATION) {
                this.get(t.getCoordinate().x, t.getCoordinate().y).setBuilding(new Station(this, t, buildingTile.getBuilding()));
            }
        }
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
