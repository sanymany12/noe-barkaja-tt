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

    protected double screenX;
    protected double screenY;
    protected Point targetTile;
    protected double targetScreenX;
    protected double targetScreenY;

    public double getScreenX() {
        return screenX;
    }

    public double getScreenY() {
        return screenY;
    }

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
        this.screenX = p.x;
        this.screenY = p.y;
        this.speed = 0.05;
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

    public void update() throws Exception {

        if (this.targetTile == null && !this.path.isEmpty()) {
            this.targetTile = this.path.removeFirst();

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

            // Kiszámoljuk a jelenlegi irányt
            int relativeX = this.currentPlace.x - this.targetTile.x;
            int relativeY = this.currentPlace.y - this.targetTile.y;

            if (relativeX == -1) this.currentDirection = RoadDirection.EAST;
            else if (relativeX == 1) this.currentDirection = RoadDirection.WEST;
            else if (relativeY == -1) this.currentDirection = RoadDirection.SOUTH;
            else if (relativeY == 1) this.currentDirection = RoadDirection.NORTH;

            // beállítjuk a vizuális méreteket
            switch (this.currentDirection) {
                case RoadDirection.NORTH:
                case RoadDirection.SOUTH:
                    this.width = 0.5F;
                    this.height = 0.5F;
                    break;
                case RoadDirection.EAST:
                case RoadDirection.WEST:
                    this.width = 0.75F;
                    this.height = 0.5F;
                    break;
            }

            // Megnézzük, merre fogunk menni a kövi lépésben
            RoadDirection nextDirection = this.currentDirection;

            if (!this.path.isEmpty()) {
                Point nextPathTile = this.path.getFirst();
                int nextRelX = this.targetTile.x - nextPathTile.x;
                int nextRelY = this.targetTile.y - nextPathTile.y;

                if (nextRelX == -1) nextDirection = RoadDirection.EAST;
                else if (nextRelX == 1) nextDirection = RoadDirection.WEST;
                else if (nextRelY == -1) nextDirection = RoadDirection.SOUTH;
                else if (nextRelY == 1) nextDirection = RoadDirection.NORTH;
            }

            // célpont és sáv eltolás kiszámitása
            double dist = 0.1; // Sáv közepe a csempe közepétől
            double mid = 0.5;

            // Alapértelmezett eltolás
            double offsetX = mid - (this.width / 2.0);
            double offsetY = mid - (this.height / 2.0);

            if (this.currentDirection != nextDirection) {
                // kanyarba pontosan a két sáv metszéspontját célozzuk meg a kereszteződés közepén
                if (this.currentDirection == RoadDirection.NORTH || nextDirection == RoadDirection.NORTH) offsetX = (mid + dist) - (this.width / 2.0);
                else if (this.currentDirection == RoadDirection.SOUTH || nextDirection == RoadDirection.SOUTH) offsetX = (mid - dist) - (this.width / 2.0);

                if (this.currentDirection == RoadDirection.EAST || nextDirection == RoadDirection.EAST) offsetY = (mid + dist) - (this.height / 2.0);
                else if (this.currentDirection == RoadDirection.WEST || nextDirection == RoadDirection.WEST) offsetY = (mid - dist) - (this.height / 2.0);
            } else {
                // egyenesen a csempe túlsó szélét célozzuk meg a sávon BELÜL
                if (this.currentDirection == RoadDirection.NORTH) {
                    offsetX = (mid + dist) - (this.width / 2.0);
                    offsetY = 0.0; // Kilépés felül
                }
                else if (this.currentDirection == RoadDirection.SOUTH) {
                    offsetX = (mid - dist) - (this.width / 2.0);
                    offsetY = 1.0; // Kilépés alul
                }
                else if (this.currentDirection == RoadDirection.EAST) {
                    offsetX = 1.0; // Kilépés jobbra
                    offsetY = (mid + dist) - (this.height / 2.0);
                }
                else if (this.currentDirection == RoadDirection.WEST) {
                    offsetX = 0.0; // Kilépés balra
                    offsetY = (mid - dist) - (this.height / 2.0);
                }
            }

            this.targetScreenX = this.targetTile.x + offsetX;
            this.targetScreenY = this.targetTile.y + offsetY;
        }

        // folyamatos mozgás
        if (this.targetTile != null) {

            double dX = this.targetScreenX - this.screenX;
            double dY = this.targetScreenY - this.screenY;
            double distance = Math.sqrt(dX * dX + dY * dY);

            if (distance <= this.speed) {

                this.screenX = this.targetScreenX;
                this.screenY = this.targetScreenY;

                // Logikailag is frissítjük a pozíciót
                this.currentPlace = this.targetTile;

                // Jármű megérkezik a következő tile-re
                if (world.get(this.currentPlace.x, this.currentPlace.y).getBuilding() != null) {
                    switch (world.get(this.currentPlace.x, this.currentPlace.y).getBuilding().getBuildingType()) {
                        case BuildingType.STATION:
                            ((Station) (world.get(this.currentPlace.x, this.currentPlace.y).getBuilding())).vehicleArrives(this);
                            break;
                        case BuildingType.BUSSTOP:
                            ((BusStop) (world.get(this.currentPlace.x, this.currentPlace.y).getBuilding())).vehicleArrives(this);
                            break;
                        default:
                            break;
                    }
                } else if (world.get(this.currentPlace.x, this.currentPlace.y).getRoad() != null) {
                    this.world.get(this.currentPlace.x, this.currentPlace.y).getRoad().vehicleEnters(this, this.currentDirection);
                }

                this.targetTile = null;
            }
            else {
                double dirX = dX / distance;
                double dirY = dY / distance;

                this.screenX += dirX * this.speed;
                this.screenY += dirY * this.speed;
            }
        }
        else if (this.routeStops.size() >= 2) {
            if (movingForward) {
                stopIndex++;
                if (stopIndex >= routeStops.size()) {
                    movingForward = false;
                    stopIndex = routeStops.size() - 2;
                }
            } else {
                stopIndex--;
                if (stopIndex < 0) {
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
