package org.comu;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KullaniciTercihPenceresi extends JFrame {
    public KullaniciTercihPenceresi(OyunDao oyunDao) {
        setTitle("Tercihlerini Seç");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 1));

        JCheckBox fps = new JCheckBox("FPS");
        JCheckBox rpg = new JCheckBox("RPG");
        JCheckBox strateji = new JCheckBox("Strateji");

        JCheckBox pc = new JCheckBox("PC");
        JCheckBox ps5 = new JCheckBox("PS5");

        JTextField yilField = new JTextField("2015");

        JButton btn = new JButton("Bana Öner");

        btn.addActionListener(e -> {
            List<String> secilenTurler = Arrays.asList(
                    fps.isSelected() ? "FPS" : null,
                    rpg.isSelected() ? "RPG" : null,
                    strateji.isSelected() ? "Strateji" : null
            ).stream().filter(s -> s != null).collect(Collectors.toList());

            List<String> secilenPlatformlar = Arrays.asList(
                    pc.isSelected() ? "PC" : null,
                    ps5.isSelected() ? "PS5" : null
            ).stream().filter(s -> s != null).collect(Collectors.toList());

            int minYil;
            try {
                minYil = Integer.parseInt(yilField.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Geçerli bir yıl girin!");
                return;
            }

            KullaniciTercihi tercih = new KullaniciTercihi(secilenTurler, secilenPlatformlar, minYil);
            new OnerilenOyunlarPenceresi(oyunDao.tumOyunlar(), tercih); // Alttaki classı tanımladık!
            dispose(); // Bu pencereyi kapat
        });

        panel.add(new JLabel("Tür seç:"));
        panel.add(fps);
        panel.add(rpg);
        panel.add(strateji);

        panel.add(new JLabel("Platform seç:"));
        panel.add(pc);
        panel.add(ps5);

        panel.add(new JLabel("Minimum yıl:"));
        panel.add(yilField);

        panel.add(btn);

        add(panel);
        setVisible(true);
    }
}
