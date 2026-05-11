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
    private JButton stoneBridgeToggle;
    private JButton woodenBridgeToggle;
    private JButton glassBridgeToggle;
    private JButton speedPaused;
    private JButton saveBtn;
    private JButton loadBtn;
    private JButton speedNormal;
    private JButton speedFast;
    private JButton speedSuperFast;

    private VehicleType tempSelectedType = null;
    private VehicleAction tempVehicleAction = VehicleAction.NONE;
    private AnimalType tempSelectedAnimal = null;

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
        Color panelBg = new Color(238, 238, 238);

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
        buildPanel.setBorder(BorderFactory.createTitledBorder("Építési lehetőségek"));

        roadToggle = iconButton("build-road", "Út építése");
        buildPanel.add(roadToggle);

        stationToggle = iconButton("build-station", "Megálló építése");
        buildPanel.add(stationToggle);

        woodenBridgeToggle = iconButton("build-wooden-bridge", "Híd építése");
        buildPanel.add(woodenBridgeToggle);

        stoneBridgeToggle = iconButton("build-stone-bridge", "Híd építése");
        buildPanel.add(stoneBridgeToggle);

        glassBridgeToggle = iconButton("build-glass-bridge", "Híd építése");
        buildPanel.add(glassBridgeToggle);

        demolishToggle = iconButton("build-bomb", "Rombolás");
        buildPanel.add(demolishToggle);

        upperPanel.add(buildPanel, BorderLayout.CENTER);

        JPanel napContainer = new JPanel(new GridBagLayout());
        napContainer.setOpaque(false);

        JPanel napPanel = new JPanel(new BorderLayout());
        Dimension napMeret = new Dimension(110,110);
        napPanel.setPreferredSize(napMeret);
        napPanel.setMinimumSize(napMeret);
        napPanel.setMaximumSize(napMeret);

        dayCounter = new JLabel("<html><center><b style='font-size:22px'>1</b></center></html>", SwingConstants.CENTER);
        dayCounter.setFont(new Font("Monospaced", Font.PLAIN, 14));
        napPanel.add(dayCounter, BorderLayout.CENTER);
        napContainer.add(napPanel);
        upperPanel.add(napPanel, BorderLayout.EAST);

        Image napKep = engine.AssetManager.get("sun").getScaledInstance(110,110,Image.SCALE_SMOOTH);
        dayCounter.setIcon(new ImageIcon(napKep));
        dayCounter.setHorizontalTextPosition(JLabel.CENTER);
        dayCounter.setVerticalTextPosition(JLabel.CENTER);

        upperPanel.setBackground(panelBg);
        gameWindow.add(upperPanel, BorderLayout.NORTH);

        mapPanel = new GameMapPanel();
        mapPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));

        JPanel mapContainer = new JPanel(new BorderLayout());
        mapContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mapContainer.add(mapPanel, BorderLayout.CENTER);

        gameWindow.add(mapContainer, BorderLayout.CENTER);

        JPanel alsoPanel = new JPanel(new BorderLayout(10, 0));
        alsoPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        balanceLabel = new JLabel("<html><span style='font-family: SansSerif; font-size:24px'>$13000</span><sup style='font-family: SansSerif; font-size:14px; color:#E74C3C;'>-0</sup></html>");
        alsoPanel.add(balanceLabel, BorderLayout.WEST);

        JPanel idoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        Color timeBg = new Color(92, 92, 92);
        Color timeHover = new Color(115, 115, 115);
        Font timeFont = new Font("Monospaced", Font.BOLD, 18);

        speedPaused = new JButton("⏸");
        styleButtons(speedPaused, timeBg, timeHover, Color.WHITE, timeFont);
        idoPanel.add(speedPaused);
        speedNormal = new JButton("▶");
        styleButtons(speedNormal, timeBg, timeHover, Color.WHITE, timeFont);
        idoPanel.add(speedNormal);
        speedFast = new JButton("⏩");
        styleButtons(speedFast, timeBg, timeHover, Color.WHITE, timeFont);
        idoPanel.add(speedFast);
        speedSuperFast = new JButton("⏭");
        styleButtons(speedSuperFast, timeBg, timeHover, Color.WHITE, timeFont);
        idoPanel.add(speedSuperFast);
        alsoPanel.add(idoPanel, BorderLayout.CENTER);

        JPanel lowerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        Font actionFont = new Font("Monospaced", Font.BOLD, 14);
        Color actionBg = new Color(143, 168, 118);
        Color actionHover = new Color(165, 188, 142);

        saveBtn = new JButton("Mentés");
        loadBtn = new JButton("Betöltés");

        styleButtons(saveBtn, actionBg, actionHover, Color.WHITE, actionFont);
        styleButtons(loadBtn, actionBg, actionHover, Color.WHITE, actionFont);

        lowerPanel.add(saveBtn);
        lowerPanel.add(loadBtn);

        Color exitBg = new Color(180, 100, 100);
        Color exitHover = new Color(200, 120, 120);

        JButton exitGomb = new JButton("Kilépés");
        styleButtons(exitGomb, exitBg, exitHover, Color.WHITE, actionFont);
        exitGomb.addActionListener(e -> System.exit(0));
        lowerPanel.add(exitGomb);

        Color helpBg = new Color(190, 165, 100);
        Color helpHover = new Color(210, 185, 120);
        JButton helpGomb = new JButton("?");

        styleButtons(helpGomb, helpBg, helpHover, Color.WHITE, actionFont);
        lowerPanel.add(helpGomb);
        alsoPanel.add(lowerPanel, BorderLayout.EAST);

        alsoPanel.setBackground(panelBg);
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

    private JButton iconButton(String asset, String tooltip)
    {
        JButton butt = new JButton();
        butt.setToolTipText(tooltip);
        butt.setPreferredSize(new Dimension(50,50));
        butt.setFocusPainted(false);
        butt.setContentAreaFilled(false);
        butt.setOpaque(false);
        butt.setBorderPainted(false);

        Image scaled = engine.AssetManager.get(asset).getScaledInstance(40,40,Image.SCALE_SMOOTH);
        butt.setIcon(new ImageIcon(scaled));

        return butt;
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
                dialog.setSize(600, 250);
                dialog.add(siloWindow((Silo) b, dialog));
                dialog.setVisible(true);
                return tempBuildingAction;
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
                dialog.setSize(600, 250);
                dialog.add(cityWindow((City) b, dialog));
                dialog.setVisible(true);
                return tempBuildingAction;
            }
            else if (b instanceof CloningFacility) {
                dialog.setSize(650, 300);
                dialog.add(cloningFacilityWindow((CloningFacility) b, dialog));
                dialog.setVisible(true);
                return tempBuildingAction;
            }
            else if(b instanceof Station)
            {
                dialog.setSize(550, 250);
                dialog.add(stationWindow((Station) b, dialog));
                dialog.setVisible(true);
                return tempBuildingAction;
            }
            else if(b instanceof BusStop)
            {
                dialog.setSize(550,250);
                dialog.add(busStopWindow((BusStop) b, dialog));
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

        rightPanel.add(actionPanel, BorderLayout.SOUTH);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        dialog.add(mainPanel);
        dialog.setVisible(true);

        return tempBuildingAction;
    }

    public VehicleAction showVehicleInfo(Vehicle v)
    {
        tempVehicleAction = VehicleAction.NONE;

        JDialog dialog = new JDialog(gameWindow, "Jármű információk", true);
        dialog.setSize(650, 250);
        dialog.setLocationRelativeTo(gameWindow);
        dialog.add(vehicleWindow(v, dialog));

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

    private JPanel cityWindow(City city, JDialog dialog)
    {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(buildingRightPanel(city.getSpriteName(), "Város"), BorderLayout.EAST);
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));

        String aniAss = city.hasOrder() ? city.getOrderedAnimal().name().toLowerCase() : null;
        String aniName = city.hasOrder() ? city.getOrderedAnimal().name() : "Nincs rendelés";
        String amountText = city.hasOrder() ? city.getRemainingAmount() + " db" : "-";

        topPanel.add(resourceBox(aniAss, aniName, amountText), BorderLayout.WEST);

        String info;
        if (city.hasOrder()) {
            info = "<html><center><b>Aktív rendelés!</b><br><br>Jutalom a teljesítésért:<br><font color='green'><b>$" + city.getOrderWorth() + "</b></font></center></html>";
        } else {
            info = "<html><center>Jelenleg <b>nincs</b> aktív rendelés.<br><br>A projekt vezetői hamarosan<br>új igényt adnak le.</center></html>";
        }

        topPanel.add(infoPanel(info), BorderLayout.CENTER);
        leftPanel.add(topPanel, BorderLayout.CENTER);

        JButton close = new JButton("Bezárás");
        close.addActionListener(e -> dialog.dispose());
        leftPanel.add(buttonRow(close), BorderLayout.SOUTH);
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

        if (!enc.hasAnimals()) {
            JButton buy = new JButton("Állat vásárlása");
            buy.addActionListener(e -> {
                tempBuildingAction = BuildingAction.BUY_ANIMAL;
                dialog.dispose();
            });
            leftPanel.add(buttonRow(buy), BorderLayout.SOUTH);
        } else {
            JButton transport = new JButton("Állat elszállítása");
            transport.addActionListener(e -> {
                tempBuildingAction = BuildingAction.TRANSPORT_ANIMAL;
                dialog.dispose();
            });

            int sellValue = enc.getSpecies().getValue();
            JButton sell = new JButton("Állat eladása (+$" + sellValue + ")");
            sell.addActionListener(e -> {
                tempBuildingAction = BuildingAction.SELL_ANIMAL;
                dialog.dispose();
            });

            leftPanel.add(buttonRow(transport, sell), BorderLayout.SOUTH);
        }

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

    private JPanel siloWindow(Silo silo, JDialog dialog)
    {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(buildingRightPanel(silo.getSpriteName(), "Siló"), BorderLayout.EAST);
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));

        int maxCap = silo.getNumOfFood() + silo.getRemainingCapacity();
        String capText = silo.getNumOfFood() + " / " + maxCap;
        topPanel.add(resourceBox("food", "Étel", capText), BorderLayout.WEST);

        int consumption = silo.getFoodConsumptionPerDay();
        int daysLeft = silo.getDaysUntilStarvation();

        String daysLeftText = (daysLeft > 0) ? daysLeft + " napra elegendő." : "Nincs elegendő étel!";
        if (consumption == 0) {
            daysLeftText = "Nincs aktív fogyasztás.";
        }

        String info = "<html><center><b>Fogyasztás:</b><br>" + consumption + " / NAP<br><br>" + daysLeftText + "</center></html>";
        topPanel.add(infoPanel(info), BorderLayout.CENTER);

        leftPanel.add(topPanel, BorderLayout.CENTER);

        JButton upgrade = new JButton("Kapacitás növelése (-$" + silo.getUpgradeCost() + ")");
        upgrade.setEnabled(silo.canIncreaseCapacity());
        upgrade.addActionListener(e -> {
            tempBuildingAction = BuildingAction.UPGRADE_SILO;
            dialog.dispose();
        });

        leftPanel.add(buttonRow(upgrade), BorderLayout.SOUTH);
        mainPanel.add(leftPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel vehicleWindow(Vehicle v, JDialog dialog)
    {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String vName = "Jármű";
        if (v instanceof FoodTruck) vName = "Étel Szállító";
        else if (v instanceof AnimalTruck) vName = "Állat Szállító";
        else if (v instanceof Bus) vName = "Busz";

        mainPanel.add(buildingRightPanel(v.getSpriteName(), vName), BorderLayout.EAST);

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));

        String resAss = null;
        String resName = "Üres";
        String capText = v.getCurrentCargoNum() + " / " + v.getCapacity();

        if (v instanceof FoodTruck) {
            if (v.getCargoType() != null) {
                resAss = ((world.resources.ResourceType) v.getCargoType()).name().toLowerCase();
                resName = ((world.resources.ResourceType) v.getCargoType()).name();
                if (resName.equals("GRAIN")) resName = "Gabona";
                if (resName.equals("FOOD")) resName = "Étel";
            }
        } else if (v instanceof AnimalTruck) {
            capText = (v.isEmpty() ? "0" : "1") + " / 1";
            if (v.getCargoType() != null) {
                resAss = ((world.resources.AnimalType) v.getCargoType()).name().toLowerCase();
                resName = ((world.resources.AnimalType) v.getCargoType()).name();
            }
        } else if (v instanceof Bus) {
            if (!v.isEmpty()) {
                resAss = "nincslol";
                resName = "Utasok";
            }
        }
        topPanel.add(resourceBox(resAss, resName, capText), BorderLayout.WEST);
        String info = "<html><center><b>Sebesség:</b> " + v.getSpeed() + " km/h<br><br>";
        info += "Fenntartás: <font color='red'>-$" + v.getCostToOperate() + "</font> / hó<br>";
        info += "Eladási ár: <font color='green'>+$" + v.getCostToSell() + "</font></center></html>";

        topPanel.add(infoPanel(info), BorderLayout.CENTER);
        leftPanel.add(topPanel, BorderLayout.CENTER);

        JButton route = new JButton("Útvonal kijelölése");
        route.addActionListener(e -> {
            tempVehicleAction = VehicleAction.ASSIGN_ROUTE;
            dialog.dispose();
        });

        JButton sell = new JButton("Eladás (+$" + v.getCostToSell() + ")");
        sell.addActionListener(e -> {
            tempVehicleAction = VehicleAction.SELL;
            dialog.dispose();
        });
        leftPanel.add(buttonRow(route, sell), BorderLayout.SOUTH);
        mainPanel.add(leftPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel stationWindow(Station station, JDialog dialog)
    {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(buildingRightPanel(station.getSpriteName(), "Ipari Megálló"), BorderLayout.EAST);

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));

        String info = "<html><center><b>Tájolás:</b> " + station.getDirection().name() + "<br><br>";

        if (station.isOccupied()) {
            info += "<font color='blue'><b>Állapot:</b> Jármű bent áll</font><br>";
            info += "(" + station.getVehicle().getVehicleType().name() + ")";
        } else {
            info += "<font color='green'><b>Állapot:</b> Üres</font>";
        }
        info += "</center></html>";

        topPanel.add(infoPanel(info), BorderLayout.CENTER);
        leftPanel.add(topPanel, BorderLayout.CENTER);

        JButton buyVehicle = new JButton("Jármű vásárlása");
        buyVehicle.addActionListener(e -> {
            tempBuildingAction = BuildingAction.BUY_VEHICLE;
            dialog.dispose();
        });

        leftPanel.add(buttonRow(buyVehicle), BorderLayout.SOUTH);
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

    private JPanel busStopWindow(BusStop bs, JDialog dialog)
    {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(buildingRightPanel(bs.getSpriteName(), "Buszmegálló"), BorderLayout.EAST);

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));

        String info = "<html><center><b>Tájolás:</b> " + bs.getDirection().name() + "<br><br>";

        if (bs.isStart()) {
            info += "<font color='blue'><b>Állapot:</b> Indulási hely</font><br>";
            info += "Várakozó utasok: <b>" + bs.getNumOfPeople() + " fő</b>";
        } else if (bs.isStop()) {
            info += "<font color='green'><b>Állapot:</b> Célállomás</font><br>";
            info += "Ide várják a járatot.";
        } else {
            info += "<b>Állapot:</b> Üres<br>(Nincs aktív utazási igény)";
        }
        info += "</center></html>";

        topPanel.add(infoPanel(info), BorderLayout.CENTER);
        leftPanel.add(topPanel, BorderLayout.CENTER);

        JButton close = new JButton("Bezárás");
        close.addActionListener(e -> dialog.dispose());

        leftPanel.add(buttonRow(close), BorderLayout.SOUTH);
        mainPanel.add(leftPanel, BorderLayout.CENTER);

        return mainPanel;
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

    private void styleButtons(JButton butt, Color bgColor, Color hoverColor, Color fgColor, Font font)
    {
        butt.setBackground(bgColor);
        butt.setForeground(fgColor);
        butt.setFont(font);
        butt.setFocusPainted(false);
        butt.setBorderPainted(false);
        butt.setOpaque(true);
        butt.setCursor(new Cursor(Cursor.HAND_CURSOR));

        butt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                butt.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                butt.setBackground(bgColor);
            }
        });
    }

    public void showGameOver()
    {
        JDialog gameOverDialog = new JDialog(gameWindow, "Csőd:(", true);
        gameOverDialog.setSize(450, 250);
        gameOverDialog.setLocationRelativeTo(gameWindow);

        gameOverDialog.setUndecorated(true);
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(45, 45, 45));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(180, 100, 100), 6));

        JLabel titleLabel = new JLabel("Projekt vége", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 36));
        titleLabel.setForeground(new Color(220, 80, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));

        JLabel messageLabel = new JLabel("<html><center>A játék véget ért, a kiadások felemésztették a projektet.</center></html>", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Monospaced", Font.PLAIN, 16));
        messageLabel.setForeground(Color.LIGHT_GRAY);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(45, 45, 45));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton exit = new JButton("Kilépés");
        exit.setPreferredSize(new Dimension(150, 45));
        styleButtons(exit, new Color(180, 100, 100), new Color(200, 120, 120), Color.WHITE, new Font("Monospaced", Font.BOLD, 18));
        exit.addActionListener(e -> System.exit(0));

        buttonPanel.add(exit    );

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(messageLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        gameOverDialog.add(mainPanel);
        gameOverDialog.setVisible(true);
    }

    public AnimalType showAnimalSelector()
    {
        tempSelectedAnimal = null;

        JDialog dialog = new JDialog(gameWindow, "Állat vásárlása", true);
        dialog.setLayout(new GridLayout(1, 5, 10, 10));
        dialog.setSize(750, 200);
        dialog.setLocationRelativeTo(gameWindow);

        dialog.add(createAnimalOption("boxed-bear", AnimalType.BEAR, dialog));
        dialog.add(createAnimalOption("boxed-fish", AnimalType.FISH, dialog));
        dialog.add(createAnimalOption("boxed-cat", AnimalType.CAT, dialog));
        dialog.add(createAnimalOption("boxed-horse", AnimalType.HORSE, dialog));
        dialog.add(createAnimalOption("boxed-pig", AnimalType.PIG, dialog));

        dialog.setVisible(true);

        return tempSelectedAnimal;
    }

    private JPanel createAnimalOption(String assName, AnimalType type, JDialog dialog)
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(100, 100));

        BufferedImage img = engine.AssetManager.get(assName);
        if (img != null) {
            Image scaled = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
        } else {
            imgLabel.setText("Nincs kép");
            imgLabel.setOpaque(true);
            imgLabel.setBackground(Color.DARK_GRAY);
            imgLabel.setForeground(Color.WHITE);
        }
        panel.add(imgLabel, BorderLayout.CENTER);

        int price = type.getValue() * 5;
        JButton buy = new JButton("$" + price);
        buy.addActionListener(e -> {
            tempSelectedAnimal = type;
            dialog.dispose();
        });

        panel.add(buy, BorderLayout.SOUTH);

        return panel;
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

    public void errorPopup(String errorMes)
    {
        JDialog errorDialog = new JDialog(gameWindow, "Hiba", true);
        errorDialog.setSize(350, 150);
        errorDialog.setLocationRelativeTo(gameWindow);
        errorDialog.setUndecorated(true);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(45, 45, 45));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 80, 80), 4));

        JLabel titleLabel = new JLabel("Hiba", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        titleLabel.setForeground(new Color(220, 80, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        JLabel messageLabel = new JLabel("<html><center>" + errorMes + "</center></html>", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        messageLabel.setForeground(Color.LIGHT_GRAY);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(45, 45, 45));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JButton ok = new JButton("OK");
        ok.setPreferredSize(new Dimension(80, 30));
        styleButtons(ok, new Color(92, 92, 92), new Color(115, 115, 115), Color.WHITE, new Font("Monospaced", Font.BOLD, 14));

        ok.addActionListener(e -> errorDialog.dispose());
        buttonPanel.add(ok);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(messageLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        errorDialog.add(mainPanel);
        errorDialog.setVisible(true);
    }

    public void setDay(int day) {
        dayCounter.setText("<html><center><b style='font-size:22px'>"+ day +"</b></center></html>");
    }

    public void setBalance(int money, int expenses)
    {
        balanceLabel.setText("<html><span style='font-family: SansSerif; font-size:24px'>$" + money +
                "</span><sup style='font-family: SansSerif; font-size:14px; color:#E74C3C;'>-" + expenses + "</sup></html>");
    }

    public GameMapPanel getMapPanel() { return mapPanel; }
    public MinimapPanel getMinimapPanel() { return minimapPanel; }

    public JButton getRoadToggle() { return roadToggle; }

    public JButton getDemolishToggle() { return demolishToggle; }

    public JButton getStoneBridgeToggle() { return stoneBridgeToggle; }

    public JButton getWoodenBridgeToggle() { return woodenBridgeToggle; }

    public JButton getGlassBridgeToggle() { return glassBridgeToggle; }

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
