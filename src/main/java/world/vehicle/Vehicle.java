package world.vehicle;

import world.World;
import world.building.Building;
import world.resources.ICargo;
import world.tile.Point;
import world.tile.Tile;
import world.tile.road.RoadDirection;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Vehicle {
    protected final World world;

    protected Point currentPlace;
    protected RoadDirection currentDirection;

    protected float width; //1.0 = 1 cella
    protected float height;//1.0 = 1 cella

    protected double speed;
    protected int capacity;
    protected int costToOperate;

    protected int cargoNum;
    protected List<Point> path;

    public Point getCurrentPlace() {
        return currentPlace;
    }

    protected ICargo cargoType;

    protected VehicleType type;

    public Vehicle(World world, Point p) throws Exception {
        this.world = world;

        if (this.world.isValidTile(p.x, p.y)) {
            this.currentPlace = p;
        } else {
            throw new Exception("Invalid tile!");
        }

        this.currentDirection = RoadDirection.SOUTH;

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
            Point nextTile = this.path.removeFirst();
            int relativeX = this.currentPlace.x - nextTile.x;
            int relativeY = this.currentPlace.y - nextTile.y;

            switch (relativeX) {
                case -1:
                    this.currentDirection = RoadDirection.WEST;
                    break;
                case 0:
                    switch (relativeY) {
                        case -1:
                            this.currentDirection = RoadDirection.NORTH;
                            break;
                        case 1:
                            this.currentDirection = RoadDirection.SOUTH;
                            break;
                    }
                    break;
                case 1:
                    this.currentDirection = RoadDirection.EAST;
                    break;
            }

            switch (this.currentDirection) {
                case RoadDirection.NORTH:
                    this.width = 0.5F;
                    this.height = 0.5F;
                    break;
                case RoadDirection.SOUTH:
                    this.width = 0.5F;
                    this.height = 0.5F;
                    break;
                case RoadDirection.EAST:
                    this.width = 0.75F;
                    this.height = 0.5F;
                    break;
                case RoadDirection.WEST:
                    this.width = 0.75F;
                    this.height = 0.5F;
                    break;
            }

            this.currentPlace = nextTile;
        }
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void findPath(Tile destination) throws Exception {
        Tile currentPosition = world.get(this.currentPlace.x, this.currentPlace.y);

        List<Point> path = world.findPath(currentPosition, destination);

        this.path = path;
    }

    public abstract String getSpriteName();
}
