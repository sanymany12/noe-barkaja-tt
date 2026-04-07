package world.vehicle;

import world.World;
import world.resources.AnimalType;
import world.building.Building;
import world.resources.ResourceType;
import world.tile.Point;
import world.tile.road.RoadDirection;

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
        String spriteName = "animaltruck";
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
        if (this.cargoType != null) {
            switch(this.cargoType) {
                case AnimalType.BEAR:
                    spriteName = spriteName.concat("-bear");
                    break;
                case AnimalType.CAPYBARA:
                    spriteName = spriteName.concat("-capybara");
                    break;
                case AnimalType.CAT:
                    spriteName = spriteName.concat("-cat");
                    break;
                case AnimalType.FISH:
                    spriteName = spriteName.concat("-fish");
                    break;
                case AnimalType.GUINEAPIG:
                    spriteName = spriteName.concat("-guineapig");
                    break;
                case AnimalType.HORSE:
                    spriteName = spriteName.concat("-horse");
                    break;
                case AnimalType.PIG:
                    spriteName = spriteName.concat("-pig");
                    break;
                case AnimalType.RACOON:
                    spriteName = spriteName.concat("-racoon");
                    break;
                case AnimalType.SEAHORSE:
                    spriteName = spriteName.concat("-seahorse");
                    break;
            }
        }
        return spriteName;
    }
}
