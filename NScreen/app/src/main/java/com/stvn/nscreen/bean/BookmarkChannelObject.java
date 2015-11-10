package com.stvn.nscreen.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by swlim on 2015. 11. 10..
 */
public class BookmarkChannelObject extends RealmObject {
    @PrimaryKey
    private String sChannelId;
    private String sChannelNumber;
    private String sChannelName;

    public String getsChannelId() {
        return sChannelId;
    }

    public void setsChannelId(String sChannelId) {
        this.sChannelId = sChannelId;
    }

    public String getsChannelNumber() {
        return sChannelNumber;
    }

    public void setsChannelNumber(String sChannelNumber) {
        this.sChannelNumber = sChannelNumber;
    }

    public String getsChannelName() {
        return sChannelName;
    }

    public void setsChannelName(String sChannelName) {
        this.sChannelName = sChannelName;
    }
}
