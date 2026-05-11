package world.building;

import world.World;
import world.tile.Tile;
import world.vehicle.VehicleType;

import java.util.ArrayList;
import java.util.List;

public abstract class Building<In, Out> {
    transient protected World world;

    protected int width;
    protected int height;

    protected BuildingType type;

    protected List<Station> stations;

    public Building(World world) {
        this.world = world;

        this.width = 0;
        this.height = 0;
        
        this.type = null;

        this.stations = new ArrayList<Station>();
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public BuildingType getBuildingType() {
        return this.type;
    }

    public void setStation(Station station) {
        this.stations.add(station);
    }

    public void removeStation(Station station) {
        this.stations.remove(station);
    }

    public void takeAnimalButton() throws Exception {
        for (int i = 0; i < this.stations.size(); i++) {
            if (this.stations.get(i).getVehicle() != null) {
                if (this.stations.get(i).getVehicle().getVehicleType() == VehicleType.ANIMALTRUCK) {
                    this.stations.get(i).getVehicle().loadFrom(this);
                }
            }
        }
    }

    public void takeResourceButton() throws Exception {
        for (int i = 0; i < this.stations.size(); i++) {
            if (this.stations.get(i).getVehicle() != null) {
                if (this.stations.get(i).getVehicle().getVehicleType() == VehicleType.FOODTRUCK) {
                    this.stations.get(i).getVehicle().loadFrom(this);
                }
            }
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public abstract void newDay() throws Exception;

    public abstract String getSpriteName();
}
