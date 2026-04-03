package world.building;

import world.World;
import world.tile.road.RoadDirection;

public class AgriculturalPlant extends Building<Integer,Integer> {
    public AgriculturalPlant(World world) {
        super(world);
    }

    @Override
    public String getSpriteName() {
        return "spriteName";
    }
}
