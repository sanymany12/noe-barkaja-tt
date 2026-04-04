package world.building;

import world.World;
import world.resources.AnimalType;
import java.util.List;

public class CloningFacility extends Building<AnimalType, List<AnimalType>> {
    public CloningFacility(World world) {
        super(world);
    }

    @Override
    public String getSpriteName() {
        return "spriteName";
    }
}
