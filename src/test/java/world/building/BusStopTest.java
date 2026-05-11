package world.building;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.World;
import world.building.BusStop;
import world.building.Building;
import world.tile.Point;
import world.tile.TerrainType;
import world.tile.Tile;
import world.tile.road.RoadDirection;
import world.vehicle.Vehicle;
import world.vehicle.VehicleType;

import static org.junit.jupiter.api.Assertions.*;

public class BusStopTest {

    private World world;
    private BusStop busStop;

    // --- SEGÉDOSZTÁLYOK A TESZTELÉSHEZ ---

    // Dummy Busz szimulálása
    private class DummyBus extends Vehicle {
        public boolean isEmpty = true;
        public boolean loadCalled = false;
        public boolean unloadCalled = false;

        public DummyBus(World world) throws Exception { super(world, new Point(0,0)); }

        @Override public void move() {}
        @Override public String getSpriteName() { return "dummy-bus"; }
        @Override public VehicleType getVehicleType() { return VehicleType.BUS; }
        @Override public boolean isEmpty() { return isEmpty; }

        @Override
        public void loadFrom(Building b) {
            loadCalled = true;
            isEmpty = false;
        }

        @Override
        public void unloadTo(Building b) {
            unloadCalled = true;
            isEmpty = true;
        }

    }

    // Dummy Kamion szimulálása (amit a megállónak ignorálnia kell)
    private class DummyTruck extends Vehicle {
        public DummyTruck(World world) throws Exception { super(world, new Point(0,0)); }
        @Override public void move() {}
        @Override public String getSpriteName() { return "dummy-truck"; }
        @Override public VehicleType getVehicleType() { return VehicleType.FOODTRUCK; }
        @Override
        public void loadFrom(Building building){}
        @Override
        public void unloadTo(Building building){}
    }

    @BeforeEach
    public void setUp() {
        world = new World(40, 40);
        busStop = new BusStop(world, RoadDirection.NORTH);
    }



    @Test
    public void testInitialState() {
        assertEquals(RoadDirection.NORTH, busStop.getDirection());
        assertEquals(0, busStop.getNumOfPeople());
        assertFalse(busStop.isStart());
        assertFalse(busStop.isStop());
        assertFalse(busStop.isOccupied());
        assertNull(busStop.getConnectedRoad());
    }

    @Test
    public void testSetConnectedRoad_AndInitAfterLoad() {

        Tile roadTile = world.get(5, 5);
        roadTile.setTerrainType(TerrainType.ROAD);


        busStop.setConnectedRoad(roadTile);


        assertEquals(roadTile, busStop.getConnectedRoad(), "Az út nem kapcsolódott a megállóhoz!");

        // Szimuláljuk a betöltést (amikor a Gson miatt a connectedRoad elveszik, de a Point megvan)
        busStop.initAfterLoad();

        assertEquals(roadTile, busStop.getConnectedRoad(), "A betöltés után nem kötődött vissza az út referenciája!");
    }



    @Test
    public void testSetAsStart_Success_GeneratesPeople() throws Exception {
        busStop.setAsStart();

        assertTrue(busStop.isStart(), "A megállónak kiindulópontnak kell lennie!");
        assertFalse(busStop.isStop());
        // A ThreadLocalRandom.current().nextInt(1,20) miatt 1 és 19 közötti utast generál
        assertTrue(busStop.getNumOfPeople() >= 1 && busStop.getNumOfPeople() < 20,
                "Az utasok számának 1 és 19 közé kell esnie! Jelenlegi: " + busStop.getNumOfPeople());
    }

    @Test
    public void testSetAsStart_WhenAlreadyStop_ThrowsException() throws Exception {
        busStop.setAsStop();

        Exception exception = assertThrows(Exception.class, () -> {
            busStop.setAsStart();
        });

        assertEquals("This stop is already assigned as a destination!", exception.getMessage());
    }

    @Test
    public void testSetAsStop_Success() throws Exception {
        busStop.setAsStop();

        assertTrue(busStop.isStop(), "A megállónak célpontnak kell lennie!");
        assertFalse(busStop.isStart());
    }

    @Test
    public void testSetAsStop_WhenAlreadyStart_ThrowsException() throws Exception {
        busStop.setAsStart();

        Exception exception = assertThrows(Exception.class, () -> {
            busStop.setAsStop();
        });

        assertEquals("This stop is already assigned as a starting point!", exception.getMessage());
    }

    @Test
    public void testResetStop_ClearsState() throws Exception {
        busStop.setAsStart();
        busStop.resetStop();

        assertFalse(busStop.isStart());
        assertFalse(busStop.isStop());
        assertEquals(0, busStop.getNumOfPeople());
    }



    @Test
    public void testLoadOntoBus_ResetsPeopleAndState() throws Exception {
        busStop.setAsStart();
        assertTrue(busStop.getNumOfPeople() > 0);

        busStop.loadOntoBus();

        assertEquals(0, busStop.getNumOfPeople(), "A felszállás után az utasok számának nullázódnia kell!");
        assertFalse(busStop.isStart(), "A felszállás után meg kell szűnnie kiindulópontnak lenni!");
    }

    @Test
    public void testPeopleArrived_AddsMoneyToWorld() throws Exception {
        busStop.setAsStop();
        int initialMoney = world.getMoney();

        // 10 utas érkezik (10 * 50 = 500 pénz)
        busStop.peopleArrived(10);

        assertEquals(initialMoney + 500, world.getMoney(), "A jegyárat nem írta jóvá a World-ben!");
        assertFalse(busStop.isStop(), "Az érkezés után meg kell szűnnie célpontnak lenni!");
    }



    @Test
    public void testVehicleArrives_Truck_IsIgnored() throws Exception {
        busStop.setAsStart(); // Emberek várnak
        DummyTruck truck = new DummyTruck(world);

        busStop.vehicleArrives(truck);

        assertTrue(busStop.isOccupied(), "A megálló foglalt kell legyen, amíg ott áll a teherautó!");
        assertTrue(busStop.isStart(), "A teherautó nem veheti fel a buszra váró utasokat!");
    }

    @Test
    public void testVehicleArrives_EmptyBusAtStart_LoadsPassengers() throws Exception {
        busStop.setAsStart();
        DummyBus bus = new DummyBus(world);

        busStop.vehicleArrives(bus);

        assertTrue(bus.loadCalled, "A megállónak utasítania kell a buszt a felszállásra!");
    }

    @Test
    public void testVehicleArrives_FullBusAtStop_UnloadsPassengers() throws Exception {
        busStop.setAsStop();
        DummyBus bus = new DummyBus(world);
        bus.isEmpty = false; // Tele van a busz

        busStop.vehicleArrives(bus);

        assertTrue(bus.unloadCalled, "A megállónak utasítania kell a tele buszt a leszállásra!");
    }

    @Test
    public void testVehicleLeaves_FreesStop() throws Exception {
        DummyBus bus = new DummyBus(world);
        busStop.vehicleArrives(bus);

        busStop.vehicleLeaves();

        assertFalse(busStop.isOccupied(), "A jármű távozása után a megállónak fel kell szabadulnia!");
    }



    @Test
    public void testGetSpriteName_Combinations() throws Exception {
        BusStop stopNorth = new BusStop(world, RoadDirection.NORTH);
        assertEquals("bus-stop-s-", stopNorth.getSpriteName());

        stopNorth.setAsStart();
        assertEquals("bus-stop-s-start", stopNorth.getSpriteName());

        stopNorth.resetStop();
        stopNorth.setAsStop();
        assertEquals("bus-stop-s-stop", stopNorth.getSpriteName());

        BusStop stopEast = new BusStop(world, RoadDirection.EAST);
        assertEquals("bus-stop-w-", stopEast.getSpriteName());
    }
}