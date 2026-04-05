package world.building;

import world.World;
import world.resources.AnimalType;
import java.util.List;

public class CloningFacility extends Building<AnimalType, List<AnimalType>> {
    private AnimalType receivedAnimal;
    private int animalsMade;

    private boolean isCloning;
    private int daysSinceStarted;

    private final int DAYS_TO_CLONE = 10;
    private final int COST_OF_CLONING = 200;

    public CloningFacility(World world) {
        super(world);

        this.type = BuildingType.CLONINGFACILITY;

        this.receivedAnimal = null;
        this.animalsMade = 0;

        this.isCloning = false;
        this.daysSinceStarted = 0;
    }

    // getter a klónozás árának
    public int getCostOfCloning() {
        return this.COST_OF_CLONING;
    }

    // állat fogadásának metódusa
    // igaz, ha sikeres, hamis, ha nem
    public boolean receiveAnimal(AnimalType animal) {
        if (this.receivedAnimal == null) {
            this.receivedAnimal = animal;
            this.animalsMade = 1;
            return true;
        } else {
            return false;
        }
    }

    // klónozás megkezdésének metódusa
    // igazat ad vissza, ha sikeres, hamisat, ha nem
    public boolean startCloning() {
        if (this.hasAnimal()) {
            this.isCloning = true;
            return true;
        } else {
            return false;
        }
    }

    // segédfüggvény arra, van-e itt állat
    private boolean hasAnimal() {
        if (this.receivedAnimal != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void newDay() {
        if (this.isCloning) {
            this.daysSinceStarted++;
        }
        if (this.daysSinceStarted == this.DAYS_TO_CLONE) {
            this.animalsMade++;
            this.isCloning = false;
        }
    }

    @Override
    public String getSpriteName() {
        return "spriteName";
    }
}
