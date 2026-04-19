package engine;

import engine.rendering.Camera;
import engine.rendering.Minimap;
import engine.rendering.Renderer;
import world.World;
import world.vehicle.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameEngine {
    private World world;
    private Camera camera;
    private Renderer renderer;
    private Minimap minimap;
    private AssetManager assetManager;
    private boolean isRunning;
    private TimeSpeed timeMultiplier;
    private ForestManager forestManager;
    private BuildManager buildManager;
    private Timer timer;
    private int tickCounter;

    private GameListener listener; //Milán: MVC módosítás

    private int delay;
    private final int ticksPerDay = 100;

    public void start(GameListener listener) {
        this.listener = listener;
        this.world = new World(20, 20);
        world.initWorld();
        this.camera = new Camera(0, 0, 2.0, 1000, 750);
        this.renderer = new Renderer(camera, world);
        this.minimap = new Minimap(world, camera, 200, 120);
        this.assetManager = new AssetManager();
        this.isRunning = true;
        this.delay = 5;
        this.timeMultiplier = TimeSpeed.PAUSED;
        this.forestManager = new ForestManager(world);
        this.buildManager = new BuildManager(world);
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

    public void saveGame(String fileName) {

        TimeSpeed oldSpeed = this.timeMultiplier; //pause
        setTimeMultiplier(TimeSpeed.PAUSED);

        SaveManager.saveGame(this.world, fileName); //mentés

        setTimeMultiplier(oldSpeed); //continue
    }

    public void loadGame(String fileName) {

        setTimeMultiplier(TimeSpeed.PAUSED); //pause

        World loadedWorld = SaveManager.loadGame(fileName);

        if (loadedWorld != null) {
            //a régi világot és kicseréljük az újra
            world = loadedWorld;

            renderer.setWorld(world);
            minimap.setWorld(world);
            forestManager.setWorld(world);

            buildManager.setWorld(world);

            minimap.generateImage();

            if (listener != null) {
                listener.onNewDay(world.getElapsedTime()); //GUI frissítése
            }

        } else {
            System.out.println("Hiba történt a betöltés során.");
        }
    }

    public Renderer getRenderer() { return this.renderer; }

    public Minimap getMinimap() {return minimap;}

    public World getWorld() { return this.world; }

    public Camera getCamera() { return this.camera; }

    public BuildManager getBuildManager() {
        return this.buildManager;
    }

    private void update() {

        if (world != null && world.getVehicles() != null) {
            for (Vehicle vehicle : world.getVehicles()) {
                try {
                    vehicle.update();
                } catch (Exception ex) {
                    System.err.println("Hiba a jármű mozgatásakor!");
                    ex.printStackTrace();
                }
            }
        }
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
                try{
                    world.newDay();
                }catch (Exception ex){
                    ex.printStackTrace();
                }

                forestManager.updateForests();
                if(listener != null)
                {
                    listener.onNewDay(world.getElapsedTime() + 1);
                }
            }
        }
    }
}
