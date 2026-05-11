package world.vehicle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.World;
import world.building.*;
import world.resources.ResourceType;
import world.tile.Point;
import world.tile.TerrainType;
import world.tile.Tile;
import world.tile.road.Road;
import world.tile.road.RoadDirection;

import static org.junit.jupiter.api.Assertions.*;

public class FoodTruckTest {
    private World world;
    private FoodTruck  ft;

    @BeforeEach
    public void setUp() throws Exception {
        world = new World(20, 20);

        Tile t1 = world.get(0, 0);
        Tile t2 = world.get(1, 0);
        Tile t3 = world.get(2, 0);
        Tile t4 = world.get(3, 0);
        Tile t5 = world.get(4, 0);

        // Setup world
        t1.setBuilding(new Farm(world));
        t1.setTerrainType(TerrainType.BUILDING);
        t2.setBuilding(new Station(world, t1.getBuilding(), RoadDirection.WEST, true));
        t2.setTerrainType(TerrainType.STOP);
        t3.setRoad(new Road(t3.getCoordinate().x, t3.getCoordinate().y, true));
        t3.setTerrainType(TerrainType.ROAD);
        t5.setBuilding(new AgriculturalPlant(world));
        t4.setBuilding(new Station(world, t5.getBuilding(), RoadDirection.EAST, true));
        t4.setTerrainType(TerrainType.STOP);

        ft = new FoodTruck(world, new Point(t4.getCoordinate().x, t4.getCoordinate().y));
        ((Station) t4.getBuilding()).vehicleArrives(ft);
    }

    @Test
    public void testConstructor() {
        assertTrue(ft.isEmpty());
        assertEquals(VehicleType.FOODTRUCK, ft.getVehicleType());
        assertEquals(VehicleType.FOODTRUCK.getBaseSpeed(), ft.getSpeed());
    }

    @Test
    public void testLoadFromFarm() throws Exception {
        Tile t1 = world.get(0, 0);

        Farm farm = (Farm) t1.getBuilding();

        world.newDay();

        ft.loadFrom(farm);

        assertFalse(ft.isEmpty());
        assertEquals(ResourceType.GRAIN, ft.getCargoType());
    }

    @Test
    public void testLoadFromAgriculturalPlant() throws Exception {
        Tile t5 = world.get(4, 0);

        AgriculturalPlant agriculturalplant = (AgriculturalPlant) t5.getBuilding();

        agriculturalplant.loadFromTruck(40);

        world.newDay();

        ft.loadFrom(agriculturalplant);
        assertFalse(ft.isEmpty());
        assertEquals(ResourceType.FOOD, ft.getCargoType());
        assertEquals(30, ft.getCurrentCargoNum());
    }

    @Test
    public void testUnloadToSilo() throws Exception {
        Tile t5 = world.get(4, 0);
        Tile t6 = world.get(5, 5);

        t6.setBuilding(new Silo(world));
        t6.setTerrainType(TerrainType.BUILDING);

        AgriculturalPlant ap = (AgriculturalPlant) t5.getBuilding();
        Silo silo = (Silo) t6.getBuilding();

        ap.loadFromTruck(40);

        world.newDay();

        ft.loadFrom(ap);

        assertFalse(ft.isEmpty());
        assertEquals(ResourceType.FOOD, ft.getCargoType());
        assertEquals(30, ft.getCurrentCargoNum());

        ft.unloadTo(silo);

        assertTrue(ft.isEmpty());
    }
}
