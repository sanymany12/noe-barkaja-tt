package world.building;

import world.World;

public class Silo extends Building<Integer,Integer> {
    public Silo(World world) {
        super(world);
    }

    @Override
    public String getSpriteName() {
        return "spriteName";
    }
}
