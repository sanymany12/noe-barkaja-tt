package world.vehicle;

import world.World;
import world.building.Building;
import world.building.BusStop;
import world.resources.PersonType;
import world.tile.Point;

public class Bus extends Vehicle {
    private PersonType cargoType;

    public Bus(World world, Point p) throws Exception {
        super(world, p);

        this.speed = 1;
        this.capacity = 10;
        this.costToOperate = 10;
        this.cargoType = PersonType.PERSON;
        this.type = VehicleType.BUS;
    }

    @Override
    public void loadFrom(Building building) {
        
    }

    @Override
    public void unloadTo(Building building) {

    }
}
