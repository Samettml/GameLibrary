package org.comu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class OyunEklePenceresi extends JFrame {
    private Color primaryColor = new Color(44, 62, 80); // Koyu mavi
    private Color accentColor = new Color(52, 152, 219); // Parlak mavi
    private Color textColor = new Color(236, 240, 241); // Beyazımsı
    private Color backgroundColor = new Color(52, 73, 94); // Koyu gri-mavi
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 20);
    private Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);

    private JTextField isimField;
    private JTextField turField;
    private JTextField yilField;
    private JTextField platformField;
    private JTextField resimYoluField;
    private JLabel resimOnizlemeLabel;
    private String secilenResimYolu = "";

    public OyunEklePenceresi(OyunDao oyunDao) {
        setTitle("Yeni Oyun Ekle");
        setSize(600, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(backgroundColor);

        // Başlık Paneli
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Yeni Oyun Ekle");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(textColor);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Paneli
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(backgroundColor);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // İsim
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel isimLabel = createLabel("Oyun İsmi:");
        formPanel.add(isimLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        isimField = createTextField();
        formPanel.add(isimField, gbc);

        // Tür
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel turLabel = createLabel("Tür:");
        formPanel.add(turLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        turField = createTextField();
        formPanel.add(turField, gbc);

        // Yıl
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        JLabel yilLabel = createLabel("Çıkış Yılı:");
        formPanel.add(yilLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        yilField = createTextField();
        formPanel.add(yilField, gbc);

        // Platform
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        JLabel platformLabel = createLabel("Platformlar:");
        formPanel.add(platformLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        platformField = createTextField();
        formPanel.add(platformField, gbc);
        JLabel platformInfo = new JLabel("Virgülle ayırarak birden fazla platform ekleyebilirsiniz");
        platformInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        platformInfo.setForeground(textColor);
        gbc.gridy = 4;
        formPanel.add(platformInfo, gbc);

        // Resim
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        JLabel resimLabel = createLabel("Resim:");
        formPanel.add(resimLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JPanel resimPanel = new JPanel(new BorderLayout(5, 0));
        resimPanel.setOpaque(false);

        resimYoluField = createTextField();
        resimYoluField.setEditable(false);

        JButton browseButton = new JButton("Gözat");
        browseButton.setFont(inputFont);
        browseButton.setBackground(accentColor);
        browseButton.setForeground(textColor);
        browseButton.setBorderPainted(false);
        browseButton.setFocusPainted(false);
        browseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        resimPanel.add(resimYoluField, BorderLayout.CENTER);
        resimPanel.add(browseButton, BorderLayout.EAST);

        formPanel.add(resimPanel, gbc);

        // Resim Önizleme
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBackground(backgroundColor.brighter());
        previewPanel.setBorder(BorderFactory.createLineBorder(accentColor));
        previewPanel.setPreferredSize(new Dimension(300, 200));

        resimOnizlemeLabel = new JLabel("Resim Önizleme", SwingConstants.CENTER);
        resimOnizlemeLabel.setForeground(textColor);
        previewPanel.add(resimOnizlemeLabel, BorderLayout.CENTER);

        formPanel.add(previewPanel, gbc);

        // Butonlar
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton iptalButton = new JButton("İptal");
        iptalButton.setFont(labelFont);
        iptalButton.setBackground(new Color(231, 76, 60)); // Kırmızı
        iptalButton.setForeground(textColor);
        iptalButton.setBorderPainted(false);
        iptalButton.setFocusPainted(false);
        iptalButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton kaydetButton = new JButton("Kaydet");
        kaydetButton.setFont(labelFont);
        kaydetButton.setBackground(new Color(46, 204, 113)); // Yeşil
        kaydetButton.setForeground(textColor);
        kaydetButton.setBorderPainted(false);
        kaydetButton.setFocusPainted(false);
        kaydetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(iptalButton);
        buttonPanel.add(kaydetButton);

        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // Olay Dinleyicileri
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Resim Dosyaları", "jpg", "jpeg", "png", "gif"));

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                secilenResimYolu = selectedFile.getAbsolutePath();
                resimYoluField.setText(secilenResimYolu);

                // Resim önizleme
                ImageIcon icon = new ImageIcon(secilenResimYolu);
                Image img = icon.getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH);
                resimOnizlemeLabel.setIcon(new ImageIcon(img));
                resimOnizlemeLabel.setText("");
            }
        });

        iptalButton.addActionListener(e -> {
            dispose();
        });

        kaydetButton.addActionListener(e -> {
            try {
                String isim = isimField.getText().trim();
                String tur = turField.getText().trim();
                int yil = Integer.parseInt(yilField.getText().trim());
                List<String> platformlar = Arrays.asList(platformField.getText().split(","));

                if (isim.isEmpty() || tur.isEmpty() || platformField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Oyun oyun = new Oyun();
                oyun.setIsim(isim);
                oyun.setTur(tur);
                oyun.setYil(yil);
                oyun.setPlatformlar(platformlar);
                oyun.setResimYolu(secilenResimYolu);

                oyunDao.oyunEkle(oyun);

                JOptionPane.showMessageDialog(this, "Oyun başarıyla eklendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Yıl sayısal bir değer olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(labelFont);
        label.setForeground(textColor);
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(inputFont);
        textField.setBackground(backgroundColor.brighter());
        textField.setForeground(textColor);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)));
        textField.setCaretColor(textColor);
        return textField;
    }
}
