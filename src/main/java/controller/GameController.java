package controller;

import UI.ingameGUI;
import engine.GameEngine;
import engine.GameListener;
import engine.TimeSpeed;
import world.tile.Point;
import world.tile.Tile;
import world.tile.TerrainType;
import world.vehicle.Vehicle;
import world.vehicle.VehicleType;

import javax.swing.KeyStroke;
import javax.swing.InputMap;
import javax.swing.ActionMap;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.BitSet;
import javax.swing.SwingUtilities;

public class GameController implements GameListener {

    private final GameEngine model;
    private final ingameGUI view;

    private int lastMouseX;
    private int lastMouseY;

    private int mapWidthTiles;
    private int mapHeightTiles;

    private enum BuildState { NONE, BUY_VEHICLE, BUILD_ROAD, ASSIGN_ROUTE}
    public enum VehicleAction { NONE, ASSIGN_ROUTE, SELL }
    private BuildState currentState = BuildState.NONE;
    private VehicleType selectedVehicleType = null;
    private Vehicle routingVehicle = null;

    public GameController(GameEngine model, ingameGUI view)
    {
        this.model = model;
        this.view = view;

        this.model.start(this);

        this.view.getMapPanel().setRenderer(this.model.getRenderer());
        this.view.getMinimapPanel().setMinimap(this.model.getMinimap());

        mapWidthTiles = model.getWorld().getCols();
        mapHeightTiles = model.getWorld().getRows();

        setupButtons();
        setupMouseControl();
        setupKeyboardControl();
    }

    private void setupButtons()
    {
        view.getRoadToggle().addActionListener(e -> {
            if(currentState == BuildState.BUILD_ROAD)
            {
                currentState = BuildState.NONE;
                view.getRoadToggle().setText("Ut ikon");
            }
            else
            {
                currentState = BuildState.BUILD_ROAD;
                view.getRoadToggle().setText("**Ut ikon**");

            }
        });
        view.getVehicleToggle().addActionListener(e -> {
            if(currentState == BuildState.BUY_VEHICLE) {
                currentState = BuildState.NONE;
                view.getRoadToggle().setText("Jarmu ikon");
            } else {
                VehicleType selectedType = view.showVehicleSelector();

                if(selectedType != null)
                {
                    currentState = BuildState.BUY_VEHICLE;
                    this.selectedVehicleType = selectedType;

                    view.getRoadToggle().setText("Ut ikon");
                    view.getVehicleToggle().setText("**" + selectedVehicleType.name() + "++");
                }

            }
        });
        view.getSpeedPaused().addActionListener(e -> {
            model.setTimeMultiplier(TimeSpeed.PAUSED);
        });
        view.getSpeedNormal().addActionListener(e -> {
            model.setTimeMultiplier(TimeSpeed.NORMAL);
        });
        view.getSpeedFast().addActionListener(e -> {
            model.setTimeMultiplier(TimeSpeed.FAST);
        });
        view.getSpeedSuperFast().addActionListener(e -> {
            model.setTimeMultiplier(TimeSpeed.SUPERFAST);
        });
    }

    private void setupMouseControl()
    {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                if(SwingUtilities.isRightMouseButton(e))
                {
                    if(currentState == BuildState.ASSIGN_ROUTE && routingVehicle != null)
                    {
                        routingVehicle.startRoute();
                        System.out.println("Útvonal véglegesítve");
                    }

                    currentState = BuildState.NONE;
                    selectedVehicleType = null;
                    routingVehicle = null;
                    view.getRoadToggle().setText("Ut ikon");
                    view.getVehicleToggle().setText("Jarmu ikon");
                    return;
                }

                Point gridPos = model.getCamera().screenToWorld(e.getX(), e.getY());
                Tile clickedTile = model.getWorld().get(gridPos.x, gridPos.y);

                if(currentState == BuildState.NONE && SwingUtilities.isLeftMouseButton(e))
                {
                    if(clickedTile != null && clickedTile.getTerrainType() == TerrainType.ROAD && clickedTile.getRoad() != null)
                    {
                        Vehicle clickedVehicle = clickedTile.getRoad().getRightLane();

                        if(clickedVehicle != null)
                        {
                            VehicleAction action = view.showVehicleInfo(clickedVehicle);

                            if(action == VehicleAction.ASSIGN_ROUTE)
                            {
                                currentState = BuildState.ASSIGN_ROUTE;
                                routingVehicle = clickedVehicle;
                                routingVehicle.clearRoute();
                                System.out.println("Kattints BAL gombbal az utakra a megállókhoz, majd JOBB KLIKK a befejezéshez");
                            }
                            else if(action == VehicleAction.SELL)
                            {
                                // TODO
                            }
                            return;
                        }
                    }

                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                }

                //UTVONAL KIJELOLES
                else if(currentState == BuildState.ASSIGN_ROUTE && SwingUtilities.isLeftMouseButton(e))
                {
                    if(clickedTile != null && clickedTile.getTerrainType() == TerrainType.ROAD)
                    {
                        routingVehicle.addRouteStop(clickedTile);
                        System.out.println("Megálló hozzáadva a listához");
                    }
                }

                //UT EPITES
                else if(currentState == BuildState.BUILD_ROAD && SwingUtilities.isLeftMouseButton(e))
                {
                    buildRoadAtScreen(e.getX(), e.getY());
                }

                //JARMU LERAKASA
                else if(currentState == BuildState.BUY_VEHICLE && SwingUtilities.isLeftMouseButton(e))
                {
                    try {
                        buyVehicleAtScreen(e.getX(), e.getY(), selectedVehicleType);
                    } catch (Exception ex) {
                        System.err.println("Hiba jármű vásárláskor: " + ex.getMessage() );
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e)
            {
                if(currentState == BuildState.BUILD_ROAD && SwingUtilities.isLeftMouseButton(e))
                {
                    buildRoadAtScreen(e.getX(), e.getY());
                }
                else if(currentState == BuildState.NONE && SwingUtilities.isLeftMouseButton(e))
                {
                    int dx = e.getX() - lastMouseX;
                    int dy = e.getY() - lastMouseY;

                    model.getCamera().move(-dx, -dy, mapWidthTiles, mapHeightTiles);

                    lastMouseX = e.getX();
                    lastMouseY = e.getY();

                    view.mapRefresh();
                }

            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                double zoomSensitivity = 0.2;
                double currentZoom = model.getCamera().getZoom();
                double newZoom = currentZoom - (e.getWheelRotation() * zoomSensitivity);

                model.getCamera().setZoom(newZoom, mapWidthTiles, mapHeightTiles);

                view.mapRefresh();
            }
        };

        view.getMapPanel().addMouseListener(mouseAdapter);
        view.getMapPanel().addMouseMotionListener(mouseAdapter);
        view.getMapPanel().addMouseWheelListener(mouseAdapter);
    }

    // ESC útvonal kijelölés megszakításához
    private void setupKeyboardControl()
    {
        InputMap inputMap = view.getMapPanel().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = view.getMapPanel().getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancelAction");

        actionMap.put("cancelAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState != BuildState.NONE) {
                    System.out.println("Művelet megszakítva (ESC)");

                    if (currentState == BuildState.ASSIGN_ROUTE && routingVehicle != null) {
                        routingVehicle.clearRoute();
                    }

                    currentState = BuildState.NONE;
                    selectedVehicleType = null;
                    routingVehicle = null;

                    view.getRoadToggle().setText("Ut ikon");
                    view.getVehicleToggle().setText("Jarmu ikon");
                }
            }
        });
    }

    private void buildRoadAtScreen(int screenX, int screenY)
    {
        Point gridPos = model.getCamera().screenToWorld(screenX, screenY);
        Tile tile = model.getWorld().get(gridPos.x, gridPos.y);

        if(tile != null && tile.getTerrainType() == TerrainType.LAND && tile.getBuilding() == null)
        {
            model.getBuildManager().buildRoad(tile);
            view.mapRefresh();
            view.getMinimapPanel().getMinimap().generateImage(); //frissítjük a minimap hátterét
        }

        afterSpending(model.getWorld().getMoney());
    }

    private void buyVehicleAtScreen(int screenX, int screenY, VehicleType type) throws Exception
    {
        Point gridPos = model.getCamera().screenToWorld(screenX, screenY);
        Tile tile = model.getWorld().get(gridPos.x, gridPos.y);

        if(tile != null && tile.getTerrainType() == TerrainType.ROAD && tile.getRoad() != null)
        {
            model.getBuildManager().buyVehicle(tile, type);
            view.mapRefresh();
        }

        afterSpending(model.getWorld().getMoney());
    }

    @Override
    public void onTick()
    {
        view.mapRefresh();
    }

    @Override
    public void onNewDay(int currentDay)
    {
        view.setDay(currentDay);
    }

    @Override
    public void afterSpending(int money) {
        view.setBalance(money);
    }
}
