package world;

import engine.BuildManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.tile.Point;
import world.tile.TerrainType;
import world.tile.Tile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WorldTest {

    private World world;

    @BeforeEach
    public void setUp() {
        world = new World(20, 20);
    }

    @Test
    public void testWorldInitialization() {
        assertEquals(20, world.getRows());
        assertEquals(20, world.getCols());
        assertEquals(20000, world.getMoney());
        assertEquals(0, world.getElapsedTime());
    }

    @Test
    public void testMoneyManagement() {
        int initialMoney = world.getMoney();

        world.receiveMoney(5000);
        assertEquals(initialMoney + 5000, world.getMoney());

        world.spendMoney(2000);
        assertEquals(initialMoney + 3000, world.getMoney());
    }

    @Test
    public void testGetTileValidAndInvalid() {
        assertNotNull(world.get(0, 0));
        assertNotNull(world.get(19, 19));

        assertNull(world.get(-1, 0));
        assertNull(world.get(0, -1));
        assertNull(world.get(20, 0));
        assertNull(world.get(0, 20));
    }

    @Test
    public void testIsValidTile() {
        assertTrue(world.isValidTile(0, 0));
        assertTrue(world.isValidTile(19, 19));
        assertTrue(world.isValidTile(-1, -1));
    }

    @Test
    public void testTimeProgression() throws Exception {
        int initialDays = world.getElapsedTime();
        int ticksToAdvance = world.getTicksPerDay();

        for (int i = 0; i < ticksToAdvance; i++) {
            world.increaseTickCounter();
        }

        assertEquals(initialDays + 1, world.getElapsedTime());
    }

    @Test
    public void testPathfindingOnStraightRoad() throws Exception {
        BuildManager buildManager = new BuildManager(world);
        Tile t1 = world.get(0, 0);
        Tile t2 = world.get(0, 1);
        Tile t3 = world.get(0, 2);

        buildManager.buildRoad(t1);
        buildManager.buildRoad(t2);
        buildManager.buildRoad(t3);

        List<Point> path = world.findPathRoad(t1, t3);

        assertNotNull(path);
        assertEquals(3, path.size());
    }

    @Test
    public void testPathfindingWithNoConnection() throws Exception {
        BuildManager buildManager = new BuildManager(world);
        Tile t1 = world.get(5, 5);
        Tile t2 = world.get(7, 7);

        buildManager.buildRoad(t1);
        buildManager.buildRoad(t2);

        List<Point> path = world.findPathRoad(t1, t2);

        assertNull(path);
    }

    @Test
    public void testFindPathInvalidDestination() {
        Tile t1 = world.get(0, 0);
        Tile t2 = world.get(0, 1);
        t2.setTerrainType(TerrainType.LAND);

        Exception exception = assertThrows(Exception.class, () -> {
            world.findPathRoad(t1, t2);
        });

        assertEquals("The destination isn't on the road!", exception.getMessage());
    }
}