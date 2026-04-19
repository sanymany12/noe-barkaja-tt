package world.vehicle;

import world.World;
import world.building.Building;
import world.building.BuildingType;
import world.building.BusStop;
import world.resources.PersonType;
import world.tile.Point;
import world.tile.road.RoadDirection;

public class Bus extends Vehicle {


    private Point destination;

    public Bus(World world, Point p) throws Exception {
        super(world, p);


        this.capacity = 10;
        this.costToOperate = 10;

        this.cargoType = null;

        this.type = VehicleType.BUS;

        this.width = 0.5f;
        this.height = 0.5f;
    }

    @Override
    public void loadFrom(Building building) {
        if (building.getBuildingType() == BuildingType.BUSSTOP && this.cargoNum == 0) {
            if (((BusStop) building).isStart()) {
                this.cargoNum = ((BusStop) building).getNumOfPeople();
                ((BusStop) building).loadOntoBus();
            }
        }
    }

    @Override
    public void unloadTo(Building building) {
        if (building.getBuildingType() == BuildingType.BUSSTOP) {
            if (((BusStop) building).isStop() && this.cargoNum > 0) {
                ((BusStop) building).peopleArrived(this.cargoNum);
                this.cargoNum = 0;
            }
        }
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
