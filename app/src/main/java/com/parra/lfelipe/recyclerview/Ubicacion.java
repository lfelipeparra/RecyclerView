package com.parra.lfelipe.recyclerview;

/**
 * Created by Mango on 3/12/2017.
 */

public class Ubicacion {
    private String uid;
    private String lid;
    private String sid;

    public Ubicacion(String uid, String lid, String sid) {
        this.uid = uid;
        this.lid = lid;
        this.sid = sid;
    }

    public Ubicacion() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
