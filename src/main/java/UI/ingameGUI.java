package UI;

import engine.rendering.Minimap;
import engine.rendering.Renderer;
import world.tile.Tile;
import world.vehicle.Vehicle;
import world.vehicle.VehicleType;
import controller.GameController.VehicleAction;
import controller.GameController.BuildingAction;

import javax.swing.*;
import java.awt.*;

public class ingameGUI {

    private JFrame gameWindow;
    private GameMapPanel mapPanel;
    private MinimapPanel minimapPanel;
    private JLabel dayCounter;
    private JLabel balanceLabel;
    private JButton roadToggle;
    private JButton stationToggle;
    private JButton speedPaused;
    private JButton speedNormal;
    private JButton speedFast;
    private JButton speedSuperFast;

    private VehicleType tempSelectedType = null;
    private VehicleAction tempVehicleAction = VehicleAction.NONE;

    private BuildingAction tempBuildingAction = BuildingAction.NONE;


    public class GameMapPanel extends JPanel {
        private Renderer renderer;

        public GameMapPanel() {
            setBackground(Color.BLACK);
        }

        public Renderer getRenderer(){return renderer;}
        public void setRenderer(Renderer renderer) {
            this.renderer = renderer;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (renderer != null) {
                renderer.renderMap(g);
            }
        }
    }

    public class MinimapPanel extends JPanel {
        private Minimap minimap;

        public MinimapPanel() {
            setBackground(Color.BLACK);
        }

        public void setMinimap(Minimap minimap){ this.minimap = minimap;}
        public Minimap getMinimap(){return this.minimap;}
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (minimap != null) {
                minimap.render(g);
            }
        }
    }

    public ingameGUI() {
        gameWindow = new JFrame("Noé bárkája");
        gameWindow.setSize(1000, 750);
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameWindow.setLocationRelativeTo(null);
        gameWindow.setLayout(new BorderLayout(10, 10));

        JPanel upperPanel = new JPanel(new BorderLayout(10, 0));
        upperPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        minimapPanel = new MinimapPanel();
        minimapPanel.setPreferredSize(new Dimension(200, 120));
        minimapPanel.setBorder(BorderFactory.createTitledBorder("Minimap"));

        upperPanel.add(minimapPanel, BorderLayout.WEST);

        JPanel buildPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buildPanel.setBorder(BorderFactory.createTitledBorder("Build Options"));

        roadToggle = new JButton("Ut ikon");
        buildPanel.add(roadToggle);

        stationToggle = new JButton("Megálló ikon");
        buildPanel.add(stationToggle);

        buildPanel.add(new JButton("További ikonok"));
        upperPanel.add(buildPanel, BorderLayout.CENTER);

        JPanel napPanel = new JPanel(new BorderLayout());
        napPanel.setPreferredSize(new Dimension(100, 100));
        napPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        dayCounter = new JLabel("<html><center>Day<br><b>1</b></center></html>", SwingConstants.CENTER);
        dayCounter.setFont(new Font("Arial", Font.PLAIN, 18));
        napPanel.add(dayCounter, BorderLayout.CENTER);
        upperPanel.add(napPanel, BorderLayout.EAST);

        gameWindow.add(upperPanel, BorderLayout.NORTH);

        mapPanel = new GameMapPanel();
        mapPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));

        JPanel mapContainer = new JPanel(new BorderLayout());
        mapContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mapContainer.add(mapPanel, BorderLayout.CENTER);

        gameWindow.add(mapContainer, BorderLayout.CENTER);

        JPanel alsoPanel = new JPanel(new BorderLayout(10, 0));
        alsoPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        balanceLabel = new JLabel("<html><span style='font-size:24px'>$10000</span><sup style='font-size:14px'></sup></html>");
        alsoPanel.add(balanceLabel, BorderLayout.WEST);

        JPanel idoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        speedPaused = new JButton("||");
        idoPanel.add(speedPaused);
        speedNormal = new JButton("►");
        idoPanel.add(speedNormal);
        speedFast = new JButton("►►");
        idoPanel.add(speedFast);
        speedSuperFast = new JButton("►|");
        idoPanel.add(speedSuperFast);
        alsoPanel.add(idoPanel, BorderLayout.CENTER);

        JPanel lowerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        lowerPanel.add(new JButton("Mentés"));
        JButton exitGomb = new JButton("Kilépés");
        exitGomb.addActionListener(e -> System.exit(0));
        lowerPanel.add(exitGomb);
        lowerPanel.add(new JButton("?"));
        alsoPanel.add(lowerPanel, BorderLayout.EAST);

        gameWindow.add(alsoPanel, BorderLayout.SOUTH);
    }

    public void mapRefresh() {
        mapPanel.getRenderer().getCamera().setDimensions(mapPanel.getWidth(), mapPanel.getHeight()); //Camera tényleges képernyőméret frissítése
        mapPanel.repaint();

        minimapPanel.getMinimap().setDimensions(minimapPanel.getWidth(), minimapPanel.getHeight()); //Minimap tényleges képernyőméret frissítése
        minimapPanel.repaint();
    }

    public VehicleType showVehicleSelector()
    {
        tempSelectedType = null;

        JDialog dialog = new JDialog(gameWindow, "Jármű vásárlása", true);
        dialog.setLayout(new GridLayout(1, 3, 10, 10));
        dialog.setSize(450, 200);
        dialog.setLocationRelativeTo(gameWindow);

        dialog.add(createVehicleOption("Busz", "Kép helye", "$3000", VehicleType.BUS, dialog));
        dialog.add(createVehicleOption("Étel Szállító", "Kép helye", "$2000", VehicleType.FOODTRUCK, dialog));
        dialog.add(createVehicleOption("Állat Szállító", "Kép helye", "$2500", VehicleType.ANIMALTRUCK, dialog));

        dialog.setVisible(true);

        return tempSelectedType;
    }

    private JPanel createVehicleOption(String name, String imagePlaceholder, String price, VehicleType type, JDialog dialog)
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        JLabel title = new JLabel(name, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(title, BorderLayout.NORTH);

        JLabel imgLabel = new JLabel(imagePlaceholder, SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(100, 100));
        imgLabel.setOpaque(true);
        imgLabel.setBackground(Color.DARK_GRAY);
        imgLabel.setForeground(Color.WHITE);
        panel.add(imgLabel, BorderLayout.CENTER);

        JButton buyBtn = new JButton(price);
        buyBtn.addActionListener(e -> {
            tempSelectedType = type;
            dialog.dispose();
        });

        panel.add(buyBtn, BorderLayout.SOUTH);

        return panel;
    }

    public BuildingAction showBuildingInfo(Tile tile)
    {
        tempBuildingAction = BuildingAction.NONE;

        JDialog dialog = new JDialog(gameWindow, "Building info", true);
        dialog.setSize(600, 300);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(gameWindow);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Bal panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));

        String infoText = "<html><h2 style='text-align:center;'>Információ az épületről</h2><br><center>";
        if (tile.getBuilding() != null) {
            infoText += "<b>Típus:</b> " + tile.getBuilding().getBuildingType().name() + "<br>";
        }
        infoText += "</center></html>";

        JLabel infoLabel = new JLabel(infoText, SwingConstants.CENTER);
        infoLabel.setVerticalAlignment(SwingConstants.TOP);
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // Jobb panel
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setPreferredSize(new Dimension(200, 0));

        JLabel imgLabel = new JLabel("Kép helye", SwingConstants.CENTER);
        imgLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
        rightPanel.add(imgLabel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JLabel typeLabel = new JLabel(tile.getTerrainType().name(), SwingConstants.CENTER);
        actionPanel.add(typeLabel);

        // Ha megallo, vasarolhato jarmu
        if (tile.getTerrainType() == world.tile.TerrainType.STOP) {
            JButton buyVehicleBtn = new JButton("Jármű vásárlása");
            buyVehicleBtn.addActionListener(e -> {
                tempBuildingAction = controller.GameController.BuildingAction.BUY_VEHICLE;
                dialog.dispose(); // Bezárjuk az ablakot, a futás folytatódik
            });
            actionPanel.add(buyVehicleBtn);
        }

        rightPanel.add(actionPanel, BorderLayout.SOUTH);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        dialog.add(mainPanel);
        dialog.setVisible(true);

        return tempBuildingAction;
    }

    public VehicleAction showVehicleInfo(Vehicle v)
    {
        tempVehicleAction = VehicleAction.NONE;
        JDialog dialog = new JDialog(gameWindow, "Jármű információ", true);
        dialog.setSize(500, 300);
        dialog.setLayout(new BorderLayout(15, 15));
        dialog.setLocationRelativeTo(gameWindow);

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));

        String infoText = "<html><h3>Információ a járműről:</h3>"
                + "Sebesség: " + v.getSpeed() + "<br>"
                + "Kapacitás: " + v.getCapacity() + "<br>"
                + (v.getCargoType() != null ? "Rakomány: " + v.getCargoType() : "Rakomány: Üres")
                + "</html>";
        JLabel infoLabel = new JLabel(infoText);
        infoLabel.setVerticalAlignment(SwingConstants.TOP);
        infoLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
        leftPanel.add(infoLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        JButton routeBtn = new JButton("Útvonal kijelölése");
        JButton sellBtn = new JButton("Eladás");
        routeBtn.addActionListener(e -> {
            tempVehicleAction = VehicleAction.ASSIGN_ROUTE;
            dialog.dispose();
        });
        sellBtn.addActionListener(e -> {
            tempVehicleAction = VehicleAction.SELL;
            dialog.dispose();
        });

        buttonPanel.add(routeBtn);
        buttonPanel.add(sellBtn);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(leftPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        rightPanel.setPreferredSize(new Dimension(150, 0));

        JLabel imagePlaceholder = new JLabel("Kép helye", SwingConstants.CENTER);
        imagePlaceholder.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
        rightPanel.add(imagePlaceholder, BorderLayout.CENTER);

        JLabel typeLabel = new JLabel("Típus: " + (v.getVehicleType() != null ? v.getVehicleType().name() : "Ismeretlen"), SwingConstants.CENTER);
        typeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        rightPanel.add(typeLabel, BorderLayout.SOUTH);

        dialog.add(rightPanel, BorderLayout.EAST);

        dialog.setVisible(true);
        return tempVehicleAction;
    }

    public void setDay(int day) {
        dayCounter.setText("<html><center>Nap:<br><b>" + day + "</b></center></html>");
    }

    public void setBalance(int money)
    {
        balanceLabel.setText("<html><span style='font-size:24px'>$" + money + "</span><sup style='font-size:14px'></sup></html>");
    }

    public GameMapPanel getMapPanel() { return mapPanel; }
    public MinimapPanel getMinimapPanel() { return minimapPanel; }

    public JButton getRoadToggle() { return roadToggle; }

    public JButton getSpeedPaused() { return speedPaused; }

    public JButton getSpeedNormal() { return speedNormal; }

    public JButton getSpeedFast() { return speedFast; }

    public JButton getStationToggle() { return stationToggle; }

    public JButton getSpeedSuperFast() { return speedSuperFast; }

    public void show() {
        gameWindow.setVisible(true);
    }


}
