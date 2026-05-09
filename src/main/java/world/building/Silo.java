package world.building;

import world.World;
import world.resources.ResourceType;
import world.vehicle.FoodTruck;

public class Silo extends Building<Integer,Integer> {
    private int numOfFood;
    private int plusCapacity;

    private Enclosure enclosure;

    private final int CAPACITY_INCREASE = 10;
    private final int CAPACITY_INCREASE_BASE_COST = 5000;
    private final int CAPACITY_INCREASE_COST_INCREMENT = 1000;

    private final int STARTING_STOCK = 20;
    private final int CAPACITY = 500;

    public Silo(World world) {
        super(world);

        this.type = BuildingType.SILO;
        this.enclosure = null;

        this.numOfFood = STARTING_STOCK;

        this.plusCapacity = 0;

        this.width = 2;
        this.height = 2;
    }

    public void setEnclosure(Enclosure enclosure) {
        this.enclosure = enclosure;
    }

    public Enclosure getEnclosure() {
        return this.enclosure;
    }

    public int getFoodConsumptionPerDay() {
        if (this.enclosure != null) {
            return this.enclosure.getNumOfAnimals();
        } else {
            return 0;
        }
    }

    public int getDaysUntilStarvation() {
        if (this.enclosure != null) {
            return this.enclosure.getDaysUntilStarvation();
        } else {
            return 0;
        }
    }

    // getter az itt tárolt étel mennyiségére
    public int getNumOfFood() {
        return this.numOfFood;
    }

    public int getRemainingCapacity() {
        return (this.CAPACITY + this.plusCapacity) - this.numOfFood;
    }

    public void increaseCapacity() {
        if (this.plusCapacity < this.CAPACITY_INCREASE * 50) {
            this.world.spendMoney(this.CAPACITY_INCREASE_BASE_COST + (this.plusCapacity / this.CAPACITY_INCREASE) * this.CAPACITY_INCREASE_COST_INCREMENT);
            this.plusCapacity = this.plusCapacity + this.CAPACITY_INCREASE;
        }
    }

    // étel érkezése teherautóról
    public void loadFromTruck(int load) throws Exception {
        if (this.getRemainingCapacity() < load) {
            throw new Exception("Nincs elég hely az ételnek!");
        }
        this.numOfFood = this.numOfFood + load;
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
        if (this.numOfFood == 0) {
            return "silo-empty";
        } else {
            return "silo";
        }
    }
}
