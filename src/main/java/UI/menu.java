package UI;

import engine.AssetManager;

import javax.swing.*;
import java.awt.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Consumer;

public class menu {

    private JFrame menuWindow;
    private Consumer<String> startGameListener;
    private final int TITLE_WIDTH = 480;
    private final int TITLE_HEIGHT = 320;
    private final int BUTTON_SIZE = 180;

    public menu() {
        menuWindow = new JFrame("Noé Bárkája");
        menuWindow.setSize(1000, 750);
        menuWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuWindow.setLocationRelativeTo(null);
        menuWindow.setResizable(false);

        BackgroundPanel bgPanel = new BackgroundPanel();
        menuWindow.setContentPane(bgPanel);

        bgPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Noé Bárkája");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);

        BufferedImage rawTitle = (BufferedImage) AssetManager.get("menu-logo-graphic");
        if (rawTitle != null) {
            Image scaledTitle = rawTitle.getScaledInstance(TITLE_WIDTH, TITLE_HEIGHT, Image.SCALE_SMOOTH);
            titleLabel.setIcon(new ImageIcon(scaledTitle));
            titleLabel.setText("");
        }
        bgPanel.add(titleLabel, gbc);

        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonContainer.setOpaque(false);

        JButton newGameBtn = menuButton("paw-button-new-game");
        JButton loadGameBtn = menuButton("paw-button-load-game");
        JButton helpBtn = menuButton("paw-button-help");
        JButton exitBtn = menuButton("paw-button-exit");

        newGameBtn.addActionListener(e -> {
            if (startGameListener != null) {
                menuWindow.dispose();
                startGameListener.accept(null);
            }
        });

        loadGameBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Játék betöltése");
            fileChooser.setFileFilter(new FileNameExtensionFilter("JSON fájlok", "json"));
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

            int result = fileChooser.showOpenDialog(menuWindow);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (startGameListener != null) {
                    menuWindow.dispose();
                    startGameListener.accept(selectedFile.getAbsolutePath());
                }
            }
        });

        helpBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(menuWindow,
                    "A játék célja felépíteni és menedzselni a projektet.\nKattints a gombokra az építkezéshez!",
                    "Súgó",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        exitBtn.addActionListener(e -> System.exit(0));

        buttonContainer.add(newGameBtn);
        buttonContainer.add(loadGameBtn);
        buttonContainer.add(helpBtn);
        buttonContainer.add(exitBtn);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 140, 0);
        bgPanel.add(buttonContainer, gbc);
    }

    private JButton menuButton(String assetName) {
        JButton butt = new JButton();

        BufferedImage rawImg = (BufferedImage) AssetManager.get(assetName);
        if (rawImg != null) {
            Image scaledImg = rawImg.getScaledInstance(BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
            butt.setIcon(new ImageIcon(scaledImg));
        } else {
            butt.setFont(new Font("Monospaced", Font.BOLD, 24));
            butt.setPreferredSize(new Dimension(200, 60));
            butt.setBackground(new Color(143, 168, 118));
            butt.setForeground(Color.WHITE);
            return butt;
        }

        butt.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        butt.setContentAreaFilled(false);
        butt.setBorderPainted(false);
        butt.setFocusPainted(false);
        butt.setOpaque(false);
        butt.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return butt;
    }

    public void showMenu() {
        menuWindow.setVisible(true);
    }

    public void setStartGameListener(Consumer<String> listener) {
        this.startGameListener = listener;
    }

    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            BufferedImage rawBg = (BufferedImage) AssetManager.get("menu-background");
            if (rawBg != null) {
                this.backgroundImage = rawBg;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(new Color(40, 40, 40));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
}
