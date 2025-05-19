package org.comu;

import javax.swing.*;
import java.awt.*;

public class OyunKartPanel extends JPanel {

    public OyunKartPanel(Oyun oyun) {
        setPreferredSize(new Dimension(200, 250));
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setBackground(Color.WHITE);

        JLabel isimLabel = new JLabel(oyun.getIsim(), SwingConstants.CENTER);
        isimLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel resimLabel;
        if (oyun.getResimYolu() != null && !oyun.getResimYolu().isEmpty()) {
            ImageIcon icon = new ImageIcon(oyun.getResimYolu());
            Image img = icon.getImage().getScaledInstance(180, 100, Image.SCALE_SMOOTH);
            resimLabel = new JLabel(new ImageIcon(img));
        } else {
            resimLabel = new JLabel("Resim Yok", SwingConstants.CENTER);
        }

        JTextArea detayArea = new JTextArea(
                "Tür: " + oyun.getTur() +
                        "\nYıl: " + oyun.getYil() +
                        "\nPlatform: " + String.join(", ", oyun.getPlatformlar())
        );
        detayArea.setEditable(false);
        detayArea.setFont(new Font("Arial", Font.PLAIN, 12));
        detayArea.setBackground(Color.WHITE);
        detayArea.setLineWrap(true);
        detayArea.setWrapStyleWord(true);

        add(isimLabel, BorderLayout.NORTH);
        add(resimLabel, BorderLayout.CENTER);
        add(detayArea, BorderLayout.SOUTH);
    }


    public class OyunKartPanell extends JPanel {
        public OyunKartPanell(Oyun oyun) {
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(200, 120));
            setBackground(new Color(52, 73, 94));
            setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219)));

            JLabel nameLabel = new JLabel(oyun.getIsim());
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

            add(nameLabel, BorderLayout.NORTH);

            // Diğer oyun bilgileri eklenebilir (Tür, Yıl vs)
        }
    }


}
