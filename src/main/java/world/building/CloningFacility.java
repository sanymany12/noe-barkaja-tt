package world.building;

import world.World;
import world.resources.AnimalType;
import java.util.List;

public class CloningFacility extends Building<AnimalType, List<AnimalType>> {
    private AnimalType receivedAnimal;
    private boolean doneCloning;

    private final int DAYS_TO_CLONE = 10;
    private final int COST_OF_CLONING = 200;

    public CloningFacility(World world) {
        super(world);

        this.type = BuildingType.CLONINGFACILITY;

        this.receivedAnimal = null;
        this.doneCloning = false;
    }

    public boolean receiveAnimal(AnimalType animal) {
        if (this.receivedAnimal == null) {
            this.receivedAnimal = animal;
            return true;
        } else {
            return false;
        }
    }



    @Override
    public String getSpriteName() {
        return "spriteName";
    }
}
