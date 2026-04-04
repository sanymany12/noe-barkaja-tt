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

    public void consumeFood() {
        if (!this.silo.consumeFood(numOfAnimals)) {
            this.starving = true;
        } else {
            this.starving = false;
        }
    }

    public void animalBorn() {
        if (this.numOfAnimals < this.CAPACITY && !this.starving) {
            this.numOfAnimals++;
        }
    }

    @Override
    public String getSpriteName() {
        return "spriteName";
    }
}
