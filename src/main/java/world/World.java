package world;

import world.building.Building;
import world.tile.Tile;
import world.vehicle.Vehicle;

import java.util.List;

public class World {
    private int rows;
    private int cols;
    private Tile[][] grid;
    private List<Vehicle> vehicles;
    private List<Building> buildings;
    private int money;
    private int elapsedTime;

    public World() {

    }

    public Tile getTile(int x, int y) throws Exception {
        if (isValidTile(x, y)) {
            return grid[x][y];
        }
        else {
            throw new Exception("Invalid tile!");
        }
    }

    public boolean isValidTile(int x, int y) {
        if (x < rows && y < cols) {
            return true;
        }
        else {
            return false;
        }
    }
}
