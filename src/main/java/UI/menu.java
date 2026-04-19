package UI;

import javax.swing.*;
import java.awt.Font;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.function.Consumer;

public class menu{

    private Consumer<String> onGameStart;

    public void setStartGameListener(Consumer<String> listener) {
        this.onGameStart = listener;
    }

    public void showMenu() {

        JFrame ablak = new JFrame("Noé bárkája");
        ablak.setSize(800, 600);
        ablak.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ablak.setLocationRelativeTo(null);
        ablak.setLayout(null);

        JLabel cim = new JLabel("Noé bárkája");
        cim.setFont(new Font("Arial", Font.BOLD, 60));
        cim.setBounds(100, 80, 600, 80);
        cim.setHorizontalAlignment(SwingConstants.CENTER);
        ablak.add(cim);

        JButton ujJatekGomb = new JButton("<html><center>Új<br>Játék</center></html>");
        ujJatekGomb.setBounds(120, 300, 120, 80);
        ablak.add(ujJatekGomb);

        JButton betoltesGomb = new JButton("<html><center>Játék<br>Betöltése</center></html>");
        betoltesGomb.setBounds(260, 300, 120, 80);
        ablak.add(betoltesGomb);

        JButton sugoGomb = new JButton("Súgó");
        sugoGomb.setBounds(400, 300, 120, 80);
        ablak.add(sugoGomb);

        JButton kilepesGomb = new JButton("Kilépés");
        kilepesGomb.setBounds(540, 300, 120, 80);
        ablak.add(kilepesGomb);

        kilepesGomb.addActionListener(e -> System.exit(0));

        ujJatekGomb.addActionListener(e -> {
            ablak.dispose();
            if(onGameStart != null)
                onGameStart.accept(null);
        });

        betoltesGomb.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Játék betöltése");
            fileChooser.setFileFilter(new FileNameExtensionFilter("JSON fájlok", "json"));
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

            int userSelection = fileChooser.showOpenDialog(ablak);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToLoad = fileChooser.getSelectedFile();
                ablak.dispose();
                if (onGameStart != null) onGameStart.accept(fileToLoad.getAbsolutePath());
            }
        });

        ablak.setResizable(false);
        ablak.setVisible(true);
    }
}
