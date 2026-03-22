package world;

import world.tile.Point;
import world.tile.TerrainType;
import world.tile.Tile;
import world.tile.road.Road;
import world.tile.road.RoadDirection;

import java.util.ArrayList;
import java.util.List;

public class World {
    private final int rows;
    private final int cols;
    private final Tile[][] grid;

    private List<Road> roads;

    public World(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        grid = new Tile[rows][cols];
        initWorld();
        this.roads = new ArrayList<Road>();
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
        if (x < 0 || x > rows || y < 0 || y > cols) {
            return null;
        }
        return grid[x][y];
    }

    public List<Point> findPath(Point start, Point stop) throws Exception {
//        if (start.getTerrainType() != TerrainType.STOP && start.getTerrainType() != TerrainType.ROAD && start.getTerrainType() != TerrainType.BRIDGE) {
//            throw new Exception("The start isn't on the road!");
//        }
//        else if (stop.getTerrainType() != TerrainType.STOP) {
//            throw new Exception("The destination isn't a stop!");
//        }
        if (isValidTile(start.x, start.y) && isValidTile(stop.x, stop.y)) {
            throw new Exception("Invalid tile!");
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
        t.setRoad(new Road(t.getCoordinate().x, t.getCoordinate().y));
        this.roads.add(t.getRoad());
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

}
