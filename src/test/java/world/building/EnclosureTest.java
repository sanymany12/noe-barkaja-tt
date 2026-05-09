package world.building;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.World;
import world.building.Enclosure;
import world.building.Silo;
import world.resources.AnimalType;
import world.tile.Point;

import static org.junit.jupiter.api.Assertions.*;

public class EnclosureTest {

    private World world;
    private Enclosure enclosure;
    private Silo silo;

    @BeforeEach
    public void setUp() {
        // 1. Létrehozunk egy tiszta világot
        world = new World(50, 50);
        world.receiveMoney(20000); // Adunk pénzt a játékosnak a teszteléshez

        // 2. Leteszünk egy Silót a rácsra (pl. az 5,5-ös koordinátára)
        Point siloPos = new Point(5, 5);
        silo = new Silo(world);
        world.get(siloPos.x, siloPos.y).setBuilding(silo);

        // 3. Létrehozzuk az Enclosure-t, ami így már sikeresen rácsatlakozik a Silóra
        enclosure = new Enclosure(world, siloPos);
    }

    @Test
    public void testInitialState() {
        assertEquals(0, enclosure.getNumOfAnimals(), "Kezdetben 0 állatnak kell lennie!");
        assertNull(enclosure.getSpecies(), "Kezdetben a fajnak null-nak kell lennie!");
        assertFalse(enclosure.isStarving(), "Üres állathely nem éhezhet!");
        assertEquals(silo, enclosure.getSilo(), "A Siló referencia nem kötődött be megfelelően!");
    }

    @Test
    public void testPurchaseAnimal_EmptyEnclosure_SetsSpeciesAndDeductsMoney() {
        int initialMoney = world.getMoney();
        int expectedCost = AnimalType.BEAR.getValue() * 5;

        enclosure.purchaseAnimal(AnimalType.BEAR);

        assertEquals(AnimalType.BEAR, enclosure.getSpecies(), "A faj nem állítódott be medvére!");
        assertEquals(1, enclosure.getNumOfAnimals(), "Az állatok száma nem nőtt 1-re!");
        assertEquals(initialMoney - expectedCost, world.getMoney(), "A vásárlás nem vonta le a megfelelő pénzt!");
    }

    @Test
    public void testPurchaseAnimal_DifferentSpecies_Ignored() {
        enclosure.purchaseAnimal(AnimalType.BEAR); // Veszünk egy medvét

        // Megpróbálunk betenni egy malacot a medvék közé
        enclosure.purchaseAnimal(AnimalType.PIG);

        assertEquals(AnimalType.BEAR, enclosure.getSpecies(), "A fajnak medvének kellett volna maradnia!");
        assertEquals(1, enclosure.getNumOfAnimals(), "A disznó vásárlása nem lett volna szabad, hogy megtörténjen!");
    }

    @Test
    public void testTakeAnimal_ReachingZero_ClearsSpecies() {
        enclosure.newSpeciesArrives(AnimalType.HORSE);
        assertEquals(AnimalType.HORSE, enclosure.getSpecies());

        enclosure.takeAnimal(); // Kiveszünk 1 állatot (így 0 lesz)

        assertEquals(0, enclosure.getNumOfAnimals());
        assertNull(enclosure.getSpecies(), "Ha elfogynak az állatok, a fajnak nullázódnia kell!");
    }

    @Test
    public void testNewDay_WithFood_AnimalsBreed() {

        enclosure.newSpeciesArrives(AnimalType.CAT);
        enclosure.receiveAnimal(); // Most 2 macska van
        // (A Silónak alapból 20 étele van, ami bőven elég 2 macskának)

        enclosure.newDay();

        assertFalse(enclosure.isStarving(), "Az állatoknak nem szabadna éhezniük!");

        // Matek: numOfAnimals = 2. Born = ceil(2 * 1.2) = ceil(2.4) = 3. Összesen: 5.
        assertEquals(5, enclosure.getNumOfAnimals(), "Az állatok nem szaporodtak megfelelően!");
        assertEquals(18, silo.getNumOfFood(), "A két macska nem evett meg pontosan 2 egység ételt a silóból!");
    }

    @Test
    public void testNewDay_InsufficientFood_StarvesAndDoesNotBreed() {

        enclosure.newSpeciesArrives(AnimalType.BEAR);
        enclosure.receiveAnimal(); // 2 medve van

        // Kiürítjük a silót (0 étel)
        silo.consumeFood(20);

        enclosure.newDay();

        assertTrue(enclosure.isStarving(), "Az állatoknak éhezniük kellene az üres siló miatt!");
        assertEquals(2, enclosure.getNumOfAnimals(), "Az éhező állatok nem szaporodhatnak!");
    }

    @Test
    public void testNewDay_BreedingCapsAtMaxCapacity() throws Exception {

        enclosure.newSpeciesArrives(AnimalType.CAPYBARA);
        // Beletöltünk mesterségesen 195 állatot
        for(int i = 0; i < 194; i++) {
            enclosure.receiveAnimal();
        }

        // Megtöltjük a silót kajával, hogy ne haljanak éhen a teszt alatt
        silo.loadFromTruck(200);

        enclosure.newDay();

        // A matek szerint rengeteg állat születne, de a limit 200 (CAPACITY).
        assertEquals(200, enclosure.getNumOfAnimals(), "Az állatok túllépték a maximum 200-as kapacitást!");
        assertTrue(enclosure.isFull(), "A rendszer nem ismeri fel, hogy az állathely megtelt!");
    }

    @Test
    public void testGetDaysUntilStarvation() {
        // Ha nincs állat
        assertEquals(-1, enclosure.getDaysUntilStarvation(), "0 állatnál -1-et kell visszaadnia!");

        enclosure.newSpeciesArrives(AnimalType.CAT); // 1 állat, a silóban van 20 kaja
        assertEquals(20, enclosure.getDaysUntilStarvation(), "1 állatnál a napok száma megegyezik a kaja mennyiségével!");

        enclosure.receiveAnimal(); // 2 állat
        enclosure.receiveAnimal(); // 3 állat, kaja: 20 -> 20 / 3 = 6 (egészosztás)
        assertEquals(6, enclosure.getDaysUntilStarvation(), "A matematikai osztás nem megfelelő a napok számításánál!");
    }

    @Test
    public void testGetSpriteName() {
        assertEquals("enclosure", enclosure.getSpriteName(), "Üres állathely rossz sprite-ot ad!");

        enclosure.newSpeciesArrives(AnimalType.PIG);
        assertEquals("enclosure-pig", enclosure.getSpriteName(), "Malac sprite-ja nem egyezik!");

        enclosure.takeAnimal(); // Üres lesz ismét
        assertEquals("enclosure", enclosure.getSpriteName(), "Kiürülés után nem állt vissza az alap sprite-ra!");
    }
}