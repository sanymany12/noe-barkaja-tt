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
import world.tile.road.Road;
import world.tile.road.RoadDirection;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Vehicle {
    transient protected World world;

    protected Point currentPlace;
    protected RoadDirection currentDirection;

    protected float width;  //1.0 = 1 cella
    protected float height; //1.0 = 1 cella

    protected int speed;
    protected int capacity;
    protected int costToOperate;

    protected int cargoNum;
    protected List<Point> path;

    transient protected List<Tile> routeStops;
    protected List<Point> savedRouteStops;
    protected int stopIndex = 0;
    protected boolean movingForward = true;
    protected boolean isOnTour;
    protected ICargo cargoType;

    protected int tickCount;
    protected int ticksPerMove;

    protected double movedPercentage;

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
        this.routeStops = new ArrayList<Tile>();
        this.savedRouteStops = new ArrayList<Point>();
        this.isOnTour = false;

        this.movingForward = false;
        this.movedPercentage = 0;

        this.type = null;

        this.tickCount = 0;
    }

    public void increaseTickCount() throws Exception {
        if (this.movingForward) {
            this.tickCount++;
            this.movedPercentage = (double) this.tickCount / this.ticksPerMove;
            // System.out.println("A jármű előrehaladása a következő tile felé: " + this.movedPercentage * 100 + "%");
            if (this.tickCount == this.ticksPerMove) {
                this.move();
                this.tickCount = 0;
            }
        }
    }

    public void resetTickCount() {
        tickCount = 0;
    }

    public void clearRoute()
    {
        this.routeStops.clear();
        this.path.clear();
        this.stopIndex = 0;
        this.movingForward = true;
        System.out.println("Jármű útvonala törölve.");
    }

    public void addRouteStop(Tile stop)
    {
        this.routeStops.add(stop);
        this.savedRouteStops.add(new Point(stop.getCoordinate().x, stop.getCoordinate().y));
    }

    public void startRoute()
    {
        if (!this.routeStops.isEmpty())
        {
            this.stopIndex = 0;
            this.movingForward = true;
            if (this.routeStops.get(routeStops.size() - 1) == this.world.get(this.currentPlace.x, this.currentPlace.y)) {
                this.isOnTour = true;
            }
            try {
                findPath(this.routeStops.get(this.stopIndex));
                System.out.println("Útvonal megtalálva.");
            } catch (Exception e)
            {
                System.err.println("Nem található útvonal a kezdéshez: " + e.getMessage());
            }
        }
    }

    public void initAfterLoad(World loadedWorld) {
        this.world = loadedWorld;

        this.routeStops = new ArrayList<>();
        if (this.savedRouteStops != null) {
            for (Point p : this.savedRouteStops) {
                this.routeStops.add(this.world.get(p.x, p.y));
            }
        }

        Tile currentTile = world.get(this.currentPlace.x, this.currentPlace.y);

        if (currentTile.getBuilding() != null) {
            //Visszajegyezzük az autót a megállóba
            if(currentTile.getBuilding() instanceof BusStop busStop){
                try{
                    busStop.vehicleArrives(this);
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else if (currentTile.getBuilding() instanceof Station station) {
                try{
                    station.vehicleArrives(this);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } else if (currentTile.getRoad() != null) {
            // Ha úton állunk, bejegyezzük magunkat a sávba
            currentTile.getRoad().vehicleEnters(this, this.currentDirection);
        }
    }

    public void setWorld(World world) {
        this.world = world;
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
        if (this.cargoNum == 0 && this.cargoType == null) {
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
        // Úton a következő megállóba
        if (!this.path.isEmpty()) {
            // Ellenőrizzük, hogy mehet-e előre
            boolean canGoOn = true;
            if (this.world.get(this.path.getFirst().x, this.path.getFirst().y).getBuilding() != null) {
                switch (world.get(this.path.getFirst().x, this.path.getFirst().y).getBuilding().getBuildingType()) {
                    case BuildingType.STATION:
                        if (((Station) world.get(this.path.getFirst().x, this.path.getFirst().y).getBuilding()).isOccupied()) {
                            canGoOn = false;
                        }
                        break;
                    case BuildingType.BUSSTOP:
                        if (((BusStop) world.get(this.path.getFirst().x, this.path.getFirst().y).getBuilding()).isOccupied()) {
                            canGoOn = false;
                        }
                        break;
                    default:
                        break;
                }
            } else if (this.world.get(this.path.getFirst().x, this.path.getFirst().y).getRoad() != null) {
                // Kiszámoljuk, milyen irányba halad éppen
                int relativeX = this.currentPlace.x - this.path.getFirst().x;
                int relativeY = this.currentPlace.y - this.path.getFirst().y;

                RoadDirection nextDirection = RoadDirection.NORTH;

                // Kiszámoljuk a jármű következő irányát
                switch (relativeX) {
                    case -1:
                        nextDirection = RoadDirection.EAST;
                        break;
                    case 0:
                        switch (relativeY) {
                            case -1:
                                nextDirection = RoadDirection.SOUTH;
                                break;
                            case 1:
                                nextDirection = RoadDirection.NORTH;
                                break;
                        }
                        break;
                    case 1:
                        nextDirection = RoadDirection.WEST;
                        break;
                }

                if (this.world.get(this.path.getFirst().x, this.path.getFirst().y).getRoad().isOccupied(nextDirection)) {
                    canGoOn = false;
                }
                System.out.println("A jármű következő iránya: " + nextDirection);
            }

            if (canGoOn) {
                System.out.println("A jármű halad tovább.");
                Point nextTile = this.path.removeFirst();

                // Jármű elhagyja a jelenlegi tile-t
                // Amennyiben megállóban van:
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
                    // Amennyiben úton van
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
            } else {
                System.out.println("Can't go on.");
            }

        // Ha elfogyott az út és nem üres a megállók listája
        } else if (!this.routeStops.isEmpty()) {
            // Ha van még hátralevő megállónk, léptetjük a stopIndexet
            if (this.stopIndex + 1 < this.routeStops.size()) {
                this.stopIndex++;
            // Ha nincs, de körúton van, visszaállítjuk a StopIndexet az elsőre
            } else if (this.isOnTour) {
                this.stopIndex = 0;
            } else if (this.stopIndex == this.routeStops.size() - 1) {
                this.movingForward = false;
            }
//            if(movingForward)
//            {
//                stopIndex++;
//                if(stopIndex >= routeStops.size())
//                {
//                    movingForward = false;
//                    stopIndex = routeStops.size() - 2;
//                }
//            } else {
//                stopIndex--;
//                if(stopIndex < 0)
//                {
//                    movingForward = true;
//                    stopIndex = 1;
//                }
//            }

            if (movingForward) {
                try {
                    findPath(this.routeStops.get(stopIndex));
                } catch (Exception e) {
                    System.err.println("Hiba az allomassal: " + e.getMessage());
                }
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
