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
    private AssetManager assetManager;
    private boolean isRunning;
    private TimeSpeed timeMultiplier;
    private ForestManager forestManager;
    private Timer timer;
    private int tickCounter;

    private GameListener listener; //Milán: MVC módosítás

    private int delay;
    private final int ticksPerDay = 100;

    public void start(GameListener listener) {
        this.listener = listener;
        this.world = new World(20, 20);
        world.initWorld();
        this.camera = new Camera(0, 0, 2.0, 800, 600);
        this.renderer = new Renderer(camera, world);
        this.assetManager = new AssetManager();
        this.isRunning = true;
        this.delay = 5;
        this.timeMultiplier = TimeSpeed.PAUSED;
        this.forestManager = new ForestManager(world);
        this.timer = new Timer(delay * timeMultiplier.getMultiplier(), new TimerListener());
    }

    public void setTimeMultiplier(TimeSpeed ts) {
        if (ts != this.timeMultiplier) {
            if (ts == TimeSpeed.PAUSED) {
                timer.stop();
            } else {
                if (this.timeMultiplier == TimeSpeed.PAUSED) {
                    timer.start();
                }
                timer.setDelay(delay * ts.getMultiplier());
            }
            this.timeMultiplier = ts;
        }
    }

    public Renderer getRenderer() { return this.renderer; }

    public World getWorld() { return this.world; }

    public Camera getCamera() { return this.camera; }

    private void update() {

    }

    class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isRunning) {
                update();
                tickCounter++;

                if(listener != null)
                {
                    listener.onTick();
                }
            }
            if (tickCounter == ticksPerDay) {
                tickCounter = 0;
                world.newDay();

                if(listener != null)
                {
                    listener.onNewDay(world.getElapsedTime() + 1);
                }
            }
        }
    }
}
