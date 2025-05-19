package org.comu;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.gson.JsonArray;
import java.util.ArrayList;
import java.util.List;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SteamApiHelper {
    private static final String API_KEY = "STEAM API-KEY";
    private static final String STEAM_API_BASE_URL = "https://api.steampowered.com";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    // Önbellek mekanizması - aynı oyun için tekrar tekrar API çağrısı yapmamak için
    private static final Map<String, SteamGameInfo> gameInfoCache = new HashMap<>();

    // Oyun adına göre Steam'de arama yapma
    public static SteamGameInfo searchGame(String gameName) {
        // Önbellekte varsa oradan döndür
        if (gameInfoCache.containsKey(gameName)) {
            return gameInfoCache.get(gameName);
        }

        try {
            // Steam Store API'sini kullanarak oyun arama
            String encodedName = java.net.URLEncoder.encode(gameName, "UTF-8");
            String url = "https://store.steampowered.com/api/storesearch/?term=" +
                    encodedName + "&l=turkish&cc=TR";

            System.out.println("Oyun aranıyor: " + gameName);

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.out.println("API yanıt hatası: " + response.code());
                    return null;
                }

                String responseBody = response.body().string();
                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

                if (jsonObject.has("items") && jsonObject.getAsJsonArray("items").size() > 0) {
                    JsonObject gameObject = jsonObject.getAsJsonArray("items").get(0).getAsJsonObject();

                    int appId = Integer.parseInt(gameObject.get("id").getAsString());
                    String name = gameObject.get("name").getAsString();

                    System.out.println("Oyun bulundu: " + name + " (AppID: " + appId + ")");

                    // Oyun detaylarını al
                    SteamGameInfo gameInfo = getGameDetails(appId);

                    // Önbelleğe ekle
                    if (gameInfo != null) {
                        gameInfoCache.put(gameName, gameInfo);
                        return gameInfo;
                    }
                } else {
                    System.out.println("Oyun bulunamadı: " + gameName);
                }
            }
        } catch (Exception e) {
            System.out.println("Hata: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Oyun ID'sine göre detayları alma
    private static SteamGameInfo getGameDetails(int appId) {
        try {
            // Oyun detaylarını al
            String detailsUrl = "https://store.steampowered.com/api/appdetails?appids=" + appId + "&cc=TR&l=turkish";

            Request request = new Request.Builder()
                    .url(detailsUrl)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return null;
                }

                String responseBody = response.body().string();
                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

                if (jsonObject.has(String.valueOf(appId))) {
                    JsonObject appData = jsonObject.getAsJsonObject(String.valueOf(appId));

                    if (appData.get("success").getAsBoolean()) {
                        JsonObject data = appData.getAsJsonObject("data");

                        String name = data.get("name").getAsString();
                        String headerImage = data.has("header_image") ? data.get("header_image").getAsString() : "";
                        int metacritic = data.has("metacritic") ? data.getAsJsonObject("metacritic").get("score").getAsInt() : 0;
                        String releaseDate = data.has("release_date") ? data.getAsJsonObject("release_date").get("date").getAsString() : "";

                        // Anlık oyuncu sayısını al
                        int playerCount = getCurrentPlayerCount(appId);

                        return new SteamGameInfo(appId, name, headerImage, metacritic, releaseDate, playerCount);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Anlık oyuncu sayısını alma
    private static int getCurrentPlayerCount(int appId) {
        try {
            String url = STEAM_API_BASE_URL + "/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?appid=" + appId;

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return 0;
                }

                String responseBody = response.body().string();
                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

                if (jsonObject.has("response") && jsonObject.getAsJsonObject("response").has("player_count")) {
                    return jsonObject.getAsJsonObject("response").get("player_count").getAsInt();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    public static List<Oyun> getPopularSteamGames() {
        List<Oyun> oyunlar = new ArrayList<>();

        try {
            // Steam mağazasından popüler oyunları al
            String url = "https://store.steampowered.com/api/featuredcategories";

            System.out.println("Popüler oyunlar alınıyor...");

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.out.println("API yanıt hatası: " + response.code());
                    return oyunlar;
                }

                String responseBody = response.body().string();
                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

                // Top Sellers kategorisini al
                if (jsonObject.has("top_sellers") && jsonObject.getAsJsonObject("top_sellers").has("items")) {
                    JsonArray itemsArray = jsonObject.getAsJsonObject("top_sellers").getAsJsonArray("items");
                    System.out.println("Bulunan popüler oyun sayısı: " + itemsArray.size());

                    for (JsonElement itemElement : itemsArray) {
                        JsonObject itemObject = itemElement.getAsJsonObject();

                        int appId = itemObject.has("id") ? Integer.parseInt(itemObject.get("id").getAsString()) : 0;
                        String name = itemObject.has("name") ? itemObject.get("name").getAsString() : "";

                        System.out.println("İşleniyor: " + name + " (AppID: " + appId + ")");

                        if (appId > 0 && !name.isEmpty()) {
                            // Oyun detaylarını al
                            SteamGameInfo gameInfo = getGameDetails(appId);

                            if (gameInfo != null) {
                                // Tür bilgisini al
                                // Tür bilgisini al
                                String genre = "Bilinmiyor"; // Varsayılan değer
                                if (gameInfo != null) {
                                    try {
                                        if (gameInfo.getGenres() != null && !gameInfo.getGenres().isEmpty()) {
                                            genre = gameInfo.getGenres().get(0); // İlk türü al
                                        } else if (gameInfo.getGenre() != null && !gameInfo.getGenre().isEmpty()) {
                                            genre = gameInfo.getGenre();
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Tür bilgisi alınamadı: " + e.getMessage());
                                    }
                                }

                                // Yayın tarihinden yıl bilgisini çıkar
                                int year = 2000; // Varsayılan değer
                                if (gameInfo.getReleaseDate() != null && !gameInfo.getReleaseDate().isEmpty()) {
                                    try {
                                        String releaseDate = gameInfo.getReleaseDate();
                                        if (releaseDate.contains(",")) {
                                            String yearStr = releaseDate.split(",")[1].trim();
                                            year = Integer.parseInt(yearStr);
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Tarih ayrıştırma hatası: " + e.getMessage());
                                    }
                                }

                                // Platform bilgisini al (varsayılan olarak PC)
                                List<String> platforms = new ArrayList<>();
                                platforms.add("PC");

                                // Resim URL'sini al
                                String imageUrl = gameInfo.getHeaderImage();
                                System.out.println("Resim URL: " + imageUrl);

                                // Oyun nesnesini oluştur
                                Oyun oyun = new Oyun(
                                        name,
                                        genre,
                                        year,
                                        platforms,
                                        imageUrl // Resim URL'sini doğrudan burada ayarla
                                );

                                oyunlar.add(oyun);
                                System.out.println("Oyun eklendi: " + name);
                            } else {
                                System.out.println("Oyun detayları alınamadı: " + name);
                            }
                        }
                    }
                } else {
                    System.out.println("'top_sellers' kategorisi veya 'items' alanı bulunamadı.");
                }
            }
        } catch (Exception e) {
            System.out.println("Hata: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Toplam eklenen oyun sayısı: " + oyunlar.size());
        return oyunlar;
    }


    // SteamApiHelper sınıfına ekleyin
    public static List<Oyun> getUserSteamLibrary(String steamId) {
        List<Oyun> oyunlar = new ArrayList<>();

        try {
            // Kullanıcının sahip olduğu oyunları al
            String url = STEAM_API_BASE_URL + "/IPlayerService/GetOwnedGames/v0001/?key=" + API_KEY +
                    "&steamid=" + steamId + "&format=json&include_appinfo=1&include_played_free_games=1";

            System.out.println("Steam API URL: " + url);

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                System.out.println("API Yanıt Kodu: " + response.code());

                if (!response.isSuccessful()) {
                    System.out.println("API Yanıt Hatası: " + response.message());
                    return oyunlar;
                }

                String responseBody = response.body().string();
                System.out.println("API Yanıtı: " + responseBody);

                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

                if (jsonObject.has("response")) {
                    JsonObject responseObj = jsonObject.getAsJsonObject("response");
                    System.out.println("Response objesi var: " + responseObj);

                    if (responseObj.has("games")) {
                        JsonArray gamesArray = responseObj.getAsJsonArray("games");
                        System.out.println("Bulunan oyun sayısı: " + gamesArray.size());

                        // Geri kalan kod aynı...
                    } else {
                        System.out.println("'games' alanı bulunamadı. Response içeriği: " + responseObj);
                    }
                } else {
                    System.out.println("'response' alanı bulunamadı. Yanıt içeriği: " + jsonObject);
                }
            }
        } catch (Exception e) {
            System.out.println("Hata: " + e.getMessage());
            e.printStackTrace();
        }

        return oyunlar;
    }


    // Steam oyun bilgilerini tutan sınıf
    public static class SteamGameInfo {
        private int appId;
        private String name;
        private String headerImage;
        private int metacriticScore;
        private String releaseDate;
        private int currentPlayerCount;
        private String genre; // Yeni eklenen alan
        private List<String> genres; // Yeni eklenen alan
        private double rating; // Yeni eklenen alan

        public SteamGameInfo() {
            this.genres = new ArrayList<>();
        }

        public SteamGameInfo(int appId, String name, String headerImage, int metacriticScore,
                             String releaseDate, int currentPlayerCount) {
            this.appId = appId;
            this.name = name;
            this.headerImage = headerImage;
            this.metacriticScore = metacriticScore;
            this.releaseDate = releaseDate;
            this.currentPlayerCount = currentPlayerCount;
            this.genres = new ArrayList<>();
            this.genre = "Bilinmiyor"; // Varsayılan değer
            this.rating = 0.0; // Varsayılan değer
        }

        // Mevcut getter metodları
        public int getAppId() {
            return appId;
        }

        public String getName() {
            return name;
        }

        public String getHeaderImage() {
            return headerImage;
        }

        public int getMetacriticScore() {
            return metacriticScore;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public int getCurrentPlayerCount() {
            return currentPlayerCount;
        }

        // Yeni eklenen getter ve setter metodları
        public String getGenre() {
            if (genre != null && !genre.isEmpty()) {
                return genre;
            } else if (genres != null && !genres.isEmpty()) {
                return genres.get(0);
            }
            return "Bilinmiyor";
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }

        public List<String> getGenres() {
            return genres;
        }

        public void setGenres(List<String> genres) {
            this.genres = genres;
        }

        public void addGenre(String genre) {
            if (this.genres == null) {
                this.genres = new ArrayList<>();
            }
            this.genres.add(genre);
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

        // Setter metodları (ihtiyaç duyarsanız)
        public void setAppId(int appId) {
            this.appId = appId;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setHeaderImage(String headerImage) {
            this.headerImage = headerImage;
        }

        public void setMetacriticScore(int metacriticScore) {
            this.metacriticScore = metacriticScore;
        }

        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        public void setCurrentPlayerCount(int currentPlayerCount) {
            this.currentPlayerCount = currentPlayerCount;
        }
    }
}