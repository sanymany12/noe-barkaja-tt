package world.vehicle;

import world.World;
import world.building.Building;
import world.resources.ResourceType;

public class FoodTruck extends Vehicle {
    private ResourceType cargoType;

    public FoodTruck(World world, int x, int y) throws Exception {
        super(world, x, y);

        this.speed = 1;
        this.capacity = 5;
        this.costToOperate = 5;
        this.cargoType = null;
    }

    @Override
    public void loadFrom(Building building) {

    }

    @Override
    public void unloadFrom(Building building) {

    }
}
