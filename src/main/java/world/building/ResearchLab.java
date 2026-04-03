package world.building;

import world.World;
import world.resources.AnimalType;
import java.util.List;

public class ResearchLab extends Building<AnimalType, List<AnimalType>> {
    public ResearchLab(World world) {
        super(world);
    }

    @Override
    public String getSpriteName() {
        return "spriteName";
    }
}
