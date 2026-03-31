package world.vehicle;

import world.World;
import world.building.Building;
import world.building.BusStop;
import world.resources.PersonType;

public class Bus extends Vehicle {
    private PersonType cargoType;

    public Bus(World world, int x, int y) throws Exception {
        super(world, x, y);

        this.speed = 1;
        this.capacity = 10;
        this.costToOperate = 10;
        this.cargoType = PersonType.PERSON;
        this.type = VehicleType.BUS;
    }

    @Override
    public void loadFrom(Building building) {
        if (building instanceof BusStop) {

        }
    }

    @Override
    public void unloadTo(Building building) {

    }
}
