package world.vehicle;

import world.World;
import world.building.Building;
import world.resources.ICargo;
import world.tile.Point;
import world.tile.Tile;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Vehicle {
    protected final World world;

    protected int currentRow;
    protected int currentCol;

    protected double speed;
    protected int capacity;
    protected int costToOperate;

    protected int cargoNum;
    protected List<Point> path;

    public Vehicle(World world, int x, int y) throws Exception {
        this.world = world;

        if (world.isValidTile(x, y)) {
            this.currentRow = x;
            this.currentCol = y;
        }
        else {
            throw new Exception("Invalid tile!");
        }

        this.cargoNum = 0;
        this.path = new ArrayList<Point>();
    }

    public abstract void loadFrom(Building building);

    public abstract void unloadFrom(Building building);

    public boolean isEmpty() {
        if (this.cargoNum == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isFull() {
        if (this.cargoNum == this.capacity) {
            return true;
        }
        else {
            return false;
        }
    }

    public void findPath(Tile destination) throws Exception {
        Tile currentPosition = world.get(this.currentRow, this.currentCol);

        List<Point> path = world.findPath(currentPosition, destination);
    }
}
