package world.building;

import org.junit.jupiter.api.BeforeEach;


import org.junit.jupiter.api.Test;
import world.World;
import world.building.ResearchLab;
import world.resources.AnimalType;

import static org.junit.jupiter.api.Assertions.*;

public class ResearchLabTest {

    private World world;
    private ResearchLab lab;

    @BeforeEach
    public void setUp() {
        world = new World(20, 20);
        world.receiveMoney(5000); // Adunk elég pénzt a kutatások teszteléséhez
        lab = new ResearchLab(world);
    }



    @Test
    public void testInitialState() {
        assertNull(lab.getReceivedAnimal1(), "Az 1. fogadóhelynek üresnek kell lennie!");
        assertNull(lab.getReceivedAnimal2(), "A 2. fogadóhelynek üresnek kell lennie!");
        assertNull(lab.getDiscoveredAnimal(), "A felfedezett állat helyének üresnek kell lennie!");
        assertFalse(lab.isResearchHappening(), "Alapból nem mehet kutatás!");
        assertEquals(0, lab.getDaysSinceResearchStarted(), "A kutatási napok száma 0 kell legyen!");
        assertEquals(500, lab.getCostOfResearch(), "A kutatás árának 500-nak kell lennie!");
    }



    @Test
    public void testReceiveAnimal_ValidDistinctAnimals_ReturnsTrue() {
        assertTrue(lab.receiveAnimal(AnimalType.HORSE), "Az első állatot be kell fogadnia!");
        assertEquals(AnimalType.HORSE, lab.getReceivedAnimal1());

        assertTrue(lab.receiveAnimal(AnimalType.FISH), "A második, eltérő állatot be kell fogadnia!");
        assertEquals(AnimalType.FISH, lab.getReceivedAnimal2());
    }

    @Test
    public void testReceiveAnimal_DuplicateAnimal_ReturnsFalse() {
        lab.receiveAnimal(AnimalType.HORSE);

        // Megpróbáljuk ugyanazt a fajt még egyszer betenni
        assertFalse(lab.receiveAnimal(AnimalType.HORSE), "Nem fogadhat be két ugyanolyan fajtájú állatot!");
        assertNull(lab.getReceivedAnimal2(), "A második helynek üresnek kell maradnia!");
    }

    @Test
    public void testReceiveAnimal_MutatedAnimals_ReturnsFalse() {
        assertFalse(lab.receiveAnimal(AnimalType.CAPYBARA), "Nem fogadhat be Capybarát!");
        assertFalse(lab.receiveAnimal(AnimalType.GUINEAPIG), "Nem fogadhat be Tengerimalacot!");
        assertFalse(lab.receiveAnimal(AnimalType.RACOON), "Nem fogadhat be Mosómedvét!");
        assertFalse(lab.receiveAnimal(AnimalType.SEAHORSE), "Nem fogadhat be Csikóhalat!");
    }

    @Test
    public void testReceiveAnimal_LabIsFull_ReturnsFalse() {
        lab.receiveAnimal(AnimalType.HORSE);
        lab.receiveAnimal(AnimalType.BEAR);

        // A harmadik állat
        assertFalse(lab.receiveAnimal(AnimalType.PIG), "Nem fogadhat be állatot, ha már tele van a 2 hely!");
    }



    @Test
    public void testStartResearch_WithEnoughAnimals_DeductsMoneyAndStarts() {
        lab.receiveAnimal(AnimalType.BEAR);
        lab.receiveAnimal(AnimalType.CAT);

        int initialMoney = world.getMoney();

        assertTrue(lab.canStartResearch(), "Két állat esetén engednie kell a kutatást!");
        lab.startResearch();

        assertTrue(lab.isResearchHappening(), "A kutatásnak el kellett volna indulnia!");
        assertEquals(initialMoney - 500, world.getMoney(), "A kutatás árát le kellett vonni!");
    }

    @Test
    public void testStartResearch_NotEnoughAnimals_DoesNothing() {
        lab.receiveAnimal(AnimalType.BEAR); // Csak 1 állat

        assertFalse(lab.canStartResearch(), "Egy állattal nem szabad engednie a kutatást!");
        lab.startResearch();

        assertFalse(lab.isResearchHappening(), "A kutatás nem indulhat el egy állattal!");
    }



    @Test
    public void testNewDay_ProgressesResearch_UntilDay5() {
        lab.receiveAnimal(AnimalType.HORSE);
        lab.receiveAnimal(AnimalType.FISH);
        lab.startResearch();

        // 4 nap eltelik
        for (int i = 0; i < 4; i++) {
            lab.newDay();
        }

        assertTrue(lab.isResearchHappening(), "A 4. nap végén a kutatásnak még folynia kell!");
        assertEquals(4, lab.getDaysSinceResearchStarted());
        assertNull(lab.getDiscoveredAnimal(), "Még nem lehet kész az új állat!");

        // 5. nap eltelik
        lab.newDay();

        assertFalse(lab.isResearchHappening(), "A kutatásnak be kellett fejeződnie!");
        assertEquals(0, lab.getDaysSinceResearchStarted(), "A kutatási időzítőnek nullázódnia kellett!");
        assertEquals(AnimalType.SEAHORSE, lab.getDiscoveredAnimal(), "Létre kellett jönnie a Csikóhalnak!");
        assertNull(lab.getReceivedAnimal1(), "A forrásállatokat fel kellett emésztenie!");
        assertNull(lab.getReceivedAnimal2(), "A forrásállatokat fel kellett emésztenie!");
    }

    @Test
    public void testCompatibilityTest_AllCombinations() {
        // Ellenőrizzük a hardkódolt szabályokat

        // 1. BEAR + HORSE = CAPYBARA
        lab.receiveAnimal(AnimalType.BEAR);
        lab.receiveAnimal(AnimalType.HORSE);
        lab.startResearch();
        passTime(5);
        assertEquals(AnimalType.CAPYBARA, lab.getDiscoveredAnimal());
        lab.takeDiscoveredAnimal(); // Kiürítjük

        // 2. BEAR + CAT = RACOON
        lab.receiveAnimal(AnimalType.BEAR);
        lab.receiveAnimal(AnimalType.CAT);
        lab.startResearch();
        passTime(5);
        assertEquals(AnimalType.RACOON, lab.getDiscoveredAnimal());
        lab.takeDiscoveredAnimal();

        // 3. FISH + PIG = GUINEAPIG
        lab.receiveAnimal(AnimalType.FISH);
        lab.receiveAnimal(AnimalType.PIG);
        lab.startResearch();
        passTime(5);
        assertEquals(AnimalType.GUINEAPIG, lab.getDiscoveredAnimal());
    }

    @Test
    public void testCompatibilityTest_IncompatibleAnimals_NoDiscovery() {
        // Rossz kombináció: Malac + Ló
        lab.receiveAnimal(AnimalType.PIG);
        lab.receiveAnimal(AnimalType.HORSE);
        lab.startResearch();

        passTime(5); // 5 nap eltelik

        // Mivel rossz volt a kombináció, a kódod logikája alapján az állatok NEM törlődnek ki,
        // és nem is keletkezik új állat.
        assertNull(lab.getDiscoveredAnimal(), "Inkompatibilis állatokból nem keletkezhet új faj!");
        assertNotNull(lab.getReceivedAnimal1(), "A forrásállatok a laborban rekednek!");
        assertNotNull(lab.getReceivedAnimal2(), "A forrásállatok a laborban rekednek!");
    }

    @Test
    public void testTakeDiscoveredAnimal_ClearsSlot() {
        // Előkészítünk egy sikeres kutatást
        lab.receiveAnimal(AnimalType.FISH);
        lab.receiveAnimal(AnimalType.HORSE);
        lab.startResearch();
        passTime(5);

        assertNotNull(lab.getDiscoveredAnimal());


        lab.takeDiscoveredAnimal();


        assertNull(lab.getDiscoveredAnimal(), "Az elvitel után a slotnak üresnek kell lennie!");
    }

    // Segédfüggvény az idő múlásához a tesztben
    private void passTime(int days) {
        for (int i = 0; i < days; i++) {
            lab.newDay();
        }
    }
}