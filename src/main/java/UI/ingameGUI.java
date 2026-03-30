package UI;

import engine.rendering.Minimap;
import engine.rendering.Renderer;

import javax.swing.*;
import java.awt.*;

public class ingameGUI {

    private JFrame gameWindow;
    private GameMapPanel mapPanel;
    private MinimapPanel minimapPanel;
    private JLabel dayCounter;
    private JLabel balanceLabel;
    private JButton roadToggle;
    private JButton speedPaused;
    private JButton speedNormal;
    private JButton speedFast;
    private JButton speedSuperFast;

    public class GameMapPanel extends JPanel {
        private Renderer renderer;

        public GameMapPanel() {
            setBackground(Color.BLACK);
        }

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
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (minimap != null) {
                minimap.render(g, 0, 0);
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
        buildPanel.add(new JButton("Megálló ikon"));
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

        mapPanel.repaint();
        minimapPanel.repaint();
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

    public JButton getSpeedSuperFast() { return speedSuperFast; }

    public void show() {
        gameWindow.setVisible(true);
    }
}
