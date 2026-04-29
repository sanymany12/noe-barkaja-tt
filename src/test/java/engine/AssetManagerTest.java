package engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

public class AssetManagerTest {

    @BeforeEach
    public void setUp() {
        AssetManager.clearForTesting();
    }

    @Test
    public void testSingleton_ReturnsSameInstance() {

        AssetManager instance1 = AssetManager.getInstance();
        AssetManager instance2 = AssetManager.getInstance();

        assertNotNull(instance1, "A getInstance() nem adhat vissza null-t!");
        assertSame(instance1, instance2, "A Singleton minta hibás: Két különböző példány jött létre!");
    }

    @Test
    public void testGet_WhenAssetMissing_ReturnsNull() {

        AssetManager.getInstance();

        BufferedImage image = AssetManager.get("nem_letezo_kep_xyz");

        assertNull(image, "Nem létező kép esetén null-t kellene visszakapnunk!");
    }

    @Test
    public void testLoadAsset_WithInvalidPath_FailsGracefully() {

        AssetManager manager = AssetManager.getInstance();

        manager.loadAsset("hibas_kep", "/assets/nem/letezo/utvonal.png");

        BufferedImage image = AssetManager.get("hibas_kep");
        assertNull(image, "A hibás elérési úttal megadott kép nem kerülhet a memóriába!");
    }

    @Test
    public void testLoadAsset_DuplicateName_DoesNotCrash() {
        AssetManager manager = AssetManager.getInstance();

        assertDoesNotThrow(() -> {
            manager.loadAsset("teszt_kulcs", "/assets/valami.png");
            manager.loadAsset("teszt_kulcs", "/assets/valami.png");
        }, "A dupla betöltés nem okozhat programösszeomlást (Exception-t)!");
    }
}