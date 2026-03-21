package engine;

import engine.rendering.Camera;
import engine.rendering.Renderer;
import world.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameEngine {
    private World world;
    private Camera camera;
    private Renderer renderer;
    private Graphics graphics;
    private AssetManager assetManager;
    private boolean isRunning;
    private TimeSpeed timeMultiplier;
    private ForestManager forestManager;
    private Timer timer;
    private int tickCounter;

    private int ticksPerSecond = 20;
    private final int ticksPerDay = 100;

    public void start() {
        this.world = new World(20, 20);
        this.camera = new Camera(0, 0, 1, 1920, 1080);
        this.renderer = new Renderer(camera, world);
        this.assetManager = new AssetManager();
        this.isRunning = true;
        this.ticksPerSecond = 20;
        this.timeMultiplier = TimeSpeed.PAUSED;
        this.forestManager = new ForestManager(world);
        this.timer = new Timer(ticksPerSecond * timeMultiplier.getMultiplier(), new TimerListener());
        timer.start();
    }

    public void setTimeMultiplier(TimeSpeed ts) {
        this.timeMultiplier = ts;
    }

    private void update() {
        renderer.renderMap(graphics);
    }

    class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isRunning) {
                update();
                tickCounter++;
            }
            if (tickCounter == ticksPerDay) {
                tickCounter = 0;
                world.newDay();
            }
        }
    }
}
