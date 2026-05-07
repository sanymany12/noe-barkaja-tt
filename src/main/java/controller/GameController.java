package controller;

import UI.ingameGUI;
import engine.GameEngine;
import engine.GameListener;
import engine.TimeSpeed;
import world.building.Building;
import world.building.BuildingType;
import world.building.Station;
import world.tile.Point;
import world.tile.Tile;
import world.tile.TerrainType;
import world.tile.road.Road;
import world.tile.road.RoadDirection;
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
import java.util.ArrayList;
import java.util.BitSet;
import javax.swing.SwingUtilities;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.List;

public class GameController implements GameListener {

    private final GameEngine model;
    private final ingameGUI view;

    private int lastMouseX;
    private int lastMouseY;

    private int mapWidthTiles;
    private int mapHeightTiles;

    private enum BuildState { NONE, BUILD_ROAD, ASSIGN_ROUTE, BUILD_STATION, DEMOLISH, BUILD_BRIDGE }
    public enum VehicleAction { NONE, ASSIGN_ROUTE, SELL }
    public enum BuildingAction { NONE, BUY_VEHICLE }

    private BuildState currentState = BuildState.NONE;
    private VehicleType selectedVehicleType = null;
    private Vehicle routingVehicle = null;
    private List<Tile> tempRouteStops = new ArrayList<>();
    private Tile bridgeStartTile = null;

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
                bridgeStartTile = null;
                view.getRoadToggle().setText("**Ut ikon**");
                view.getStationToggle().setText("Megálló ikon");
                view.getDemolishToggle().setText("Bomba ikon");
                view.getBridgeToggle().setText("Hid ikon");

            }
        });
        view.getStationToggle().addActionListener(e -> {
            if(currentState == BuildState.BUILD_STATION) {
                currentState = BuildState.NONE;
                view.getStationToggle().setText("Megálló ikon");
            } else {
                currentState = BuildState.BUILD_STATION;
                bridgeStartTile = null;
                view.getStationToggle().setText("**Megálló ikon**");
                view.getRoadToggle().setText("Ut ikon");
                view.getDemolishToggle().setText("Bomba ikon");
                view.getBridgeToggle().setText("Hid ikon");
            }
        });
        view.getDemolishToggle().addActionListener(e -> {
           if(currentState == BuildState.DEMOLISH)
           {
               currentState = BuildState.NONE;
               view.getDemolishToggle().setText("Bomba ikon");
           } else {
               currentState = BuildState.DEMOLISH;
               bridgeStartTile = null;
               view.getDemolishToggle().setText("**Bomba ikon**");
               view.getRoadToggle().setText("Ut ikon");
               view.getStationToggle().setText("Megallo ikon");
               view.getBridgeToggle().setText("Hid ikon");
           }
        });
        view.getBridgeToggle().addActionListener(e -> {
            if(currentState == BuildState.BUILD_BRIDGE)
            {
                currentState = BuildState.NONE;
                bridgeStartTile = null;
                view.getBridgeToggle().setText("Hid ikon");
            } else {
                currentState = BuildState.BUILD_BRIDGE;
                bridgeStartTile = null;
                view.getBridgeToggle().setText("**Hid ikon");
                view.getRoadToggle().setText("Ut ikon");
                view.getStationToggle().setText("Megallo ikon");
                view.getDemolishToggle().setText("Bomba ikon");
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
        view.getSaveBtn().addActionListener(e -> handleSaveGame());
        view.getLoadBtn().addActionListener(e -> handleLoadGame());
    }

    private void setupMouseControl()
    {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (currentState == BuildState.ASSIGN_ROUTE && routingVehicle != null) {
                        if (!tempRouteStops.isEmpty()) {
                            routingVehicle.clearRoute();
                            for (Tile stop : tempRouteStops) {
                                routingVehicle.addRouteStop(stop);
                            }
                            routingVehicle.startRoute();
                            System.out.println("Útvonal véglegesítve");
                        } else {
                            System.out.println("Útvonal kijelölése megszakadt, a régi útvonal marad.");
                        }
                    }

                    currentState = BuildState.NONE;
                    selectedVehicleType = null;
                    routingVehicle = null;
                    bridgeStartTile = null;
                    view.getRoadToggle().setText("Út ikon");
                    view.getStationToggle().setText("Megálló ikon");
                    view.getBridgeToggle().setText("Hid ikon");
                    view.getDemolishToggle().setText("Bomba ikon");
                    return;
                }

                Point gridPos = model.getCamera().screenToWorld(e.getX(), e.getY());
                Tile clickedTile = model.getWorld().get(gridPos.x, gridPos.y);

                if (currentState == BuildState.NONE && SwingUtilities.isLeftMouseButton(e)) {
                    Vehicle clickedVehicle = null;

                    // Jarmure kattintas
                    if (clickedTile != null) {
                        if (clickedTile.getTerrainType() == TerrainType.ROAD && clickedTile.getRoad() != null) {
                            Road r = clickedTile.getRoad();
                            if (r.getRightLaneV() != null) clickedVehicle = r.getRightLaneV();
                            else if (r.getLeftLaneV() != null) clickedVehicle = r.getLeftLaneV();
                            else if (r.getRightLaneH() != null) clickedVehicle = r.getRightLaneH();
                            else if (r.getLeftLaneH() != null) clickedVehicle = r.getLeftLaneH();
                        } else if (clickedTile.getTerrainType() == TerrainType.STOP && clickedTile.getBuilding() != null && clickedTile.getBuilding().getBuildingType() == BuildingType.STATION) {
                            Station station = (Station) clickedTile.getBuilding();
                            clickedVehicle = station.getVehicle();
                        }
                    }

                    if (clickedVehicle != null) {
                        VehicleAction action = view.showVehicleInfo(clickedVehicle);

                        if (action == VehicleAction.ASSIGN_ROUTE) {
                            currentState = BuildState.ASSIGN_ROUTE;
                            routingVehicle = clickedVehicle;
                            tempRouteStops.clear();
                            System.out.println("Kattints BAL gombbal a MEGÁLLÓKRA, majd JOBB KLIKK a befejezéshez");
                        } else if (action == VehicleAction.SELL) {
                            clickedVehicle.sellVehicle();
                            afterSpending(model.getWorld().getMoney());
                            view.mapRefresh();
                        }
                        return;
                    }

                    // Epuletre vagy megallora kattintas
                    if (clickedTile != null && (clickedTile.getTerrainType() == TerrainType.STOP || clickedTile.getTerrainType() == TerrainType.BUILDING)) {
                        // Info ablak es felhasznalo dontese
                        BuildingAction action = view.showBuildingInfo(clickedTile);

                        // Ha vasarolni szeretne
                        if (action == BuildingAction.BUY_VEHICLE) {
                            VehicleType selectedType = view.showVehicleSelector();

                            if (selectedType != null) {
                                try {
                                    model.getBuildManager().buyVehicle(clickedTile, selectedType);
                                    afterSpending(model.getWorld().getMoney());
                                    view.mapRefresh();
                                } catch (Exception ex) {
                                    System.err.println("Hiba (jármű vásárlás): " + ex.getMessage());
                                }
                            }
                        }
                        return;
                    }

                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                }

                //UTVONAL KIJELOLES
                else if (currentState == BuildState.ASSIGN_ROUTE && SwingUtilities.isLeftMouseButton(e)) {
                    if (clickedTile != null && clickedTile.getTerrainType() == TerrainType.STOP) {
                        tempRouteStops.add(clickedTile);
                        System.out.println("Megálló hozzáadva a listához");
                    } else {
                        System.out.println("Kérlek, egy megállóra kattints!");
                    }
                }

                //Ut epites
                else if (currentState == BuildState.BUILD_ROAD && SwingUtilities.isLeftMouseButton(e)) {
                    buildRoadAtScreen(e.getX(), e.getY());
                }

                // Megallo epitese
                else if (currentState == BuildState.BUILD_STATION && SwingUtilities.isLeftMouseButton(e)) {
                    buildStationAtScreen(e.getX(), e.getY());
                }

                // Rombolas
                else if (currentState == BuildState.DEMOLISH && SwingUtilities.isLeftMouseButton(e)) {
                    demolishAtScreen(e.getX(), e.getY());
                }

                // Hid epitese
                else if (currentState == BuildState.BUILD_BRIDGE && SwingUtilities.isLeftMouseButton(e)) {
                    if (bridgeStartTile == null) {
                        if (clickedTile != null && clickedTile.getTerrainType() == TerrainType.WATER) {
                            bridgeStartTile = clickedTile;
                            System.out.println("Híd kezdőpont kiválasztva. Kattints a túloldalra!");
                        } else {
                            System.out.println("A híd kezdőpontja szarazfolddel szomszedos viz legyen!");
                        }
                    } else {
                        if (clickedTile != null && clickedTile.getTerrainType() == TerrainType.WATER) {
                            try {
                                model.getBuildManager().buildBridge(bridgeStartTile, clickedTile, world.tile.road.BridgeType.WOOD);

                                view.mapRefresh();
                                view.getMinimapPanel().getMinimap().generateImage();
                                afterSpending(model.getWorld().getMoney());
                            } catch (Exception ex) {
                                System.err.println("Hiba: " + ex.getMessage());
                            }
                        } else {
                            System.out.println("A híd végpontja szarazfold melletti viz!");
                        }
                        bridgeStartTile = null;
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
                else if(currentState == BuildState.DEMOLISH && SwingUtilities.isLeftMouseButton(e))
                {
                    demolishAtScreen(e.getX(), e.getY());
                }

            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                double zoomSensitivity = 0.2;
                double currentZoom = model.getCamera().getZoom();
                double newZoom = currentZoom - (e.getWheelRotation() * zoomSensitivity);

                model.getCamera().setZoom(newZoom, e.getX(), e.getY(), mapWidthTiles, mapHeightTiles);

                view.mapRefresh();
            }
        };

        view.getMapPanel().addMouseListener(mouseAdapter);
        view.getMapPanel().addMouseMotionListener(mouseAdapter);
        view.getMapPanel().addMouseWheelListener(mouseAdapter);
    }

    // ESC megszakításokhoz
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
                        tempRouteStops.clear();
                    }

                    currentState = BuildState.NONE;
                    selectedVehicleType = null;
                    routingVehicle = null;
                    bridgeStartTile = null;

                    view.getRoadToggle().setText("Út ikon");
                    view.getStationToggle().setText("Megálló ikon");
                    view.getDemolishToggle().setText("Bomba ikon");
                    view.getBridgeToggle().setText("Hid ikon");
                }
            }
        });
    }

    private void handleSaveGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Játék mentése");
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON fájlok", "json"));
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        int userSelection = fileChooser.showSaveDialog(view.getMapPanel());

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            if (!filePath.toLowerCase().endsWith(".json")) {
                filePath += ".json";
            }

            model.saveGame(filePath);
            System.out.println("Mentés befejezve: " + filePath);
        }
    }

    private void handleLoadGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Játék betöltése");
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON fájlok", "json"));
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        int userSelection = fileChooser.showOpenDialog(view.getMapPanel());

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();

            model.loadGame(fileToLoad.getAbsolutePath());

            view.setBalance(model.getWorld().getMoney());
            view.setDay(model.getWorld().getElapsedTime());
            view.mapRefresh();
        }
    }

    private void buildRoadAtScreen(int screenX, int screenY)
    {
        Point gridPos = model.getCamera().screenToWorld(screenX, screenY);
        Tile tile = model.getWorld().get(gridPos.x, gridPos.y);

        if(tile != null && tile.getTerrainType() == TerrainType.LAND && tile.getBuilding() == null)
        {
            model.getBuildManager().buildRoad(tile, false);
            view.mapRefresh();
            view.getMinimapPanel().getMinimap().generateImage(); //frissítjük a minimap hátterét
        }

        afterSpending(model.getWorld().getMoney());
    }

    private void demolishAtScreen(int screenX, int screenY)
    {
        Point gridPos = model.getCamera().screenToWorld(screenX, screenY);
        Tile tile = model.getWorld().get(gridPos.x, gridPos.y);

        if(tile != null)
        {
            boolean changed = false;

            if(tile.getTerrainType() == TerrainType.ROAD && tile.getRoad() != null)
            {
                model.getWorld().spendMoney(tile.getRoad().getCostToRemove());
                tile.setRoad(null);
                tile.setTerrainType(TerrainType.LAND);
                changed = true;
            }
            else if(tile.getTerrainType() == TerrainType.STOP && tile.getBuilding() != null)
            {
                model.getWorld().spendMoney(15);
                tile.setBuilding(null);
                tile.setTerrainType(TerrainType.LAND);
                changed = true;
            }

            if(changed)
            {
                view.mapRefresh();
                view.getMinimapPanel().getMinimap().generateImage();
                afterSpending(model.getWorld().getMoney());
            }
        }
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

    private void buildStationAtScreen(int screenX, int screenY)
    {
        Point gridPos = model.getCamera().screenToWorld(screenX, screenY);
        Tile tile = model.getWorld().get(gridPos.x, gridPos.y);

        if(tile != null && tile.getTerrainType() == TerrainType.LAND && tile.getBuilding() == null)
        {
            RoadDirection buildingDir = null;

            Tile eszak = model.getWorld().get(gridPos.x, gridPos.y - 1);
            if(eszak != null && eszak.getTerrainType() == TerrainType.BUILDING) buildingDir = RoadDirection.NORTH;

            Tile del = model.getWorld().get(gridPos.x, gridPos.y + 1);
            if(del != null && del.getTerrainType() == TerrainType.BUILDING) buildingDir = RoadDirection.SOUTH;

            Tile kelet = model.getWorld().get(gridPos.x + 1, gridPos.y);
            if(kelet != null && kelet.getTerrainType() == TerrainType.BUILDING) buildingDir = RoadDirection.EAST;

            Tile nyugat = model.getWorld().get(gridPos.x - 1, gridPos.y);
            if(nyugat != null && nyugat.getTerrainType() == TerrainType.BUILDING) buildingDir = RoadDirection.WEST;

            if(buildingDir != null)
            {
                model.getBuildManager().buildStation(tile, buildingDir, false);
                view.mapRefresh();
                view.getMinimapPanel().getMinimap().generateImage();
            } else {
                System.out.println("Az ipari megállót egy épület mellé kell építeni!");
            }
        }
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
        view.getMinimapPanel().getMinimap().generateImage(); //frissítjük a minimap hátterét
    }

    @Override
    public void afterSpending(int money) {
        view.setBalance(money);
    }
}
