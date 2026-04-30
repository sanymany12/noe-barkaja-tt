package engine;

import world.World;
import world.building.BuildingType;
import world.building.BusStop;
import world.building.Station;
import world.tile.TerrainType;
import world.tile.Tile;
import world.tile.road.Bridge;
import world.tile.road.BridgeType;
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
    // TODO: több ellenőrzés?
    public void buildRoad(Tile t, boolean isPreBuilt) {
        Road newRoad = new Road(t.getCoordinate().x, t.getCoordinate().y, isPreBuilt);
        if (!isPreBuilt) {
            world.spendMoney(newRoad.getCostToBuild() + t.getTreeCount() * world.getCostToCutTree());
        }
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
    public void buildStation(Tile t, RoadDirection dir, boolean isPreBuilt) {
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
                    Station newStation = new Station(this.world, buildingTile.getBuilding(), dir, isPreBuilt);
                    t.setBuilding(newStation);
                    if (!isPreBuilt) {
                        world.spendMoney(newStation.getCostToBuild() + t.getTreeCount() * world.getCostToCutTree());
                    }
                    t.setTerrainType(TerrainType.STOP);
                    t.setAnchor(true);
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

    public void buildBridge(Tile start, Tile end, BridgeType type) throws Exception {
        boolean matchingCoordinateIsX = false;
        RoadDirection startDirection = null;

        Tile startNeighbour = null;
        Tile endNeighbour = null;

        int bridgesBuilt = 0;

        // Ellenőrzés, hogy nem csak egy cellára akarunk hidat építeni
        if (start == end) {
            throw new Exception("A híd legalább 2 cella hosszú kell, hogy legyen!");
        // Ellenőrzés, hogy végig vízre akarunk-e építeni és hogy a kezdő- és végpont egy sorban/oszlopban van-e
        // Ellenőrzés, hogy megegyezik-e az oszlop koordinátája
        } else if (start.getCoordinate().x == end.getCoordinate().x) {
            matchingCoordinateIsX = true;
            if (start.getCoordinate().y > end.getCoordinate().y) {
                startDirection = RoadDirection.NORTH;
                startNeighbour = world.get(start.getCoordinate().x, start.getCoordinate().y + 1);
                endNeighbour = world.get(end.getCoordinate().x, end.getCoordinate().y - 1);
                if (startNeighbour != null && endNeighbour != null) {
                    if (!(startNeighbour.getTerrainType() == TerrainType.LAND || startNeighbour.getTerrainType() == TerrainType.ROAD) || !(endNeighbour.getTerrainType() == TerrainType.LAND || endNeighbour.getTerrainType() == TerrainType.ROAD)) {
                        throw new Exception("A kezdő és végpont mellet földnek / útnak kell lennie!");
                    }
                } else {
                    throw new Exception("A kezdő és végpont mellet földnek / útnak kell lennie!");
                }
                for (int i = start.getCoordinate().y; i < end.getCoordinate().y + 1; i++) {
                    if (this.world.get(start.getCoordinate().x, i) != null) {
                        if (this.world.get(start.getCoordinate().x, i).getTerrainType() != TerrainType.WATER) {
                            throw new Exception("A híd csak vízre építhető!");
                        }
                    } else {
                        throw new Exception("Hiba a mezővel a kezdőpont és végpont között!");
                    }
                }
            } else {
                startDirection = RoadDirection.SOUTH;
                startNeighbour = world.get(start.getCoordinate().x, start.getCoordinate().y - 1);
                endNeighbour = world.get(end.getCoordinate().x, end.getCoordinate().y + 1);
                if (startNeighbour != null && endNeighbour != null) {
                    if (!(startNeighbour.getTerrainType() == TerrainType.LAND || startNeighbour.getTerrainType() == TerrainType.ROAD) || !(endNeighbour.getTerrainType() == TerrainType.LAND || endNeighbour.getTerrainType() == TerrainType.ROAD)) {
                        throw new Exception("A kezdő és végpont mellet földnek / útnak kell lennie!");
                    }
                } else {
                    throw new Exception("A kezdő és végpont mellet földnek / útnak kell lennie!");
                }
                for (int i = end.getCoordinate().y; i < start.getCoordinate().y + 1; i++) {
                    if (this.world.get(start.getCoordinate().x, i) != null) {
                        if (this.world.get(start.getCoordinate().x, i).getTerrainType() != TerrainType.WATER) {
                            throw new Exception("A híd csak vízre építhető!");
                        }
                    } else {
                        throw new Exception("Hiba a mezővel a kezdőpont és végpont között!");
                    }
                }
            }
        // Ellenőrzés, hogy megegyezik-e a sorkoodináta
        } else if (start.getCoordinate().y == end.getCoordinate().y) {
            matchingCoordinateIsX = false;
            if (start.getCoordinate().x > end.getCoordinate().x) {
                startDirection = RoadDirection.EAST;
                startNeighbour = world.get(start.getCoordinate().x + 1, start.getCoordinate().y);
                endNeighbour = world.get(end.getCoordinate().x - 1, end.getCoordinate().y);
                if (startNeighbour != null && endNeighbour != null) {
                    if (!(startNeighbour.getTerrainType() == TerrainType.LAND || startNeighbour.getTerrainType() == TerrainType.ROAD) || !(endNeighbour.getTerrainType() == TerrainType.LAND || endNeighbour.getTerrainType() == TerrainType.ROAD)) {
                        throw new Exception("A kezdő és végpont mellet földnek / útnak kell lennie!");
                    }
                } else {
                    throw new Exception("A kezdő és végpont mellet földnek / útnak kell lennie!");
                }
                for (int i = start.getCoordinate().x; i < end.getCoordinate().x + 1; i++) {
                    if (this.world.get(i, start.getCoordinate().y) != null) {
                        if (this.world.get(i, start.getCoordinate().y).getTerrainType() != TerrainType.WATER) {
                            throw new Exception("A híd csak vízre építhető!");
                        }
                    } else {
                        throw new Exception("Hiba a mezővel a kezdőpont és végpont között!");
                    }
                }
            } else {
                startDirection = RoadDirection.WEST;
                startNeighbour = world.get(start.getCoordinate().x - 1, start.getCoordinate().y);
                endNeighbour = world.get(end.getCoordinate().x + 1, end.getCoordinate().y);
                if (startNeighbour != null && endNeighbour != null) {
                    if (!(startNeighbour.getTerrainType() == TerrainType.LAND || startNeighbour.getTerrainType() == TerrainType.ROAD) || !(endNeighbour.getTerrainType() == TerrainType.LAND || endNeighbour.getTerrainType() == TerrainType.ROAD)) {
                        throw new Exception("A kezdő és végpont mellet földnek / útnak kell lennie!");
                    }
                } else {
                    throw new Exception("A kezdő és végpont mellet földnek / útnak kell lennie!");
                }
                for (int i = end.getCoordinate().x; i < start.getCoordinate().x + 1; i++) {
                    if (this.world.get(i, start.getCoordinate().y) != null) {
                        if (this.world.get(i, start.getCoordinate().y).getTerrainType() != TerrainType.WATER) {
                            throw new Exception("A híd csak vízre építhető!");
                        }
                    } else {
                        throw new Exception("Hiba a mezővel a kezdőpont és végpont között!");
                    }
                }
            }
        } else {
            throw new Exception("A híd csak egyenes vonalban építhető!");
        }

        // Ha minden feltételnek megfelelt
        if (matchingCoordinateIsX) {
            Bridge startBridge = new Bridge(start.getCoordinate().x, start.getCoordinate().y, type, startDirection, true);
            start.setRoad(startBridge);
            start.setTerrainType(TerrainType.BRIDGE);
            bridgesBuilt++;
            if (startNeighbour.getTerrainType() == TerrainType.ROAD && startNeighbour.getRoad() != null) {
                start.getRoad().setConnection(startDirection);
                startNeighbour.getRoad().setConnection(startDirection.getOpposite());
            }
            for (int i = start.getCoordinate().y + 1; i < end.getCoordinate().y; i++) {
                Bridge newBridge = new Bridge(start.getCoordinate().x, i, type, RoadDirection.NORTH, false);
                newBridge.setConnection(RoadDirection.NORTH);
                newBridge.setConnection(RoadDirection.SOUTH);
                bridgesBuilt++;
                world.get(start.getCoordinate().x, i).setRoad(newBridge);
                world.get(start.getCoordinate().x, i).setTerrainType(TerrainType.BRIDGE);
            }
            Bridge endBridge = new Bridge(end.getCoordinate().x, end.getCoordinate().y, type, startDirection.getOpposite(), true);
            end.setRoad(endBridge);
            end.setTerrainType(TerrainType.BRIDGE);
            bridgesBuilt++;
            if (endNeighbour.getTerrainType() == TerrainType.ROAD && endNeighbour.getRoad() != null) {
                end.getRoad().setConnection(startDirection.getOpposite());
                endNeighbour.getRoad().setConnection(startDirection);
            }
        } else {
            Bridge startBridge = new Bridge(start.getCoordinate().x, start.getCoordinate().y, type, startDirection, true);
            start.setRoad(startBridge);
            start.setTerrainType(TerrainType.BRIDGE);
            bridgesBuilt++;
            if (startNeighbour.getTerrainType() == TerrainType.ROAD && startNeighbour.getRoad() != null) {
                start.getRoad().setConnection(startDirection);
                startNeighbour.getRoad().setConnection(startDirection.getOpposite());
            }
            for (int i = start.getCoordinate().x + 1; i < end.getCoordinate().x; i++) {
                Bridge newBridge = new Bridge(i, start.getCoordinate().y, type, RoadDirection.EAST, false);
                newBridge.setConnection(RoadDirection.WEST);
                newBridge.setConnection(RoadDirection.EAST);
                bridgesBuilt++;
                world.get(i, start.getCoordinate().y).setRoad(newBridge);
                world.get(i, start.getCoordinate().y).setTerrainType(TerrainType.BRIDGE);
            }
            Bridge endBridge = new Bridge(end.getCoordinate().x, end.getCoordinate().y, type, startDirection.getOpposite(), true);
            end.setRoad(endBridge);
            end.setTerrainType(TerrainType.BRIDGE);
            bridgesBuilt++;
            if (endNeighbour.getTerrainType() == TerrainType.ROAD && endNeighbour.getRoad() != null) {
                end.getRoad().setConnection(startDirection.getOpposite());
                endNeighbour.getRoad().setConnection(startDirection);
            }
        }
        world.spendMoney(type.getCost() * bridgesBuilt);
    }

    public void buyVehicle(Tile t, VehicleType type) throws Exception
    {
        if(t.getTerrainType() != TerrainType.STOP || t.getBuilding() == null || t.getBuilding().getBuildingType() != BuildingType.STATION)
        {
            throw new Exception("Ide nem tudsz vasarolni!");
        }

        Station station = (Station) t.getBuilding();
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
                AnimalTruck at = new AnimalTruck(this.world, t.getCoordinate());
                cost = 500;
                newVehicle = at;
                break;
        }

        if(newVehicle != null)
        {
            world.spendMoney(cost);
            station.vehicleArrives(newVehicle);
            world.getVehicles().add(newVehicle);
        }
    }

    public void destroy(Tile t) {
        boolean didDamage = false;
        if (!t.isEmpty()) {
            if (t.getBuilding() != null) {
                if (t.getBuilding().getBuildingType() == BuildingType.STATION) {
                    if (!((Station) (t.getBuilding())).getIsPreBuilt()) {
                        if (((Station) (t.getBuilding())).isOccupied()) {
                            ((Station) (t.getBuilding())).getVehicle().sellVehicle();
                            t.removeStation();
                            didDamage = true;
                        }
                    }
                }
            } else if (t.getRoad() != null) {
                if (!t.getRoad().getIsPreBuilt()) {
                    t.getRoad().getsDestroyed();
                    t.removeRoad();
                    didDamage = true;
                }
            } else if (t.getTreeCount() > 0) {
                world.spendMoney(t.getTreeCount() * world.getCostToCutTree());
                t.setTreeCount(0);
            }
        }
        if (didDamage) {
            world.spendMoney(world.getCostToDestroy());
        }
    }

    public void setWorld(World world) { this.world = world;}
}
