package world.tile.road;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.building.Building;
import world.tile.Point;
import world.tile.road.Road;
import world.tile.road.RoadDirection;
import world.vehicle.Vehicle;

import static org.junit.jupiter.api.Assertions.*;

public class RoadTest {

    private Road road;

    // Teszteléshez használt segédosztály a járművek helyettesítésére
    private class DummyVehicle extends Vehicle {
        public boolean isSold = false;

        public DummyVehicle() throws Exception{
            super(null, new Point(0, 0));
        }

        @Override
        public void sellVehicle() {
            this.isSold = true;
        }

        // Szükséges absztrakt metódusok implementálása (ha vannak)
        @Override public void move() {}

        @Override
        public void loadFrom(Building building) throws Exception {

        }

        @Override
        public void unloadTo(Building building) throws Exception {

        }

        @Override public String getSpriteName() { return "dummy"; }
    }

    @BeforeEach
    public void setUp() {
        road = new Road(5, 5, true);
    }

    @Test
    public void testInitialState() {
        assertEquals(5, road.getCostToBuild());
        assertTrue(road.getIsPreBuilt());
        assertFalse(road.getIsBridge());

        assertNull(road.getRightLaneV());
        assertNull(road.getLeftLaneV());
        assertNull(road.getRightLaneH());
        assertNull(road.getLeftLaneH());

        assertFalse(road.canGo(RoadDirection.NORTH));
        assertFalse(road.canGo(RoadDirection.SOUTH));
        assertFalse(road.canGo(RoadDirection.EAST));
        assertFalse(road.canGo(RoadDirection.WEST));
    }

    @Test
    public void testConnectionManagement() {
        road.setConnection(RoadDirection.NORTH);
        assertTrue(road.canGo(RoadDirection.NORTH));
        assertFalse(road.canGo(RoadDirection.SOUTH));

        road.setConnection(RoadDirection.NORTH);

        road.destroyConnection(RoadDirection.NORTH);
        assertFalse(road.canGo(RoadDirection.NORTH));

        assertDoesNotThrow(() -> road.destroyConnection(RoadDirection.SOUTH));
    }

    @Test
    public void testVehicleEntersAndLeaves() {
        try{
            DummyVehicle vehicle = new DummyVehicle();
            road.vehicleEnters(vehicle, RoadDirection.NORTH);
            assertEquals(vehicle, road.getRightLaneV());
            assertNull(road.getLeftLaneV());

            road.vehicleLeaves(vehicle, RoadDirection.NORTH);
            assertNull(road.getRightLaneV());

            road.vehicleEnters(vehicle, RoadDirection.SOUTH);
            assertEquals(vehicle, road.getLeftLaneV());

            road.vehicleEnters(vehicle, RoadDirection.EAST);
            assertEquals(vehicle, road.getLeftLaneH());

            road.vehicleEnters(vehicle, RoadDirection.WEST);
            assertEquals(vehicle, road.getRightLaneH());
        }catch (Exception e){}



    }

    @Test
    public void testIsOccupied_NorthDirection() {
        assertFalse(road.isOccupied(RoadDirection.NORTH));
        try{
            DummyVehicle vehicle = new DummyVehicle();

            road.vehicleEnters(vehicle, RoadDirection.NORTH);
            assertTrue(road.isOccupied(RoadDirection.NORTH));
            road.vehicleLeaves(vehicle, RoadDirection.NORTH);

            road.vehicleEnters(vehicle, RoadDirection.EAST);
            assertTrue(road.isOccupied(RoadDirection.NORTH));
            road.vehicleLeaves(vehicle, RoadDirection.EAST);

            road.vehicleEnters(vehicle, RoadDirection.WEST);
            assertTrue(road.isOccupied(RoadDirection.NORTH));
            road.vehicleLeaves(vehicle, RoadDirection.WEST);

            road.vehicleEnters(vehicle, RoadDirection.SOUTH);
            assertFalse(road.isOccupied(RoadDirection.NORTH));
        }catch (Exception e){}

    }

    @Test
    public void testGetsDestroyed_SellsAllVehicles() {
        try{
            DummyVehicle v1 = new DummyVehicle();
            DummyVehicle v2 = new DummyVehicle();
            DummyVehicle v3 = new DummyVehicle();
            DummyVehicle v4 = new DummyVehicle();

            road.vehicleEnters(v1, RoadDirection.NORTH);
            road.vehicleEnters(v2, RoadDirection.SOUTH);
            road.vehicleEnters(v3, RoadDirection.EAST);
            road.vehicleEnters(v4, RoadDirection.WEST);

            road.getsDestroyed();

            assertTrue(v1.isSold);
            assertTrue(v2.isSold);
            assertTrue(v3.isSold);
            assertTrue(v4.isSold);
        }catch (Exception e){}

    }

    @Test
    public void testGetSpriteName_EmptyRoad() {
        assertEquals("road-0-", road.getSpriteName());
    }

    @Test
    public void testGetSpriteName_TwoConnections() {
        road.setConnection(RoadDirection.NORTH);
        road.setConnection(RoadDirection.EAST);

        assertEquals("road-2-ne", road.getSpriteName());
    }

    @Test
    public void testGetSpriteName_FourConnections() {
        road.setConnection(RoadDirection.NORTH);
        road.setConnection(RoadDirection.SOUTH);
        road.setConnection(RoadDirection.EAST);
        road.setConnection(RoadDirection.WEST);

        assertEquals("road-4-nwse", road.getSpriteName());
    }
}