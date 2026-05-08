package UI;

import engine.rendering.Minimap;
import engine.rendering.Renderer;
import world.resources.AnimalType;
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
import java.awt.image.BufferedImage;

public class ingameGUI {

    private JFrame gameWindow;
    private GameMapPanel mapPanel;
    private MinimapPanel minimapPanel;
    private JLabel dayCounter;
    private JLabel balanceLabel;
    private JButton roadToggle;
    private JButton stationToggle;
    private JButton demolishToggle;
    private JButton bridgeToggle;
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

        bridgeToggle = new JButton("Hid ikon");
        buildPanel.add(bridgeToggle);

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
                dialog.setSize(650, 300);
                dialog.add(farmWindow((Farm) b, dialog));
                dialog.setVisible(true);
                return tempBuildingAction;
            }
            else if (b instanceof Silo) {
                Silo silo = (Silo) b;
                int maxCap = silo.getNumOfFood() + silo.getRemainingCapacity();
                infoText += "<b>Tárolt étel:</b> " + silo.getNumOfFood() + " / " + maxCap + "<br>";
                infoText += createProgressBar(silo.getNumOfFood(), maxCap);
            }
            else if (b instanceof AgriculturalPlant) {
                dialog.setSize(750, 300);
                dialog.add(agriPlantWindow((AgriculturalPlant) b, dialog));
                dialog.setVisible(true);
                return tempBuildingAction;
            }
            else if (b instanceof Enclosure) {
                dialog.setSize(650, 300);
                dialog.add(enclosureWindow((Enclosure) b, dialog));
                dialog.setVisible(true);
                return tempBuildingAction;
            }
            else if (b instanceof City) {
                City city = (City) b;
                infoText += "<b>Rendelés alatt:</b> " + (city.hasOrder() ? city.getOrderedAmount() + " db " + city.getOrderedAnimal().name() : "Nincs") + "<br>";
            }
            else if (b instanceof CloningFacility) {
                dialog.setSize(650, 300);
                dialog.add(cloningFacilityWindow((CloningFacility) b, dialog));
                dialog.setVisible(true);
                return tempBuildingAction;
            }
            else if (b instanceof ResearchLab) {
                dialog.setSize(800, 350);
                dialog.add(researchLabWindow((ResearchLab) b, dialog));
                dialog.setVisible(true);
                return tempBuildingAction;
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

    private JPanel researchLabWindow(ResearchLab rl, JDialog dialog)
    {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Epulet neve es kepe
        mainPanel.add(buildingRightPanel("researchlab", "Kutatólabor"), BorderLayout.EAST);

        // Allatok
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        JPanel animalsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        String ani1Ass = (rl.getReceivedAnimal1() != null) ? rl.getReceivedAnimal1().name().toLowerCase() : null;
        String ani1Name = (rl.getReceivedAnimal1() != null) ? rl.getReceivedAnimal1().name().toLowerCase() : "Alany 1";
        animalsPanel.add(resourceBox(ani1Ass, ani1Name, ""));

        JLabel plusLabel = new JLabel("+");
        plusLabel.setFont(new Font("Arial", Font.BOLD, 24));
        animalsPanel.add(plusLabel);

        String ani2Ass = (rl.getReceivedAnimal2() != null) ? rl.getReceivedAnimal2().name().toLowerCase() : null;
        String ani2Name = (rl.getReceivedAnimal2() != null) ? rl.getReceivedAnimal2().name().toLowerCase() : "Alany 1";
        animalsPanel.add(resourceBox(ani2Ass, ani2Name, ""));

        JLabel equalsLabel = new JLabel("=");
        equalsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        animalsPanel.add(equalsLabel);

        String discAss = (rl.getDiscoveredAnimal() != null) ? rl.getDiscoveredAnimal().name().toLowerCase() : null;
        String discName = (rl.getDiscoveredAnimal() != null) ? rl.getDiscoveredAnimal().name() : "Új állat";
        animalsPanel.add(resourceBox(discAss, discName, ""));

        leftPanel.add(animalsPanel, BorderLayout.NORTH);

        // Tudnivalok
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        if (rl.isResearchHappening()) {
            JLabel progressText = new JLabel("Kutatás folyamatban: " + rl.getDaysSinceResearchStarted() + " / " + rl.getResearchDays() + " nap", SwingConstants.CENTER);
            progressText.setFont(new Font("Arial", Font.BOLD, 14));
            JLabel progressBar = new JLabel("<html>" + createProgressBar(rl.getDaysSinceResearchStarted(), rl.getResearchDays()) + "</html>", SwingConstants.CENTER);

            infoPanel.add(progressText);
            infoPanel.add(progressBar);
        } else {
            JLabel costTimeLabel = new JLabel("Kutatás ára: $" + rl.getCostOfResearch() + "  |  Kutatás ideje: " + rl.getResearchDays() + " nap", SwingConstants.CENTER);
            costTimeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            infoPanel.add(costTimeLabel);
        }
        leftPanel.add(infoPanel, BorderLayout.CENTER);

        // Gombok
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton start = new JButton("Kutatás kezdése (-$" + rl.getCostOfResearch() + ")");
        boolean canStart = (rl.getReceivedAnimal1() != null && rl.getReceivedAnimal2() != null && rl.getDiscoveredAnimal() == null && !rl.isResearchHappening());
        start.setEnabled(canStart);
        start.addActionListener(e -> {
            tempBuildingAction = BuildingAction.START_RESEARCH;
            dialog.dispose();
        });

        JButton transport = new JButton("Felfedezett állat elszállítása");
        boolean canTransport = (rl.getDiscoveredAnimal() != null);
        transport.setEnabled(canTransport);
        transport.addActionListener(e -> {
            tempBuildingAction = BuildingAction.TRANSPORT_ANIMAL;
            dialog.dispose();
        });

        buttonPanel.add(start);
        buttonPanel.add(transport);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(leftPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel enclosureWindow(Enclosure enc, JDialog dialog)
    {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(buildingRightPanel(enc.getSpriteName(), "Karám"), BorderLayout.EAST);

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(15, 0));

        String aniAss = (enc.hasAnimals()) ? enc.getSpecies().name().toLowerCase() : null;
        String aniName = (enc.hasAnimals()) ? enc.getSpecies().name() : "Üres";
        String cap = enc.getNumOfAnimals() + " / 200";

        topPanel.add(resourceBox(aniAss, aniName, cap), BorderLayout.WEST);

        String info;
        if(enc.isStarving()) {
            info = "<html><center><font color='red'><b>Az állatok éheznek!</b></font></center></html>";
        } else if(!enc.hasAnimals()) {
            info = "<html><center>A karám üres,<br>nincs fogyasztás.</center></html>";
        } else {
            int daysLeft = enc.getDaysUntilStarvation();
            info = "<html><center>Az állatok meg vannak etetve.<br><br><b>Hátralévő napok éhezésig: " + daysLeft + "</b></center></html>";
        }
        topPanel.add(infoPanel(info), BorderLayout.CENTER);

        leftPanel.add(topPanel, BorderLayout.CENTER);

        JButton transport = new JButton("Állat elszállítása");
        transport.setEnabled(enc.hasAnimals());
        transport.addActionListener(e -> {
            tempBuildingAction = BuildingAction.TRANSPORT_ANIMAL;
            dialog.dispose();
        });

        int sellValue = enc.hasAnimals() ? enc.getSpecies().getValue() : 0;
        JButton sell = new JButton("Állat eladása (+$" + sellValue + ")");
        sell.setEnabled(enc.hasAnimals());
        sell.addActionListener(e -> {
            tempBuildingAction = BuildingAction.SELL_ANIMAL;
            dialog.dispose();
        });

        leftPanel.add(buttonRow(transport, sell), BorderLayout.SOUTH);

        mainPanel.add(leftPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel cloningFacilityWindow(CloningFacility cf, JDialog dialog)
    {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(buildingRightPanel("cloningfacility", "Klónozó labor"), BorderLayout.EAST);

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        String aniAss = (cf.hasAnimal()) ? cf.getAnimalType().name().toLowerCase() : null;
        String aniName = (cf.hasAnimal()) ? cf.getAnimalType().name() : "Üres";
        String cap = cf.getAnimalsMade() + " / " + cf.getCapacity();

        topPanel.add(resourceBox(aniAss, aniName, cap), BorderLayout.WEST);

        String info;
        if (cf.isCloning()) {
            int daysLeft = cf.getDaysToClone() - cf.getDaysSinceStarted();
            info = "<html><center><b>Klónozás hátralévő ideje:</b> " + daysLeft + " nap</center></html>";
        } else {
            info = "<html><center><b>Klónozás ára:</b> $" + cf.getCostOfCloning() + "<br><br><b>Klónozás ideje:</b> " + cf.getDaysToClone() + " nap</center></html>";
        }
        topPanel.add(infoPanel(info), BorderLayout.CENTER);

        leftPanel.add(topPanel, BorderLayout.CENTER);

        JButton start = new JButton("Klónozás megkezdése (-$" + cf.getCostOfCloning() + ")");
        boolean canStart = cf.hasAnimal() && !cf.isCloning() && (cf.getAnimalsMade() < cf.getCapacity());
        start.setEnabled(canStart);
        start.addActionListener(e -> {
            tempBuildingAction = BuildingAction.START_CLONING;
            dialog.dispose();
        });

        JButton transport = new JButton("Állat elszállítása");
        transport.setEnabled(cf.getAnimalsMade() > 0);
        transport.addActionListener(e -> {
            tempBuildingAction = BuildingAction.TRANSPORT_ANIMAL;
            dialog.dispose();
        });

        leftPanel.add(buttonRow(start, transport), BorderLayout.SOUTH);

        mainPanel.add(leftPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel farmWindow(Farm farm, JDialog dialog)
    {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(buildingRightPanel("farm", "Farm"), BorderLayout.EAST);

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(15, 0));

        String capacityText = farm.getGrainMade() + " / " + farm.getCapacity();
        topPanel.add(resourceBox("grain", "Gabona", capacityText), BorderLayout.WEST);

        String info;
        if (farm.isBoosted()) {
            info = "<html><center><b>Termelés növelve!</b><br><br>Bónusz: +" + farm.getBoostAmount() + " db / nap<br>Hátralévő idő: " + farm.getDaysLeftOfBoost() + " nap</center></html>";
        } else {
            info = "<html><center><b>Normál termelés</b><br><br>Fejlesztés után:<br>+" + farm.getBoostAmount() + " db / nap</center></html>";
        }
        topPanel.add(infoPanel(info), BorderLayout.CENTER);

        leftPanel.add(topPanel, BorderLayout.CENTER);

        JButton boost = new JButton("Termelés növelése (-$" + farm.getBoostCost() + ")");
        boost.setEnabled(!farm.isBoosted());
        boost.addActionListener(e -> {
            tempBuildingAction = BuildingAction.BOOST_PRODUCTION;
            dialog.dispose();
        });

        JButton transport = new JButton("Gabona elszállítása");
        transport.setEnabled(farm.getGrainMade() > 0);
        transport.addActionListener(e -> {
            tempBuildingAction = BuildingAction.TRANSPORT_RESOURCE;
            dialog.dispose();
        });

        leftPanel.add(buttonRow(boost, transport), BorderLayout.SOUTH);

        mainPanel.add(leftPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel agriPlantWindow(AgriculturalPlant plant, JDialog dialog)
    {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(buildingRightPanel("agriculturalplant", "Agrárüzem"), BorderLayout.EAST);

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));

        JPanel middleContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        String grainCap = plant.getIncomingGrain() + " / " + plant.getCapacityIn();
        middleContainer.add(resourceBox("grain", "Gabona", grainCap));

        String info;
        if (plant.isBoosted()) {
            int total = plant.getBatchAmount() + plant.getBoostAmount();
            info = "<html><center><b>Termelés növelve!</b><br><br>Feldolgozás:<br>" + total + " db / NAP<br>Hátralévő: " + plant.getDaysLeftOfBoost() + " NAP</center></html>";
        } else {
            info = "<html><center><b>Feldolgozás:</b><br>" + plant.getBatchAmount() + " db / NAP<br><br>Fejlesztéssel:<br>+" + plant.getBoostAmount() + " / NAP</center></html>";
        }
        middleContainer.add(infoPanel(info));

        String foodCap = plant.getOutgoingFood() + " / " + plant.getCapacityOut();
        middleContainer.add(resourceBox("food", "Étel", foodCap));

        leftPanel.add(middleContainer, BorderLayout.CENTER);

        JButton boost = new JButton("Termelés növelése (-$" + plant.getBoostCost() + ")");
        boost.setEnabled(!plant.isBoosted());
        boost.addActionListener(e -> {
            tempBuildingAction = BuildingAction.BOOST_PRODUCTION;
            dialog.dispose();
        });

        JButton transport = new JButton("Étel elszállítása");
        transport.setEnabled(plant.getOutgoingFood() > 0);
        transport.addActionListener(e -> {
            tempBuildingAction = BuildingAction.TRANSPORT_RESOURCE;
            dialog.dispose();
        });

        leftPanel.add(buttonRow(boost, transport), BorderLayout.SOUTH);

        mainPanel.add(leftPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel animalBox(AnimalType animal, String placeholder)
    {
        JPanel box = new JPanel(new BorderLayout());
        box.setPreferredSize(new Dimension(110, 130));
        box.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));

        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        imgLabel.setOpaque(true);
        imgLabel.setBackground(Color.WHITE);

        if(animal != null)
        {
            BufferedImage img = engine.AssetManager.get(animal.name().toLowerCase());

            if(img != null)
            {
                Image scaledImg = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(scaledImg));
            } else {
                imgLabel.setText(animal.name());
            }
        } else {
            imgLabel.setText("Üres");
        }

        box.add(imgLabel, BorderLayout.CENTER);

        String nameText = (animal != null) ? animal.name() : placeholder;
        JLabel nameLabel = new JLabel(nameText, SwingConstants.CENTER);
        nameLabel.setPreferredSize(new Dimension(110, 30));
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        box.add(nameLabel, BorderLayout.SOUTH);

        return box;
    }

    private JPanel buildingRightPanel(String asset, String buildingName)
    {
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setPreferredSize(new Dimension(200, 0));

        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        imgLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));

        BufferedImage img = engine.AssetManager.get(asset);

        if(img != null)
        {
            Image scaledImg = img.getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaledImg));
        } else {
            imgLabel.setText("Sikertelen betöltés");
        }

        rightPanel.add(imgLabel, BorderLayout.CENTER);

        JLabel nameLabel = new JLabel(buildingName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        rightPanel.add(nameLabel, BorderLayout.SOUTH);

        return rightPanel;
    }

    private JPanel resourceBox(String asset, String title, String capacity)
    {
        JPanel box = new JPanel(new BorderLayout());
        box.setPreferredSize(new Dimension(130, 160));
        box.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));

        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        imgLabel.setOpaque(true);
        imgLabel.setBackground(Color.WHITE);

        if(asset != null && !asset.isEmpty())
        {
            BufferedImage img = engine.AssetManager.get(asset);
            if(img != null)
            {
                Image scaledImg = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(scaledImg));
            } else {
                imgLabel.setText(title);
            }
        } else {
            imgLabel.setText("Üres");
        }

        box.add(imgLabel, BorderLayout.CENTER);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 0));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel capLabel = new JLabel(capacity, SwingConstants.CENTER);
        capLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        textPanel.add(titleLabel);
        textPanel.add(capLabel);

        box.add(textPanel, BorderLayout.SOUTH);

        return box;
    }

    private JPanel infoPanel(String content)
    {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
        infoPanel.setBackground(Color.WHITE);

        JLabel textLabel = new JLabel(content, SwingConstants.CENTER);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        textLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        infoPanel.add(textLabel, BorderLayout.CENTER);

        return infoPanel;
    }

    private JPanel buttonRow(JButton... buttons)
    {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        for (JButton btn : buttons) {
            btn.setFont(new Font("Arial", Font.BOLD, 13));
            btn.setFocusPainted(false);
            buttonPanel.add(btn);
        }

        return buttonPanel;
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

    public JButton getBridgeToggle() { return bridgeToggle; }

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
