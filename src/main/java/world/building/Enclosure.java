package world.building;

import world.resources.AnimalType;
import world.tile.Tile;
import world.vehicle.AnimalTruck;
import world.vehicle.Vehicle;

public class Enclosure extends Building<AnimalType, AnimalType> {
    private AnimalType species;

    public Enclosure(Tile tile) {
        super(tile);
        this.species = null;
    }

    public AnimalType getSpecies() {
        return this.species;
    }
}
