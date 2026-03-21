package UI;

import engine.rendering.Camera;
import engine.rendering.Renderer;
import world.World;
import javax.swing.*;
import java.awt.*;

public class ingameGUI {

    static class GameMapPanel extends JPanel {
        private Renderer renderer;

        public GameMapPanel(Renderer renderer) {
            this.renderer = renderer;
            setBackground(Color.BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (renderer != null) {
                renderer.renderMap(g);
            }
        }
    }

    public static void main(String[] args) {
        engine.AssetManager.getInstance();

        World vilag = new World(50,50);
        vilag.initWorld();
        Camera kamera = new Camera(400,100,0.3,800,500);
        Renderer jatekRenderer = new Renderer(kamera, vilag);

        JFrame ablak = new JFrame("Noé bárkája");
        ablak.setSize(1000, 750);
        ablak.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ablak.setLocationRelativeTo(null);

        ablak.setLayout(new BorderLayout(10, 10));

        JPanel felsoPanel = new JPanel(new BorderLayout(10, 0));
        felsoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // Pici üres margó a szélekre

        JPanel minimapPanel = new JPanel();
        minimapPanel.setPreferredSize(new Dimension(200, 120));
        minimapPanel.setBorder(BorderFactory.createTitledBorder("Minimap"));
        felsoPanel.add(minimapPanel, BorderLayout.WEST);

        JPanel buildPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buildPanel.setBorder(BorderFactory.createTitledBorder("Build Options"));
        buildPanel.add(new JButton("Út"));
        buildPanel.add(new JButton("Megálló"));
        buildPanel.add(new JButton("Jármű"));
        felsoPanel.add(buildPanel, BorderLayout.CENTER);

        JPanel napPanel = new JPanel(new BorderLayout());
        napPanel.setPreferredSize(new Dimension(100, 100));
        napPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        JLabel napSzoveg = new JLabel("<html><center>Day<br><b>1</b></center></html>", SwingConstants.CENTER);
        napSzoveg.setFont(new Font("Arial", Font.PLAIN, 18));
        napPanel.add(napSzoveg, BorderLayout.CENTER);
        felsoPanel.add(napPanel, BorderLayout.EAST);

        ablak.add(felsoPanel, BorderLayout.NORTH);

        GameMapPanel mapPanel = new GameMapPanel(jatekRenderer);
        mapPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));

        JPanel mapContainer = new JPanel(new BorderLayout());
        mapContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mapContainer.add(mapPanel, BorderLayout.CENTER);

        ablak.add(mapContainer, BorderLayout.CENTER);

        JPanel alsoPanel = new JPanel(new BorderLayout(10, 0));
        alsoPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JLabel penzLabel = new JLabel("<html><span style='font-size:24px'>$12345</span><sup style='font-size:14px'>-123</sup></html>");
        alsoPanel.add(penzLabel, BorderLayout.WEST);

        JPanel idoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        idoPanel.add(new JButton("||"));
        idoPanel.add(new JButton("►"));
        idoPanel.add(new JButton("►►"));
        idoPanel.add(new JButton("►|"));
        alsoPanel.add(idoPanel, BorderLayout.CENTER);

        JPanel rendszerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rendszerPanel.add(new JButton("Save"));
        JButton exitGomb = new JButton("Exit");
        exitGomb.addActionListener(e -> System.exit(0));
        rendszerPanel.add(exitGomb);
        rendszerPanel.add(new JButton("?"));
        alsoPanel.add(rendszerPanel, BorderLayout.EAST);

        ablak.add(alsoPanel, BorderLayout.SOUTH);

        ablak.setVisible(true);
    }
}
