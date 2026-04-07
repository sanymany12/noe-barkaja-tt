package engine;

import world.World;
import world.building.BuildingType;
import world.building.Station;
import world.tile.TerrainType;
import world.tile.Tile;
import world.tile.road.Road;
import world.tile.road.RoadDirection;
import world.vehicle.FoodTruck;

public class BuildManager {
    private World world;

    public BuildManager(World world) {
        this.world = world;
    }

    // Ennek meghívásával frissülnek a környékén található utak és megépül az út a megadott mezőre
    public void buildRoad(Tile t) {
        Road newRoad = new Road(t.getCoordinate().x, t.getCoordinate().y);
        world.spendMoney(newRoad.getCostToBuild() + t.getTreeCount() * 5);
        t.setRoad(newRoad);
        t.setTerrainType(TerrainType.ROAD);
        Tile neighbourNorth = this.world.get(t.getCoordinate().x, t.getCoordinate().y-1);
        Tile neighbourWest = this.world.get(t.getCoordinate().x-1, t.getCoordinate().y);
        Tile neighbourEast = this.world.get(t.getCoordinate().x+1, t.getCoordinate().y);
        Tile neighbourSouth = this.world.get(t.getCoordinate().x, t.getCoordinate().y+1);
        if (neighbourNorth != null) {
            if (neighbourNorth.getRoad() != null) {
                t.getRoad().setConnection(RoadDirection.NORTH);
                neighbourNorth.getRoad().setConnection(RoadDirection.NORTH.getOpposite());
            }
        }
        if (neighbourSouth != null) {
            if (neighbourSouth.getRoad() != null) {
                t.getRoad().setConnection(RoadDirection.SOUTH);
                neighbourSouth.getRoad().setConnection(RoadDirection.SOUTH.getOpposite());;
            }
        }
        if (neighbourEast != null) {
            if (neighbourEast.getRoad() != null) {
                t.getRoad().setConnection(RoadDirection.EAST);
                neighbourEast.getRoad().setConnection(RoadDirection.EAST.getOpposite());
            }
        }
        if (neighbourWest != null) {
            if (neighbourWest.getRoad() != null) {
                t.getRoad().setConnection(RoadDirection.WEST);
                neighbourWest.getRoad().setConnection(RoadDirection.WEST.getOpposite());;
            }
        }
    }

    // A direction itt az épület irányára hivatkozik!!!
    public void buildStation(Tile t, RoadDirection dir) {
        Tile buildingTile = null;
        Tile roadTile = null;
        switch(dir) {
            case RoadDirection.NORTH:
                buildingTile = this.world.get(t.getCoordinate().x, t.getCoordinate().y - 1);
                roadTile = this.world.get(t.getCoordinate().x, t.getCoordinate().y + 1);
                break;
            case RoadDirection.EAST:
                buildingTile = this.world.get(t.getCoordinate().x + 1, t.getCoordinate().y);
                roadTile = this.world.get(t.getCoordinate().x - 1, t.getCoordinate().y);
                break;
            case RoadDirection.SOUTH:
                buildingTile = this.world.get(t.getCoordinate().x, t.getCoordinate().y + 1);
                roadTile = this.world.get(t.getCoordinate().x, t.getCoordinate().y - 1);
                break;
            case RoadDirection.WEST:
                buildingTile = this.world.get(t.getCoordinate().x - 1, t.getCoordinate().y);
                roadTile = this.world.get(t.getCoordinate().x + 1, t.getCoordinate().y);
                break;
        }
        // Ellenőrzés, hogy van-e épület abban az irányban, ahova építeni szeretnénk
        if (buildingTile != null && buildingTile.getTerrainType() == TerrainType.BUILDING && buildingTile.getBuilding() != null) {
            // Ellenőrzés, hogy van-e a megállóhoz kapcsolódó út
            if (roadTile != null && roadTile.getTerrainType() == TerrainType.ROAD && roadTile.getRoad() != null) {
                // Ellenőrzés, hogy a megálló melletti épület nem buszmegálló / ipari megálló
                if (buildingTile.getBuilding().getBuildingType() != BuildingType.BUSSTOP && buildingTile.getBuilding().getBuildingType() != BuildingType.STATION) {
                    // Ipari megálló "megépítése"
                    t.setBuilding(new Station(this.world, buildingTile.getBuilding(), dir.getOpposite()));
                    t.setTerrainType(TerrainType.STOP);
                    // Út elágazásainak frissítése
                    this.world.get(roadTile.getCoordinate().x, roadTile.getCoordinate().y).getRoad().setConnection(dir);
                }
            }
        }
    }

    // Temporary itt, majd át kell rakni máshova ig
    public void buyFoodTruck(Tile t) throws Exception {
        if (t.getTerrainType() != TerrainType.ROAD && t.getRoad() != null) {
            throw new Exception("Unable to purchase food truck here!");
        } else {
            FoodTruck ft = new FoodTruck(this.world, t.getCoordinate());
            world.spendMoney(ft.getCostToBuy());
            t.getRoad().vehicleEnters(ft);
        }
    }
}
