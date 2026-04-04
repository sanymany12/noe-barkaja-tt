package world.building;

import world.World;
import world.resources.AnimalType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResearchLab extends Building<AnimalType, List<AnimalType>> {
    private AnimalType receivedAnimal1;
    private AnimalType receivedAnimal2;
    private AnimalType discoveredAnimal;

    private boolean researchHappening;
    private int daysSinceResearchStarted;

    private final int COST_OF_RESEARCH = 500;
    private final int RESEARCH_DAYS = 5;

    public ResearchLab(World world) {
        super(world);

        this.type = BuildingType.RESEARCHLAB;

        this.receivedAnimal1 = null;
        this.receivedAnimal2 = null;
        this.discoveredAnimal = null;

        this.researchHappening = false;
        this.daysSinceResearchStarted = 0;
    }

    public void receiveAnimal(AnimalType animal) {
        if (this.receivedAnimal1 == null) {
            this.receivedAnimal1 = animal;
        } else {
            if (this.receivedAnimal1 != animal && this.receivedAnimal2 == null) {
                this.receivedAnimal2 = animal;
            }
        }
    }

    public boolean research() {
        if (!this.hasAnimalsForResearch() || this.discoveredAnimal != null) {
            return false;
        } else {
            this.researchHappening = true;
            this.daysSinceResearchStarted = 0;
            return true;
        }
    }

    public void newDay() {
        if (this.researchHappening) {
            this.daysSinceResearchStarted++;
        }
        if (this.daysSinceResearchStarted == this.RESEARCH_DAYS) {
            this.compatibilityTest();
            this.researchHappening = false;
        }
    }

    public AnimalType takeDiscoveredAnimal() {
        AnimalType animal = this.discoveredAnimal;
        this.discoveredAnimal = null;
        return animal;
    }

    private boolean hasAnimalsForResearch() {
        if (this.receivedAnimal1 == null || this.receivedAnimal2 == null) {
            return false;
        } else {
            return true;
        }
    }

    public void compatibilityTest() {
        if (this.hasAnimalsForResearch() && this.discoveredAnimal == null) {
            switch(this.receivedAnimal1) {
                case AnimalType.HORSE:
                    switch(this.receivedAnimal2) {
                        case AnimalType.FISH:
                            this.receivedAnimal1 = null;
                            this.receivedAnimal2 = null;
                            this.discoveredAnimal = AnimalType.SEAHORSE;
                            break;
                        case AnimalType.BEAR:
                            this.receivedAnimal1 = null;
                            this.receivedAnimal2 = null;
                            this.discoveredAnimal = AnimalType.CAPYBARA;
                            break;
                        default:
                            break;
                    }
                    break;
                case AnimalType.BEAR:
                    switch(this.receivedAnimal2) {
                        case AnimalType.HORSE:
                            this.receivedAnimal1 = null;
                            this.receivedAnimal2 = null;
                            this.discoveredAnimal = AnimalType.CAPYBARA;
                            break;
                        case AnimalType.CAT:
                            this.receivedAnimal1 = null;
                            this.receivedAnimal2 = null;
                            this.discoveredAnimal = AnimalType.RACOON;
                            break;
                        default:
                            break;
                    }
                    break;
                case AnimalType.FISH:
                    switch(this.receivedAnimal2) {
                        case AnimalType.HORSE:
                            this.receivedAnimal1 = null;
                            this.receivedAnimal2 = null;
                            this.discoveredAnimal = AnimalType.SEAHORSE;
                            break;
                        case AnimalType.PIG:
                            this.receivedAnimal1 = null;
                            this.receivedAnimal2 = null;
                            this.discoveredAnimal = AnimalType.GUINEAPIG;
                            break;
                        default:
                            break;
                    }
                    break;
                case AnimalType.PIG:
                    switch(this.receivedAnimal2) {
                        case AnimalType.FISH:
                            this.receivedAnimal1 = null;
                            this.receivedAnimal2 = null;
                            this.discoveredAnimal = AnimalType.GUINEAPIG;
                        default:
                            break;
                    }
                    break;
                case AnimalType.CAT:
                    switch(this.receivedAnimal2) {
                        case AnimalType.BEAR:
                            this.receivedAnimal1 = null;
                            this.receivedAnimal2 = null;
                            this.discoveredAnimal = AnimalType.RACOON;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public int getCostOfResearch() {
        return this.COST_OF_RESEARCH;
    }

    @Override
    public String getSpriteName() {
        return "spriteName";
    }
}
