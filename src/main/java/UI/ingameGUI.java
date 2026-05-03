package UI;

import engine.rendering.Minimap;
import engine.rendering.Renderer;
import world.tile.Tile;
import world.vehicle.Vehicle;
import world.vehicle.VehicleType;
import controller.GameController.VehicleAction;
import controller.GameController.BuildingAction;
import world.building.*;
import world.vehicle.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ingameGUI {

    private JFrame gameWindow;
    private GameMapPanel mapPanel;
    private MinimapPanel minimapPanel;
    private JLabel dayCounter;
    private JLabel balanceLabel;
    private JButton roadToggle;
    private JButton stationToggle;
    private JButton demolishToggle;
    private JButton speedPaused;
    private JButton saveBtn;
    private JButton loadBtn;
    private JButton speedNormal;
    private JButton speedFast;
    private JButton speedSuperFast;

    private VehicleType tempSelectedType = null;
    private VehicleAction tempVehicleAction = VehicleAction.NONE;

    private BuildingAction tempBuildingAction = BuildingAction.NONE;


    public class GameMapPanel extends JPanel {
        private Renderer renderer;

        public GameMapPanel() {
            setBackground(new Color(135, 179, 207));
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
        minimapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                minimapPanel.getMinimap().jumpCameraTo(e.getX(), e.getY());
                mapRefresh();
            }
        });
        minimapPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                minimapPanel.getMinimap().jumpCameraTo(e.getX(), e.getY());
                mapRefresh();
            }
        });

        upperPanel.add(minimapPanel, BorderLayout.WEST);

        JPanel buildPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buildPanel.setBorder(BorderFactory.createTitledBorder("Build Options"));

        roadToggle = new JButton("Ut ikon");
        buildPanel.add(roadToggle);

        stationToggle = new JButton("Megálló ikon");
        buildPanel.add(stationToggle);

        demolishToggle = new JButton("Bomba ikon");
        buildPanel.add(demolishToggle);

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

        saveBtn = new JButton("Mentés");
        loadBtn = new JButton("Betöltés");

        lowerPanel.add(saveBtn);
        lowerPanel.add(loadBtn);

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

        JDialog dialog = new JDialog(gameWindow, "Épület információk", true);
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
            Building b = tile.getBuilding();
            infoText += "<b>Típus:</b> " + b.getBuildingType().name() + "<br><br>";

            if (b instanceof Farm) {
                Farm farm = (Farm) b;
                infoText += "<b>Tárolt gabona:</b> " + farm.getGrainMade() + " / " + farm.getCapacity() + "<br>";
                infoText += createProgressBar(farm.getGrainMade(), farm.getCapacity());
            }
            else if (b instanceof Silo) {
                Silo silo = (Silo) b;
                int maxCap = silo.getNumOfFood() + silo.getRemainingCapacity();
                infoText += "<b>Tárolt étel:</b> " + silo.getNumOfFood() + " / " + maxCap + "<br>";
                infoText += createProgressBar(silo.getNumOfFood(), maxCap);
            }
            else if (b instanceof AgriculturalPlant) {
                AgriculturalPlant plant = (AgriculturalPlant) b;
                infoText += "<b>Bemenet (Gabona):</b> " + plant.getIncomingGrain() + " / " + plant.getCapacityIn() + "<br>";
                infoText += createProgressBar(plant.getIncomingGrain(), plant.getCapacityIn()) + "<br><br>";
                infoText += "<b>Kimenet (Étel):</b> " + plant.getOutgoingFood() + " / " + plant.getCapacityOut() + "<br>";
                infoText += createProgressBar(plant.getOutgoingFood(), plant.getCapacityOut());
            }
            else if (b instanceof Enclosure) {
                Enclosure enc = (Enclosure) b;
                infoText += "<b>Faj:</b> " + (enc.getSpecies() != null ? enc.getSpecies().name() : "Üres") + "<br>";
                infoText += "<b>Állatok száma:</b> " + enc.getNumOfAnimals() + " / 200<br>";
                infoText += createProgressBar(enc.getNumOfAnimals(), 200);
            }
            else if (b instanceof City) {
                City city = (City) b;
                infoText += "<b>Rendelés alatt:</b> " + (city.hasOrder() ? city.getOrderedAmount() + " db " + city.getOrderedAnimal().name() : "Nincs") + "<br>";
            }
            else if (b instanceof CloningFacility) {
                CloningFacility cf = (CloningFacility) b;
                String animalName = cf.hasAnimal() ? cf.getAnimalType().name() : "Nincs";

                infoText += "<b>Klónozott faj:</b> " + animalName + "<br>";
                infoText += "<b>Tárolt klónok:</b> " + cf.getAnimalsMade() + " / " + cf.getCapacity() + "<br>";
                infoText += createProgressBar(cf.getAnimalsMade(), cf.getCapacity()) + "<br><br>";

                if (cf.isCloning()) {
                    infoText += "<b>Folyamat:</b> " + cf.getDaysSinceStarted() + " / " + cf.getDaysToClone() + " nap<br>";
                    infoText += createProgressBar(cf.getDaysSinceStarted(), cf.getDaysToClone());
                } else {
                    if(cf.getAnimalsMade() >= cf.getCapacity()) {
                        infoText += "<b>Állapot:</b> <font color='red'>A tároló megtelt!</font><br>";
                    } else if (cf.hasAnimal()) {
                        infoText += "<b>Állapot:</b> Klónozásra kész.<br>";
                    } else {
                        infoText += "<b>Állapot:</b> Várakozás DNS mintára<br>";
                    }
                }
            }
            else if (b instanceof ResearchLab) {
                ResearchLab rl = (ResearchLab) b;
                String anim1 = rl.getReceivedAnimal1() != null ? rl.getReceivedAnimal1().name() : "Üres";
                String anim2 = rl.getReceivedAnimal2() != null ? rl.getReceivedAnimal2().name() : "Üres";
                String discovered = rl.getDiscoveredAnimal() != null ? rl.getDiscoveredAnimal().name() : "Nincs";

                infoText += "<b>Alany 1:</b> " + anim1 + "<br>";
                infoText += "<b>Alany 2:</b> " + anim2 + "<br><br>";

                if (rl.isResearchHappening()) {
                    infoText += "<b>Kutatás folyamatban:</b> " + rl.getDaysSinceResearchStarted() + " / " + rl.getResearchDays() + " nap<br>";
                    infoText += createProgressBar(rl.getDaysSinceResearchStarted(), rl.getResearchDays()) + "<br><br>";
                } else {
                    infoText += "<b>Kutatás állapota:</b> Inaktív<br><br>";
                }

                if (rl.getDiscoveredAnimal() != null) {
                    infoText += "<b>Eredmény:</b> <font color='green'>Felfedezve: " + discovered + "</font><br>";
                } else {
                    infoText += "<b>Eredmény:</b> Még nincs felfedezés<br>";
                }
            }

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
                + "<b>Sebesség:</b> " + v.getSpeed() + "<br>"
                + "<b>Kapacitás:</b> " + v.getCapacity() + "<br><br>";

        if (v instanceof FoodTruck) {
            FoodTruck ft = (FoodTruck) v;
            infoText += "<b>Rakomány:</b> " + (ft.getCargoType() != null ? ft.getCargoType() : "Üres") + "<br>";
            infoText += "<b>Mennyiség:</b> " + ft.getCurrentCargoNum() + " / " + ft.getCapacity() + "<br>";
            infoText += createProgressBar(ft.getCurrentCargoNum(), ft.getCapacity());
        }
        else if (v instanceof AnimalTruck) {
            infoText += "<b>Szállított állat:</b> " + (v.getCargoType() != null ? v.getCargoType() : "Üres") + "<br>";
            infoText += "<b>Férőhely:</b> " + (v.isEmpty() ? "0" : "1") + " / 1<br>";
            infoText += createProgressBar(v.isEmpty() ? 0 : 1, 1);
        }
        else if (v instanceof Bus) {
            infoText += "<b>Utasok:</b> " + (v.isEmpty() ? "Üres" : (v.isFull() ? "Tele" : "Van rajta utas")) + "<br>";
        }
        infoText += "</html>";

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

    private String createProgressBar(int current, int max) {
        if (max <= 0) return "[Hiba: Max 0]";
        int bars = 20;
        int filled = (int) (((double) current / max) * bars);

        StringBuilder sb = new StringBuilder("<span style='font-family: monospace; letter-spacing: 0px;'>[");
        for (int i = 0; i < bars; i++) {
            if (i < filled) sb.append("<font color='green'>|</font>");
            else sb.append("<font color='gray'>.</font>");
        }
        sb.append("]</span>");
        return sb.toString();
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

    public JButton getDemolishToggle() { return demolishToggle; }

    public JButton getSpeedPaused() { return speedPaused; }

    public JButton getSpeedNormal() { return speedNormal; }

    public JButton getSpeedFast() { return speedFast; }

    public JButton getStationToggle() { return stationToggle; }

    public JButton getSpeedSuperFast() { return speedSuperFast; }

    public JButton getSaveBtn() { return saveBtn; }

    public JButton getLoadBtn() { return loadBtn; }

    public void show() {
        gameWindow.setVisible(true);
    }


}
