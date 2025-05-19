package org.comu.db;

import org.comu.config.CevreYoneticisi;

public class VeriTabaniFabrikasi {
    public static IVeriTabaniSaglayici getSaglayici() {
        CevreYoneticisi cevre = CevreYoneticisi.getInstance();
        String dbType = cevre.getDbType().toLowerCase();

        System.out.println("Veritabanı türü: " + dbType);

        if ("mongo".equals(dbType) || "mongodb".equals(dbType)) {
            String connection = cevre.getMongoConnection();
            String database = cevre.getMongoDatabase();
            System.out.println("MongoDB bağlantısı kullanılıyor: " + connection);
            return new MongoDBSaglayici(connection, database);
        } else if ("mysql".equals(dbType)) {
            String connection = cevre.getMysqlConnection();
            String username = cevre.getMysqlUsername();
            String password = cevre.getMysqlPassword();
            System.out.println("MySQL bağlantısı kullanılıyor: " + connection);
            return new MySQLSaglayici(connection, username, password);
        } else {
            System.out.println("Desteklenmeyen veritabanı türü: " + dbType + ", MongoDB kullanılacak");
            String connection = cevre.getMongoConnection();
            String database = cevre.getMongoDatabase();
            return new MongoDBSaglayici(connection, database);
        }
    }
}