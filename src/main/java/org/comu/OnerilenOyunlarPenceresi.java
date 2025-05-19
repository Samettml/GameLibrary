package org.comu;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class OnerilenOyunlarPenceresi extends JFrame {
    // Ana uygulamanın renk şeması
    private static Color primaryColor = new Color(44, 62, 80); // Koyu mavi
    private static Color accentColor = new Color(52, 152, 219); // Parlak mavi
    private static Color textColor = new Color(236, 240, 241); // Beyazımsı
    private static Color backgroundColor = new Color(52, 73, 94); // Koyu gri-mavi

    public OnerilenOyunlarPenceresi(List<Oyun> tumOyunlar, KullaniciTercihi tercih) {
        setTitle("Sana Önerilen Oyunlar");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Filtrelenmiş oyunları al
        List<Oyun> filtrelenmis = tumOyunlar.stream()
                .filter(o -> tercih.getTurler().isEmpty() || tercih.getTurler().contains(o.getTur()))
                .filter(o -> tercih.getPlatformlar().isEmpty() || o.getPlatformlar().stream().anyMatch(p -> tercih.getPlatformlar().contains(p)))
                .filter(o -> o.getYil() >= tercih.getMinYil())
                .collect(Collectors.toList());

        // Ana panel - FlowLayout kullanarak kartları yan yana dizeriz
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        mainPanel.setBackground(backgroundColor); // Koyu gri-mavi arka plan

        // Eğer hiç oyun bulunamazsa bilgi mesajı göster
        if (filtrelenmis.isEmpty()) {
            JLabel noGameLabel = new JLabel("Seçtiğiniz kriterlere uygun oyun bulunamadı.");
            noGameLabel.setForeground(textColor);
            noGameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            mainPanel.add(noGameLabel);
        } else {
            // Her oyun için özel kart paneli oluştur ve ana panele ekle
            for (Oyun oyun : filtrelenmis) {
                JPanel kartPanel = createOyunKartPanel(oyun);
                mainPanel.add(kartPanel);
            }
        }

        // Kaydırma çubuğu ekle
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Daha hızlı kaydırma

        // Başlık paneli
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(primaryColor); // Koyu mavi
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Sana Önerilen Oyunlar");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(textColor);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel countLabel = new JLabel(filtrelenmis.size() + " oyun bulundu");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        countLabel.setForeground(textColor);
        headerPanel.add(countLabel, BorderLayout.EAST);

        // Ana pencereye panelleri ekle
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    // Özel kart paneli oluşturan metod
    private JPanel createOyunKartPanel(Oyun oyun) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 280)); // Biraz daha yüksek yaptık
        panel.setLayout(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createLineBorder(accentColor, 1));
        panel.setBackground(primaryColor);

        // Oyun adı
        JLabel isimLabel = new JLabel(oyun.getIsim(), SwingConstants.CENTER);
        isimLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        isimLabel.setForeground(textColor);
        isimLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Oyun resmi
        JLabel resimLabel;
        if (oyun.getResimYolu() != null && !oyun.getResimYolu().isEmpty()) {
            ImageIcon icon = new ImageIcon(oyun.getResimYolu());
            Image img = icon.getImage().getScaledInstance(180, 100, Image.SCALE_SMOOTH);
            resimLabel = new JLabel(new ImageIcon(img));
        } else {
            resimLabel = new JLabel("Resim Yok", SwingConstants.CENTER);
            resimLabel.setForeground(textColor);
        }
        resimLabel.setBackground(backgroundColor);
        resimLabel.setOpaque(true);
        resimLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

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

                        // Steam'den resim varsa ve yerel resim yoksa, Steam resmini kullan
                        if ((oyun.getResimYolu() == null || oyun.getResimYolu().isEmpty()) &&
                                !steamInfo.getHeaderImage().isEmpty()) {
                            new Thread(() -> {
                                try {
                                    // Steam'den resmi indir
                                    URL url = new URL(steamInfo.getHeaderImage());
                                    Image image = ImageIO.read(url);
                                    if (image != null) {
                                        Image scaledImage = image.getScaledInstance(180, 100, Image.SCALE_SMOOTH);
                                        SwingUtilities.invokeLater(() -> {
                                            resimLabel.setIcon(new ImageIcon(scaledImage));
                                            resimLabel.setText("");
                                        });
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).start();
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

        // Panele bileşenleri ekle
        panel.add(isimLabel, BorderLayout.NORTH);
        panel.add(resimLabel, BorderLayout.CENTER);
        panel.add(detayPanel, BorderLayout.SOUTH);

        return panel;
    }
}