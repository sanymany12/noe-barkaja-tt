package world.vehicle;

import world.World;
import world.building.Building;
import world.resources.ICargo;
import world.tile.Point;
import world.tile.Tile;
import world.vehicle.order.OrderManager;

import java.awt.*;
import java.util.List;

public abstract class Vehicle {
    protected int currentRow;
    protected int currentCol;
    protected double pixelX;
    protected double pixelY;
    protected double speed;
    protected int capacity;
    protected int cargoNum;
    protected ICargo cargoType;
    protected List<Point> path;
    protected int pathIndex;
    protected boolean isLoading;
    protected OrderManager orderManager;
    protected int costToOperate;

    public Vehicle(World world, int x, int y) throws Exception {
        if (world.isValidTile(x, y)) {
            this.currentRow = x;
            this.currentCol = y;
        }
        else {
            throw new Exception("Invalid tile!");
        }
    }

    public abstract void onDestinationReached(World world);

    public void update() {

    }

    public void draw(Graphics g, int screenX, int screenY) {

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

    public void findPath(Tile tile) {

    }
}
