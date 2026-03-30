package world.building;

import world.World;
import world.resources.AnimalType;
import world.tile.Tile;
import world.vehicle.AnimalTruck;
import world.vehicle.Vehicle;

public class Enclosure extends Building<AnimalType, AnimalType> {
    private AnimalType species;

    public Enclosure(World world, Tile tile) {
        super(world, tile);
        this.species = null;
    }

    public AnimalType getSpecies() {
        return this.species;
    }
}
