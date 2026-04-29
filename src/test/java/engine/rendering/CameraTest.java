package engine.rendering;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.tile.Point;

import static org.junit.jupiter.api.Assertions.*;

public class CameraTest {

    private Camera camera;

    @BeforeEach
    public void setUp() {
        camera = new Camera(0, 0, 1.0, 800, 600);
    }

    @Test
    public void testZoomLimits() {
        // Teszteljük, hogy a zoom nem mehet-e a MIN_ZOOM (0.5) alá
        camera.setZoom(0.1, 0,0,20, 20);
        assertEquals(0.5, camera.getZoom(), "A zoomnak meg kell állnia a MIN_ZOOM (0.5) értéknél!");

        // Teszteljük, hogy a zoom nem mehet-e a MAX_ZOOM (4.0) fölé
        camera.setZoom(5.0, 0,0,20, 20);
        assertEquals(4.0, camera.getZoom(), "A zoomnak meg kell állnia a MAX_ZOOM (4.0) értéknél!");

        // Teszteljük a normál értéket
        camera.setZoom(2.0, 0,0,20, 20);
        assertEquals(2.0, camera.getZoom(), "A zoomnak be kell állnia a megadott helyes értékre!");
    }

    @Test
    public void testWorldToScreen_NoOffset_NoZoom() {
        // 1.0 zoom, 0 offset esetén a 2. oszlop (x=2) és 3. sor (y=3)
        // x = 2 * 64 = 128
        // y = 3 * 64 = 192
        Point screenPos = camera.worldToScreen(2, 3);

        assertEquals(128, screenPos.x, "Az X pixel koordináta hibás!");
        assertEquals(192, screenPos.y, "Az Y pixel koordináta hibás!");
    }

    @Test
    public void testWorldToScreen_WithOffsetAndZoom() {
        camera = new Camera(100, 50, 2.0, 800, 600);

        // 2.0 zoom, offset (100, 50)
        // x = (2 * 64 * 2.0) - 100 = 256 - 100 = 156
        // y = (3 * 64 * 2.0) - 50 = 384 - 50 = 334
        Point screenPos = camera.worldToScreen(2, 3);

        assertEquals(156, screenPos.x, "Az eltolt/nagyított X pixel koordináta hibás!");
        assertEquals(334, screenPos.y, "Az eltolt/nagyított Y pixel koordináta hibás!");
    }

    @Test
    public void testScreenToWorld_ClickDetection() {
        // Ha a játékos kattint egy pixelre (pl. 150, 200), megkapjuk a megfelelő rácsot?
        // 150 / 64 = 2.34 -> kerekítve lefelé: 2
        // 200 / 64 = 3.125 -> kerekítve lefelé: 3
        Point worldPos = camera.screenToWorld(150, 200);

        assertEquals(2, worldPos.x, "A kattintás X csempe indexe hibás!");
        assertEquals(3, worldPos.y, "A kattintás Y csempe indexe hibás!");
    }

    @Test
    public void testScreenToWorld_WithOffsetAndZoom() {
        camera = new Camera(100, 50, 2.0, 800, 600);

        // Visszafelé teszteljük a korábbi esetet: ha a 156, 334-re kattintunk,
        // a 2,3 -as csempét kell visszakapnunk!
        Point worldPos = camera.screenToWorld(156, 334);

        assertEquals(2, worldPos.x, "Az eltolt kattintás X csempe indexe hibás!");
        assertEquals(3, worldPos.y, "Az eltolt kattintás Y csempe indexe hibás!");
    }

    @Test
    public void testClampCamera_MapLargerThanScreen() {
        // Térkép: 20x20 csempe (1280x1280 pixel zoom 1.0-nél)
        // Képernyő: 800x600
        int mapWidth = 20;
        int mapHeight = 20;

        // 1. Teszt: Bal felső sarok túllépése (negatív offset)
        camera.move(-500, -500, mapWidth, mapHeight);

        // A MARGIN 15, tehát a minimális eltolás -15 lehet
        assertEquals(-15.0, camera.getOffsetX(), "A bal margó határa nem jó!");
        assertEquals(-15.0, camera.getOffsetY(), "A felső margó határa nem jó!");

        // 2. Teszt: Jobb alsó sarok túllépése
        camera.move(5000, 5000, mapWidth, mapHeight);

        // Max X = 1280 - 800 + 15 = 495
        // Max Y = 1280 - 600 + 15 = 695
        assertEquals(495.0, camera.getOffsetX(), "A jobb margó határa nem jó!");
        assertEquals(695.0, camera.getOffsetY(), "Az alsó margó határa nem jó!");
    }

    @Test
    public void testClampCamera_MapSmallerThanScreen() {
        // Térkép: 5x5 csempe (320x320 pixel zoom 1.0-nél)
        // Képernyő: 800x600
        int mapWidth = 5;
        int mapHeight = 5;

        // Bárhova is akarjuk mozgatni a kamerát, a térkép fixen középre kell hogy ugorjon
        camera.move(100, 100, mapWidth, mapHeight);

        // Középre igazítás X: -(800 - 320) / 2 = -240
        // Középre igazítás Y: -(600 - 320) / 2 = -140
        assertEquals(-240.0, camera.getOffsetX(), "Kisebb térképnél az X középre igazítás hibás!");
        assertEquals(-140.0, camera.getOffsetY(), "Kisebb térképnél az Y középre igazítás hibás!");
    }
}