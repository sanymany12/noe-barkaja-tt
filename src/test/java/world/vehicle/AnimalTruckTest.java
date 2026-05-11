package world.vehicle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.World;
import world.building.CloningFacility;
import world.building.Enclosure;
import world.building.Silo;
import world.building.Station;
import world.resources.AnimalType;
import world.tile.Point;
import world.tile.TerrainType;
import world.tile.Tile;
import world.tile.road.Road;
import world.tile.road.RoadDirection;

import static org.junit.jupiter.api.Assertions.*;

public class AnimalTruckTest {
    private World world;
    private AnimalTruck at;

    @BeforeEach
    public void setUp() throws Exception {
        world = new World(40, 40);

        Tile t1 = world.get(0, 0);
        Tile t2 = world.get(1, 0);
        Tile t3 = world.get(2, 0);
        Tile t4 = world.get(3, 0);
        Tile t5 = world.get(4, 0);
        Tile t6 = world.get(3, 3);

        // Setup world
        t6.setBuilding(new Silo(world));
        t6.setTerrainType(TerrainType.BUILDING);
        t1.setBuilding(new CloningFacility(world));
        t1.setTerrainType(TerrainType.BUILDING);
        t2.setBuilding(new Station(world, t1.getBuilding(), RoadDirection.WEST, true));
        t2.setTerrainType(TerrainType.STOP);
        t3.setRoad(new Road(t3.getCoordinate().x, t3.getCoordinate().y, true));
        t3.setTerrainType(TerrainType.ROAD);
        t5.setBuilding(new Enclosure(world, new Point(t6.getCoordinate().x, t6.getCoordinate().y)));
        t5.setTerrainType(TerrainType.BUILDING);
        t4.setBuilding(new Station(world, t5.getBuilding(), RoadDirection.EAST, true));
        t4.setTerrainType(TerrainType.STOP);

        at = new AnimalTruck(world, new Point(t2.getCoordinate().x, t2.getCoordinate().y));
        ((Station) t2.getBuilding()).vehicleArrives(at);
    }

    @Test
    public void testConstructor() {
        assertTrue(at.isEmpty());
        assertEquals(VehicleType.ANIMALTRUCK, at.getVehicleType());
        assertEquals(VehicleType.ANIMALTRUCK.getBaseSpeed(), at.getSpeed());
    }

    @Test
    public void testLoadFromEnclosure() {
        Tile t5 = world.get(4, 0);

        Enclosure e = (Enclosure) t5.getBuilding();
        e.newSpeciesArrives(AnimalType.BEAR);

        at.loadFrom(e);

        assertFalse(at.isEmpty());
        assertEquals(AnimalType.BEAR, at.getCargoType());
    }

    @Test
    public void testUnloadToCloningFacility() {
        Tile t1 = world.get(0, 0);

        CloningFacility cf = (CloningFacility) t1.getBuilding();

        Tile t5 = world.get(4, 0);

        Enclosure e = (Enclosure) t5.getBuilding();
        e.newSpeciesArrives(AnimalType.BEAR);

        at.loadFrom(e);

        assertFalse(at.isEmpty());
        assertEquals(AnimalType.BEAR, at.getCargoType());

        at.unloadTo(cf);

        assertTrue(at.isEmpty());
    }
}
