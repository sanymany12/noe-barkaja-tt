package controller;

import UI.ingameGUI;
import engine.GameEngine;
import engine.GameListener;
import engine.TimeSpeed;
import world.building.*;
import world.resources.AnimalType;
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
import java.sql.Time;
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

    private enum BuildState { NONE, BUILD_ROAD, ASSIGN_ROUTE, BUILD_STATION, DEMOLISH, BUILD_WOODEN_BRIDGE, BUILD_STONE_BRIDGE, BUILD_GLASS_BRIDGE }
    public enum VehicleAction { NONE, ASSIGN_ROUTE, SELL }
    public enum BuildingAction { NONE, BUY_VEHICLE, START_RESEARCH, TRANSPORT_ANIMAL, SELL_ANIMAL, START_CLONING, BOOST_PRODUCTION, TRANSPORT_RESOURCE, BUY_ANIMAL }

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
                view.getRoadToggle().setBorderPainted(false);
            }
            else
            {
                currentState = BuildState.BUILD_ROAD;
                bridgeStartTile = null;
                view.getRoadToggle().setBorderPainted(true);
                view.getStationToggle().setBorderPainted(false);
                view.getDemolishToggle().setBorderPainted(false);
                view.getStoneBridgeToggle().setBorderPainted(false);
                view.getWoodenBridgeToggle().setBorderPainted(false);

            }
        });
        view.getStationToggle().addActionListener(e -> {
            if(currentState == BuildState.BUILD_STATION) {
                currentState = BuildState.NONE;
                view.getStationToggle().setBorderPainted(false);
            } else {
                currentState = BuildState.BUILD_STATION;
                bridgeStartTile = null;
                view.getStationToggle().setBorderPainted(true);
                view.getRoadToggle().setBorderPainted(false);
                view.getDemolishToggle().setBorderPainted(false);
                view.getStoneBridgeToggle().setBorderPainted(false);
                view.getWoodenBridgeToggle().setBorderPainted(false);
            }
        });
        view.getDemolishToggle().addActionListener(e -> {
           if(currentState == BuildState.DEMOLISH)
           {
               currentState = BuildState.NONE;
               view.getDemolishToggle().setBorderPainted(false);
           } else {
               currentState = BuildState.DEMOLISH;
               bridgeStartTile = null;
               view.getDemolishToggle().setBorderPainted(true);
               view.getRoadToggle().setBorderPainted(false);
               view.getStationToggle().setBorderPainted(false);
               view.getStoneBridgeToggle().setBorderPainted(false);
               view.getWoodenBridgeToggle().setBorderPainted(false);
           }
        });
        view.getStoneBridgeToggle().addActionListener(e -> {
            if(currentState == BuildState.BUILD_STONE_BRIDGE)
            {
                currentState = BuildState.NONE;
                bridgeStartTile = null;
                view.getStoneBridgeToggle().setBorderPainted(false);
            } else {
                currentState = BuildState.BUILD_STONE_BRIDGE;
                bridgeStartTile = null;
                view.getStoneBridgeToggle().setBorderPainted(true);
                view.getWoodenBridgeToggle().setBorderPainted(false);
                view.getGlassBridgeToggle().setBorderPainted(false);
                view.getRoadToggle().setBorderPainted(false);
                view.getStationToggle().setBorderPainted(false);
                view.getDemolishToggle().setBorderPainted(false);
            }
        });
        view.getWoodenBridgeToggle().addActionListener(e -> {
            if(currentState == BuildState.BUILD_WOODEN_BRIDGE)
            {
                currentState = BuildState.NONE;
                bridgeStartTile = null;
                view.getWoodenBridgeToggle().setBorderPainted(false);
            } else {
                currentState = BuildState.BUILD_WOODEN_BRIDGE;
                bridgeStartTile = null;
                view.getWoodenBridgeToggle().setBorderPainted(true);
                view.getStoneBridgeToggle().setBorderPainted(false);
                view.getGlassBridgeToggle().setBorderPainted(false);
                view.getRoadToggle().setBorderPainted(false);
                view.getStationToggle().setBorderPainted(false);
                view.getDemolishToggle().setBorderPainted(false);
            }
        });
        view.getGlassBridgeToggle().addActionListener(e -> {
            if(currentState == BuildState.BUILD_GLASS_BRIDGE)
            {
                currentState = BuildState.NONE;
                bridgeStartTile = null;
                view.getGlassBridgeToggle().setBorderPainted(false);
            } else {
                currentState = BuildState.BUILD_GLASS_BRIDGE;
                bridgeStartTile = null;
                view.getGlassBridgeToggle().setBorderPainted(true);
                view.getWoodenBridgeToggle().setBorderPainted(false);
                view.getStoneBridgeToggle().setBorderPainted(false);
                view.getRoadToggle().setBorderPainted(false);
                view.getStationToggle().setBorderPainted(false);
                view.getDemolishToggle().setBorderPainted(false);
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
                            view.errorPopup("Útvonal kijelölése megszakadt, a régi útvonal marad.");
                        }
                    }

                    currentState = BuildState.NONE;
                    selectedVehicleType = null;
                    routingVehicle = null;
                    bridgeStartTile = null;
                    view.getRoadToggle().setBorderPainted(false);
                    view.getStationToggle().setBorderPainted(false);
                    view.getStoneBridgeToggle().setBorderPainted(false);
                    view.getWoodenBridgeToggle().setBorderPainted(false);
                    view.getGlassBridgeToggle().setBorderPainted(false);
                    view.getDemolishToggle().setBorderPainted(false);
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
                        TimeSpeed originalSpeed = model.getTimeSpeed();
                        model.setTimeMultiplier(TimeSpeed.PAUSED);
                        VehicleAction action = view.showVehicleInfo(clickedVehicle);

                        model.setTimeMultiplier(originalSpeed);

                        if (action == VehicleAction.ASSIGN_ROUTE) {
                            currentState = BuildState.ASSIGN_ROUTE;
                            routingVehicle = clickedVehicle;
                            tempRouteStops.clear();
                            model.setTimeMultiplier(TimeSpeed.PAUSED);
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
                        TimeSpeed originalSpeed = model.getTimeSpeed();
                        model.setTimeMultiplier(TimeSpeed.PAUSED);

                        // Info ablak es felhasznalo dontese
                        BuildingAction action = view.showBuildingInfo(clickedTile);

                        model.setTimeMultiplier(originalSpeed);

                        // Ha vasarolni szeretne
                        if (action == BuildingAction.BUY_VEHICLE) {
                            model.setTimeMultiplier(TimeSpeed.PAUSED);
                            VehicleType selectedType = view.showVehicleSelector();

                            if (selectedType != null) {
                                try {
                                    model.getBuildManager().buyVehicle(clickedTile, selectedType);
                                    afterSpending(model.getWorld().getMoney());
                                    view.mapRefresh();
                                } catch (Exception ex) {
                                    view.errorPopup("Hiba (jármű vásárlás): " + ex.getMessage());
                                }
                            }

                            model.setTimeMultiplier(originalSpeed);
                        }
                        else if(action == BuildingAction.BUY_ANIMAL)
                        {
                            model.setTimeMultiplier(TimeSpeed.PAUSED);
                            AnimalType selectedAnimal = view.showAnimalSelector();

                            if(selectedAnimal != null && clickedTile.getBuilding() instanceof Enclosure)
                            {
                                Enclosure enc = (Enclosure) clickedTile.getBuilding();
                                enc.purchaseAnimal(selectedAnimal);
                                afterSpending(model.getWorld().getMoney());
                                view.mapRefresh();
                            }

                            model.setTimeMultiplier(originalSpeed);
                        }
                        else if(action == BuildingAction.START_RESEARCH)
                        {
                            ResearchLab rl = (ResearchLab) clickedTile.getBuilding();
                            if(model.getWorld().getMoney() >= rl.getCostOfResearch())
                            {
                                if(rl.startResearch())
                                {
                                    model.getWorld().spendMoney(rl.getCostOfResearch());
                                    afterSpending(model.getWorld().getMoney());
                                    view.mapRefresh();
                                }
                            } else {
                                view.errorPopup("Nincs elég pénz a kutatáshoz!");
                            }
                        }
                        else if(action == BuildingAction.TRANSPORT_ANIMAL)
                        {
                            // TODO
                        }
                        else if(action == BuildingAction.SELL_ANIMAL)
                        {
                            if(clickedTile.getBuilding() instanceof Enclosure)
                            {
                                Enclosure enc = (Enclosure) clickedTile.getBuilding();
                                if(enc.hasAnimals())
                                {
                                    enc.sellAnimal();
                                    afterSpending(model.getWorld().getMoney());
                                    view.mapRefresh();
                                }
                            }
                        }
                        else if(action == BuildingAction.START_CLONING) {
                            if(clickedTile.getBuilding() instanceof CloningFacility)
                            {
                                CloningFacility cf = (CloningFacility) clickedTile.getBuilding();
                                if(model.getWorld().getMoney() >= cf.getCostOfCloning())
                                {
                                    if(cf.startCloning())
                                    {
                                        model.getWorld().spendMoney(cf.getCostOfCloning());
                                        afterSpending(model.getWorld().getMoney());
                                        view.mapRefresh();
                                    }
                                } else
                                {
                                    view.errorPopup("Nincs elég pénz a klónozáshoz!");
                                }
                            }
                        }
                        else if(action == BuildingAction.BOOST_PRODUCTION)
                        {
                            if(clickedTile.getBuilding() instanceof Farm)
                            {
                                Farm farm = (Farm) clickedTile.getBuilding();
                                if(model.getWorld().getMoney() >= farm.getBoostCost())
                                {
                                    farm.boostProduction();
                                    afterSpending(model.getWorld().getMoney());
                                    view.mapRefresh();
                                } else {
                                    view.errorPopup("Nincs elég pénz a boosterhez!");
                                }
                            }
                            else if(clickedTile.getBuilding() instanceof AgriculturalPlant)
                            {
                                AgriculturalPlant plant =  (AgriculturalPlant) clickedTile.getBuilding();
                                if(model.getWorld().getMoney() >= plant.getBoostCost())
                                {
                                    plant.boostProduction();
                                    afterSpending(model.getWorld().getMoney());
                                } else {
                                    view.errorPopup("Nincs elég pénz a boosterhez!");
                                }
                            }
                        }
                        else if(action == BuildingAction.TRANSPORT_RESOURCE)
                        {
                            // TODO
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
                        view.errorPopup("Kérlek, egy megállóra kattints!");
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
                else if (currentState == BuildState.BUILD_WOODEN_BRIDGE && SwingUtilities.isLeftMouseButton(e)) {
                    if (bridgeStartTile == null) {
                        if (clickedTile != null && clickedTile.getTerrainType() == TerrainType.WATER) {
                            bridgeStartTile = clickedTile;
                            System.out.println("Híd kezdőpont kiválasztva. Kattints a túloldalra!");
                        } else {
                            view.errorPopup("A híd kezdőpontja szarazfolddel szomszedos viz legyen!");
                        }
                    } else {
                        if (clickedTile != null && clickedTile.getTerrainType() == TerrainType.WATER) {
                            try {
                                model.getBuildManager().buildBridge(bridgeStartTile, clickedTile, world.tile.road.BridgeType.WOOD, false);

                                view.mapRefresh();
                                view.getMinimapPanel().getMinimap().generateImage();
                                afterSpending(model.getWorld().getMoney());
                            } catch (Exception ex) {
                                view.errorPopup("Hiba: " + ex.getMessage());
                            }
                        } else {
                            view.errorPopup("A híd végpontja szarazfold melletti viz!");
                        }
                        bridgeStartTile = null;
                    }
                }
                else if (currentState == BuildState.BUILD_STONE_BRIDGE && SwingUtilities.isLeftMouseButton(e)) {
                    if (bridgeStartTile == null) {
                        if (clickedTile != null && clickedTile.getTerrainType() == TerrainType.WATER) {
                            bridgeStartTile = clickedTile;
                            System.out.println("Híd kezdőpont kiválasztva. Kattints a túloldalra!");
                        } else {
                            view.errorPopup("A híd kezdőpontja szarazfolddel szomszedos viz legyen!");
                        }
                    } else {
                        if (clickedTile != null && clickedTile.getTerrainType() == TerrainType.WATER) {
                            try {
                                model.getBuildManager().buildBridge(bridgeStartTile, clickedTile, world.tile.road.BridgeType.STONE, false);

                                view.mapRefresh();
                                view.getMinimapPanel().getMinimap().generateImage();
                                afterSpending(model.getWorld().getMoney());
                            } catch (Exception ex) {
                                view.errorPopup("Hiba: " + ex.getMessage());
                            }
                        } else {
                            view.errorPopup("A híd végpontja szarazfold melletti viz!");
                        }
                        bridgeStartTile = null;
                    }
                }
                else if (currentState == BuildState.BUILD_GLASS_BRIDGE && SwingUtilities.isLeftMouseButton(e)) {
                    if (bridgeStartTile == null) {
                        if (clickedTile != null && clickedTile.getTerrainType() == TerrainType.WATER) {
                            bridgeStartTile = clickedTile;
                            System.out.println("Híd kezdőpont kiválasztva. Kattints a túloldalra!");
                        } else {
                            view.errorPopup("A híd kezdőpontja szarazfolddel szomszedos viz legyen!");
                        }
                    } else {
                        if (clickedTile != null && clickedTile.getTerrainType() == TerrainType.WATER) {
                            try {
                                model.getBuildManager().buildBridge(bridgeStartTile, clickedTile, world.tile.road.BridgeType.GLASS, false);

                                view.mapRefresh();
                                view.getMinimapPanel().getMinimap().generateImage();
                                afterSpending(model.getWorld().getMoney());
                            } catch (Exception ex) {
                                view.errorPopup("Hiba: " + ex.getMessage());
                            }
                        } else {
                            view.errorPopup("A híd végpontja szarazfold melletti viz!");
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

                    view.getRoadToggle().setBorderPainted(false);
                    view.getStationToggle().setBorderPainted(false);
                    view.getDemolishToggle().setBorderPainted(false);
                    view.getStoneBridgeToggle().setBorderPainted(false);
                    view.getWoodenBridgeToggle().setBorderPainted(false);
                    view.getGlassBridgeToggle().setBorderPainted(false);
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

            view.setBalance(model.getWorld().getMoney(), model.getWorld().getAnnualCostOfVehicles());
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
            model.getBuildManager().destroy(tile);
            view.mapRefresh();
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
                view.errorPopup("Az ipari megállót egy épület mellé kell építeni!");
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
        view.setBalance(model.getWorld().getMoney(), model.getWorld().getAnnualCostOfVehicles());
        view.getMinimapPanel().getMinimap().generateImage();

        if (model.getWorld().getMoney() < 0) {
            model.setTimeMultiplier(TimeSpeed.PAUSED);
            view.showGameOver();
        }
    }

    @Override
    public void afterSpending(int money) {
        view.setBalance(money, model.getWorld().getAnnualCostOfVehicles());

        if (money < 0) {
            model.setTimeMultiplier(TimeSpeed.PAUSED);
            view.showGameOver();
        }
    }

}
