package world.vehicle;

import world.World;
import world.building.Building;
import world.resources.ResourceType;
import world.tile.Point;
import world.tile.road.RoadDirection;

public class FoodTruck extends Vehicle {
    private ResourceType cargoType;

    private int currentCargoNum;

    private final int CAPACITY = 10;
    private final int COST_TO_BUY = 2000;
    private final int COST_TO_SELL = 1000;

    public FoodTruck(World world, Point p) throws Exception {
        super(world, p);

        this.speed = 1;
        this.capacity = 5;
        this.costToOperate = 5;

        this.cargoType = null;

        this.type = VehicleType.FOODTRUCK;

        this.width = 0.5f;
        this.height = 0.5f;

        this.currentCargoNum = 0;
    }

    public ResourceType getCargoType() {
        return this.cargoType;
    }

    public int getCurrentCargoNum() {
        return this.currentCargoNum;
    }

    public int getCapacity() {
        return this.CAPACITY;
    }

    public int getCostToBuy() {
        return this.COST_TO_BUY;
    }

    public boolean hasCargo() {
        if (this.currentCargoNum > 0 && this.cargoType != null) {
            return true;
        } else {
            return false;
        }
    }

    public void addCargo(ResourceType type, int n) throws Exception {
        if (this.currentCargoNum + n > this.CAPACITY) {
            throw new Exception("Can't take that much cargo!");
        } else {
            if (!this.hasCargo()) {
                this.cargoType = type;
            }
            if (this.cargoType != type) {
                throw new Exception("Cargo types are mismatched!");
            } else {
                this.currentCargoNum = this.currentCargoNum + n;
            }
        }
    }

    public void decreaseCargo(int n) throws Exception {
        if (this.currentCargoNum - n < 0) {
            throw new Exception("Don't have that much cargo!");
        } else if (this.currentCargoNum == n) {
            this.emptyCargo();
        } else {
            this.currentCargoNum = this.currentCargoNum - n;
        }
    }

    public void emptyCargo() {
        this.currentCargoNum = 0;
        this.cargoType = null;
    }

    @Override
    public void loadFrom(Building building) {

    }

    @Override
    public void unloadTo(Building building) {

    }

    @Override
    public String getSpriteName(){
        String spriteName = "foodtruck";
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
        if (this.currentDirection != RoadDirection.SOUTH && this.currentCargoNum != 0) {
            switch(this.cargoType) {
                case ResourceType.GRAIN:
                    spriteName = spriteName.concat("-grain");
                    break;
                case ResourceType.FOOD:
                    spriteName = spriteName.concat("-food");
                    break;
            }
        }
        return spriteName;
    }
}
