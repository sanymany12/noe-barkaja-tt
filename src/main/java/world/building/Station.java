package world.building;

import world.resources.ResourceType;
import world.World;
import world.tile.Point;
import world.tile.Tile;
import world.vehicle.Vehicle;
import world.vehicle.VehicleType;
import world.building.BuildingType;
import world.tile.road.RoadDirection;

public class Station extends Building<Object, Object> {
    private Building building;
    private Vehicle vehicle;

    transient private Tile connectedRoad;
    private Point savedConnectedRoad;
    private RoadDirection direction;

    private boolean isPreBuilt;

    private final int COST_TO_BUILD = 300;

    public Station(World world, Building building, RoadDirection dir, boolean isPreBuilt) {
        super(world);
        this.type = BuildingType.STATION;
        this.width = 1;
        this.height = 1;
        this.building = building;
        this.vehicle = null;
        this.connectedRoad = null;
        this.savedConnectedRoad = null;
        this.direction = dir;
        this.isPreBuilt = isPreBuilt;
    }
    //connectedRoad és vehicle visszaállítása
    public void initAfterLoad(){
        connectedRoad = world.get(savedConnectedRoad.x, savedConnectedRoad.y);
    }
    // getter a megállóhoz tartozó épület irányának
    public RoadDirection getDirection() {
        return this.direction;
    }

    // getter az ehhez tartozó útnak
    public Tile getConnectedRoad() {
        return this.connectedRoad;
    }

    public int getCostToBuild() {
        return this.COST_TO_BUILD;
    }

    public boolean getIsPreBuilt() {
        return this.isPreBuilt;
    }

    // setter az ehhez tartozó útnak
    public void setConnectedRoad(Tile t) {
        this.connectedRoad = t;
        this.savedConnectedRoad = connectedRoad.getCoordinate();
    }

    public void vehicleArrives(Vehicle vehicle) throws Exception {
        this.vehicle = vehicle;

        if(this.vehicle == null) return;

        switch (this.vehicle.getVehicleType()) {
            case VehicleType.FOODTRUCK:
                switch (this.building.getBuildingType()) {
                    case BuildingType.FARM:
                        if (this.vehicle.isEmpty() || (!this.vehicle.isFull() && this.vehicle.getCargoType() == ResourceType.GRAIN)) {
                            this.vehicle.loadFrom(building);
                        }
                        break;
                    case BuildingType.AGRICULTURALPLANT:
                        if (this.vehicle.isEmpty() || (!this.vehicle.isFull() && this.vehicle.getCargoType() == ResourceType.FOOD)) {
                            this.vehicle.loadFrom(building);
                        }
                        if (!this.vehicle.isEmpty() && this.vehicle.getCargoType() == ResourceType.GRAIN) {
                            this.vehicle.unloadTo(building);
                        }
                        break;
                    case BuildingType.SILO:
                        if (!this.vehicle.isEmpty() && this.vehicle.getCargoType() == ResourceType.FOOD) {
                            this.vehicle.unloadTo(building);
                        }
                        break;
                }
                break;
            case VehicleType.ANIMALTRUCK:
                switch (this.building.getBuildingType()) {
                    case BuildingType.CITY:
                        // TODO
                        break;
                    case BuildingType.ENCLOSURE, BuildingType.RESEARCHLAB, BuildingType.CLONINGFACILITY:
                        if (this.vehicle.isEmpty()) {
                            this.vehicle.loadFrom(building);
                        }
                        else {
                            this.vehicle.unloadTo(building);
                        }
                        break;
                }
        }
    }

    public void vehicleLeaves() {
        this.vehicle = null;
    }

    public boolean isOccupied() {
        return this.vehicle != null;
    }

    public Vehicle getVehicle() {
        return this.vehicle;
    }

    @Override
    // Újra triggereljük a járműérkezést, ha esetleg valami termelés történt volna???
    public void newDay() throws Exception {
        if(this.vehicle != null)
        {
            this.vehicleArrives(this.vehicle);
        }
    }

    @Override
    public String getSpriteName() {
        String spriteName = "industrial-stop-";
        switch (this.direction) {
            case RoadDirection.NORTH:
                spriteName = spriteName.concat("n");
                break;
            case RoadDirection.EAST:
                spriteName = spriteName.concat("e");
                break;
            case RoadDirection.WEST:
                spriteName = spriteName.concat("w");
                break;
            case RoadDirection.SOUTH:
                spriteName = spriteName.concat("s");
                break;
        }
        return spriteName;
    }
}
