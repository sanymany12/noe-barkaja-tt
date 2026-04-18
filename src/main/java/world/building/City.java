package world.building;

import world.World;
import world.resources.AnimalType;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class City extends Building<AnimalType, Integer> {
    private AnimalType orderedAnimal;
    private int orderedAmount;

    private int deliveredAmount;

    private int daysSinceLastOrder;

    private final int MAX_TO_BE_ORDERED = 10;
    private final int MIN_TO_BE_ORDERED = 1;
    private final int DAYS_TIL_NEW_ORDER = 14;

    public City (World world) {
        super(world);
        this.type = BuildingType.CITY;

        this.orderedAnimal = null;
        this.orderedAmount = 0;

        this.deliveredAmount = 0;
        this.daysSinceLastOrder = 0;

        this.newOrder();
    }

    public AnimalType getOrderedAnimal() {
        return this.orderedAnimal;
    }

    public int getOrderedAmount() {
        return this.orderedAmount;
    }

    public boolean hasOrder() {
        return this.orderedAnimal != null && this.orderedAmount > 0;
    }

    public int getOrderWorth() {
        return this.orderedAnimal.getValue() * this.orderedAmount * 5;
    }

    public void receiveAnimal() {
        this.deliveredAmount = this.deliveredAmount + 1;
        if (this.deliveredAmount == this.orderedAmount) {
            this.world.receiveMoney(this.getOrderWorth());
            this.orderedAnimal = null;
            this.orderedAmount = 0;
            this.deliveredAmount = 0;
        }
    }

    private void newOrder() {
        if (this.deliveredAmount == 0) {
            int animalId = ThreadLocalRandom.current().nextInt(1, 10);
            switch (animalId) {
                case 1:
                    this.orderedAnimal = AnimalType.BEAR;
                    break;
                case 2:
                    this.orderedAnimal = AnimalType.CAPYBARA;
                    break;
                case 3:
                    this.orderedAnimal = AnimalType.CAT;
                    break;
                case 4:
                    this.orderedAnimal = AnimalType.FISH;
                    break;
                case 5:
                    this.orderedAnimal = AnimalType.GUINEAPIG;
                    break;
                case 6:
                    this.orderedAnimal = AnimalType.HORSE;
                    break;
                case 7:
                    this.orderedAnimal = AnimalType.PIG;
                    break;
                case 8:
                    this.orderedAnimal = AnimalType.RACOON;
                    break;
                case 9:
                    this.orderedAnimal = AnimalType.SEAHORSE;
                    break;
            }
            this.orderedAmount = ThreadLocalRandom.current().nextInt(this.MIN_TO_BE_ORDERED, this.MAX_TO_BE_ORDERED + 1);
            this.daysSinceLastOrder = 0;
        }
    }

    @Override
    public void newDay() {
        this.daysSinceLastOrder = this.daysSinceLastOrder + 1;
        if (this.daysSinceLastOrder >= this.DAYS_TIL_NEW_ORDER && (this.deliveredAmount == 0)) {
            this.newOrder();
        }
    }

    // TODO
    @Override
    public String getSpriteName() {
        return "building";
    }
}
