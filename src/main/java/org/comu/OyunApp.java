package org.comu;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class OyunApp extends JFrame {
    private OyunDao dao;
    private DefaultListModel<Oyun> listModel;
    private JList<Oyun> oyunListesi;
    private JTextField adField, turField, yilField, platformField, resimField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OyunApp::new);
    }

    public OyunApp() {
        MongoClient client = MongoClients.create("mongodb://localhost:27017/");
        MongoDatabase db = client.getDatabase("kutuphane");
        dao = new OyunDao(db);

        setTitle("Oyun Kütüphanesi Yönetimi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 450);
        setLocationRelativeTo(null);

        listModel = new DefaultListModel<>();
        oyunListesi = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(oyunListesi);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5)); // 6 satır oldu
        adField = new JTextField();
        turField = new JTextField();
        yilField = new JTextField();
        platformField = new JTextField();
        resimField = new JTextField();
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

        JButton ekleButton = new JButton("Ekle");
        JButton guncelleButton = new JButton("Güncelle");
        JButton silButton = new JButton("Sil");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(ekleButton);
        buttonPanel.add(guncelleButton);
        buttonPanel.add(silButton);

        add(scrollPane, BorderLayout.WEST);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        oyunlariYukle();

        oyunListesi.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Oyun secili = oyunListesi.getSelectedValue();
                if (secili != null) {
                    adField.setText(secili.getIsim());
                    turField.setText(secili.getTur());
                    yilField.setText(String.valueOf(secili.getYil()));
                    platformField.setText(String.join(",", secili.getPlatformlar()));
                    resimField.setText(secili.getResimYolu() != null ? secili.getResimYolu() : "");
                }
            }
        });

        ekleButton.addActionListener(e -> oyunEkle());
        guncelleButton.addActionListener(e -> oyunGuncelle());
        silButton.addActionListener(e -> oyunSil());

        setVisible(true);
    }

    private void oyunlariYukle() {
        listModel.clear();
        List<Oyun> oyunlar = dao.tumOyunlar();
        for (Oyun oyun : oyunlar) {
            listModel.addElement(oyun);
        }
    }

    private void oyunEkle() {
        String ad = adField.getText();
        String tur = turField.getText();
        int yil = Integer.parseInt(yilField.getText());
        List<String> platformlar = Arrays.asList(platformField.getText().split(","));
        String resimYolu = resimField.getText();

        Oyun oyun = new Oyun(ad, tur, yil, platformlar, resimYolu);
        dao.oyunEkle(oyun);

        adField.setText("");
        turField.setText("");
        yilField.setText("");
        platformField.setText("");
        resimField.setText("");
        oyunlariYukle();
    }

    private void oyunGuncelle() {
        Oyun secili = oyunListesi.getSelectedValue();
        if (secili != null) {
            String ad = adField.getText();
            String tur = turField.getText();
            int yil = Integer.parseInt(yilField.getText());
            List<String> platformlar = Arrays.asList(platformField.getText().split(","));
            String resimYolu = resimField.getText();

            Oyun guncellenen = new Oyun(ad, tur, yil, platformlar, resimYolu);
            dao.oyunGuncelleById(secili.getId().toHexString(), guncellenen);
            oyunlariYukle();
        }
    }

    private void oyunSil() {
        Oyun secili = oyunListesi.getSelectedValue();
        if (secili != null) {
            dao.oyunSilById(secili.getId().toHexString());
            oyunlariYukle();
        }
    }
}
