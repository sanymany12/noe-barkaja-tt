package world.tile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.World;
import world.building.Building;
import world.building.BuildingType;
import world.tile.Point;
import world.tile.TerrainType;
import world.tile.Tile;
import world.tile.road.Road;

import static org.junit.jupiter.api.Assertions.*;

public class TileTest {

    private Point defaultPoint;
    private Tile emptyTile;

    @BeforeEach
    public void setUp() {
        defaultPoint = new Point(5, 5);
        // Létrehozunk egy teljesen üres, alapértelmezett csempét
        emptyTile = new Tile(defaultPoint, TerrainType.LAND, 0, null, null, false);
    }

    //ISEMPTY() TESZTEK ---

    @Test
    public void testIsEmpty_WhenTrulyEmpty_ReturnsTrue() {
        assertTrue(emptyTile.isEmpty(), "Egy újonnan létrehozott, üres csempének true-t kell visszaadnia!");
    }

    @Test
    public void testIsEmpty_WithTrees_ReturnsFalse() {
        emptyTile.setTreeCount(1);
        assertFalse(emptyTile.isEmpty(), "Ha van rajta fa, nem lehet üres!");
    }

    @Test
    public void testIsEmpty_WithRoad_ReturnsFalse() {
        emptyTile.setRoad(new Road(5, 5, false));
        assertFalse(emptyTile.isEmpty(), "Ha van rajta út, nem lehet üres!");
    }

    @Test
    public void testIsEmpty_WithBuilding_ReturnsFalse() {
        // Létrehozunk egy névtelen (anonymous) Building osztályt a teszthez
        Building<?, ?> dummyBuilding = new Building<Object, Object>(null) {
            @Override public void newDay() {}
            @Override public String getSpriteName() { return "dummy"; }
        };

        emptyTile.setBuilding(dummyBuilding);
        assertFalse(emptyTile.isEmpty(), "Ha van rajta épület, nem lehet üres!");
    }

    //ÁLLOMÁS ÉS ÚT ELTÁVOLÍTÁSA TESZTEK ---

    @Test
    public void testRemoveStation_WithStation_RemovesAndResetsToLand() {

        Building<?, ?> stationBuilding = new Building<Object, Object>(null) {
            { this.type = BuildingType.STATION; } // Beállítjuk a típust Station-re
            @Override public void newDay() {}
            @Override public String getSpriteName() { return "station"; }
        };

        emptyTile.setBuilding(stationBuilding);
        emptyTile.setTerrainType(TerrainType.STOP);
        emptyTile.setAnchor(true);

        emptyTile.removeStation();

        assertNull(emptyTile.getBuilding(), "Az állomást el kellett volna távolítani (null)!");
        assertEquals(TerrainType.LAND, emptyTile.getTerrainType(), "A terepnek vissza kell állnia LAND-re!");
        assertFalse(emptyTile.isAnchor(), "Az isAnchor-nak false-ra kell állnia!");
    }

    @Test
    public void testRemoveStation_WithNonStationBuilding_DoesNothing() {
        Building<?, ?> farmBuilding = new Building<Object, Object>(null) {
            { this.type = BuildingType.FARM; } // NEM Station!
            @Override public void newDay() {}
            @Override public String getSpriteName() { return "farm"; }
        };

        emptyTile.setBuilding(farmBuilding);
        emptyTile.setTerrainType(TerrainType.BUILDING);
        emptyTile.setAnchor(true);

        emptyTile.removeStation();

        assertNotNull(emptyTile.getBuilding(), "A nem-állomás épületet nem szabad törölni!");
        assertEquals(TerrainType.BUILDING, emptyTile.getTerrainType(), "A tereptípus nem változhat!");
        assertTrue(emptyTile.isAnchor(), "Az anchor státusz nem változhat!");
    }

    @Test
    public void testRemoveRoad_NormalRoad_ResetsToLand() {
        Road normalRoad = new Road(5, 5, false);
        // a normalRoad.getIsBridge() false lesz alapból

        emptyTile.setRoad(normalRoad);
        emptyTile.setTerrainType(TerrainType.ROAD);

        emptyTile.removeRoad();

        assertNull(emptyTile.getRoad(), "Az utat törölni kell!");
        assertEquals(TerrainType.LAND, emptyTile.getTerrainType(), "Sima út törlése után LAND lesz a terep!");
    }

    @Test
    public void testRemoveRoad_Bridge_ResetsToWater() {
        Road bridge = new Road(5, 5, false) {
            @Override public boolean getIsBridge() { return true; } // Felülírjuk, hogy híd legyen
        };

        emptyTile.setRoad(bridge);
        emptyTile.setTerrainType(TerrainType.BRIDGE);

        emptyTile.removeRoad();

        assertNull(emptyTile.getRoad(), "A hidat törölni kell!");
        assertEquals(TerrainType.WATER, emptyTile.getTerrainType(), "Híd törlése után VÍZ-nek kell maradnia!");
    }

    //SPRITE GENERÁLÁS (SAKKTÁBLA LOGIKA) TESZTEK ---

    @Test
    public void testGetSpriteName_CheckerboardPattern_WorksProperly() {
        // Páros X, Páros Y -> land-1
        Tile tileEvenEven = new Tile(new Point(2, 2), TerrainType.LAND, 0, null, null, false);
        assertEquals("land-1", tileEvenEven.getSpriteName());

        // Páros X, Páratlan Y -> land-2
        Tile tileEvenOdd = new Tile(new Point(2, 3), TerrainType.LAND, 0, null, null, false);
        assertEquals("land-2", tileEvenOdd.getSpriteName());

        // Páratlan X, Páros Y -> land-2
        Tile tileOddEven = new Tile(new Point(3, 2), TerrainType.LAND, 0, null, null, false);
        assertEquals("land-2", tileOddEven.getSpriteName());

        // Páratlan X, Páratlan Y -> land-1
        Tile tileOddOdd = new Tile(new Point(3, 3), TerrainType.LAND, 0, null, null, false);
        assertEquals("land-1", tileOddOdd.getSpriteName());
    }

    @Test
    public void testGetSpriteName_WaterAndBridge_ReturnsWater() {
        emptyTile.setTerrainType(TerrainType.WATER);
        assertEquals("water", emptyTile.getSpriteName());

        emptyTile.setTerrainType(TerrainType.BRIDGE);
        assertEquals("water", emptyTile.getSpriteName());
    }

    @Test
    public void testGetSpriteName_CliffAndVoid_ReturnsCliff() {
        emptyTile.setTerrainType(TerrainType.CLIFF);
        assertEquals("cliff", emptyTile.getSpriteName());

        emptyTile.setTerrainType(TerrainType.VOID);
        assertEquals("cliff", emptyTile.getSpriteName());
    }
}