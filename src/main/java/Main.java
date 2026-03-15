import engine.AssetManager;
import engine.InputManager;
import engine.rendering.Camera;
import engine.rendering.Renderer;
import world.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main{
    public static void main(String[] args) {
        AssetManager assetManager = AssetManager.getInstance();

        JFrame frame = new JFrame("Testing enviorment");

        frame.setSize(700,700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.add(new RajzPanel());
        frame.setVisible(true);


    }
}

class RajzPanel extends JPanel {

    private World world = new World(100,100);
    private Camera camera = new Camera(0,0,1,700,700);
    private Renderer renderer = new Renderer(camera);
    private InputManager inputManager = new InputManager(camera, world);


    public RajzPanel(){
        this.addKeyListener(inputManager);
        this.addMouseWheelListener(inputManager);
        this.setFocusable(true);
        this.requestFocusInWindow();

        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputManager.update();
                repaint();
            }
        });
        timer.start();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        renderer.renderMap(g, world);

    }
}