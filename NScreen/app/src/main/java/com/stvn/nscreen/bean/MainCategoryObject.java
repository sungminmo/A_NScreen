package com.stvn.nscreen.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by swlim on 2015. 11. 11..
 */
public class MainCategoryObject extends RealmObject {

    @PrimaryKey
    private String sSortNo;
    private String sCategoryId;
    private String sCategoryType;
    private String sCategoryTitle;

    public String getsSortNo() {
        return sSortNo;
    }

    public void setsSortNo(String sSortNo) {
        this.sSortNo = sSortNo;
    }

    public String getsCategoryId() {
        return sCategoryId;
    }

    public void setsCategoryId(String sCategoryId) {
        this.sCategoryId = sCategoryId;
    }

    public String getsCategoryType() {
        return sCategoryType;
    }

    public void setsCategoryType(String sCategoryType) {
        this.sCategoryType = sCategoryType;
    }

    public String getsCategoryTitle() {
        return sCategoryTitle;
    }

    public void setsCategoryTitle(String sCategoryTitle) {
        this.sCategoryTitle = sCategoryTitle;
    }
}
