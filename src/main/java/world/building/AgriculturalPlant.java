package world.building;

import world.World;
import world.resources.ResourceType;
import world.tile.road.RoadDirection;
import world.vehicle.FoodTruck;

import java.util.ArrayList;
import java.util.List;

public class AgriculturalPlant extends Building<Integer,Integer> {
    private int incomingGrain;
    private int outgoingFood;

    private int productionBoost;
    private int boostDay;

    private final int PRODUCTION_BOOST = 10;
    private final int PRODUCTION_BOOST_DAYS = 7;
    private final int PRODUCTION_BOOST_COST = 3000;
    private final int BATCH = 20;
    private final int CAPACITY_IN = 500;
    private final int CAPACITY_OUT = 300;

    public AgriculturalPlant(World world) {
        super(world);

        this.type = BuildingType.AGRICULTURALPLANT;

        this.height = 2;
        this.width = 4;

        this.productionBoost = 0;
        this.boostDay = this.PRODUCTION_BOOST_DAYS;

        this.incomingGrain = 0;
        this.outgoingFood = 0;
    }

    // getter az itt tartott gabona mennyiségének
    public int getIncomingGrain() {
        return this.incomingGrain;
    }

    // getter a gyártott étel mennyiségének
    public int getOutgoingFood() {
        return this.outgoingFood;
    }

    public int getRemainingCapacityIn() {
        return this.CAPACITY_IN - this.incomingGrain;
    }

    // getter a teljes gabonakapacitásnak
    public int getCapacityIn() {
        return this.CAPACITY_IN;
    }

    // getter a teljes ételkapacitásnak
    public int getCapacityOut() {
        return this.CAPACITY_OUT;
    }

    // getter annak, hogy hány nap, amíg még a teljes beküldött gabonakészletet feldolgozza
    public int getNumOfDaysToBeDone() {
        int days = (int) Math.ceil(this.incomingGrain / this.BATCH);
        return days;
    }

    public void boostProduction() {
        this.world.spendMoney(this.PRODUCTION_BOOST_COST);
        this.productionBoost = this.PRODUCTION_BOOST;
        this.boostDay = this.boostDay - this.PRODUCTION_BOOST_DAYS;
    }

    public int getDaysLeftOfBoost() {
        return this.PRODUCTION_BOOST_DAYS - this.boostDay;
    }

    // új adag étel készül
    private void newBatchMade() {
        int capacityleft = this.CAPACITY_OUT - this.outgoingFood;
        if (capacityleft >= this.BATCH) {
            if (this.incomingGrain > this.BATCH) {
                this.incomingGrain = this.incomingGrain - this.BATCH;
                this.outgoingFood = this.outgoingFood + this.BATCH;
            } else {
                this.outgoingFood = this.outgoingFood + this.incomingGrain;
                this.incomingGrain = 0;
            }
        } else {
            if (this.incomingGrain > capacityleft) {
                this.incomingGrain = this.incomingGrain - capacityleft;
                this.outgoingFood = this.outgoingFood + capacityleft;
            } else {
                this.outgoingFood = this.outgoingFood + this.incomingGrain;
                this.incomingGrain = 0;
            }
        }
    }

    // összes étel teherautóra való töltése
    public void loadOntoTruck() {
        this.outgoingFood = 0;
    }

    // az ételből bizonyos mennyiség teherautóra való töltése
    public void loadOntoTruck(int load) throws Exception {
        if (this.outgoingFood < load) {
            throw new Exception("Nincs elég étel az üzemben!");
        }
        this.outgoingFood = this.outgoingFood - load;
    }

    // gabona érkezése egy teherautóról
    public void loadFromTruck(int load) throws Exception {
        if (this.getRemainingCapacityIn() < load) {
            throw new Exception("Nincs elég hely az üzemben!");
        }
        this.incomingGrain = this.incomingGrain + load;
    }

    // napi update függvény
    @Override
    public void newDay() {
        if (this.incomingGrain > 0 && this.outgoingFood <= this.CAPACITY_OUT) {
            this.newBatchMade();
        }
    }

    @Override
    public String getSpriteName() {
        return "agriculturalplant";
    }
}
