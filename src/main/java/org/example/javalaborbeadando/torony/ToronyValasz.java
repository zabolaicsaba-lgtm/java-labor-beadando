package org.example.javalaborbeadando.torony;

public record ToronyValasz(Integer id, Integer darab, Integer teljesitmeny, Integer kezdev,
                           Integer helyszinId, String helyszin, Integer megyeId, String megye, String regio) {

    public static ToronyValasz from(Torony torony) {
        var helyszin = torony.getHelyszin();
        var megye = helyszin.getMegye();
        return new ToronyValasz(torony.getId(), torony.getDarab(), torony.getTeljesitmeny(), torony.getKezdev(),
                helyszin.getId(), helyszin.getNev(), megye.getId(), megye.getNev(), megye.getRegio());
    }
}
