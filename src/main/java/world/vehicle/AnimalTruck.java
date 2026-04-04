package world.vehicle;

import world.World;
import world.resources.AnimalType;
import world.building.Building;
import world.tile.Point;

public class AnimalTruck extends Vehicle {
    private AnimalType cargoType;

    public AnimalTruck(World world, Point p) throws Exception {
        super(world, p);

        this.speed = 1;
        this.capacity = 1;
        this.costToOperate = 5;
        this.cargoType = null;
        this.type = VehicleType.ANIMALTRUCK;
        this.width = 0.5f;
        this.height = 0.5f;
    }

    @Override
    public void loadFrom(Building building) {

    }

    @Override
    public void unloadTo(Building building) {

    }

    @Override
    public String getSpriteName(){
        return "spriteName";
    }
}
