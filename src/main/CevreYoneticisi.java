package org.comu.config;

import io.github.cdimascio.dotenv.Dotenv;

public class CevreYoneticisi {
    private static CevreYoneticisi instance;
    private Dotenv dotenv;

    private CevreYoneticisi() {
        try {
            // .env dosyasını yükle
            dotenv = Dotenv.configure().ignoreIfMissing().load();
            System.out.println(".env dosyası yüklendi");
            System.out.println("Veritabanı türü: " + getDbType());
        } catch (Exception e) {
            System.err.println(".env dosyası yüklenemedi: " + e.getMessage());
            System.err.println("Varsayılan ayarlar kullanılacak (MongoDB)");
            e.printStackTrace();
        }
    }

    public static CevreYoneticisi getInstance() {
        if (instance == null) {
            instance = new CevreYoneticisi();
        }
        return instance;
    }

    public String getDbType() {
        return dotenv != null ? dotenv.get("DB_TYPE", "mongo") : "mongo";
    }

    public String getMongoConnection() {
        return dotenv != null ? dotenv.get("MONGO_CONNECTION", "mongodb://localhost:27017") : "mongodb://localhost:27017";
    }

    public String getMongoDatabase() {
        return dotenv != null ? dotenv.get("MONGO_DATABASE", "oyun_kutuphanesi") : "oyun_kutuphanesi";
    }

    public String getMysqlConnection() {
        return dotenv != null ? dotenv.get("MYSQL_CONNECTION", "jdbc:mysql://localhost:3306/oyun_kutuphanesi") : "jdbc:mysql://localhost:3306/oyun_kutuphanesi";
    }

    public String getMysqlUsername() {
        return dotenv != null ? dotenv.get("MYSQL_USERNAME", "root") : "root";
    }

    public String getMysqlPassword() {
        return dotenv != null ? dotenv.get("MYSQL_PASSWORD", "") : "";
    }
}