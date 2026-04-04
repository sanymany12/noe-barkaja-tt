package world.building;

import world.World;
import world.tile.road.RoadDirection;

public class AgriculturalPlant extends Building<Integer,Integer> {
    private int incomingGrain;
    private int outgoingFood;

    private final int BATCH = 20;
    private final int CAPACITY_IN = 400;
    private final int CAPACITY_OUT = 100;

    public AgriculturalPlant(World world) {
        super(world);

        this.incomingGrain = 0;
        this.outgoingFood = 0;
    }

    public void newBatchMade() {
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

    public int getNumOfDaysToBeDone() {
        int days = (int) Math.ceil(this.incomingGrain / this.BATCH);
        return days;
    }

    public int getIncomingGrain() {
        return this.incomingGrain;
    }

    public int getOutgoingFood() {
        return this.outgoingFood;
    }

    public int getCAPACITY_IN() {
        return this.CAPACITY_IN;
    }

    public int getCAPACITY_OUT() {
        return this.CAPACITY_OUT;
    }

    @Override
    public String getSpriteName() {
        return "spriteName";
    }
}
