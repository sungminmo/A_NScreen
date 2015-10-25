package com.stvn.nscreen.common;

/**
 * Created by leejunghoon on 15. 10. 9..
 */
public class SearchDataObject {
    /***
     * <channelName>VIKI_HD</channelName>
     <channelInfo>HD</channelInfo>
     <channelLogoImg>http://58.141.255.69:8080/logo/193.png</channelLogoImg>
     <channelProgramID>S318116647</channelProgramID>
     <channelProgramTime>2015-10-28 04:30:00</channelProgramTime>
     <channelProgramTitle>(한)철없는 파출부의 무한욕정</channelProgramTitle>
     <channelProgramSeq>318116647</channelProgramSeq>
     <channelProgramGrade>19세 이상</channelProgramGrade>
     <channelProgramHD>YES</channelProgramHD>
     */
    private String channelId;
    private String channelNumber;
    private String channelName;
    private String channelInfo;
    private String channelLogoImg;
    private String channelProgramID;
    private String channelProgramTime;
    private String channelProgramTitle;
    private String channelProgramSeq;
    private String channelProgramGrade;
    private String channelProgramHD;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(String channelNumber) {
        this.channelNumber = channelNumber;
    }

    public String getChannelLogoImg() {
        return channelLogoImg;
    }

    public void setChannelLogoImg(String channelLogoImg) {
        this.channelLogoImg = channelLogoImg;
    }

    public String getChannelProgramID() {
        return channelProgramID;
    }

    public void setChannelProgramID(String channelProgramID) {
        this.channelProgramID = channelProgramID;
    }

    public String getChannelProgramTime() {
        return channelProgramTime;
    }

    public void setChannelProgramTime(String channelProgramTime) {
        this.channelProgramTime = channelProgramTime;
    }

    public String getChannelProgramTitle() {
        return channelProgramTitle;
    }

    public void setChannelProgramTitle(String channelProgramTitle) {
        this.channelProgramTitle = channelProgramTitle;
    }

    public String getChannelProgramSeq() {
        return channelProgramSeq;
    }

    public void setChannelProgramSeq(String channelProgramSeq) {
        this.channelProgramSeq = channelProgramSeq;
    }

    public String getChannelProgramGrade() {
        return channelProgramGrade;
    }

    public void setChannelProgramGrade(String channelProgramGrade) {
        this.channelProgramGrade = channelProgramGrade;
    }

    public String getChannelProgramHD() {
        return channelProgramHD;
    }

    public void setChannelProgramHD(String channelProgramHD) {
        this.channelProgramHD = channelProgramHD;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelInfo() {
        return channelInfo;
    }

    public void setChannelInfo(String channelInfo) {
        this.channelInfo = channelInfo;
    }

    @Override
    public String toString() {
        return "SearchDataObject{" +
                "channelId='" + channelId + '\'' +
                ", channelNumber='" + channelNumber + '\'' +
                ", channelName='" + channelName + '\'' +
                ", channelInfo='" + channelInfo + '\'' +
                ", channelLogoImg='" + channelLogoImg + '\'' +
                ", channelProgramID='" + channelProgramID + '\'' +
                ", channelProgramTime='" + channelProgramTime + '\'' +
                ", channelProgramTitle='" + channelProgramTitle + '\'' +
                ", channelProgramSeq='" + channelProgramSeq + '\'' +
                ", channelProgramGrade='" + channelProgramGrade + '\'' +
                ", channelProgramHD='" + channelProgramHD + '\'' +
                '}';
    }
}
