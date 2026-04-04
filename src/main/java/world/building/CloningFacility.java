package world.building;

import world.World;
import world.resources.AnimalType;
import java.util.List;

public class CloningFacility extends Building<AnimalType, List<AnimalType>> {
    private AnimalType receivedAnimal;
    private boolean doneCloning;

    private boolean isCloning;
    private int daysSinceStarted;

    private final int DAYS_TO_CLONE = 10;
    private final int COST_OF_CLONING = 200;

    public CloningFacility(World world) {
        super(world);

        this.type = BuildingType.CLONINGFACILITY;

        this.receivedAnimal = null;
        this.doneCloning = false;

        this.isCloning = false;
        this.daysSinceStarted = 0;
    }

    public boolean receiveAnimal(AnimalType animal) {
        if (this.receivedAnimal == null) {
            this.receivedAnimal = animal;
            return true;
        } else {
            return false;
        }
    }

    public void startCloning() {
        if (this.hasAnimal() && !this.doneCloning) {
            this.isCloning = true;
        }
    }

    public boolean hasAnimal() {
        if (this.receivedAnimal != null) {
            return true;
        } else {
            return false;
        }
    }

    public void newDay() {
        if (this.isCloning) {
            this.daysSinceStarted++;
        }
        if (this.daysSinceStarted == this.DAYS_TO_CLONE) {
            this.doneCloning = true;
            this.isCloning = false;
        }
    }

    public AnimalType takeAnimals() {
        if (this.doneCloning) {
            this.doneCloning = false;
            AnimalType animal = this.receivedAnimal;
            this.receivedAnimal = null;
            return animal;
        } else {
            return null;
        }
    }

    public int getCostOfCloning() {
        return this.COST_OF_CLONING;
    }

    @Override
    public String getSpriteName() {
        return "spriteName";
    }
}
