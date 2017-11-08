package com.parra.lfelipe.recyclerview;

/**
 * Created by Manuela on 7/11/2017.
 */

public class Checkin {
    private String uid;
    private String sid;

    public Checkin(String uid, String sid) {
        this.uid = uid;
        this.sid = sid;
    }

    public Checkin(){}

    public String getUid() {
        return uid;
    }

    public String getSid() {
        return sid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
