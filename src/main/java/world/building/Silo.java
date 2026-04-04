package world.building;

import world.World;

public class Silo extends Building<Integer,Integer> {
    private int numOfFood;

    private final int STARTING_STOCK = 20;
    private final int CAPACITY = 500;

    public Silo(World world) {
        super(world);

        this.numOfFood = STARTING_STOCK;
    }

    public int getNumOfFood() {
        return this.numOfFood;
    }

    // A boolean visszaadja, volt-e elég étel a silóban.
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
    public String getSpriteName() {
        return "spriteName";
    }
}
