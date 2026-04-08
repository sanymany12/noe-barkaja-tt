package world.vehicle;

import world.World;
import world.building.Building;
import world.building.BusStop;
import world.resources.PersonType;
import world.tile.Point;
import world.tile.road.RoadDirection;

public class Bus extends Vehicle {
    private PersonType cargoType;

    public Bus(World world, Point p) throws Exception {
        super(world, p);

        this.speed = 1;
        this.capacity = 10;
        this.costToOperate = 10;

        this.cargoType = PersonType.PERSON;

        this.type = VehicleType.BUS;

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
        String spriteName = "bus";
        switch(this.currentDirection) {
            case RoadDirection.NORTH:
                spriteName = spriteName.concat("-n");
                break;
            case RoadDirection.EAST:
                spriteName = spriteName.concat("-e");
                break;
            case RoadDirection.SOUTH:
                spriteName = spriteName.concat("-s");
                break;
            case RoadDirection.WEST:
                spriteName = spriteName.concat("-w");
                break;
        }
        return spriteName;
    }
}
