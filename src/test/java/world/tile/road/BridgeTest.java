package world.tile.road;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.tile.Point;
import world.tile.TerrainType;
import world.tile.Tile;
import world.tile.road.Bridge;
import world.tile.road.BridgeType;
import world.tile.road.RoadDirection;

import static org.junit.jupiter.api.Assertions.*;

public class BridgeTest {

    private Tile startTile;
    private Tile endTile;
    private Bridge bridge;

    @BeforeEach
    public void setUp() {
        // Létrehozunk két dummy csempét a kezdő és végpontnak
        startTile = new Tile(new Point(2, 2), TerrainType.LAND, 0, null, null, false);
        endTile = new Tile(new Point(5, 2), TerrainType.LAND, 0, null, null, false);

        // Létrehozzuk magát a hidat
        bridge = new Bridge(3, 2, BridgeType.WOOD, RoadDirection.EAST, startTile, endTile, false, true);
    }

    @Test
    public void testBridgeInitialization() {
        // Ellenőrizzük, hogy az ősosztály (Road) változóit jól állította-e be
        assertTrue(bridge.getIsBridge(), "Az isBridge értéknek true-nak kell lennie!");
        assertTrue(bridge.getIsPreBuilt(), "Az isPreBuilt értéknek true-nak kell lennie, mert így példányosítottuk!");

        // Ellenőrizzük a Bridge saját változóit
        assertEquals(BridgeType.WOOD, bridge.getType());
        assertEquals(RoadDirection.EAST, bridge.getDirection());
        assertFalse(bridge.getIsEnd(), "Ez a híd-elem most nem a híd vége!");
    }

    @Test
    public void testPositionCapturingForSerialization() {
        // Ellenőrizzük, hogy a transient Tile-okból sikeresen kimentette-e a Point-okat
        assertNotNull(bridge.getStartTilePos(), "A kezdő csempe pozíciója nem lehet null!");
        assertNotNull(bridge.getEndTilePos(), "A végcsempe pozíciója nem lehet null!");

        assertEquals(2, bridge.getStartTilePos().x);
        assertEquals(2, bridge.getStartTilePos().y);

        assertEquals(5, bridge.getEndTilePos().x);
        assertEquals(2, bridge.getEndTilePos().y);
    }


    @Test
    public void testGetSpriteName_MiddleWoodBridge() {
        Bridge middleWood = new Bridge(3, 2, BridgeType.WOOD, RoadDirection.EAST, startTile, endTile, false, false);
        assertEquals("bridge-wood-e", middleWood.getSpriteName());
    }

    @Test
    public void testGetSpriteName_EndStoneBridge() {
        Bridge endStone = new Bridge(5, 2, BridgeType.STONE, RoadDirection.SOUTH, startTile, endTile, true, false);
        assertEquals("bridge-stone-end-s", endStone.getSpriteName());
    }

    @Test
    public void testGetSpriteName_StartGlassBridge() {
        // Az "isEnd" logikád alapján a kezdő híd elem is lehet isEnd=true (vagy egy külön sprite jelöli)
        Bridge startGlass = new Bridge(2, 2, BridgeType.GLASS, RoadDirection.WEST, startTile, endTile, true, false);
        assertEquals("bridge-glass-end-w", startGlass.getSpriteName());
    }

    @Test
    public void testGetSpriteName_NorthDirection() {
        Bridge northBridge = new Bridge(3, 2, BridgeType.WOOD, RoadDirection.NORTH, startTile, endTile, false, false);
        assertEquals("bridge-wood-n", northBridge.getSpriteName());
    }
}