package org.comu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class SwingOyunApp extends JFrame {
    private OyunDao oyunDao;



    private JPanel contentPane;

    // Renk değişkenlerini static olarak tanımlayın
    private static Color primaryColor = new Color(44, 62, 80); // Koyu mavi
    private static Color accentColor = new Color(52, 152, 219); // Parlak mavi
    private static Color textColor = new Color(236, 240, 241); // Beyazımsı
    private static Color backgroundColor = new Color(52, 73, 94); // Koyu gri-mavi
    private static Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
    private static Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private static Font normalFont = new Font("Segoe UI", Font.PLAIN, 14);

    public static void main(String[] args) {
        try {
            // Look and Feel ayarı
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

            // Nimbus özelleştirmeleri
            UIManager.put("control", backgroundColor);
            UIManager.put("text", textColor);
            UIManager.put("nimbusBase", primaryColor);
            UIManager.put("nimbusFocus", accentColor);
            UIManager.put("nimbusLightBackground", backgroundColor.brighter());
            UIManager.put("nimbusSelectionBackground", accentColor);
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SwingOyunApp frame = new SwingOyunApp();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Geri kalan kod aynı kalacak...
    public SwingOyunApp() {
        // MongoDB bağlantısı
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("kutuphane");
        oyunDao = new OyunDao(database);



        setTitle("Oyun Kütüphanesi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 600);
        setLocationRelativeTo(null); // Ekranın ortasında göster

        // Ana içerik paneli
        contentPane = new JPanel();
        contentPane.setBackground(backgroundColor);
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPane.setLayout(new BorderLayout(10, 10));
        setContentPane(contentPane);

        // Başlık Paneli
        JPanel headerPanel = createHeaderPanel();
        contentPane.add(headerPanel, BorderLayout.NORTH);

        // Ana İçerik Paneli
        JPanel mainPanel = createMainPanel();
        contentPane.add(mainPanel, BorderLayout.CENTER);

        // Alt Bilgi Paneli
        JPanel footerPanel = createFooterPanel();
        contentPane.add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Oyun Kütüphanesi");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(textColor);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setOpaque(false);

        JTextField searchField = new JTextField();
        searchField.setFont(normalFont);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JButton searchButton = createStyledButton("Ara");

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(backgroundColor);

        // Sol menü paneli
        JPanel menuPanel = createMenuPanel();
        mainPanel.add(menuPanel, BorderLayout.WEST);

        // Sağ içerik paneli (oyun kartları burada gösterilecek)
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(backgroundColor.brighter());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Hoş Geldiniz! Oyun koleksiyonunuzu yönetmek için sol menüyü kullanın.");
        welcomeLabel.setFont(normalFont);
        welcomeLabel.setForeground(textColor);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(primaryColor);
        menuPanel.setPreferredSize(new Dimension(200, 0));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        // Menü başlığı
        JLabel menuTitle = new JLabel("Menü");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        menuTitle.setForeground(textColor);
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        menuPanel.add(menuTitle);

        // Menü butonları
        JButton tumOyunlarBtn = createMenuButton("Tüm Oyunlar");
        JButton oyunEkleBtn = createMenuButton("Yeni Oyun Ekle");
        JButton oyunOnerBtn = createMenuButton("Oyun Öner");
        JButton steamImportBtn = createMenuButton("Steam Kütüphanesi"); // YENİ BUTON


        menuPanel.add(tumOyunlarBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(oyunEkleBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(oyunOnerBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(steamImportBtn); // YENİ BUTONU EKLE


        // Boşluk ekleyerek butonları yukarıda tutar
        menuPanel.add(Box.createVerticalGlue());

        // Olay dinleyicileri
        tumOyunlarBtn.addActionListener(e -> {
            showOyunListesi();
        });

        oyunEkleBtn.addActionListener(e -> {
            new OyunEklePenceresi(oyunDao);
        });

        oyunOnerBtn.addActionListener(e -> {
            new KullaniciTercihPenceresi(oyunDao);
        });

        // YENİ BUTON İÇİN OLAY DİNLEYİCİSİ
        steamImportBtn.addActionListener(e -> {
            new SteamKutuphanesiPenceresi(oyunDao);
        });

        return menuPanel;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(buttonFont);
        button.setForeground(textColor);
        button.setBackground(primaryColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover efekti
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(accentColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(primaryColor);
            }
        });

        return button;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(buttonFont);
        button.setForeground(textColor);
        button.setBackground(accentColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover efekti
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(accentColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(accentColor);
            }
        });

        return button;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(primaryColor);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        footerPanel.setLayout(new BorderLayout());

        JLabel copyrightLabel = new JLabel("© 2025 Oyun Kütüphanesi - Tüm Hakları Saklıdır");
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        copyrightLabel.setForeground(textColor);
        footerPanel.add(copyrightLabel, BorderLayout.WEST);

        return footerPanel;
    }

    private void showOyunListesi() {
        new OyunKartListePenceresi(oyunDao.tumOyunlar(), oyunDao);
    }
}
