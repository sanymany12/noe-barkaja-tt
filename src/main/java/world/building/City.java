package world.building;

import world.World;
import world.resources.AnimalType;

public class City extends Building<AnimalType, Integer> {
    public City(World world) {
        super(world);
    }

    @Override
    public void newDay() {

    }

    @Override
    public String getSpriteName() {
        return "spriteName";
    }
}
