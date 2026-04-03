package world.vehicle;

import world.World;
import world.building.Building;
import world.resources.ResourceType;
import world.tile.Point;

public class FoodTruck extends Vehicle {
    private ResourceType cargoType;

    public FoodTruck(World world, Point p) throws Exception {
        super(world, p);

        this.speed = 1;
        this.capacity = 5;
        this.costToOperate = 5;
        this.cargoType = null;
        this.type = VehicleType.FOODTRUCK;
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
