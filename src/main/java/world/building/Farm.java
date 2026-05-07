package world.building;

// Teszt komment

import world.World;

import java.util.concurrent.ThreadLocalRandom;

public class Farm extends Building<Integer,Integer> {
    private int grainMade;
    private int productionBoost;
    private int boostDay;

    private final int PRODUCTION_BOOST_DAYS = 7;
    private final int PRODUCTION_BOOST_COST = 2000;
    private final int PRODUCTION_BOOST = 20;
    private final int MIN_GAIN = 20;
    private final int MAX_GAIN = 70;
    private final int CAPACITY = 400;

    public Farm(World world) {
        super(world);
        this.type = BuildingType.FARM;

        this.width = 3;
        this.height = 3;

        this.grainMade = 0;
        this.productionBoost = 0;
        this.boostDay = this.PRODUCTION_BOOST_DAYS;
    }

    // metódus az eddig termelt gabona mennyiségének lekérésére
    public int getGrainMade() {
        return this.grainMade;
    }

    // metódus a teljes kapacitás lekérésére
    public int getCapacity() {
        return this.CAPACITY;
    }

    public boolean isBoosted() { return this.boostDay < this.PRODUCTION_BOOST_DAYS; }

    public int getBoostAmount() { return this.PRODUCTION_BOOST; }

    public void boostProduction() {
        this.world.spendMoney(this.PRODUCTION_BOOST_COST);
        this.boostDay = this.boostDay - this.PRODUCTION_BOOST_DAYS;
        this.productionBoost = this.PRODUCTION_BOOST;
    }

    public int getDaysLeftOfBoost() {
        return this.PRODUCTION_BOOST_DAYS - this.boostDay;
    }

    public int getBoostCost() { return this.PRODUCTION_BOOST_COST; }

    private void grainGrows() {
        int today = ThreadLocalRandom.current().nextInt(MIN_GAIN, MAX_GAIN);

        if (this.grainMade + (today + this.productionBoost) <= this.CAPACITY) {
            this.grainMade = this.grainMade + (today + this.productionBoost);
        } else {
            this.grainMade = this.CAPACITY;
        }
    }

    // összes gabona teherautóra való töltése
    public void loadOntoTruck() {
        this.grainMade = 0;
    }

    // a gabona egy része teherautóra való töltése
    public void loadOntoTruck(int load) throws Exception {
        if (this.grainMade < load) {
            throw new Exception("Nincs elég gabona!");
        }
        this.grainMade = this.grainMade - load;
    }

    // új nap metódus, minden nap termel gabonát
    @Override
    public void newDay() {
        if (this.grainMade != this.CAPACITY) {
            this.grainGrows();
        }
        if (this.boostDay != this.PRODUCTION_BOOST_DAYS) {
            this.boostDay++;
            if (this.boostDay == this.PRODUCTION_BOOST_DAYS) {
                this.productionBoost = 0;
            }
        }
    }

    // TODO
    @Override
    public String getSpriteName() {
        return "farm";
    }
}
