package engine;

import engine.rendering.Camera;
import world.World;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class InputManager implements KeyListener, MouseWheelListener {
    private Camera camera;
    private World world; // Szükség van rá a térkép méretei miatt (clamping)

    // Gombok állapotának tárolása a folyamatos (smooth) mozgáshoz
    private boolean upPressed, downPressed, leftPressed, rightPressed;

    // Beállítások (ezeket finomhangolhatod, ahogy jól esik)
    private static final double CAMERA_SPEED = 15.0; // Pixel per update
    private static final double ZOOM_SPEED = 0.1;    // Mennyit zoomoljon egy kattanásra

    public InputManager(Camera camera, World world) {
        this.camera = camera;
        this.world = world;
    }

    // --- 1. A FOLYAMATOS MOZGÁS (Ezt a Game Loop-ból hívd meg másodpercenként 60x!) ---
    public void update() {
        double deltaX = 0;
        double deltaY = 0;

        if (upPressed) deltaY -= CAMERA_SPEED;
        if (downPressed) deltaY += CAMERA_SPEED;
        if (leftPressed) deltaX -= CAMERA_SPEED;
        if (rightPressed) deltaX += CAMERA_SPEED;

        // Csak akkor mozgunk, ha tényleg lenyomtunk valamit
        if (deltaX != 0 || deltaY != 0) {
            camera.move(deltaX, deltaY, world.getCols(), world.getRows());
        }
    }

    // --- 2. BILLENTYŰZET FIGYELÉSE (WASD) ---
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) upPressed = true;
        if (key == KeyEvent.VK_S) downPressed = true;
        if (key == KeyEvent.VK_A) leftPressed = true;
        if (key == KeyEvent.VK_D) rightPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) upPressed = false;
        if (key == KeyEvent.VK_S) downPressed = false;
        if (key == KeyEvent.VK_A) leftPressed = false;
        if (key == KeyEvent.VK_D) rightPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Erre most nincs szükségünk (ez karakterek beírásánál hasznos)
    }

    // --- 3. EGÉRGÖRGŐ FIGYELÉSE (ZOOM) ---
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // e.getWheelRotation() negatív, ha felfelé görgetsz, és pozitív, ha lefelé
        int notches = e.getWheelRotation();
        double currentZoom = camera.getZoom();

        if (notches < 0) {
            currentZoom += ZOOM_SPEED; // Felfelé görgetés = Nagyítás (Zoom In)
        } else {
            currentZoom -= ZOOM_SPEED; // Lefelé görgetés = Kicsinyítés (Zoom Out)
        }

        camera.setZoom(currentZoom, world.getCols(), world.getRows());
    }
}