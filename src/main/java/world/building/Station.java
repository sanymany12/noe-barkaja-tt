package world.building;

import world.resources.ResourceType;
import world.tile.Tile;
import world.vehicle.Vehicle;
import world.vehicle.VehicleType;
import world.building.BuildingType;

public class Station extends Building<Object, Object> {
    private Building building;
    private Vehicle vehicle;

    public Station(Tile tile, Building building) {
        super(tile);
        this.type = BuildingType.STATION;
        this.building = building;
        this.vehicle = null;
    }

    public void vehicleArrives(Vehicle vehicle) {
        this.vehicle = vehicle;
        if (this.building.getBuildingType() == BuildingType.CITY) {
            // TODO
        } else if (this.building.getBuildingType() == BuildingType.FARM) {
            if (this.vehicle.getVehicleType() == VehicleType.FOODTRUCK) {
                if (this.vehicle.isEmpty() || (!this.vehicle.isFull() && this.vehicle.getCargoType() == ResourceType.GRAIN)) {
                    this.vehicle.loadFrom(building);
                }
            }
        } else if (this.building.getBuildingType() == BuildingType.AGRICULTURALPLANT) {
            if (this.vehicle.getVehicleType() == VehicleType.FOODTRUCK) {
                if (this.vehicle.isEmpty() || (!this.vehicle.isFull() && this.vehicle.getCargoType() == ResourceType.FOOD)) {
                    this.vehicle.loadFrom(building);
                }
                else if (!this.vehicle.isEmpty() && this.vehicle.getCargoType() == ResourceType.GRAIN) {
                    this.vehicle.unloadTo(building);
                }
            }
        } else if (this.building.getBuildingType() == BuildingType.SILO) {
            if (this.vehicle.getVehicleType() == VehicleType.FOODTRUCK) {
                if (!this.vehicle.isEmpty() && this.vehicle.getCargoType() == ResourceType.FOOD) {
                    this.vehicle.unloadTo(building);
                }
            }
        } else if (this.building.getBuildingType() == BuildingType.ENCLOSURE) {
            if (this.vehicle.getVehicleType() == VehicleType.ANIMALTRUCK) {
                if (this.vehicle.isEmpty()) {
                    this.vehicle.loadFrom(building);
                }
                else {
                    this.vehicle.unloadTo(building);
                }
            }
        } else if (this.building.getBuildingType() == BuildingType.CLONINGFACILITY) {
            if (this.vehicle.getVehicleType() == VehicleType.ANIMALTRUCK) {
                if (this.vehicle.isEmpty()) {
                    this.vehicle.loadFrom(building);
                }
                else {
                    this.vehicle.unloadTo(building);
                }
            }
        } else if (this.building.getBuildingType() == BuildingType.RESEARCHLAB) {
            if (this.vehicle.getVehicleType() == VehicleType.ANIMALTRUCK) {
                if (this.vehicle.isEmpty()) {
                    this.vehicle.loadFrom(building);
                }
                else {
                    this.vehicle.unloadTo(building);
                }
            }
        }
    }

    public void vehicleLeaves() {
        this.vehicle = null;
    }
}
