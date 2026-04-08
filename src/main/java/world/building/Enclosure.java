package world.building;

import world.World;
import world.resources.AnimalType;
import world.tile.Tile;
import world.vehicle.AnimalTruck;
import world.vehicle.Vehicle;

public class Enclosure extends Building<AnimalType, AnimalType> {
    private AnimalType species;
    private int numOfAnimals;
    private Silo silo;
    private boolean starving;

    private final double ANIMAL_MULTIPLIER = 1.2;
    private final int CAPACITY = 100;

    public Enclosure(World world, Silo silo) {
        super(world);

        this.type = BuildingType.ENCLOSURE;

        this.silo = silo;

        this.width = 2;
        this.height = 1;

        this.species = null;
        this.numOfAnimals = 0;

        this.starving = false;
    }

    public AnimalType getSpecies() {
        return this.species;
    }

    public int getNumOfAnimals() {
        return this.numOfAnimals;
    }

    public boolean hasAnimals() {
        return this.numOfAnimals > 0 && this.species != null;
    }

    public boolean isFull() {
        return this.numOfAnimals == this.CAPACITY;
    }

    public void newSpeciesArrives(AnimalType a) {
        this.species = a;
    }

    public void receiveAnimal() {
        this.numOfAnimals = this.numOfAnimals + 1;
    }

    public void takeAnimal() {
        this.numOfAnimals = this.numOfAnimals - 1;
        if (this.numOfAnimals == 0) {
            this.species = null;
        }
    }

    private void consumeFood() {
        if (!this.silo.consumeFood(numOfAnimals)) {
            this.starving = true;
        } else {
            this.starving = false;
        }
    }

    private void animalBorn() {
        int animalsBornNum;
        if (this.numOfAnimals < 2) {
            animalsBornNum = 0;
        } else {
            animalsBornNum = (int) Math.ceil(this.numOfAnimals * this.ANIMAL_MULTIPLIER);
        }
        if (!this.starving) {
            if (this.numOfAnimals + animalsBornNum <= this.CAPACITY) {
                this.numOfAnimals = this.numOfAnimals + animalsBornNum;
            }
            else {
                this.numOfAnimals = this.CAPACITY;
            }
        }
    }

    @Override
    public void newDay() {
        this.consumeFood();
        if (!this.isFull()) {
            this.animalBorn();
        }
    }

    @Override
    public String getSpriteName() {
        // TODO
        return "spriteName";
    }
}
