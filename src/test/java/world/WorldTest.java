package world;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.World;
import world.tile.Point;
import world.tile.TerrainType;
import world.tile.Tile;
import world.vehicle.Vehicle;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WorldTest {

    private World world;

    private class DummyVehicle extends Vehicle {
        public DummyVehicle(World world, Point p) throws Exception {
            super(world, p);
        }
        @Override public void move() {}
        @Override public String getSpriteName() { return "dummy"; }
        @Override public int getCostToOperate() { return 100; }
        @Override public int getCostToSell() { return 500; }
        @Override public void loadFrom(world.building.Building b) {}
        @Override public void unloadTo(world.building.Building b) {}
    }

    @BeforeEach
    public void setUp() {
        // A hardkódolt initWorld miatt legalább 20x20-as pálya kell
        world = new World(40, 40);
    }

    // INICIALIZÁLÁS ÉS ALAPVETŐ ÁLLAPOTOK

    @Test
    public void testInitialization() {
        assertEquals(40, world.getRows());
        assertEquals(40, world.getCols());

        assertEquals(13000, world.getMoney());
        assertEquals(0, world.getElapsedTime());
        assertNotNull(world.getVehicles());
    }

    @Test
    public void testTileAccess_ValidAndInvalidCoordinates() {
        assertNotNull(world.get(5, 5), "Létező csempét kellene visszaadnia!");
        assertNotNull(world.get(0, 0), "A 0,0 csempét kellene visszaadnia!");

        // Határokon kívüli lekérdezések (A get() metódus null-t ad vissza kivétel helyett)
        assertNull(world.get(-1, 5), "Negatív X esetén null-t kell adnia!");
        assertNull(world.get(5, -1), "Negatív Y esetén null-t kell adnia!");
        assertNull(world.get(50, 5), "Túl nagy X esetén null-t kell adnia!");
        assertNull(world.get(5, 50), "Túl nagy Y esetén null-t kell adnia!");
    }

    //  GAZDASÁG TESZT

    @Test
    public void testMoneyManagement() {
        int initialMoney = world.getMoney();

        world.receiveMoney(5000);
        assertEquals(initialMoney + 5000, world.getMoney());

        world.spendMoney(2000);
        assertEquals(initialMoney + 3000, world.getMoney());
    }

    @Test
    public void testVehicleEconomy_AnnualCostAndSelling() throws Exception {
        DummyVehicle v1 = new DummyVehicle(world, new Point(0, 0));
        DummyVehicle v2 = new DummyVehicle(world, new Point(0, 0));

        world.getVehicles().add(v1);
        world.getVehicles().add(v2);

        // Két dummy jármű fenntartása 200 (100 + 100)
        assertEquals(200, world.getAnnualCostOfVehicles());

        int moneyBeforeSell = world.getMoney();
        world.sellVehicle(v1);

        // Eladás után a lista csökken, a pénz nő
        assertEquals(1, world.getVehicles().size());
        assertEquals(moneyBeforeSell + 500, world.getMoney()); // +500 eladási ár
    }

    // IDŐ MÚLÁSA TESZT

    @Test
    public void testTimeProgression_TriggersNewDay() throws Exception {
        int initialDays = world.getElapsedTime();
        int ticksPerDay = world.getTicksPerDay();

        // Pörgetjük a tickeket pontosan 1 napnyit
        for (int i = 0; i < ticksPerDay; i++) {
            world.increaseTickCounter();
        }

        // A napok számának 1-gyel nőnie kellett
        assertEquals(initialDays + 1, world.getElapsedTime());
    }

    //  ÚTKERESÉS

    @Test
    public void testFindPathRoad_Success_StraightLine() throws Exception {
        // Az initWorld()-ben építettél utakat X=4-től X=8-ig az Y=3 sorban.
        Tile startNode = world.get(30, 32);
        Tile endNode = world.get(32, 32);

        List<Point> path = world.findPathRoad(startNode, endNode);

        assertNotNull(path, "Találnia kellene útvonalat!");
        // A 4-es, 5-ös, 6-os, 7-es és 8-as csempék, összesen 5 lépés
        assertEquals(3, path.size());
    }

    @Test
    public void testFindPathRoad_DestinationNotRoad_ThrowsException() {
        Tile startNode = world.get(4, 3); // Út
        Tile endNode = world.get(0, 0);   // LAND (Fű)

        Exception exception = assertThrows(Exception.class, () -> {
            world.findPathRoad(startNode, endNode);
        });

        assertEquals("The destination isn't on the road!", exception.getMessage());
    }
}