package com.stvn.nscreen.bean;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by swlim on 2015. 11. 10..
 */
public class WatchVodObject extends RealmObject {

    @PrimaryKey
    private int    iSeq;
    private Date   dDate;
    private String sAssetId;
    private String sTitle;

    public int getiSeq() {
        return iSeq;
    }

    public void setiSeq(int iSeq) {
        this.iSeq = iSeq;
    }

    public Date getdDate() {
        return dDate;
    }

    public void setdDate(Date dDate) {
        this.dDate = dDate;
    }

    public String getsAssetId() {
        return sAssetId;
    }

    public void setsAssetId(String sAssetId) {
        this.sAssetId = sAssetId;
    }

    public String getsTitle() {
        return sTitle;
    }

    public void setsTitle(String sTitle) {
        this.sTitle = sTitle;
    }
}
