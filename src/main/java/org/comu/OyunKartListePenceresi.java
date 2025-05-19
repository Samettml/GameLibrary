package org.comu;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class OyunKartListePenceresi extends JFrame {
    // Ana uygulamanın renk şeması
    private static Color primaryColor = new Color(44, 62, 80); // Koyu mavi
    private static Color accentColor = new Color(52, 152, 219); // Parlak mavi
    private static Color textColor = new Color(236, 240, 241); // Beyazımsı
    private static Color backgroundColor = new Color(52, 73, 94); // Koyu gri-mavi

    private OyunDao oyunDao;

    public OyunKartListePenceresi(List<Oyun> oyunlar, OyunDao oyunDao) {
        this.oyunDao = oyunDao;

        setTitle("Tüm Oyunlar");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Ana panel - GridLayout kullanarak kartları ızgara şeklinde dizeriz
        JPanel mainPanel = new JPanel();
        // Burada FlowLayout yerine GridLayout kullanıyoruz (satır sayısı 0, sütun sayısı 4)
        // Satır sayısı 0 olduğunda, sütun sayısına göre otomatik satır oluşturur
        mainPanel.setLayout(new GridLayout(0, 4, 10, 10));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Her oyun için bir kart paneli oluştur ve ana panele ekle
        for (Oyun oyun : oyunlar) {
            JPanel kartPanel = createOyunKartPanel(oyun);
            mainPanel.add(kartPanel);
        }

        // Kaydırma çubuğu ekle
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Daha hızlı kaydırma

        // Başlık paneli
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(primaryColor);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Tüm Oyunlar");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(textColor);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel countLabel = new JLabel(oyunlar.size() + " oyun bulundu");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        countLabel.setForeground(textColor);
        headerPanel.add(countLabel, BorderLayout.EAST);

        // Ana pencereye panelleri ekle
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }




























    private JPanel createOyunKartPanel(Oyun oyun) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(180, 280)); // Biraz daha küçük boyut
        panel.setLayout(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createLineBorder(accentColor, 1));
        panel.setBackground(primaryColor);

        // Oyun adı
        JLabel isimLabel = new JLabel(oyun.getIsim(), SwingConstants.CENTER);
        isimLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        isimLabel.setForeground(textColor);
        isimLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Oyun resmi
        // Oyun resmi
        JLabel resimLabel = new JLabel("Resim Yükleniyor...", SwingConstants.CENTER);
        resimLabel.setForeground(textColor);
        resimLabel.setBackground(backgroundColor);
        resimLabel.setOpaque(true);
        resimLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        resimLabel.setPreferredSize(new Dimension(160, 90));

// Eğer oyunun resim yolu varsa, resmi yükle
        if (oyun.getResimYolu() != null && !oyun.getResimYolu().isEmpty()) {
            System.out.println("Resim yolu mevcut: " + oyun.getResimYolu());

            // Eğer resim yolu http ile başlıyorsa, URL'den yükle
            if (oyun.getResimYolu().startsWith("http")) {
                loadImageFromUrl(oyun.getResimYolu(), resimLabel);
            } else {
                // Yerel dosyadan yükle
                try {
                    ImageIcon icon = new ImageIcon(oyun.getResimYolu());
                    Image img = icon.getImage().getScaledInstance(160, 90, Image.SCALE_SMOOTH);
                    resimLabel.setIcon(new ImageIcon(img));
                    resimLabel.setText("");
                } catch (Exception e) {
                    System.out.println("Yerel resim yüklenemedi: " + e.getMessage());
                    // Resim yüklenemezse, Steam'den resim almayı dene
                    loadSteamImage(oyun.getIsim(), resimLabel);
                }
            }
        } else {
            System.out.println("Resim yolu yok, Steam'den alınacak: " + oyun.getIsim());
            // Resim yolu yoksa, Steam'den resim almayı dene
            loadSteamImage(oyun.getIsim(), resimLabel);
        }

        // Oyun detayları
        JPanel detayPanel = new JPanel();
        detayPanel.setLayout(new BoxLayout(detayPanel, BoxLayout.Y_AXIS));
        detayPanel.setBackground(backgroundColor);
        detayPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel turLabel = new JLabel("Tür: " + oyun.getTur());
        turLabel.setForeground(textColor);
        turLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel yilLabel = new JLabel("Yıl: " + oyun.getYil());
        yilLabel.setForeground(textColor);
        yilLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel platformLabel = new JLabel("Platform: " + String.join(", ", oyun.getPlatformlar()));
        platformLabel.setForeground(textColor);
        platformLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        detayPanel.add(turLabel);
        detayPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        detayPanel.add(yilLabel);
        detayPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        detayPanel.add(platformLabel);

        // Steam bilgilerini ekle (arka planda yükle)
        JLabel steamInfoLabel = new JLabel("Steam bilgileri yükleniyor...");
        steamInfoLabel.setForeground(textColor);
        steamInfoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        detayPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detayPanel.add(steamInfoLabel);

        // Arka planda Steam bilgilerini yükle
        new Thread(() -> {
            try {
                SteamApiHelper.SteamGameInfo steamInfo = SteamApiHelper.searchGame(oyun.getIsim());

                if (steamInfo != null) {
                    SwingUtilities.invokeLater(() -> {
                        // Metacritic puanı
                        String metacriticInfo = steamInfo.getMetacriticScore() > 0 ?
                                "Metacritic: " + steamInfo.getMetacriticScore() + "/100" : "Metacritic: N/A";

                        // Anlık oyuncu sayısı
                        String playerCountInfo = "Anlık Oyuncu: " +
                                (steamInfo.getCurrentPlayerCount() > 0 ?
                                        steamInfo.getCurrentPlayerCount() : "Bilgi yok");

                        // Steam bilgilerini güncelle
                        steamInfoLabel.setText("<html>" + metacriticInfo + "<br>" + playerCountInfo + "</html>");

                        // Eğer resim hala yüklenemediyse ve Steam'den resim varsa
                        if (resimLabel.getIcon() == null && !steamInfo.getHeaderImage().isEmpty()) {
                            loadImageFromUrl(steamInfo.getHeaderImage(), resimLabel);
                        }
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        steamInfoLabel.setText("Steam bilgisi bulunamadı");
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    steamInfoLabel.setText("Steam bilgisi alınamadı");
                });
            }
        }).start();

        // Düzenleme ve silme butonları
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttonPanel.setBackground(backgroundColor);

        JButton editButton = new JButton("Düzenle");
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        editButton.setForeground(textColor);
        editButton.setBackground(accentColor);
        editButton.setBorderPainted(false);
        editButton.setFocusPainted(false);

        JButton deleteButton = new JButton("Sil");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        deleteButton.setForeground(textColor);
        deleteButton.setBackground(new Color(231, 76, 60)); // Kırmızı
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Buton işlevleri
        editButton.addActionListener(e -> {
            // Oyun düzenleme penceresini aç
            new OyunDuzenlePenceresi(oyun, oyunDao);
        });

        deleteButton.addActionListener(e -> {
            // Silme onayı iste
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "\"" + oyun.getIsim() + "\" oyununu silmek istediğinize emin misiniz?",
                    "Oyun Sil",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {
                oyunDao.oyunSilById(oyun.getId().toHexString());
                JOptionPane.showMessageDialog(this, "Oyun başarıyla silindi.");
                dispose(); // Pencereyi kapat
                new OyunKartListePenceresi(oyunDao.tumOyunlar(), oyunDao); // Listeyi yenile
            }
        });

        // Panele bileşenleri ekle
        panel.add(isimLabel, BorderLayout.NORTH);
        panel.add(resimLabel, BorderLayout.CENTER);

        // Butonları en alta ekle
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(backgroundColor);
        southPanel.add(detayPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Steam'den oyun resmi yükleme
    // Steam'den oyun resmi yükleme
    private void loadSteamImage(String gameName, JLabel resimLabel) {
        System.out.println("Steam'den resim aranıyor: " + gameName);

        new Thread(() -> {
            try {
                SteamApiHelper.SteamGameInfo steamInfo = SteamApiHelper.searchGame(gameName);
                if (steamInfo != null && !steamInfo.getHeaderImage().isEmpty()) {
                    System.out.println("Steam'den resim bulundu: " + steamInfo.getHeaderImage());
                    loadImageFromUrl(steamInfo.getHeaderImage(), resimLabel);
                } else {
                    System.out.println("Steam'de resim bulunamadı: " + gameName);
                    SwingUtilities.invokeLater(() -> {
                        resimLabel.setText("Resim Bulunamadı");
                    });
                }
            } catch (Exception e) {
                System.out.println("Steam resim arama hatası: " + e.getMessage());
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    resimLabel.setText("Resim Yüklenemedi");
                });
            }
        }).start();

    }

    // URL'den resim yükleme
    // URL'den resim yükleme
    private void loadImageFromUrl(String imageUrl, JLabel resimLabel) {
        System.out.println("URL'den resim yükleniyor: " + imageUrl);

        new Thread(() -> {
            try {
                URL url = new URL(imageUrl);
                Image image = ImageIO.read(url);
                if (image != null) {
                    Image scaledImage = image.getScaledInstance(160, 90, Image.SCALE_SMOOTH);
                    SwingUtilities.invokeLater(() -> {
                        resimLabel.setIcon(new ImageIcon(scaledImage));
                        resimLabel.setText("");
                        System.out.println("Resim başarıyla yüklendi: " + imageUrl);
                    });
                } else {
                    System.out.println("Resim null döndü: " + imageUrl);
                    SwingUtilities.invokeLater(() -> {
                        resimLabel.setText("Resim Yüklenemedi");
                    });
                }
            } catch (Exception e) {
                System.out.println("Resim yükleme hatası: " + e.getMessage());
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    resimLabel.setText("Resim Yüklenemedi");
                });
            }
        }).start();
    }
}