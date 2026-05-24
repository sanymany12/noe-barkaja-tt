package world.building;

import world.World;
import world.resources.AnimalType;
import world.tile.Point;
import world.tile.Tile;
import world.vehicle.AnimalTruck;
import world.vehicle.Vehicle;

public class Enclosure extends Building<AnimalType, AnimalType> {
    private AnimalType species;
    private int numOfAnimals;
    transient private Silo silo;
    private Point siloPos;
    private boolean starving;

    private final double ANIMAL_MULTIPLIER = 1.2;
    private final int CAPACITY = 200;

    public Enclosure(World world, Point siloPos) {
        super(world);

        this.type = BuildingType.ENCLOSURE;

        this.siloPos = siloPos;
        if(world.get(siloPos.x, siloPos.y).getBuilding() instanceof Silo silo){
            this.silo = silo;
            this.silo.setEnclosure(this);
        }else{
            System.err.println("Hibás koordináta lett megadva az Enclosure siloPos-nak!");
        }


        this.width = 4;
        this.height = 2;

        this.species = null;
        this.numOfAnimals = 0;

        this.starving = false;
    }

    public void restoreSiloRef(){
        // VÉDŐHÁLÓ: Ha régi a mentés, vagy megsérült a Point
        if (this.siloPos == null) {
            System.err.println("Figyelem: siloPos null az Enclosure-ben! Ezt valószínűleg egy régi mentés okozza.");
            return; // Azonnal kilépünk, így elkerüljük a NullPointerException fagyást!
        }

        // Ha van érvényes koordináta, megpróbáljuk visszakötni
        if(world.get(siloPos.x, siloPos.y).getBuilding() instanceof Silo siloo){
            this.silo = siloo;
            this.silo.setEnclosure(this);
        } else {
            System.err.println("Hibás koordináta lett megadva az Enclosure siloPos-nak: " + siloPos.x + ", " + siloPos.y);
        }
    }

    public AnimalType getSpecies() {
        return this.species;
    }

    public int getNumOfAnimals() {
        return this.numOfAnimals;
    }

    public boolean isStarving() { return this.starving; }

    public Silo getSilo() { return this.silo; }

    public boolean hasAnimals() {
        return this.numOfAnimals > 0 && this.species != null;
    }

    public boolean isFull() {
        return this.numOfAnimals == this.CAPACITY;
    }

    public void purchaseAnimal(AnimalType animalType) {
        if ((this.numOfAnimals == 0 && this.species == null) || this.species == animalType) {
            this.world.spendMoney(animalType.getValue() * 5);
            this.species = animalType;
            this.numOfAnimals++;
        }
    }

    public void sellAnimal() {
        this.world.receiveMoney(this.species.getValue());
        this.numOfAnimals--;
        if (this.numOfAnimals == 0) {
            this.species = null;
        }
    }

    public void newSpeciesArrives(AnimalType a) {
        this.species = a;
        this.numOfAnimals = 1;
    }

    public void receiveAnimal() {
        this.numOfAnimals = this.numOfAnimals + 1;
    }

    public void takeAnimal() {
        this.numOfAnimals = this.numOfAnimals - 1;
        if (this.numOfAnimals == 0) {
            this.species = null;
        }
    }

    private void consumeFood() {
        if (!this.silo.consumeFood(numOfAnimals)) {
            this.starving = true;
        } else {
            this.starving = false;
        }
    }

    private void animalBorn() {
        int animalsBornNum;
        if (this.numOfAnimals < 2) {
            animalsBornNum = 0;
        } else {
            animalsBornNum = (int) Math.ceil(this.numOfAnimals * this.ANIMAL_MULTIPLIER);
        }
        if (!this.starving) {
            if (this.numOfAnimals + animalsBornNum <= this.CAPACITY) {
                this.numOfAnimals = this.numOfAnimals + animalsBornNum;
            }
            else {
                this.numOfAnimals = this.CAPACITY;
            }
        }
    }

    public int getDaysUntilStarvation() {
        if(this.numOfAnimals == 0)
        {
            return -1;
        }
        return this.silo.getNumOfFood() / this.numOfAnimals;
    }

    @Override
    public void newDay() {
        this.consumeFood();
        if (!this.isFull()) {
            this.animalBorn();
        }
    }

    @Override
    public String getSpriteName() {
        if(this.species == null)
        {
            return "enclosure";
        }
        else
        {
            switch (this.species) {
                case AnimalType.BEAR:
                    return "enclosure-bear";
                case AnimalType.CAPYBARA:
                    return "enclosure-capybara";
                case AnimalType.CAT:
                    return "enclosure-cat";
                case AnimalType.FISH:
                    return "enclosure-fish";
                case AnimalType.GUINEAPIG:
                    return "enclosure-guineapig";
                case AnimalType.HORSE:
                    return "enclosure-horse";
                case AnimalType.PIG:
                    return "enclosure-pig";
                case AnimalType.RACOON:
                    return "enclosure-racoon";
                case AnimalType.SEAHORSE:
                    return "enclosure-seahorse";
                default:
                    return "enclosure";
            }
        }

    }
}
