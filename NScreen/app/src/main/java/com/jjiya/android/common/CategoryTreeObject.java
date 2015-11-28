package com.jjiya.android.common;

/**
 * Created by swlim on 2015-11-29.
 */
public class CategoryTreeObject {
    private String categoryId;
    private String parentCategoryId;

    public CategoryTreeObject(String cid, String pcid) {
        this.categoryId       = cid;
        this.parentCategoryId = pcid;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(String parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }
}
