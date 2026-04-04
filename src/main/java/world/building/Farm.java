package world.building;

// Teszt komment

import world.World;

import java.util.Random;

public class Farm extends Building<Integer,Integer> {
    private int grainMade;

    private Random random;

    private final int MIN_GAIN = 20;
    private final int MAX_GAIN = 70;
    private final int CAPACITY = 200;

    public Farm(World world) {
        super(world);

        this.grainMade = 0;
        this.random = new Random();
    }

    public int getGrainMade() {
        return this.grainMade;
    }

    public int getCapacity() {
        return this.CAPACITY;
    }

    public void newDay() {
        if (this.grainMade != this.CAPACITY) {
            this.grainGrows();
        }
    }

    private void grainGrows() {
        int today = random.nextInt(MIN_GAIN, MAX_GAIN);

        if (this.grainMade + today <= this.CAPACITY) {
            this.grainMade = this.grainMade + today;
        } else {
            this.grainMade = this.CAPACITY;
        }
    }

    @Override
    public String getSpriteName() {
        return "spriteName";
    }
}
