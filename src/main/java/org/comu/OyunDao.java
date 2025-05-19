package org.comu;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class OyunDao {

    private MongoCollection<Document> oyunCollection;

    public OyunDao(MongoDatabase database) {
        this.oyunCollection = database.getCollection("oyunlar");
    }

    public void oyunEkle(Oyun oyun) {
        Document doc = new Document("isim", oyun.getIsim())
                .append("tur", oyun.getTur())
                .append("yil", oyun.getYil())
                .append("platformlar", oyun.getPlatformlar())
                .append("resimYolu", oyun.getResimYolu()); // resim yolu eklendi

        oyunCollection.insertOne(doc);
    }

    public List<Oyun> tumOyunlar() {
        List<Oyun> liste = new ArrayList<>();

        for (Document doc : oyunCollection.find()) {
            Oyun oyun = new Oyun();
            oyun.setId(doc.getObjectId("_id"));
            oyun.setIsim(doc.getString("isim"));
            oyun.setTur(doc.getString("tur"));
            oyun.setYil(doc.getInteger("yil"));
            oyun.setPlatformlar((List<String>) doc.get("platformlar"));
            oyun.setResimYolu(doc.getString("resimYolu")); // resim yolu çekildi
            liste.add(oyun);
        }

        return liste;
    }

    public void oyunGuncelleById(String id, Oyun oyun) {
        ObjectId objectId = new ObjectId(id);
        oyunCollection.updateOne(Filters.eq("_id", objectId), Updates.combine(
                Updates.set("isim", oyun.getIsim()),
                Updates.set("tur", oyun.getTur()),
                Updates.set("yil", oyun.getYil()),
                Updates.set("platformlar", oyun.getPlatformlar()),
                Updates.set("resimYolu", oyun.getResimYolu()) // resim yolu güncellendi
        ));
    }

    public void oyunSilById(String id) {
        ObjectId objectId = new ObjectId(id);
        oyunCollection.deleteOne(Filters.eq("_id", objectId));
    }
}
