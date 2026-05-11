package engine;

import world.World;
import world.building.*;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BuildManager {
    private World world;

    public BuildManager(World world) {
        this.world = world;
    }

    public void placeBuilding(Tile t, BuildingType buildingType) {
        switch (buildingType) {
            case BuildingType.FARM:
                Farm farm = new Farm(this.world);
                t.setAnchor(true);
                // System.out.println(farm.getWidth());
                // System.out.println(farm.getHeight());
                for (int i = t.getCoordinate().x; i < t.getCoordinate().x + farm.getHeight(); i++) {
                    for (int j = t.getCoordinate().y; j < t.getCoordinate().y + farm.getHeight(); j++) {
                        // System.out.println("A jelenlegi koordináták: " + i + ", " + j);
                        world.get(i, j).setBuilding(farm);
                        world.get(i, j).setTerrainType(TerrainType.BUILDING);
                    }
                }
                break;
            case BuildingType.AGRICULTURALPLANT:
                AgriculturalPlant agriculturalplant = new AgriculturalPlant(this.world);
                t.setAnchor(true);
                for (int i = t.getCoordinate().x; i < t.getCoordinate().x + agriculturalplant.getHeight(); i++) {
                    for (int j = t.getCoordinate().y; j < t.getCoordinate().y + agriculturalplant.getHeight(); j++) {
                        world.get(i, j).setBuilding(agriculturalplant);
                        world.get(i, j).setTerrainType(TerrainType.BUILDING);
                    }
                }
                break;
            case BuildingType.SILO:
                Silo silo = new Silo(this.world);
                t.setAnchor(true);
                for (int i = t.getCoordinate().x; i < t.getCoordinate().x + silo.getHeight(); i++) {
                    for (int j = t.getCoordinate().y; j < t.getCoordinate().y + silo.getHeight(); j++) {
                        world.get(i, j).setBuilding(silo);
                        world.get(i, j).setTerrainType(TerrainType.BUILDING);
                    }
                }
                break;
            case BuildingType.RESEARCHLAB:
                ResearchLab researchlab = new ResearchLab (this.world);
                t.setAnchor(true);
                for (int i = t.getCoordinate().x; i < t.getCoordinate().x + researchlab.getHeight(); i++) {
                    for (int j = t.getCoordinate().y; j < t.getCoordinate().y + researchlab.getHeight(); j++) {
                        world.get(i, j).setBuilding(researchlab);
                        world.get(i, j).setTerrainType(TerrainType.BUILDING);
                    }
                }
                break;
            case BuildingType.CLONINGFACILITY:
                CloningFacility cloningfacility = new CloningFacility(this.world);
                t.setAnchor(true);
                for (int i = t.getCoordinate().x; i < t.getCoordinate().x + cloningfacility.getHeight(); i++) {
                    for (int j = t.getCoordinate().y; j < t.getCoordinate().y + cloningfacility.getHeight(); j++) {
                        world.get(i, j).setBuilding(cloningfacility);
                        world.get(i, j).setTerrainType(TerrainType.BUILDING);
                    }
                }
                break;
            case BuildingType.CITY:
                City city = new City(this.world, ThreadLocalRandom.current().nextInt(1, 4));
                t.setAnchor(true);
                for (int i = t.getCoordinate().x; i < t.getCoordinate().x + city.getHeight(); i++) {
                    for (int j = t.getCoordinate().y; j < t.getCoordinate().y + city.getHeight(); j++) {
                        world.get(i, j).setBuilding(city);
                        world.get(i, j).setTerrainType(TerrainType.BUILDING);
                    }
                }
                break;
            default:
                break;
        }
    }

    public void placeEnclosure(Tile t, Tile silotile) {
        Silo silo = null;
        if (silotile.getBuilding() != null) {
            if (silotile.getBuilding().getBuildingType() == BuildingType.SILO) {
                silo = (Silo) silotile.getBuilding();
            }
        }
        if (silo != null && silo.getEnclosure() == null) {
            Enclosure enclosure = new Enclosure(this.world, silotile.getCoordinate());
            t.setAnchor(true);
            for (int i = t.getCoordinate().x; i < t.getCoordinate().x + enclosure.getHeight(); i++) {
                for (int j = t.getCoordinate().y; j < t.getCoordinate().y + enclosure.getHeight(); j++) {
                    world.get(i, j).setBuilding(enclosure);
                    world.get(i, j).setTerrainType(TerrainType.BUILDING);
                }
            }
        }
    }

    public void placeBusStop(Tile t, RoadDirection dir) {
        BusStop busstop = new BusStop(this.world, dir);
        t.setBuilding(busstop);
        t.setAnchor(true);
    }

    // Ennek meghívásával frissülnek a környékén található utak és megépül az út a megadott mezőre
    public void buildRoad(Tile t, boolean isPreBuilt) {
        if (t.getTerrainType() == TerrainType.LAND && t.getBuilding() == null && t.getRoad() == null) {
            Road newRoad = new Road(t.getCoordinate().x, t.getCoordinate().y, isPreBuilt);
            if (!isPreBuilt) {
                world.spendMoney(newRoad.getCostToBuild() + t.getTreeCount() * world.getCostToCutTree());
            }
            t.setRoad(newRoad);
            t.setTerrainType(TerrainType.ROAD);
            t.setTreeCount(0);
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
                    t.setTreeCount(0);
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

    public void buildBridge(Tile start, Tile end, BridgeType type, boolean isPreBuilt) throws Exception {
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
                startDirection = RoadDirection.SOUTH;
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
                startDirection = RoadDirection.NORTH;
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
                startDirection = RoadDirection.WEST;
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
                startDirection = RoadDirection.EAST;
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
            Bridge startBridge = new Bridge(start.getCoordinate().x, start.getCoordinate().y, type, startDirection, start, end, true, isPreBuilt);
            start.setRoad(startBridge);
            start.setTerrainType(TerrainType.BRIDGE);
            bridgesBuilt++;
            if (startNeighbour.getTerrainType() == TerrainType.ROAD && startNeighbour.getRoad() != null) {
                start.getRoad().setConnection(startDirection);
                startNeighbour.getRoad().setConnection(startDirection.getOpposite());
            }
            for (int i = start.getCoordinate().y + 1; i < end.getCoordinate().y; i++) {
                Bridge newBridge = new Bridge(start.getCoordinate().x, i, type, RoadDirection.NORTH, start, end, false, isPreBuilt);
                newBridge.setConnection(RoadDirection.NORTH);
                newBridge.setConnection(RoadDirection.SOUTH);
                bridgesBuilt++;
                world.get(start.getCoordinate().x, i).setRoad(newBridge);
                world.get(start.getCoordinate().x, i).setTerrainType(TerrainType.BRIDGE);
            }
            Bridge endBridge = new Bridge(end.getCoordinate().x, end.getCoordinate().y, type, startDirection.getOpposite(), start, end, true, isPreBuilt);
            end.setRoad(endBridge);
            end.setTerrainType(TerrainType.BRIDGE);
            bridgesBuilt++;
            if (endNeighbour.getTerrainType() == TerrainType.ROAD && endNeighbour.getRoad() != null) {
                end.getRoad().setConnection(startDirection.getOpposite());
                endNeighbour.getRoad().setConnection(startDirection);
            }
        } else {
            Bridge startBridge = new Bridge(start.getCoordinate().x, start.getCoordinate().y, type, startDirection, start, end,true, isPreBuilt);
            start.setRoad(startBridge);
            start.setTerrainType(TerrainType.BRIDGE);
            bridgesBuilt++;
            if (startNeighbour.getTerrainType() == TerrainType.ROAD && startNeighbour.getRoad() != null) {
                start.getRoad().setConnection(startDirection);
                startNeighbour.getRoad().setConnection(startDirection.getOpposite());
            }
            for (int i = start.getCoordinate().x + 1; i < end.getCoordinate().x; i++) {
                Bridge newBridge = new Bridge(i, start.getCoordinate().y, type, RoadDirection.EAST, start, end,false, isPreBuilt);
                newBridge.setConnection(RoadDirection.WEST);
                newBridge.setConnection(RoadDirection.EAST);
                bridgesBuilt++;
                world.get(i, start.getCoordinate().y).setRoad(newBridge);
                world.get(i, start.getCoordinate().y).setTerrainType(TerrainType.BRIDGE);
            }
            Bridge endBridge = new Bridge(end.getCoordinate().x, end.getCoordinate().y, type, startDirection.getOpposite(), start, end, true, isPreBuilt);
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
                cost = bus.getCostToBuy();
                newVehicle = bus;
                break;
            case ANIMALTRUCK:
                AnimalTruck at = new AnimalTruck(this.world, t.getCoordinate());
                cost = at.getCostToBuy();
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
        System.out.println("A rombolás megkezdődött.");
        boolean didDamage = false;
        // ellenőrizzük, hogy van-e a tile-on épület, út vagy fa
        if (!t.isEmpty()) {
            // ha van épület
            if (t.getBuilding() != null) {
                System.out.println("A mezőn épület van.");
                // ha az épület station
                if (t.getBuilding().getBuildingType() == BuildingType.STATION) {
                    System.out.println("A mezőn egy megálló van.");
                    // ha az épület nem volt előre lehelyezve
                    if (!((Station) (t.getBuilding())).getIsPreBuilt()) {
                        System.out.println("A mezőn lévő megálló rombolható.");
                        ((Station) (t.getBuilding())).getConnectedBuilding().removeStation(((Station) (t.getBuilding())));
                        // ha van rajta jármű, azt eladjuk
                        if (((Station) (t.getBuilding())).isOccupied()) {
                            ((Station) (t.getBuilding())).getVehicle().sellVehicle();
                            System.out.println("A mezőn található járművet eladtuk.");
                        }
                        // ha van hozzá kapcsolt út, eltávolítjuk ezt a kapcsolatot
                        if (((Station) (t.getBuilding())).getConnectedRoad() != null) {
                            if (((Station) (t.getBuilding())).getConnectedRoad().getRoad() != null) {
                                ((Station) (t.getBuilding())).getConnectedRoad().getRoad().destroyConnection(((Station) (t.getBuilding())).getDirection());
                                System.out.println("A megállóhoz tartozó úttal a kapcsolat megszakadt.");
                            }
                        }
                        // eltávolítjuk a Stationt és újratervezzük a világban lévő járművek útvonalait
                        t.removeStation();
                        world.rerouteVehiclesStation(t);
                        didDamage = true;
                    }
                }
            // ha utat akarunk törölni
            } else if (t.getRoad() != null) {
                System.out.println("Utat próbálunk rombolni.");
                // ha nem előre lehelyezett
                if (!t.getRoad().getIsPreBuilt()) {
                    System.out.println("Az út rombolható.");
                    // ha híd
                    if (t.getRoad().getIsBridge()) {
                        System.out.println("Az út egy híd.");
                        Tile startTile = ((Bridge) t.getRoad()).getStartTile();
                        Tile endTile = ((Bridge) t.getRoad()).getEndTile();
                        if (((Bridge) startTile.getRoad()).getDirection() == RoadDirection.NORTH || ((Bridge) startTile.getRoad()).getDirection() == RoadDirection.SOUTH) {
                            if (startTile.getCoordinate().y > endTile.getCoordinate().y) {
                                for (int i = endTile.getCoordinate().y; i < startTile.getCoordinate().y + 1; i++) {
                                    Tile currentBridge = world.get(startTile.getCoordinate().x, i);
                                    if (currentBridge != null && currentBridge.getRoad() != null) {
                                        currentBridge.getRoad().getsDestroyed();
                                        this.unlinkConnectingRoads(currentBridge);
                                        currentBridge.removeRoad();
                                        didDamage = true;
                                    }
                                }
                            } else {
                                for (int i = startTile.getCoordinate().y; i < endTile.getCoordinate().y + 1; i++) {
                                    Tile currentBridge = world.get(startTile.getCoordinate().x, i);
                                    if (currentBridge != null && currentBridge.getRoad() != null) {
                                        currentBridge.getRoad().getsDestroyed();
                                        this.unlinkConnectingRoads(currentBridge);
                                        currentBridge.removeRoad();
                                        didDamage = true;
                                    }
                                }
                            }
                        } else {
                            if (startTile.getCoordinate().x > endTile.getCoordinate().x) {
                                for (int i = endTile.getCoordinate().x; i < startTile.getCoordinate().x + 1; i++) {
                                    Tile currentBridge = world.get(i, startTile.getCoordinate().y);
                                    if (currentBridge != null && currentBridge.getRoad() != null) {
                                        currentBridge.getRoad().getsDestroyed();
                                        this.unlinkConnectingRoads(currentBridge);
                                        currentBridge.removeRoad();
                                        didDamage = true;
                                    }
                                }
                            } else {
                                for (int i = startTile.getCoordinate().x; i < endTile.getCoordinate().x + 1; i++) {
                                    Tile currentBridge = world.get(i, startTile.getCoordinate().y);
                                    if (currentBridge != null && currentBridge.getRoad() != null) {
                                        currentBridge.getRoad().getsDestroyed();
                                        this.unlinkConnectingRoads(currentBridge);
                                        currentBridge.removeRoad();
                                        didDamage = true;
                                    }
                                }
                            }
                        }
                        if (didDamage) {
                            world.rerouteVehicles();
                        }
                    } else {
                        System.out.println("Sima utat rombolunk");
                        // eladjuk a rajta található járműveket
                        t.getRoad().getsDestroyed();
                        System.out.println("A mezőn található járműveket eladtuk.");
                        // újratervezzük a világban található vehicle-ek útvonalait
                        world.rerouteVehiclesRoad(t);
                        System.out.println("A járművek útvonalait újraterveztük.");
                        // lecsatoljuk a szomszédos utakról, mint kapcsolat
                        this.unlinkConnectingRoads(t);
                        System.out.println("A mező kapcsolatait eltávolítottuk.");
                        // eltávolítjuk az utat
                        t.removeRoad();
                        System.out.println("Az utat leromboltuk.");
                        didDamage = true;
                    }
                }
            } else if (t.getTreeCount() > 0) {
                // kivágjuk a fákat
                world.spendMoney(t.getTreeCount() * world.getCostToCutTree());
                t.setTreeCount(0);
            }
        }
        // ha komolyabb rombolás történt (megálló / út), akkor kifizetjük a rombolás árát
        if (didDamage) {
            world.spendMoney(world.getCostToDestroy());
        }
    }

    private void unlinkConnectingRoads(Tile t) {
        // megkeressük a szomszédokat
        Tile northNeighbour = this.world.get(t.getCoordinate().x, t.getCoordinate().y - 1);
        Tile southNeighbour = this.world.get(t.getCoordinate().x, t.getCoordinate().y + 1);
        Tile eastNeighbour = this.world.get(t.getCoordinate().x + 1, t.getCoordinate().y);
        Tile westNeighbour = this.world.get(t.getCoordinate().x - 1, t.getCoordinate().y);

        // ha a szomszédos cellákon út van, akkor megszüntetjük az eredeti cella irányába irányuló kapcsolatukat
        if (northNeighbour != null && northNeighbour.getTerrainType() == TerrainType.ROAD && northNeighbour.getRoad() != null) {
            northNeighbour.getRoad().destroyConnection(RoadDirection.NORTH.getOpposite());
        }
        if (southNeighbour != null && southNeighbour.getTerrainType() == TerrainType.ROAD && southNeighbour.getRoad() != null) {
            southNeighbour.getRoad().destroyConnection(RoadDirection.SOUTH.getOpposite());
        }
        if (eastNeighbour != null && eastNeighbour.getTerrainType() == TerrainType.ROAD && eastNeighbour.getRoad() != null) {
            eastNeighbour.getRoad().destroyConnection(RoadDirection.EAST.getOpposite());
        }
        if (westNeighbour != null && westNeighbour.getTerrainType() == TerrainType.ROAD && westNeighbour.getRoad() != null) {
            westNeighbour.getRoad().destroyConnection(RoadDirection.WEST.getOpposite());
        }
    }

    public void setWorld(World world) { this.world = world;}
}
