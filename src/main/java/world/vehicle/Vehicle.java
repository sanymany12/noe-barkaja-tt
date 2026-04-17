package world.vehicle;

import world.World;
import world.building.Building;
import world.building.BuildingType;
import world.building.BusStop;
import world.building.Station;
import world.resources.ICargo;
import world.tile.Point;
import world.tile.TerrainType;
import world.tile.Tile;
import world.tile.road.RoadDirection;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Vehicle {
    transient protected final World world;

    protected Point currentPlace;
    protected RoadDirection currentDirection;

    protected float width;  //1.0 = 1 cella
    protected float height; //1.0 = 1 cella

    protected double speed;
    protected int capacity;
    protected int costToOperate;

    protected int cargoNum;
    protected List<Point> path;

    protected List<Tile> routeStops = new ArrayList<>();
    protected int stopIndex = 0;
    protected boolean movingForward = true;
    protected boolean isOnTour;
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

        this.width = 0.5F;
        this.height = 0.5F;

        this.cargoType = null;
        this.cargoNum = 0;

        this.path = new ArrayList<Point>();
        this.isOnTour = false;

        this.type = null;
    }

    public void clearRoute()
    {
        this.routeStops.clear();
        this.path.clear();
        this.stopIndex = 0;
        this.movingForward = true;
    }

    public void addRouteStop(Tile stop)
    {
        this.routeStops.add(stop);
    }

    public void startRoute()
    {
        if (this.routeStops.size() >= 2)
        {
            this.stopIndex = 1;
            this.movingForward = true;
            try {
                findPath(this.routeStops.get(this.stopIndex));
            } catch (Exception e)
            {
                System.err.println("Nem található útvonal a kezdéshez: " + e.getMessage());
            }
        }
    }

    public VehicleType getVehicleType() {
        return this.type;
    }

    public Point getCurrentPlace() {
        return currentPlace;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public RoadDirection getCurrentDirection() {
        return this.currentDirection;
    }

    public ICargo getCargoType() {
        return this.cargoType;
    }

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

    public void move() throws Exception {
        if (!this.path.isEmpty()) {
            Point nextTile = this.path.removeFirst();

            // Jármű elhagyja a jelenlegi tile-t
            if (world.get(this.currentPlace.x, this.currentPlace.y).getBuilding() != null) {
                switch (world.get(this.currentPlace.x, this.currentPlace.y).getBuilding().getBuildingType()) {
                    case BuildingType.STATION:
                        ((Station) (world.get(this.currentPlace.x, this.currentPlace.y).getBuilding())).vehicleLeaves();
                        break;
                    case BuildingType.BUSSTOP:
                        ((BusStop) (world.get(this.currentPlace.x, this.currentPlace.y).getBuilding())).vehicleLeaves();
                        break;
                    default:
                        break;
                }
            } else if (world.get(currentPlace.x, currentPlace.y).getRoad() != null) {
                this.world.get(currentPlace.x, currentPlace.y).getRoad().vehicleLeaves(this, this.currentDirection);
            }

            int relativeX = this.currentPlace.x - nextTile.x;
            int relativeY = this.currentPlace.y - nextTile.y;

            // Kiszámoljuk a jármű irányát
            switch (relativeX) {
                case -1:
                    this.currentDirection = RoadDirection.EAST;
                    break;
                case 0:
                    switch (relativeY) {
                        case -1:
                            this.currentDirection = RoadDirection.SOUTH;
                            break;
                        case 1:
                            this.currentDirection = RoadDirection.NORTH;
                            break;
                    }
                    break;
                case 1:
                    this.currentDirection = RoadDirection.WEST;
                    break;
            }

            // Kiszámoljuk a jármű megjelenítésének arányát
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

            // Jármű megérkezik a következő tile-re
            if (world.get(nextTile.x, nextTile.y).getBuilding() != null) {
                switch (world.get(nextTile.x, nextTile.y).getBuilding().getBuildingType()) {
                    case BuildingType.STATION:
                        ((Station) (world.get(nextTile.x, nextTile.y).getBuilding())).vehicleArrives(this);
                        break;
                    case BuildingType.BUSSTOP:
                        ((BusStop) (world.get(nextTile.x, nextTile.y).getBuilding())).vehicleArrives(this);
                        break;
                    default:
                        break;
                }
            } else if (world.get(nextTile.x, nextTile.y).getRoad() != null) {
                this.world.get(nextTile.x, nextTile.y).getRoad().vehicleEnters(this, this.currentDirection);
            }

            // Jármű pozíciójának frissítése
            this.currentPlace = nextTile;
        }
        else if(this.routeStops.size() >= 2)
        {
            if(movingForward)
            {
                stopIndex++;
                if(stopIndex >= routeStops.size())
                {
                    movingForward = false;
                    stopIndex = routeStops.size() - 2;
                }
            } else {
                stopIndex--;
                if(stopIndex < 0)
                {
                    movingForward = true;
                    stopIndex = 1;
                }
            }

            try {
                findPath(this.routeStops.get(stopIndex));
            } catch (Exception e) {
                System.err.println("Hiba az allomassal: " + e.getMessage());
            }
        }
    }

    public void findPath(Tile destination) throws Exception {
        Tile currentPosition = world.get(this.currentPlace.x, this.currentPlace.y);

        List<Point> path = world.findPath(currentPosition, destination);

        if(path != null)
        {
            this.path = path;
        } else {
            this.path = new ArrayList<>();
            System.err.println("Nincs útvonal ehhez a megállóhoz");
        }

    }

    public abstract void loadFrom(Building building) throws Exception;

    public abstract void unloadTo(Building building) throws Exception;

    public abstract String getSpriteName();

    public double getSpeed() { return speed; }

    public int getCapacity() { return capacity; }
}
