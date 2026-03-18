package world;

import world.tile.Point;
import world.tile.TerrainType;
import world.tile.Tile;

import java.util.List;

public class World {
    private final int rows;
    private final int cols;
    private final Tile[][] grid;

    public World(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        grid = new Tile[rows][cols];
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
            System.err.println("Koordinata kivul esik a griden: (" + x + "," + y + ")");
            return null;
        }
        return grid[x][y];
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
}
