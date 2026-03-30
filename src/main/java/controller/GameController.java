package controller;

import UI.ingameGUI;
import engine.GameEngine;
import engine.GameListener;
import engine.TimeSpeed;
import world.tile.Point;
import world.tile.Tile;
import world.tile.TerrainType;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.SwingUtilities;

public class GameController implements GameListener {

    private final GameEngine model;
    private final ingameGUI view;

    private int lastMouseX;
    private int lastMouseY;

    private int mapWidthTiles;
    private int mapHeightTiles;

    private enum BuildState { NONE, BUILD_ROAD}
    private BuildState currentState = BuildState.NONE;

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
                    currentState = BuildState.NONE;
                    view.getRoadToggle().setText("Ut ikon");
                    return;
                }

                if(currentState == BuildState.BUILD_ROAD && SwingUtilities.isLeftMouseButton(e))
                {
                    buildRoadAtScreen(e.getX(), e.getY());
                }
                else if(currentState == BuildState.NONE && SwingUtilities.isLeftMouseButton(e))
                {
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
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

    private void buildRoadAtScreen(int screenX, int screenY)
    {
        Point gridPos = model.getCamera().screenToWorld(screenX, screenY);
        Tile tile = model.getWorld().get(gridPos.x, gridPos.y);

        if(tile != null && tile.getTerrainType() == TerrainType.LAND && tile.getBuilding() == null)
        {
            model.getWorld().buildRoad(tile);
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
