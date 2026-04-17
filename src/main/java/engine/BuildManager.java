package engine;

import world.World;
import world.building.BuildingType;
import world.building.BusStop;
import world.building.Station;
import world.tile.TerrainType;
import world.tile.Tile;
import world.tile.road.Road;
import world.tile.road.RoadDirection;
import world.vehicle.FoodTruck;
import world.vehicle.VehicleType;
import world.vehicle.Vehicle;
import world.vehicle.Bus;
import world.vehicle.AnimalTruck;

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
            } else if (neighbourNorth.getBuilding() != null) {
                switch (neighbourNorth.getBuilding().getBuildingType()) {
                    case BuildingType.BUSSTOP:
                        if (((BusStop) (neighbourNorth.getBuilding())).getDirection() == RoadDirection.NORTH) {
                            t.getRoad().setConnection(RoadDirection.NORTH);
                            ((BusStop) (neighbourNorth.getBuilding())).setConnectedRoad(t);
                        }
                    case BuildingType.STATION:
                        if (((Station) (neighbourNorth.getBuilding())).getDirection() == RoadDirection.NORTH) {
                            t.getRoad().setConnection(RoadDirection.NORTH);
                            ((Station) (neighbourNorth.getBuilding())).setConnectedRoad(t);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        if (neighbourSouth != null) {
            if (neighbourSouth.getRoad() != null) {
                t.getRoad().setConnection(RoadDirection.SOUTH);
                neighbourSouth.getRoad().setConnection(RoadDirection.SOUTH.getOpposite());;
            } else if (neighbourSouth.getBuilding() != null) {
                switch (neighbourSouth.getBuilding().getBuildingType()) {
                    case BuildingType.BUSSTOP:
                        if (((BusStop) (neighbourSouth.getBuilding())).getDirection() == RoadDirection.SOUTH) {
                            t.getRoad().setConnection(RoadDirection.SOUTH);
                            ((BusStop) (neighbourSouth.getBuilding())).setConnectedRoad(t);
                        }
                        break;
                    case BuildingType.STATION:
                        if (((Station) (neighbourSouth.getBuilding())).getDirection() == RoadDirection.SOUTH) {
                            t.getRoad().setConnection(RoadDirection.SOUTH);
                            ((Station) (neighbourSouth.getBuilding())).setConnectedRoad(t);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        if (neighbourEast != null) {
            if (neighbourEast.getRoad() != null) {
                t.getRoad().setConnection(RoadDirection.EAST);
                neighbourEast.getRoad().setConnection(RoadDirection.EAST.getOpposite());
            } else if (neighbourEast.getBuilding() != null) {
                switch (neighbourEast.getBuilding().getBuildingType()) {
                    case BuildingType.BUSSTOP:
                        if (((BusStop) (neighbourEast.getBuilding())).getDirection() == RoadDirection.EAST) {
                            t.getRoad().setConnection(RoadDirection.EAST);
                            ((BusStop) (neighbourEast.getBuilding())).setConnectedRoad(t);
                        }
                    case BuildingType.STATION:
                        if (((Station) (neighbourEast.getBuilding())).getDirection() == RoadDirection.EAST) {
                            t.getRoad().setConnection(RoadDirection.EAST);
                            ((Station) (neighbourEast.getBuilding())).setConnectedRoad(t);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        if (neighbourWest != null) {
            if (neighbourWest.getRoad() != null) {
                t.getRoad().setConnection(RoadDirection.WEST);
                neighbourWest.getRoad().setConnection(RoadDirection.WEST.getOpposite());;
            } else if (neighbourWest.getBuilding() != null) {
                switch (neighbourWest.getBuilding().getBuildingType()) {
                    case BuildingType.BUSSTOP:
                        if (((BusStop) (neighbourWest.getBuilding())).getDirection() == RoadDirection.WEST) {
                            t.getRoad().setConnection(RoadDirection.WEST);
                            ((BusStop) (neighbourWest.getBuilding())).setConnectedRoad(t);
                        }
                    case BuildingType.STATION:
                        if (((Station) (neighbourWest.getBuilding())).getDirection() == RoadDirection.WEST) {
                            t.getRoad().setConnection(RoadDirection.WEST);
                            ((Station) (neighbourWest.getBuilding())).setConnectedRoad(t);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    // Megálló építéséhez írt metódus
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
        if (buildingTile != null) {
            if (buildingTile.getTerrainType() == TerrainType.BUILDING && buildingTile.getBuilding() != null) {
                // Ellenőrzés, hogy az épület NEM ipari megálló / buszmegálló
                if (buildingTile.getBuilding().getBuildingType() != BuildingType.BUSSTOP && buildingTile.getBuilding().getBuildingType() != BuildingType.STATION) {
                    // Megálló megépítése
                    t.setBuilding(new Station(this.world, buildingTile.getBuilding(), dir));
                    t.setTerrainType(TerrainType.STOP);
                    // Ellenőrzés, hogy van-e a megállóhoz kapcsolódó út
                    if (roadTile != null && roadTile.getTerrainType() == TerrainType.ROAD && roadTile.getRoad() != null) {
                        // Út kapcsolatainak frissítése, amennyiben van, illetve az út beállítása mint idekapcsolt út
                        this.world.get(roadTile.getCoordinate().x, roadTile.getCoordinate().y).getRoad().setConnection(dir);
                        ((Station) (t.getBuilding())).setConnectedRoad(roadTile);
                    }
                }
            }
        }
    }

    public void buyVehicle(Tile t, VehicleType type) throws Exception
    {
        if(t.getTerrainType() != TerrainType.ROAD || t.getRoad() == null)
        {
            throw new Exception("Ide nem tudsz vasarolni!");
        }

        Vehicle newVehicle = null;
        int cost = 0;

        switch(type)
        {
            case FOODTRUCK:
                FoodTruck ft = new FoodTruck(this.world, t.getCoordinate());
                cost = ft.getCostToBuy();
                newVehicle = ft;
                break;
            case BUS:
                Bus bus = new Bus(this.world, t.getCoordinate());
                cost = 300;
                newVehicle = bus;
                break;
            case ANIMALTRUCK:
                //stb
                break;
        }

        if(newVehicle != null)
        {
            world.spendMoney(cost);
            t.getRoad().vehicleEnters(newVehicle, newVehicle.getCurrentDirection());
            world.getVehicles().add(newVehicle);
        }
    }

    public void setWorld(World world) { this.world = world;}
}
