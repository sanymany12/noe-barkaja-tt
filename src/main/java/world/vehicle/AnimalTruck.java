package world.vehicle;

import world.World;
import world.resources.AnimalType;
import world.building.Building;

public class AnimalTruck extends Vehicle {
    private AnimalType cargoType;

    public AnimalTruck(World world, int x, int y) throws Exception {
        super(world, x, y);

        this.speed = 1;
        this.capacity = 1;
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
