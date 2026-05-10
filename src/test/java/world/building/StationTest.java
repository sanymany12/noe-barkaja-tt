package world.building;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.World;
import world.building.Building;
import world.building.BuildingType;
import world.building.Station;
import world.resources.ResourceType;
import world.tile.Point;
import world.tile.TerrainType;
import world.tile.Tile;
import world.tile.road.RoadDirection;
import world.vehicle.Vehicle;
import world.vehicle.VehicleType;

import static org.junit.jupiter.api.Assertions.*;

public class StationTest {

    private World world;

    private class DummyBuilding extends Building<Object, Object> {
        public DummyBuilding(World world, BuildingType type) {
            super(world);
            this.type = type;
        }
        @Override public void newDay() {}
        @Override public String getSpriteName() { return "dummy"; }
    }

    private class DummyVehicle extends Vehicle {
        public VehicleType vType;
        public ResourceType cargoType;
        public boolean empty = true;
        public boolean full = false;

        public boolean loadCalled = false;
        public boolean unloadCalled = false;

        public DummyVehicle(World world, VehicleType type) throws Exception {
            super(world, new Point(0,0));
            this.vType = type;
        }

        @Override public VehicleType getVehicleType() { return vType; }
        @Override public boolean isEmpty() { return empty; }
        @Override public boolean isFull() { return full; }
        @Override public ResourceType getCargoType() { return cargoType; }

        @Override public void loadFrom(Building b) { loadCalled = true; }
        @Override public void unloadTo(Building b) { unloadCalled = true; }

        @Override public void move() {}
        @Override public String getSpriteName() { return "dummy-v"; }
    }

    @BeforeEach
    public void setUp() {
        world = new World(20, 20);
    }

    @Test
    public void testInitialState() {
        DummyBuilding farm = new DummyBuilding(world, BuildingType.FARM);
        Station station = new Station(world, farm, RoadDirection.WEST, true);

        assertEquals(RoadDirection.WEST, station.getDirection());
        assertEquals(300, station.getCostToBuild());
        assertTrue(station.getIsPreBuilt());
        assertFalse(station.isOccupied());
        assertNull(station.getVehicle());
        assertNull(station.getConnectedRoad());
    }

    @Test
    public void testSetConnectedRoad_AndInitAfterLoad() {
        DummyBuilding farm = new DummyBuilding(world, BuildingType.FARM);
        Station station = new Station(world, farm, RoadDirection.NORTH, false);

        Tile roadTile = world.get(5, 5);
        roadTile.setTerrainType(TerrainType.ROAD);

        station.setConnectedRoad(roadTile);
        assertEquals(roadTile, station.getConnectedRoad());

        station.initAfterLoad();
        assertEquals(roadTile, station.getConnectedRoad());
    }

    @Test
    public void testVehicleArrives_FoodTruckAtFarm_Empty_Loads() throws Exception {
        DummyBuilding farm = new DummyBuilding(world, BuildingType.FARM);
        Station station = new Station(world, farm, RoadDirection.EAST, false);

        DummyVehicle truck = new DummyVehicle(world, VehicleType.FOODTRUCK);
        truck.empty = true;

        station.vehicleArrives(truck);

        assertTrue(truck.loadCalled);
        assertFalse(truck.unloadCalled);
        assertEquals(truck, station.getVehicle());
        assertTrue(station.isOccupied());
    }

    @Test
    public void testVehicleArrives_FoodTruckAtFarm_WithGrain_LoadsMore() throws Exception {
        DummyBuilding farm = new DummyBuilding(world, BuildingType.FARM);
        Station station = new Station(world, farm, RoadDirection.EAST, false);

        DummyVehicle truck = new DummyVehicle(world, VehicleType.FOODTRUCK);
        truck.empty = false;
        truck.full = false;
        truck.cargoType = ResourceType.GRAIN;

        station.vehicleArrives(truck);

        assertTrue(truck.loadCalled);
    }

    @Test
    public void testVehicleArrives_FoodTruckAtAgPlant_WithGrain_Unloads() throws Exception {
        DummyBuilding plant = new DummyBuilding(world, BuildingType.AGRICULTURALPLANT);
        Station station = new Station(world, plant, RoadDirection.NORTH, false);

        DummyVehicle truck = new DummyVehicle(world, VehicleType.FOODTRUCK);
        truck.empty = false;
        truck.cargoType = ResourceType.GRAIN;

        station.vehicleArrives(truck);

        assertTrue(truck.unloadCalled);
        assertFalse(truck.loadCalled);
    }

    @Test
    public void testVehicleArrives_FoodTruckAtSilo_WithFood_Unloads() throws Exception {
        DummyBuilding silo = new DummyBuilding(world, BuildingType.SILO);
        Station station = new Station(world, silo, RoadDirection.SOUTH, false);

        DummyVehicle truck = new DummyVehicle(world, VehicleType.FOODTRUCK);
        truck.empty = false;
        truck.cargoType = ResourceType.FOOD;

        station.vehicleArrives(truck);

        assertTrue(truck.unloadCalled);
    }

    @Test
    public void testVehicleArrives_AnimalTruckAtEnclosure_Empty_Loads() throws Exception {
        DummyBuilding enclosure = new DummyBuilding(world, BuildingType.ENCLOSURE);
        Station station = new Station(world, enclosure, RoadDirection.WEST, false);

        DummyVehicle truck = new DummyVehicle(world, VehicleType.ANIMALTRUCK);
        truck.empty = true;

        station.vehicleArrives(truck);

        assertTrue(truck.loadCalled);
    }

    @Test
    public void testVehicleArrives_AnimalTruckAtEnclosure_NotEmpty_Unloads() throws Exception {
        DummyBuilding enclosure = new DummyBuilding(world, BuildingType.ENCLOSURE);
        Station station = new Station(world, enclosure, RoadDirection.WEST, false);

        DummyVehicle truck = new DummyVehicle(world, VehicleType.ANIMALTRUCK);
        truck.empty = false;

        station.vehicleArrives(truck);

        assertTrue(truck.unloadCalled);
    }

    @Test
    public void testVehicleLeaves() throws Exception {
        DummyBuilding farm = new DummyBuilding(world, BuildingType.FARM);
        Station station = new Station(world, farm, RoadDirection.NORTH, false);
        DummyVehicle truck = new DummyVehicle(world, VehicleType.FOODTRUCK);

        station.vehicleArrives(truck);
        assertTrue(station.isOccupied());

        station.vehicleLeaves();
        assertFalse(station.isOccupied());
        assertNull(station.getVehicle());
    }

    @Test
    public void testGetSpriteName_Directions() {
        DummyBuilding farm = new DummyBuilding(world, BuildingType.FARM);

        Station stationN = new Station(world, farm, RoadDirection.NORTH, false);
        assertEquals("industrial-stop-n", stationN.getSpriteName());

        Station stationS = new Station(world, farm, RoadDirection.SOUTH, false);
        assertEquals("industrial-stop-s", stationS.getSpriteName());

        Station stationE = new Station(world, farm, RoadDirection.EAST, false);
        assertEquals("industrial-stop-e", stationE.getSpriteName());

        Station stationW = new Station(world, farm, RoadDirection.WEST, false);
        assertEquals("industrial-stop-w", stationW.getSpriteName());
    }
}