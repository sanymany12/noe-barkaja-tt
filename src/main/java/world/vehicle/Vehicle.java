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

    protected Point currentPlace;

    protected double speed;
    protected int capacity;
    protected int costToOperate;

    protected int cargoNum;
    protected List<Point> path;

    protected ICargo cargoType;

    protected VehicleType type;

    public Vehicle(World world, Point p) throws Exception {
        this.world = world;

        if (this.world.isValidTile(p.x, p.y)) {
            this.currentPlace = p;
        } else {
            throw new Exception("Invalid tile!");
        }

        this.cargoType = null;
        this.cargoNum = 0;
        this.path = new ArrayList<Point>();
        this.type = null;
    }

    public VehicleType getVehicleType() {
        return this.type;
    }

    public abstract void loadFrom(Building building);

    public abstract void unloadTo(Building building);

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

    public ICargo getCargoType() {
        return this.cargoType;
    }

    public void move() {
        if (!this.path.isEmpty()) {
            this.currentPlace = this.path.removeFirst();
        }
    }

    public void findPath(Tile destination) throws Exception {
        Tile currentPosition = world.get(this.currentPlace.x, this.currentPlace.y);

        List<Point> path = world.findPath(currentPosition, destination);

        this.path = path;
    }
}
