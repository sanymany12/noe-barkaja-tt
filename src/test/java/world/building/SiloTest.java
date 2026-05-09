package world.building;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.World;
import world.building.Enclosure;
import world.building.Silo;
import world.tile.Point;

import static org.junit.jupiter.api.Assertions.*;

public class SiloTest {

    private World world;
    private Silo silo;

    @BeforeEach
    public void setUp() {
        world = new World(50, 50);
        world.receiveMoney(50000);
        silo = new Silo(world);
        world.get(0,0).setBuilding(silo);

    }

    @Test
    public void testInitialState() {
        // A konstruktor alapján 20 étellel és 500-as alapkacitással indul
        assertEquals(20, silo.getNumOfFood(), "Az induló ételmennyiségnek 20-nak kell lennie!");
        assertEquals(480, silo.getRemainingCapacity(), "Az induló szabad kapacitásnak 480-nak kell lennie (500 - 20)!");
        assertNull(silo.getEnclosure(), "A silóhoz alapértelmezetten nem tartozhat Enclosure!");
    }

    @Test
    public void testLoadFromTruck_ValidAmount_IncreasesFoodAndDecreasesCapacity() throws Exception {
        // 100 egység ételt pakolunk be
        silo.loadFromTruck(100);

        assertEquals(120, silo.getNumOfFood(), "Az ételmennyiség nem nőtt meg megfelelően!");
        assertEquals(380, silo.getRemainingCapacity(), "A maradék kapacitás nem csökkent megfelelően!");
    }

    @Test
    public void testLoadFromTruck_ExceedsCapacity_ThrowsException() {
        // 480 a szabad hely, megpróbálunk 500-at betenni
        Exception exception = assertThrows(Exception.class, () -> {
            silo.loadFromTruck(500);
        });

        assertEquals("Nincs elég hely az ételnek!", exception.getMessage());
        // Biztosítjuk, hogy hiba esetén az eredeti érték nem változott
        assertEquals(20, silo.getNumOfFood());
    }

    @Test
    public void testConsumeFood_SufficientAmount_ReturnsTrueAndDeducts() {

        boolean result = silo.consumeFood(15); // 20-ból 15-öt kérünk

        assertTrue(result, "A fogyasztásnak sikeresnek kell lennie!");
        assertEquals(5, silo.getNumOfFood(), "A maradék ételmennyiség hibás!");
        assertEquals(495, silo.getRemainingCapacity(), "A maradék kapacitásnak frissülnie kell!");
    }

    @Test
    public void testConsumeFood_InsufficientAmount_ReturnsFalseAndEmptiesSilo() {

        boolean result = silo.consumeFood(30); // 20 van, de 30-at kérünk

        assertFalse(result, "A fogyasztásnak sikertelennek kell lennie, mert nincs elég étel!");
        assertEquals(0, silo.getNumOfFood(), "A silónak teljesen ki kell ürülnie!");
        assertEquals(500, silo.getRemainingCapacity(), "A silónak teljesen üresnek kell lennie (500 kapacitás)!");
    }

    @Test
    public void testIncreaseCapacity_FirstUpgrade_CostsBaseAmount() {

        int initialMoney = world.getMoney();
        int initialCapacity = 500;


        silo.increaseCapacity();

        // plusCapacity 0 volt. Költség: 5000 + (0 / 10) * 1000 = 5000
        assertEquals(initialMoney - 5000, world.getMoney(), "Az első fejlesztésnek pontosan 5000-be kell kerülnie!");
        // Max kapacitás 510 lett. (510 - 20 kezdő étel = 490 maradék hely)
        assertEquals(490, silo.getRemainingCapacity(), "A kapacitás nem nőtt 10-zel!");
    }

    @Test
    public void testIncreaseCapacity_SecondUpgrade_CostsIncrementedAmount() {

        silo.increaseCapacity(); // Első fejlesztés (plusCapacity = 10 lesz)
        int moneyAfterFirstUpgrade = world.getMoney();

        silo.increaseCapacity(); // Második fejlesztés

        // plusCapacity 10 volt. Költség: 5000 + (10 / 10) * 1000 = 6000
        assertEquals(moneyAfterFirstUpgrade - 6000, world.getMoney(), "A második fejlesztésnek 6000-be kell kerülnie!");
        // Max kapacitás 520 lett. (520 - 20 = 500)
        assertEquals(500, silo.getRemainingCapacity());
    }

    @Test
    public void testGetSpriteName_ChangesWhenEmpty() {

        assertEquals("silo", silo.getSpriteName(), "Kezdetben a teli sprite-ot kell visszaadnia!");


        silo.consumeFood(20); // Kiürítjük


        assertEquals("silo-empty", silo.getSpriteName(), "Üres siló esetén az 'silo-empty' sprite-ot kell visszaadnia!");
    }

    @Test
    public void testEnclosureDependencies_NullHandling() {

        // Ha nincs rákötve Enclosure, ezek a metódusok biztonságosan 0-t kell adjanak (NullPointerException nélkül)
        assertEquals(0, silo.getFoodConsumptionPerDay());
        assertEquals(0, silo.getDaysUntilStarvation());
    }

    @Test
    public void testSetEnclosure_ReturnsCorrectReference() {

        Enclosure enclosure = new Enclosure(world, new Point(0,0));


        silo.setEnclosure(enclosure);


        assertNotNull(silo.getEnclosure());
        assertEquals(enclosure, silo.getEnclosure(), "A siló nem a megfelelő Enclosure-t tárolja!");
    }
}