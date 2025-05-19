package org.comu;

import java.util.Collections;
import java.util.List;

public class KullaniciTercihi {
    private List<String> turler;
    private List<String> platformlar;
    private int minYil;

    public KullaniciTercihi(List<String> turler, List<String> platformlar, int minYil) {
        this.turler = (turler != null) ? turler : Collections.emptyList();
        this.platformlar = (platformlar != null) ? platformlar : Collections.emptyList();
        this.minYil = minYil;
    }

    public List<String> getTurler() {
        return turler;
    }

    public List<String> getPlatformlar() {
        return platformlar;
    }

    public int getMinYil() {
        return minYil;
    }
}
