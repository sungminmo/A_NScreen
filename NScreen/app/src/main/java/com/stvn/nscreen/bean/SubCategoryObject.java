package com.stvn.nscreen.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by swlim on 2015. 11. 11..
 */
public class SubCategoryObject extends RealmObject {

    @PrimaryKey
    private String sCategoryId;
    private String sAdultCategory;
    private String sCategoryName;
    private String sLeaf;
    private String sParentCategoryId;
    private String sViewerType;

    public String getsCategoryId() {
        return sCategoryId;
    }

    public void setsCategoryId(String sCategoryId) {
        this.sCategoryId = sCategoryId;
    }

    public String getsAdultCategory() {
        return sAdultCategory;
    }

    public void setsAdultCategory(String sAdultCategory) {
        this.sAdultCategory = sAdultCategory;
    }

    public String getsCategoryName() {
        return sCategoryName;
    }

    public void setsCategoryName(String sCategoryName) {
        this.sCategoryName = sCategoryName;
    }

    public String getsLeaf() {
        return sLeaf;
    }

    public void setsLeaf(String sLeaf) {
        this.sLeaf = sLeaf;
    }

    public String getsParentCategoryId() {
        return sParentCategoryId;
    }

    public void setsParentCategoryId(String sParentCategoryId) {
        this.sParentCategoryId = sParentCategoryId;
    }

    public String getsViewerType() {
        return sViewerType;
    }

    public void setsViewerType(String sViewerType) {
        this.sViewerType = sViewerType;
    }
}
