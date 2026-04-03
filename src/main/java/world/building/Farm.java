package world.building;

// Teszt komment

import world.World;

public class Farm extends Building<Integer,Integer> {
    public Farm(World world) {
        super(world);
    }

    @Override
    public String getSpriteName() {
        return "spriteName";
    }
}
