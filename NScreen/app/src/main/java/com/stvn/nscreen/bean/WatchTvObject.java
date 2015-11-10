package com.stvn.nscreen.bean;



import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by swlim on 2015. 11. 10..
 */
public class WatchTvObject extends RealmObject {

    @PrimaryKey
    private int    iSeq;

    private String sScheduleSeq;
    private String sBroadcastingDate;
    private String sProgramId;
    private String sSeriesId;
    private String sProgramTitle;
    private String sProgramContent;
    private String sProgramBroadcastingStartTime;
    private String sProgramBroadcastingEndTime;
    private String sProgramHD;
    private String sProgramGrade;
    private String sProgramPVR;

//    public int getNextKey() {
//        return realm.where(WatchTvObject.class).maximumInt("iSeq") + 1;
//    }


    public int getiSeq() {
        return iSeq;
    }

    public void setiSeq(int iSeq) {
        this.iSeq = iSeq;
    }

    public String getsScheduleSeq() {
        return sScheduleSeq;
    }

    public void setsScheduleSeq(String sScheduleSeq) {
        this.sScheduleSeq = sScheduleSeq;
    }

    public String getsBroadcastingDate() {
        return sBroadcastingDate;
    }

    public void setsBroadcastingDate(String sBroadcastingDate) {
        this.sBroadcastingDate = sBroadcastingDate;
    }

    public String getsProgramId() {
        return sProgramId;
    }

    public void setsProgramId(String sProgramId) {
        this.sProgramId = sProgramId;
    }

    public String getsSeriesId() {
        return sSeriesId;
    }

    public void setsSeriesId(String sSeriesId) {
        this.sSeriesId = sSeriesId;
    }

    public String getsProgramTitle() {
        return sProgramTitle;
    }

    public void setsProgramTitle(String sProgramTitle) {
        this.sProgramTitle = sProgramTitle;
    }

    public String getsProgramContent() {
        return sProgramContent;
    }

    public void setsProgramContent(String sProgramContent) {
        this.sProgramContent = sProgramContent;
    }

    public String getsProgramBroadcastingStartTime() {
        return sProgramBroadcastingStartTime;
    }

    public void setsProgramBroadcastingStartTime(String sProgramBroadcastingStartTime) {
        this.sProgramBroadcastingStartTime = sProgramBroadcastingStartTime;
    }

    public String getsProgramBroadcastingEndTime() {
        return sProgramBroadcastingEndTime;
    }

    public void setsProgramBroadcastingEndTime(String sProgramBroadcastingEndTime) {
        this.sProgramBroadcastingEndTime = sProgramBroadcastingEndTime;
    }

    public String getsProgramHD() {
        return sProgramHD;
    }

    public void setsProgramHD(String sProgramHD) {
        this.sProgramHD = sProgramHD;
    }

    public String getsProgramGrade() {
        return sProgramGrade;
    }

    public void setsProgramGrade(String sProgramGrade) {
        this.sProgramGrade = sProgramGrade;
    }

    public String getsProgramPVR() {
        return sProgramPVR;
    }

    public void setsProgramPVR(String sProgramPVR) {
        this.sProgramPVR = sProgramPVR;
    }
}
