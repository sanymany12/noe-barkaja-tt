package world.building;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.World;
import world.building.CloningFacility;
import world.resources.AnimalType;

import static org.junit.jupiter.api.Assertions.*;

public class CloningFacilityTest {

    private World world;
    private CloningFacility facility;

    @BeforeEach
    public void setUp() {
        world = new World(20, 20);
        world.receiveMoney(5000); // Adunk elég pénzt a klónozások teszteléséhez
        facility = new CloningFacility(world);
    }



    @Test
    public void testInitialState() {
        assertEquals(20, facility.getCapacity(), "A kapacitásnak 20-nak kell lennie!");
        assertEquals(6, facility.getDaysToClone(), "A klónozási időnek 6 napnak kell lennie!");
        assertEquals(200, facility.getCostOfCloning(), "A klónozás árának 200-nak kell lennie!");

        assertFalse(facility.hasAnimal(), "Kezdetben nem lehet benne állat!");
        assertNull(facility.getAnimalType(), "Kezdetben a típus null kell legyen!");
        assertEquals(0, facility.getAnimalsMade(), "Kezdetben 0 legyártott állat van!");

        assertFalse(facility.isCloning(), "Kezdetben nem mehet klónozás!");
        assertEquals(0, facility.getDaysSinceStarted());
        assertEquals("cloningfacility", facility.getSpriteName());
    }



    @Test
    public void testReceiveAnimal_EmptyFacility_AcceptsAndSetsToOne() {
        boolean success = facility.receiveAnimal(AnimalType.HORSE);

        assertTrue(success, "Az üres létesítménynek be kell fogadnia a mintaállatot!");
        assertTrue(facility.hasAnimal(), "Most már lennie kell benne állatnak!");
        assertEquals(AnimalType.HORSE, facility.getAnimalType());
        assertEquals(1, facility.getAnimalsMade(), "A mintaállat beérkezése után a darabszámnak 1-nek kell lennie!");
    }

    @Test
    public void testReceiveAnimal_AlreadyHasAnimal_RejectsNew() {
        facility.receiveAnimal(AnimalType.HORSE);

        // Megpróbálunk egy másikat betenni
        boolean success = facility.receiveAnimal(AnimalType.BEAR);

        assertFalse(success, "Nem fogadhat be új állatot, amíg van benne másik!");
        assertEquals(AnimalType.HORSE, facility.getAnimalType(), "A típusnak lónak kellett maradnia!");
    }



    @Test
    public void testStartCloning_WithAnimal_DeductsMoneyAndStarts() {
        facility.receiveAnimal(AnimalType.CAPYBARA);
        int initialMoney = world.getMoney();

        assertTrue(facility.canStartCloning(), "Engednie kell a klónozást egy állattal!");

        facility.startCloning();

        assertTrue(facility.isCloning(), "A klónozás státusza nem váltott true-ra!");
        assertEquals(0, facility.getDaysSinceStarted(), "Az eltelt napok száma nem nullázódott le!");
        assertEquals(initialMoney - 200, world.getMoney(), "A klónozás árát (200) nem vonta le!");
    }

    @Test
    public void testStartCloning_NoAnimal_DoesNothing() {
        assertFalse(facility.canStartCloning());

        facility.startCloning();

        assertFalse(facility.isCloning(), "Állat nélkül nem indulhat el a klónozás!");
    }



    @Test
    public void testNewDay_CloningProgress_CompletesOnDay6() {
        facility.receiveAnimal(AnimalType.PIG);
        facility.startCloning();

        // Lepörgetünk 5 napot
        for (int i = 0; i < 5; i++) {
            facility.newDay();
        }

        assertTrue(facility.isCloning(), "Az 5. nap végén még tartania kell a klónozásnak!");
        assertEquals(5, facility.getDaysSinceStarted());
        assertEquals(1, facility.getAnimalsMade(), "Még mindig csak 1 állatnak (a mintának) kell lennie!");

        // Lepörgetjük a 6. napot
        facility.newDay();

        assertFalse(facility.isCloning(), "A 6. napon a klónozásnak be kellett fejeződnie!");
        assertEquals(2, facility.getAnimalsMade(), "Meg kellett születnie a második állatnak (a klónnak)!");
    }

    @Test
    public void testNewDay_NotCloning_DoesNothing() {
        facility.receiveAnimal(AnimalType.CAT);
        // NEM indítjuk el a klónozást!

        facility.newDay();
        facility.newDay();

        assertEquals(0, facility.getDaysSinceStarted(), "Ha nem klónozunk, a napok nem telhetnek a folyamatban!");
        assertEquals(1, facility.getAnimalsMade());
    }



    @Test
    public void testTakeAnimal_ReducesCountAndClearsIfZero() {
        facility.receiveAnimal(AnimalType.FISH);
        facility.startCloning();

        // Pörgetünk 6 napot, hogy legyen 2 hal
        for (int i = 0; i < 6; i++) {
            facility.newDay();
        }
        assertEquals(2, facility.getAnimalsMade());

        // Kiveszünk egyet
        facility.takeAnimal();
        assertEquals(1, facility.getAnimalsMade(), "A darabszámnak 1-re kellett csökkennie!");
        assertEquals(AnimalType.FISH, facility.getAnimalType(), "A fajtának még bent kell maradnia!");

        // Kiveszük az utolsót is
        facility.takeAnimal();
        assertEquals(0, facility.getAnimalsMade());
        assertNull(facility.getAnimalType(), "Ha elfogyott minden állat, az üzemnek ki kell ürülnie!");
        assertFalse(facility.hasAnimal());
    }

    @Test
    public void testCapacityLimit_CannotStartCloningWhenFull() {
        facility.receiveAnimal(AnimalType.BEAR);

        // Mesterségesen legyártatunk vele még 19 állatot (összesen 20 lesz)
        // Ehhez 19x el kell indítani és lepörgetni a 6 napot
        for (int i = 0; i < 19; i++) {
            facility.startCloning();
            for (int j = 0; j < 6; j++) {
                facility.newDay();
            }
        }

        assertEquals(20, facility.getAnimalsMade(), "El kellett érnie a 20-as kapacitást!");


        assertFalse(facility.canStartCloning(), "Ha elérte a kapacitást, a canStartCloning false kell legyen!");

        facility.startCloning();
        assertFalse(facility.isCloning(), "A startCloning nem indíthatja el a folyamatot, ha tele van az üzem!");
    }
}