package org.comu;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class OyunDuzenlePenceresi extends JFrame {
    private OyunDao oyunDao;
    private Oyun oyun;
    private JTextField adField, turField, yilField, platformField, resimField;

    public OyunDuzenlePenceresi(Oyun oyun, OyunDao oyunDao) {
        this.oyun = oyun;
        this.oyunDao = oyunDao;

        setTitle("Oyun Düzenle: " + oyun.getIsim());
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        adField = new JTextField(oyun.getIsim());
        turField = new JTextField(oyun.getTur());
        yilField = new JTextField(String.valueOf(oyun.getYil()));
        platformField = new JTextField(String.join(",", oyun.getPlatformlar()));
        resimField = new JTextField(oyun.getResimYolu() != null ? oyun.getResimYolu() : "");
        JButton resimSecButton = new JButton("Seç");

        formPanel.add(new JLabel("Oyun Adı:"));
        formPanel.add(adField);
        formPanel.add(new JLabel("Tür:"));
        formPanel.add(turField);
        formPanel.add(new JLabel("Yıl:"));
        formPanel.add(yilField);
        formPanel.add(new JLabel("Platformlar (virgülle):"));
        formPanel.add(platformField);
        formPanel.add(new JLabel("Resim Yolu:"));
        formPanel.add(resimField);
        formPanel.add(new JLabel(""));
        formPanel.add(resimSecButton);

        resimSecButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                resimField.setText(selectedFile.getAbsolutePath());
            }
        });

        JButton kaydetButton = new JButton("Kaydet");
        JButton iptalButton = new JButton("İptal");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(kaydetButton);
        buttonPanel.add(iptalButton);

        kaydetButton.addActionListener(e -> {
            try {
                String ad = adField.getText();
                String tur = turField.getText();
                int yil = Integer.parseInt(yilField.getText());
                List<String> platformlar = Arrays.asList(platformField.getText().split(","));
                String resimYolu = resimField.getText();

                Oyun guncellenen = new Oyun(ad, tur, yil, platformlar, resimYolu);
                oyunDao.oyunGuncelleById(oyun.getId().toHexString(), guncellenen);

                JOptionPane.showMessageDialog(this, "Oyun başarıyla güncellendi.");
                dispose();

                // Tüm oyunlar listesini yenile
                SwingUtilities.invokeLater(() -> {
                    Window[] windows = Window.getWindows();
                    for (Window window : windows) {
                        if (window instanceof OyunKartListePenceresi) {
                            window.dispose();
                        }
                    }
                    new OyunKartListePenceresi(oyunDao.tumOyunlar(), oyunDao);
                });
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Lütfen yıl için geçerli bir sayı girin.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        iptalButton.addActionListener(e -> dispose());

        setLayout(new BorderLayout(10, 10));
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}