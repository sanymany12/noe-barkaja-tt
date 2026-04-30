package engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.World;
import world.building.BuildingType;
import world.building.BusStop;
import world.building.Station;
import world.tile.Point;
import world.tile.TerrainType;
import world.tile.Tile;
import world.tile.road.BridgeType;
import world.tile.road.RoadDirection;
import world.vehicle.VehicleType;

import static org.junit.jupiter.api.Assertions.*;

public class BuildManagerTest {

    private World world;
    private BuildManager buildManager;

    @BeforeEach
    public void setUp() {
        world = new World(20, 20);
        world.initWorld();
        buildManager = new BuildManager(world);
    }

    @Test
    public void testBuildRoad_CreatesRoadAndDeductsMoney() {
        Tile tile = world.get(5, 5);
        tile.setTerrainType(TerrainType.LAND);
        tile.setTreeCount(2);

        int initialMoney = world.getMoney();
        buildManager.buildRoad(tile, false);

        assertNotNull(tile.getRoad());
        assertEquals(TerrainType.ROAD, tile.getTerrainType());
        assertTrue(world.getMoney() < initialMoney);
    }

    @Test
    public void testBuildRoad_ConnectsToAdjacentRoad() {
        Tile tile1 = world.get(5, 5);
        Tile tile2 = world.get(5, 6);

        buildManager.buildRoad(tile1, false);
        buildManager.buildRoad(tile2, false);

        assertTrue(tile1.getRoad().canGo(RoadDirection.SOUTH));
        assertTrue(tile2.getRoad().canGo(RoadDirection.NORTH));
    }

    @Test
    public void testBuildStation_ValidPlacement() {
        Tile roadTile = world.get(5, 5);
        buildManager.buildRoad(roadTile, false);

        Tile buildingTile = world.get(5, 4);
        buildingTile.setTerrainType(TerrainType.BUILDING);
        buildingTile.setBuilding(new BusStop(world, RoadDirection.SOUTH));

        Tile targetTile = world.get(5, 5);

        buildManager.buildStation(targetTile, RoadDirection.NORTH, true);

        assertNotNull(targetTile.getBuilding());
        assertEquals(TerrainType.STOP, targetTile.getTerrainType());
    }

    @Test
    public void testBuildBridge_SingleCell_ThrowsException() {
        Tile start = world.get(5, 5);

        Exception exception = assertThrows(Exception.class, () -> {
            buildManager.buildBridge(start, start, BridgeType.WOOD, false);
        });

        assertEquals("A híd legalább 2 cella hosszú kell, hogy legyen!", exception.getMessage());
    }

    @Test
    public void testBuildBridge_Diagonal_ThrowsException() {
        Tile start = world.get(5, 5);
        Tile end = world.get(6, 6);

        Exception exception = assertThrows(Exception.class, () -> {
            buildManager.buildBridge(start, end, BridgeType.WOOD, false);
        });

        assertEquals("A híd csak egyenes vonalban építhető!", exception.getMessage());
    }


    @Test
    public void testBuyVehicle_InvalidTile_ThrowsException() {
        Tile landTile = world.get(5, 5);
        landTile.setTerrainType(TerrainType.LAND);

        Exception exception = assertThrows(Exception.class, () -> {
            buildManager.buyVehicle(landTile, VehicleType.BUS);
        });

        assertEquals("Ide nem tudsz vasarolni!", exception.getMessage());
    }

    @Test
    public void testBuyVehicle_ValidStation_CreatesVehicle() throws Exception {
        Tile roadTile = world.get(5, 5);
        buildManager.buildRoad(roadTile, false);

        Tile buildingTile = world.get(5, 4);
        buildingTile.setTerrainType(TerrainType.BUILDING);
        buildingTile.setBuilding(new world.building.Farm(world));

        Tile stationTile = world.get(5, 5);
        buildManager.buildStation(stationTile, RoadDirection.NORTH, false);

        int initialMoney = world.getMoney();
        int initialVehicleCount = world.getVehicles().size();

        buildManager.buyVehicle(stationTile, VehicleType.BUS);

        assertEquals(initialVehicleCount + 1, world.getVehicles().size());
        assertTrue(world.getMoney() < initialMoney);
    }
}