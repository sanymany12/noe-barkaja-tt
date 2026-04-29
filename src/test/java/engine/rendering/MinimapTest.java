package engine.rendering;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.World;

import static org.junit.jupiter.api.Assertions.*;

class MinimapTest {

    private World world;
    private Camera camera;
    private Minimap minimap;

    @BeforeEach
    public void setUp() {

        world = new World(20, 20); // 20x20-as pálya

        camera = new Camera(0, 0, 1.0, 800, 600);

        minimap = new Minimap(world, camera, 200, 120);
    }

    @Test
    public void testJumpCameraTo_CenterClickCalculatesCorrectOffset() {

        minimap.jumpCameraTo(100, 60);

        assertEquals(240.0, camera.getOffsetX(), 0.1, "A kamera X eltolása nem a várt érték lett!");
        assertEquals(340.0, camera.getOffsetY(), 0.1, "A kamera Y eltolása nem a várt érték lett!");
    }

    @Test
    public void testJumpCameraTo_TopLeftCornerClick() {

        minimap.jumpCameraTo(0, 0);

        assertEquals(-15, camera.getOffsetX(), 0.1);
        assertEquals(-15, camera.getOffsetY(), 0.1);
    }

    @Test
    public void testSetDimensions_ExecutesWithoutError() {

        assertDoesNotThrow(() -> {
            minimap.setDimensions(300, 150);
        }, "A dimenziók beállítása hibát dobott!");
    }
}