package world.building;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.World;
import world.resources.AnimalType;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CityTest {
    private World world;
    private City city;

    @BeforeEach
    public void setUp() {
        world = new World(40, 40);

        city = new City(world);
    }

    @Test
    public void testInitialState() {
        assertEquals(0, city.getDeliveredAmount(), "Eredetileg nincs leszállított állat!");
    }

    @Test
    public void testOrder() {
        while (city.getOrderedAnimal() == null && city.getOrderedAmount() < 2) {
            city.newDay();
        }

        int orderedAmount = city.getOrderedAmount();

        assertEquals(0, city.getDeliveredAmount());

        city.receiveAnimal();

        assertEquals(1, city.getDeliveredAmount());
        assertEquals(orderedAmount - 1, city.getRemainingAmount());
    }
}
