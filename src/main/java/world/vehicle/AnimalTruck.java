package world.vehicle;

import world.World;
import world.building.*;
import world.resources.AnimalType;
import world.resources.ResourceType;
import world.tile.Point;
import world.tile.road.RoadDirection;

public class AnimalTruck extends Vehicle {


    public AnimalTruck(World world, Point p) throws Exception {
        super(world, p);
        this.capacity = 1;
        this.costToOperate = 5;
        this.cargoType = null;
        this.type = VehicleType.ANIMALTRUCK;
        this.width = 0.5f;
        this.height = 0.5f;
    }

    @Override
    public void loadFrom(Building building) {
        switch (building.getBuildingType()) {
            case BuildingType.ENCLOSURE:
                if (this.cargoType == null && ((Enclosure) building).hasAnimals()) {
                    this.cargoType = ((Enclosure) building).getSpecies();
                    ((Enclosure) building).takeAnimal();
                }
                break;
            case BuildingType.RESEARCHLAB:
                if (this.cargoType == null && ((ResearchLab) building).getDiscoveredAnimal() != null) {
                    this.cargoType = ((ResearchLab) building).getDiscoveredAnimal();
                    ((ResearchLab) building).takeDiscoveredAnimal();
                }
                break;
            case BuildingType.CLONINGFACILITY:
                if (this.cargoType == null && ((CloningFacility) building).hasAnimal()) {
                    this.cargoType = ((CloningFacility) building).getAnimalType();
                    ((CloningFacility) building).takeAnimal();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void unloadTo(Building building) {
        switch (building.getBuildingType()) {
            case BuildingType.ENCLOSURE:
                if (this.cargoType != null && this.cargoType == ((Enclosure) building).getSpecies()) {
                    this.cargoType = null;
                    ((Enclosure) building).receiveAnimal();
                }
                break;
            case BuildingType.RESEARCHLAB:
                if (this.cargoType != null && ((ResearchLab) building).receiveAnimal((AnimalType) this.cargoType)) {
                    this.cargoType = null;
                }
                break;
            case BuildingType.CLONINGFACILITY:
                if (this.cargoType != null && ((CloningFacility) building).receiveAnimal((AnimalType) this.cargoType)) {
                    this.cargoType = null;
                }
                break;
            case BuildingType.CITY:
                if (this.cargoType != null && ((City) building).hasOrder() && this.cargoType == ((City) building).getOrderedAnimal()) {
                    this.cargoType = null;
                    ((City) building).receiveAnimal();
                }
            default:
                break;
        }
    }

    @Override
    public String getSpriteName(){
        String spriteName = "animaltruck";
        switch(this.currentDirection) {
            case RoadDirection.NORTH:
                spriteName = spriteName.concat("-n");
                break;
            case RoadDirection.EAST:
                spriteName = spriteName.concat("-e");
                break;
            case RoadDirection.SOUTH:
                spriteName = spriteName.concat("-s");
                break;
            case RoadDirection.WEST:
                spriteName = spriteName.concat("-w");
                break;
        }
        if (this.cargoType != null) {
            switch(this.cargoType) {
                case AnimalType.BEAR:
                    spriteName = spriteName.concat("-bear");
                    break;
                case AnimalType.CAPYBARA:
                    spriteName = spriteName.concat("-capybara");
                    break;
                case AnimalType.CAT:
                    spriteName = spriteName.concat("-cat");
                    break;
                case AnimalType.FISH:
                    spriteName = spriteName.concat("-fish");
                    break;
                case AnimalType.GUINEAPIG:
                    spriteName = spriteName.concat("-guineapig");
                    break;
                case AnimalType.HORSE:
                    spriteName = spriteName.concat("-horse");
                    break;
                case AnimalType.PIG:
                    spriteName = spriteName.concat("-pig");
                    break;
                case AnimalType.RACOON:
                    spriteName = spriteName.concat("-racoon");
                    break;
                case AnimalType.SEAHORSE:
                    spriteName = spriteName.concat("-seahorse");
                    break;
                default:
                    break;
            }
        }
        return spriteName;
    }
}
