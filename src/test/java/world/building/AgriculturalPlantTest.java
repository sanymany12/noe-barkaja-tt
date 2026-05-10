package world.building;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.World;

import static org.junit.jupiter.api.Assertions.*;

public class AgriculturalPlantTest {
    private World world;
    private AgriculturalPlant agriculturalplant;

    @BeforeEach
    public void setUp() {
        world = new World(20, 20);
        world.receiveMoney(20000);

        agriculturalplant = new AgriculturalPlant(world);
    }

    @Test
    public void testInitialState() {
        assertEquals(0, agriculturalplant.getIncomingGrain(), "Kezdetben 0 gabonával kell kezdenie!");
        assertEquals(0, agriculturalplant.getOutgoingFood(), "Kezdetben 0 étellel kell kezdenie!");
        assertFalse(agriculturalplant.isBoosted(), "Kezdetben nincs növelve a termelés!");
    }

    @Test
    public void testBoost() {
        int initialMoney = world.getMoney();
        int expectedCost = agriculturalplant.getBoostCost();

        agriculturalplant.boostProduction();

        assertEquals(initialMoney - expectedCost, world.getMoney(), "A vásárlás nem megfelelően vonta le a pénzt!");
        assertTrue(agriculturalplant.isBoosted(), "A termelés növelve kéne, hogy legyen!");
    }

    @Test
    public void testLoadFromTruck_Successful() throws Exception {
        int capacity = agriculturalplant.getCapacityIn();

        agriculturalplant.loadFromTruck(capacity / 2);

        assertEquals(capacity / 2, agriculturalplant.getIncomingGrain(), "A beérkező gabonát be kéne tudnia fogadni!");
    }

    @Test
    public void testLoadFromTruck_Failed() throws Exception {
        int capacity = agriculturalplant.getCapacityIn();

        try {
            agriculturalplant.loadFromTruck(capacity * 2);
        } catch (Exception e) {

        }

        assertEquals(0, agriculturalplant.getIncomingGrain());
    }

    @Test
    public void testNewDay() throws Exception {
        int capacity = agriculturalplant.getCapacityIn();
        int batch = agriculturalplant.getBatchAmount();

        agriculturalplant.loadFromTruck(capacity / 2);

        agriculturalplant.newDay();

        assertEquals((capacity / 2) - batch, agriculturalplant.getIncomingGrain(), "Nem használta fel a gabonát!");
        assertEquals(batch, agriculturalplant.getOutgoingFood(), "Nem termelte le a napi adagot!");
    }
}
