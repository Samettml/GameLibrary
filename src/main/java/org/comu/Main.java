package org.comu;

import com.mongodb.client.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        MongoClient client = MongoClients.create("mongodb://localhost:27017/");
        MongoDatabase db = client.getDatabase("kutuphane");

        OyunDao dao = new OyunDao(db);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("-------- Oyun Kütüphanesi --------");
            System.out.println("1. Oyun Ekle");
            System.out.println("2. Oyunları Listele");
            System.out.println("3. Oyun Güncelle");
            System.out.println("4. Oyun Sil");
            System.out.println("5. Çıkış");
            System.out.print("Seçiminiz: ");
            int secim = Integer.parseInt(scanner.nextLine());

            switch (secim) {
                case 1:
                    System.out.print("İsim: ");
                    String isim = scanner.nextLine();
                    System.out.print("Tür: ");
                    String tur = scanner.nextLine();
                    System.out.print("Yayın Yılı: ");
                    int yil = Integer.parseInt(scanner.nextLine());
                    System.out.print("Platformlar (virgülle ayırın): ");
                    List<String> platformlar = List.of(scanner.nextLine().split(","));
                    Oyun oyun = new Oyun(isim, tur, yil, platformlar);
                    dao.oyunEkle(oyun);
                    System.out.println("Oyun başarıyla eklendi.");
                    break;

                case 2:
                    List<Oyun> oyunlar = dao.tumOyunlar();
                    for (Oyun o : oyunlar) {
                        System.out.println(o);
                    }
                    break;

                case 3:
                    System.out.print("Güncellenecek oyun ID'si: ");
                    String guncelleId = scanner.nextLine();
                    System.out.print("Yeni İsim: ");
                    String yeniIsim = scanner.nextLine();
                    System.out.print("Yeni Tür: ");
                    String yeniTur = scanner.nextLine();
                    System.out.print("Yeni Yıl: ");
                    int yeniYil = Integer.parseInt(scanner.nextLine());
                    System.out.print("Yeni Platformlar: ");
                    List<String> yeniPlatformlar = Arrays.asList(scanner.nextLine().split(","));

                    Oyun guncelOyun = new Oyun(yeniIsim, yeniTur, yeniYil, yeniPlatformlar);
                    dao.oyunGuncelleById(guncelleId, guncelOyun);
                    System.out.println("Oyun güncellendi.");
                    break;

                case 4:
                    System.out.print("Silinecek oyun ID'si: ");
                    String silId = scanner.nextLine();
                    dao.oyunSilById(silId);
                    System.out.println("Oyun silindi.");
                    break;

                case 5:
                    client.close();
                    System.out.println("Uygulamadan çıkılıyor...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Geçersiz seçim!");
            }
        }
    }
}
