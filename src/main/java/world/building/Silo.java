package world.building;

import world.World;
import world.resources.ResourceType;
import world.vehicle.FoodTruck;

public class Silo extends Building<Integer,Integer> {
    private int numOfFood;

    private final int STARTING_STOCK = 20;
    private final int CAPACITY = 500;

    public Silo(World world) {
        super(world);

        this.type = BuildingType.SILO;

        this.numOfFood = STARTING_STOCK;
    }

    // getter az itt tárolt étel mennyiségére
    public int getNumOfFood() {
        return this.numOfFood;
    }

    // metódus az ide érkező étel lekezelésére
    public void loadFrom(FoodTruck truck) throws Exception {
        if (truck.getCargoType() != ResourceType.FOOD) {
            throw new Exception("Can't load from a truck that doesn't have food!");
        } else {
            int canTake = this.CAPACITY - this.numOfFood;
            if (canTake <= truck.getCurrentCargoNum()) {
                this.numOfFood = this.numOfFood + canTake;
                truck.decreaseCargo(canTake);
            } else {
                this.numOfFood = this.numOfFood + truck.getCurrentCargoNum();
                truck.emptyCargo();
            }
        }
    }

    // a boolean visszaadja, volt-e elég étel a silóban
    public boolean consumeFood(int amount) {
        if (amount <= numOfFood) {
            numOfFood = numOfFood - amount;
            return true;
        } else {
            numOfFood = 0;
            return false;
        }
    }

    @Override
    public void newDay() {
        // nem csinál semmit :(
    }

    @Override
    public String getSpriteName() {
        return "spriteName";
    }
}
