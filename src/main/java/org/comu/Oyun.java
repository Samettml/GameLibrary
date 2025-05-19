package org.comu;

import org.bson.types.ObjectId;
import java.util.List;

public class Oyun {
    private ObjectId id;
    private String isim;
    private String tur;
    private int yil;
    private List<String> platformlar;
    private String resimYolu;

    public Oyun() {}


    public Oyun(String isim, String tur, int yil, List<String> platformlar, String resimYolu) {
        this.isim = isim;
        this.tur = tur;
        this.yil = yil;
        this.platformlar = platformlar;
        this.resimYolu = resimYolu;
    }


    public Oyun(String isim, String tur, int yil, List<String> platformlar) {
        this(isim, tur, yil, platformlar, null);
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getTur() {
        return tur;
    }

    public void setTur(String tur) {
        this.tur = tur;
    }

    public int getYil() {
        return yil;
    }

    public void setYil(int yil) {
        this.yil = yil;
    }

    public List<String> getPlatformlar() {
        return platformlar;
    }

    public void setPlatformlar(List<String> platformlar) {
        this.platformlar = platformlar;
    }

    public String getResimYolu() {
        return resimYolu;
    }

    public void setResimYolu(String resimYolu) {
        this.resimYolu = resimYolu;
    }

    @Override
    public String toString() {
        return "ID: " + (id != null ? id.toHexString() : "") +
                "\nİsim: " + isim +
                "\nTür: " + tur +
                "\nYıl: " + yil +
                "\nPlatformlar: " + platformlar +
                (resimYolu != null ? "\nResim Yolu: " + resimYolu : "") +
                "\n";
    }
}
