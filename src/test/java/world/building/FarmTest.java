package world.building;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.World;
import world.building.Farm;

import static org.junit.jupiter.api.Assertions.*;

public class FarmTest {

    private World world;
    private Farm farm;

    @BeforeEach
    public void setUp() {
        world = new World(20, 20);
        world.receiveMoney(10000); // Adunk elég pénzt a boost teszteléséhez
        farm = new Farm(world);
    }

    @Test
    public void testInitialState() {
        assertEquals(0, farm.getGrainMade(), "Kezdetben 0 gabonának kell lennie!");
        assertEquals(400, farm.getCapacity(), "A kapacitásnak 400-nak kell lennie!");
        assertFalse(farm.isBoosted(), "A farm alapból nem lehet boostolt!");
        assertEquals(0, farm.getDaysLeftOfBoost(), "Kezdetben 0 nap boost van hátra!");
    }

    @Test
    public void testBoostProduction_SpendsMoneyAndActivatesBoost() {
        int initialMoney = world.getMoney();

        farm.boostProduction();

        assertTrue(farm.isBoosted(), "A boostnak aktívnak kell lennie!");
        assertEquals(7, farm.getDaysLeftOfBoost(), "A boostnak pontosan 7 napig kell tartania!");
        assertEquals(20, farm.getBoostAmount(), "A boost értékének 20-nak kell lennie!");
        assertEquals(initialMoney - 2000, world.getMoney(), "A boost árát (2000) le kellett volna vonni!");
    }

    @Test
    public void testNewDay_GeneratesGrainWithinBounds() {
        farm.newDay();

        // A MIN_GAIN 20, a MAX_GAIN 70 (a nextInt exclusive a maximumra, tehát max 69 lehet)
        int grain = farm.getGrainMade();
        assertTrue(grain >= 20 && grain < 70, "A napi termelésnek a megadott 20-69 tartományba kell esnie! Jelenlegi: " + grain);
    }

    @Test
    public void testNewDay_WithBoost_GeneratesMoreGrain() {
        farm.boostProduction();

        farm.newDay();

        // A minimum termelés boosttal: 20 + 20 = 40. A maximum: 69 + 20 = 89.
        int grain = farm.getGrainMade();
        assertTrue(grain >= 40 && grain <= 89, "A boostolt napi termelésnek a 40-89 tartományba kell esnie! Jelenlegi: " + grain);
    }

    @Test
    public void testNewDay_CapsAtCapacity() {

        for (int i = 0; i < 30; i++) {
            farm.newDay();
        }

        assertEquals(400, farm.getGrainMade(), "A gabonamennyiség nem lépheti túl a 400-as kapacitást!");
    }

    @Test
    public void testNewDay_BoostWearsOffAfter7Days() {
        farm.boostProduction();
        assertTrue(farm.isBoosted());

        // Lepörgetünk 7 napot
        for (int i = 0; i < 7; i++) {
            farm.newDay();
        }

        assertFalse(farm.isBoosted(), "7 nap után a boostnak le kellett volna járnia!");
        assertEquals(0, farm.getDaysLeftOfBoost(), "Nem maradhat aktív boost nap!");
    }

    @Test
    public void testLoadOntoTruck_AllGrain() {
        // Termelünk egy kis gabonát
        farm.newDay();
        assertTrue(farm.getGrainMade() > 0);

        // Teljes ürítés
        farm.loadOntoTruck();

        assertEquals(0, farm.getGrainMade(), "A teherautóra való pakolás (paraméter nélkül) nem ürítette ki a farmot!");
    }

    @Test
    public void testLoadOntoTruck_SpecificAmount_Success() throws Exception {
        // Addig pörgetjük a napokat, amíg biztosan lesz legalább 100 gabona
        while (farm.getGrainMade() < 100) {
            farm.newDay();
        }

        int grainBeforeLoad = farm.getGrainMade();

        // Felpakolunk pontosan 50-et
        farm.loadOntoTruck(50);

        assertEquals(grainBeforeLoad - 50, farm.getGrainMade(), "Nem pontosan 50 gabonát vett le a farmról!");
    }

    @Test
    public void testLoadOntoTruck_SpecificAmount_ThrowsExceptionWhenNotEnough() {
        // Kezdetben 0 gabona van
        Exception exception = assertThrows(Exception.class, () -> {
            farm.loadOntoTruck(50);
        });

        assertEquals("Nincs elég gabona!", exception.getMessage());
    }
}