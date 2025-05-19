package org.comu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SearchFilterPanel extends JPanel {
    private Color primaryColor = new Color(44, 62, 80);
    private Color accentColor = new Color(52, 152, 219);
    private Color textColor = new Color(236, 240, 241);
    private Color backgroundColor = new Color(52, 73, 94);
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 16);
    private Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font normalFont = new Font("Segoe UI", Font.PLAIN, 14);

    private JTextField searchField;
    private JComboBox<String> genreFilter;
    private JComboBox<String> platformFilter;
    private JComboBox<String> yearFilter;
    private JComboBox<String> sortByCombo;
    private JRadioButton ascendingRadio;
    private JRadioButton descendingRadio;

    private OyunDao oyunDao;
    private List<Oyun> allGames;
    private FilterChangeListener filterChangeListener;

    private JPanel togglePanel; // Toggle edilecek panel
    private JButton toggleButton;

    public interface FilterChangeListener {
        void onFilterChanged(List<Oyun> filteredGames);
    }

    public SearchFilterPanel(OyunDao oyunDao, FilterChangeListener listener) {
        this.oyunDao = oyunDao;
        this.filterChangeListener = listener;
        this.allGames = oyunDao.tumOyunlar();

        setLayout(new BorderLayout(5, 5));
        setBackground(backgroundColor);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setPreferredSize(new Dimension(300, 0)); // Sola eklenecekse

        // Arama Paneli
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        // Toggle Panel (Filtre + Sıralama)
        togglePanel = new JPanel();
        togglePanel.setLayout(new BoxLayout(togglePanel, BoxLayout.Y_AXIS));
        togglePanel.setBackground(backgroundColor);
        togglePanel.add(createFilterPanel());
        togglePanel.add(Box.createVerticalStrut(10));
        togglePanel.add(createSortPanel());
        add(togglePanel, BorderLayout.CENTER);

        // Toggle Button
        toggleButton = new JButton("▼ Filtreleri Gizle");
        toggleButton.setFont(normalFont);
        toggleButton.setBackground(accentColor);
        toggleButton.setForeground(textColor);
        toggleButton.setFocusPainted(false);
        toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toggleButton.addActionListener(e -> toggleFilterPanel());
        add(toggleButton, BorderLayout.SOUTH);

        applyFilters();
    }

    private void toggleFilterPanel() {
        togglePanel.setVisible(!togglePanel.isVisible());
        toggleButton.setText(togglePanel.isVisible() ? "▼ Filtreleri Gizle" : "▲ Filtreleri Göster");
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBackground(backgroundColor);
        panel.setBorder(new EmptyBorder(0, 0, 5, 0));

        JLabel searchLabel = new JLabel("Ara:");
        searchLabel.setFont(labelFont);
        searchLabel.setForeground(textColor);

        searchField = new JTextField();
        searchField.setFont(normalFont);
        searchField.setBackground(backgroundColor.brighter());
        searchField.setForeground(textColor);
        searchField.setCaretColor(textColor);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)));

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        JButton clearButton = new JButton("Temizle");
        clearButton.setFont(normalFont);
        clearButton.setBackground(accentColor);
        clearButton.setForeground(textColor);
        clearButton.setBorderPainted(false);
        clearButton.setFocusPainted(false);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.addActionListener(e -> {
            searchField.setText("");
            genreFilter.setSelectedIndex(0);
            platformFilter.setSelectedIndex(0);
            yearFilter.setSelectedIndex(0);
            sortByCombo.setSelectedIndex(0);
            ascendingRadio.setSelected(true);
            applyFilters();
        });

        panel.add(searchLabel, BorderLayout.WEST);
        panel.add(searchField, BorderLayout.CENTER);
        panel.add(clearButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(accentColor),
                "Filtreler", 0, 0, titleFont, textColor));

        JLabel genreLabel = new JLabel("Tür:");
        genreLabel.setFont(labelFont);
        genreLabel.setForeground(textColor);

        List<String> genres = getUniqueGenres();
        genres.add(0, "Tüm Türler");
        genreFilter = new JComboBox<>(genres.toArray(new String[0]));
        genreFilter.setFont(normalFont);
        genreFilter.setBackground(backgroundColor.brighter());
        genreFilter.setForeground(textColor);
        genreFilter.addActionListener(e -> applyFilters());

        JLabel platformLabel = new JLabel("Platform:");
        platformLabel.setFont(labelFont);
        platformLabel.setForeground(textColor);

        List<String> platforms = getUniquePlatforms();
        platforms.add(0, "Tüm Platformlar");
        platformFilter = new JComboBox<>(platforms.toArray(new String[0]));
        platformFilter.setFont(normalFont);
        platformFilter.setBackground(backgroundColor.brighter());
        platformFilter.setForeground(textColor);
        platformFilter.addActionListener(e -> applyFilters());

        JLabel yearLabel = new JLabel("Yıl:");
        yearLabel.setFont(labelFont);
        yearLabel.setForeground(textColor);

        List<String> years = getUniqueYears();
        years.add(0, "Tüm Yıllar");
        yearFilter = new JComboBox<>(years.toArray(new String[0]));
        yearFilter.setFont(normalFont);
        yearFilter.setBackground(backgroundColor.brighter());
        yearFilter.setForeground(textColor);
        yearFilter.addActionListener(e -> applyFilters());

        panel.add(genreLabel); panel.add(genreFilter);
        panel.add(platformLabel); panel.add(platformFilter);
        panel.add(yearLabel); panel.add(yearFilter);

        return panel;
    }

    private JPanel createSortPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(accentColor),
                "Sıralama", 0, 0, titleFont, textColor));

        JLabel sortByLabel = new JLabel("Sırala:");
        sortByLabel.setFont(labelFont);
        sortByLabel.setForeground(textColor);

        String[] sortOptions = {"İsim", "Tür", "Yıl"};
        sortByCombo = new JComboBox<>(sortOptions);
        sortByCombo.setFont(normalFont);
        sortByCombo.setBackground(backgroundColor.brighter());
        sortByCombo.setForeground(textColor);
        sortByCombo.addActionListener(e -> applyFilters());

        JLabel directionLabel = new JLabel("Yön:");
        directionLabel.setFont(labelFont);
        directionLabel.setForeground(textColor);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.setOpaque(false);

        ascendingRadio = new JRadioButton("Artan", true);
        ascendingRadio.setFont(normalFont);
        ascendingRadio.setForeground(textColor);
        ascendingRadio.setBackground(backgroundColor);
        ascendingRadio.addActionListener(e -> applyFilters());

        descendingRadio = new JRadioButton("Azalan");
        descendingRadio.setFont(normalFont);
        descendingRadio.setForeground(textColor);
        descendingRadio.setBackground(backgroundColor);
        descendingRadio.addActionListener(e -> applyFilters());

        ButtonGroup directionGroup = new ButtonGroup();
        directionGroup.add(ascendingRadio);
        directionGroup.add(descendingRadio);

        radioPanel.add(ascendingRadio);
        radioPanel.add(descendingRadio);

        panel.add(sortByLabel); panel.add(sortByCombo);
        panel.add(directionLabel); panel.add(radioPanel);

        return panel;
    }

    private List<String> getUniqueGenres() {
        return allGames.stream().map(Oyun::getTur).distinct().sorted().collect(Collectors.toList());
    }

    private List<String> getUniquePlatforms() {
        List<String> platforms = new ArrayList<>();
        for (Oyun oyun : allGames) {
            for (String platform : oyun.getPlatformlar()) {
                String trimmed = platform.trim();
                if (!platforms.contains(trimmed) && !trimmed.isEmpty()) {
                    platforms.add(trimmed);
                }
            }
        }
        return platforms.stream().sorted().collect(Collectors.toList());
    }

    private List<String> getUniqueYears() {
        return allGames.stream().map(o -> String.valueOf(o.getYil())).distinct().sorted().collect(Collectors.toList());
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String selectedGenre = (String) genreFilter.getSelectedItem();
        String selectedPlatform = (String) platformFilter.getSelectedItem();
        String selectedYear = (String) yearFilter.getSelectedItem();

        List<Oyun> filteredGames = allGames.stream()
                .filter(createSearchPredicate(searchText))
                .filter(createGenrePredicate(selectedGenre))
                .filter(createPlatformPredicate(selectedPlatform))
                .filter(createYearPredicate(selectedYear))
                .collect(Collectors.toList());

        String sortBy = (String) sortByCombo.getSelectedItem();
        boolean ascending = ascendingRadio.isSelected();
        sortGames(filteredGames, sortBy, ascending);

        if (filterChangeListener != null) {
            filterChangeListener.onFilterChanged(filteredGames);
        }
    }

    private Predicate<Oyun> createSearchPredicate(String searchText) {
        return oyun -> searchText.isEmpty()
                || oyun.getIsim().toLowerCase().contains(searchText)
                || oyun.getTur().toLowerCase().contains(searchText)
                || oyun.getPlatformlar().stream().anyMatch(p -> p.toLowerCase().contains(searchText));
    }

    private Predicate<Oyun> createGenrePredicate(String selectedGenre) {
        return selectedGenre == null || selectedGenre.equals("Tüm Türler")
                ? oyun -> true : oyun -> oyun.getTur().equals(selectedGenre);
    }

    private Predicate<Oyun> createPlatformPredicate(String selectedPlatform) {
        return selectedPlatform == null || selectedPlatform.equals("Tüm Platformlar")
                ? oyun -> true : oyun -> oyun.getPlatformlar().stream().anyMatch(p -> p.trim().equals(selectedPlatform));
    }

    private Predicate<Oyun> createYearPredicate(String selectedYear) {
        return selectedYear == null || selectedYear.equals("Tüm Yıllar")
                ? oyun -> true : oyun -> oyun.getYil() == Integer.parseInt(selectedYear);
    }

    private void sortGames(List<Oyun> games, String sortBy, boolean ascending) {
        games.sort((o1, o2) -> {
            int result;
            if (sortBy.equals("İsim")) {
                result = o1.getIsim().compareToIgnoreCase(o2.getIsim());
            } else if (sortBy.equals("Tür")) {
                result = o1.getTur().compareToIgnoreCase(o2.getTur());
            } else {
                result = Integer.compare(o1.getYil(), o2.getYil());
            }
            return ascending ? result : -result;
        });
    }

    public void refreshData() {
        this.allGames = oyunDao.tumOyunlar();
        updateFilterOptions();
        applyFilters();
    }

    private void updateFilterOptions() {
        updateComboBox(genreFilter, getUniqueGenres(), "Tüm Türler");
        updateComboBox(platformFilter, getUniquePlatforms(), "Tüm Platformlar");
        updateComboBox(yearFilter, getUniqueYears(), "Tüm Yıllar");
    }

    private void updateComboBox(JComboBox<String> comboBox, List<String> items, String defaultOption) {
        String selected = (String) comboBox.getSelectedItem();
        comboBox.removeAllItems();
        comboBox.addItem(defaultOption);
        items.forEach(comboBox::addItem);
        comboBox.setSelectedItem(selected);
    }
}
