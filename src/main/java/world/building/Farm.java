package world.building;

// Teszt komment

import world.World;

import java.util.concurrent.ThreadLocalRandom;

public class Farm extends Building<Integer,Integer> {
    private int grainMade;

    private final int MIN_GAIN = 20;
    private final int MAX_GAIN = 70;
    private final int CAPACITY = 200;

    public Farm(World world) {
        super(world);
        this.type = BuildingType.FARM;

        this.width = 3;
        this.height = 3;

        this.grainMade = 0;
    }

    // metódus az eddig termelt gabona mennyiségének lekérésére
    public int getGrainMade() {
        return this.grainMade;
    }

    // metódus a teljes kapacitás lekérésére
    public int getCapacity() {
        return this.CAPACITY;
    }

    private void grainGrows() {
        int today = ThreadLocalRandom.current().nextInt(MIN_GAIN, MAX_GAIN);

        if (this.grainMade + today <= this.CAPACITY) {
            this.grainMade = this.grainMade + today;
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
    }

    // TODO
    @Override
    public String getSpriteName() {
        return "farm";
    }
}
