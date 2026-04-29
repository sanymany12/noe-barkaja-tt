package engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.World;
import world.tile.Point;
import world.tile.TerrainType;
import world.tile.Tile;

import static org.junit.jupiter.api.Assertions.*;

public class ForestManagerTest {

    private World world;
    private ForestManager manager;

    @BeforeEach
    public void setUp() {

        world = new World(50, 50);
        world.initWorld();
        manager = new ForestManager(world);
    }


    @Test
    public void testSetChances_ValidValues_UpdatesCorrectly() {
        manager.setChanceToGrow(50);
        manager.setChanceToSpread(75);

        assertEquals(50, manager.getChanceToGrow(), "A növekedési esély nem frissült!");
        assertEquals(75, manager.getChanceToSpread(), "A terjedési esély nem frissült!");
    }

    @Test
    public void testSetChances_InvalidValues_ThrowsIllegalArgumentException() {

        assertThrows(IllegalArgumentException.class, () -> manager.setChanceToGrow(0));
        assertThrows(IllegalArgumentException.class, () -> manager.setChanceToGrow(-5));
        assertThrows(IllegalArgumentException.class, () -> manager.setChanceToGrow(101));


        assertThrows(IllegalArgumentException.class, () -> manager.setChanceToSpread(0));
        assertThrows(IllegalArgumentException.class, () -> manager.setChanceToSpread(105));
    }


    @Test
    public void testUpdateForests_GuaranteedGrowth_WhenTreeCountBetween1And3() {

        manager.setChanceToGrow(100);
        Tile testTile = world.get(2, 2);
        testTile.setTreeCount(2);


        manager.updateForests();

        assertEquals(3, testTile.getTreeCount(), "A fák számának 2-ről 3-ra kellett volna nőnie!");
    }

    @Test
    public void testUpdateForests_GuaranteedSpread_CreatesNewTreesInCardinalDirections() {

        manager.setChanceToSpread(100);

        Tile centerTile = world.get(2, 2);
        centerTile.setTreeCount(4);

        world.get(2, 1).setTreeCount(0);
        world.get(2, 1).setTerrainType(TerrainType.LAND); // Észak

        world.get(2, 3).setTreeCount(0);
        world.get(2, 3).setTerrainType(TerrainType.LAND); // Dél

        world.get(1, 2).setTreeCount(0);
        world.get(1, 2).setTerrainType(TerrainType.LAND); // Nyugat

        world.get(3, 2).setTreeCount(0);
        world.get(3, 2).setTerrainType(TerrainType.LAND); // Kelet


        manager.updateForests();

        assertEquals(4, centerTile.getTreeCount());


        assertEquals(1, world.get(2, 1).getTreeCount(), "Északra nem terjedt át az erdő!");
        assertEquals(1, world.get(2, 3).getTreeCount(), "Délre nem terjedt át az erdő!");
        assertEquals(1, world.get(1, 2).getTreeCount(), "Nyugatra nem terjedt át az erdő!");
        assertEquals(1, world.get(3, 2).getTreeCount(), "Keletre nem terjedt át az erdő!");


        assertEquals(0, world.get(1, 1).getTreeCount(), "Átlósan (Észak-Nyugat) nem szabadna terjednie!");
    }

    @Test
    public void testUpdateForests_DoesNotSpreadToWaterOrBuildings() {

        manager.setChanceToSpread(100);

        Tile centerTile = world.get(2, 2);
        centerTile.setTreeCount(4);

        Tile north = world.get(2, 1);
        north.setTerrainType(TerrainType.WATER);
        north.setTreeCount(0);

        Tile south = world.get(2, 3);
        south.setTerrainType(TerrainType.LAND);
        south.setTreeCount(0);

        Tile east = world.get(3, 2);
        east.setTerrainType(TerrainType.LAND);
        east.setTreeCount(2);


        manager.updateForests();

        assertEquals(0, north.getTreeCount(), "Vízre nem terjedhet erdő!");
        assertEquals(2, east.getTreeCount(), "A már fás csempét nem írhatja felül az új terjedés!");

    }
}