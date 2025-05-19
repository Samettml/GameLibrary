package org.comu;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SteamKutuphanesiPenceresi extends JFrame {
    private OyunDao oyunDao;

    public SteamKutuphanesiPenceresi(OyunDao oyunDao) {
        this.oyunDao = oyunDao;

        setTitle("Steam Oyunları İçe Aktar");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Kişisel kütüphane sekmesi
        JPanel personalPanel = createPersonalLibraryPanel();
        tabbedPane.addTab("Kişisel Kütüphane", personalPanel);

        // Popüler oyunlar sekmesi
        JPanel popularPanel = createPopularGamesPanel();
        tabbedPane.addTab("Popüler Oyunlar", popularPanel);

        add(tabbedPane);
        setVisible(true);
    }

    private JPanel createPersonalLibraryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel infoLabel = new JLabel("Steam kütüphanenizi içe aktarmak için Steam ID'nizi girin:");
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField steamIdField = new JTextField();
        steamIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, steamIdField.getPreferredSize().height));

        JLabel helpLabel = new JLabel("<html>Steam ID'nizi bulmak için: Steam profilinize gidin, URL'deki sayıyı kopyalayın.<br>Örnek: https://steamcommunity.com/profiles/<b>76561198012345678</b></html>");
        helpLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        helpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton importButton = new JButton("Oyunları İçe Aktar");
        importButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        importButton.addActionListener(e -> {
            String steamId = steamIdField.getText().trim();

            if (steamId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen Steam ID'nizi girin.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Steam ID formatını kontrol et
            if (!steamId.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Geçersiz Steam ID formatı. Sadece rakamlardan oluşmalıdır.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            importButton.setEnabled(false);
            progressBar.setVisible(true);

            // Arka planda oyunları çek
            new Thread(() -> {
                try {
                    List<Oyun> steamOyunlar = SteamApiHelper.getUserSteamLibrary(steamId);

                    if (steamOyunlar.isEmpty()) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this,
                                    "Steam kütüphanenizden oyun çekilemedi. Steam ID'nizi kontrol edin veya kütüphaneniz gizli olabilir.",
                                    "Hata",
                                    JOptionPane.ERROR_MESSAGE);
                            importButton.setEnabled(true);
                            progressBar.setVisible(false);
                        });
                        return;
                    }

                    // Oyunları veritabanına ekle
                    int eklenenOyunSayisi = 0;
                    for (Oyun oyun : steamOyunlar) {
                        // Aynı isimde oyun var mı kontrol et
                        boolean oyunVarMi = false;
                        for (Oyun mevcutOyun : oyunDao.tumOyunlar()) {
                            if (mevcutOyun.getIsim().equalsIgnoreCase(oyun.getIsim())) {
                                oyunVarMi = true;
                                break;
                            }
                        }

                        // Oyun yoksa ekle
                        if (!oyunVarMi) {
                            oyunDao.oyunEkle(oyun);
                            eklenenOyunSayisi++;
                        }
                    }

                    final int sonEklenenSayi = eklenenOyunSayisi;

                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                                "Steam kütüphanenizden " + steamOyunlar.size() + " oyun bulundu.\n" +
                                        sonEklenenSayi + " yeni oyun kütüphanenize eklendi.",
                                "İçe Aktarma Tamamlandı",
                                JOptionPane.INFORMATION_MESSAGE);

                        importButton.setEnabled(true);
                        progressBar.setVisible(false);
                        dispose();

                        // Tüm oyunlar listesini yenile
                        Window[] windows = Window.getWindows();
                        for (Window window : windows) {
                            if (window instanceof OyunKartListePenceresi) {
                                window.dispose();
                            }
                        }
                        new OyunKartListePenceresi(oyunDao.tumOyunlar(), oyunDao);
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                                "Oyunlar çekilirken bir hata oluştu: " + ex.getMessage(),
                                "Hata",
                                JOptionPane.ERROR_MESSAGE);
                        importButton.setEnabled(true);
                        progressBar.setVisible(false);
                    });
                }
            }).start();
        });

        panel.add(infoLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(steamIdField);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(helpLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(importButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(progressBar);

        return panel;
    }

    private JPanel createPopularGamesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel infoLabel = new JLabel("Steam'deki popüler oyunları kütüphanenize ekleyin:");
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton importButton = new JButton("Popüler Oyunları İçe Aktar");
        importButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        importButton.addActionListener(e -> {
            importButton.setEnabled(false);
            progressBar.setVisible(true);

            // Arka planda oyunları çek
            new Thread(() -> {
                try {
                    List<Oyun> popularOyunlar = SteamApiHelper.getPopularSteamGames();

                    if (popularOyunlar.isEmpty()) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this,
                                    "Steam'den popüler oyunlar çekilemedi.",
                                    "Hata",
                                    JOptionPane.ERROR_MESSAGE);
                            importButton.setEnabled(true);
                            progressBar.setVisible(false);
                        });
                        return;
                    }

                    // Oyunları veritabanına ekle
                    int eklenenOyunSayisi = 0;
                    for (Oyun oyun : popularOyunlar) {
                        // Aynı isimde oyun var mı kontrol et
                        boolean oyunVarMi = false;
                        for (Oyun mevcutOyun : oyunDao.tumOyunlar()) {
                            if (mevcutOyun.getIsim().equalsIgnoreCase(oyun.getIsim())) {
                                oyunVarMi = true;
                                break;
                            }
                        }

                        // Oyun yoksa ekle
                        if (!oyunVarMi) {
                            System.out.println("Veritabanına ekleniyor: " + oyun.getIsim() + ", Resim: " + oyun.getResimYolu());
                            oyunDao.oyunEkle(oyun);
                            eklenenOyunSayisi++;
                        }
                    }

                    final int sonEklenenSayi = eklenenOyunSayisi;

                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                                "Steam'den " + popularOyunlar.size() + " popüler oyun bulundu.\n" +
                                        sonEklenenSayi + " yeni oyun kütüphanenize eklendi.",
                                "İçe Aktarma Tamamlandı",
                                JOptionPane.INFORMATION_MESSAGE);

                        importButton.setEnabled(true);
                        progressBar.setVisible(false);
                        dispose();

                        // Tüm oyunlar listesini yenile
                        Window[] windows = Window.getWindows();
                        for (Window window : windows) {
                            if (window instanceof OyunKartListePenceresi) {
                                window.dispose();
                            }
                        }
                        new OyunKartListePenceresi(oyunDao.tumOyunlar(), oyunDao);
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                                "Oyunlar çekilirken bir hata oluştu: " + ex.getMessage(),
                                "Hata",
                                JOptionPane.ERROR_MESSAGE);
                        importButton.setEnabled(true);
                        progressBar.setVisible(false);
                    });
                }
            }).start();
        });

        panel.add(infoLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(importButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(progressBar);

        return panel;
    }
}