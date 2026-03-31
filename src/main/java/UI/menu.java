package UI;

import javax.swing.*;
import java.awt.Font;

public class menu{

    public static void main(String[] args) {

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

        JButton ujJatekGomb = new JButton("<html><center>New<br>Game</center></html>");
        ujJatekGomb.setBounds(120, 300, 120, 80);
        ablak.add(ujJatekGomb);

        JButton betoltesGomb = new JButton("<html><center>Load<br>Game</center></html>");
        betoltesGomb.setBounds(260, 300, 120, 80);
        ablak.add(betoltesGomb);

        JButton sugoGomb = new JButton("Help");
        sugoGomb.setBounds(400, 300, 120, 80);
        ablak.add(sugoGomb);

        JButton kilepesGomb = new JButton("Exit");
        kilepesGomb.setBounds(540, 300, 120, 80);
        ablak.add(kilepesGomb);

        kilepesGomb.addActionListener(e -> System.exit(0));

        ujJatekGomb.addActionListener(e -> {
            System.out.println("Indul az új játék!");
        });

        ablak.setResizable(false);
        ablak.setVisible(true);
    }
}
