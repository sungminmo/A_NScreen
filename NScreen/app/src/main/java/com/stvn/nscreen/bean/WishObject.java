package com.stvn.nscreen.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by swlim on 2015. 11. 6..
 */
public class WishObject  extends RealmObject {

    @PrimaryKey
    private String sAssetId;
    private String sPhoneNumber;
    private String sUserId;

    public String getsAssetId() {
        return sAssetId;
    }

    public void setsAssetId(String sAssetId) {
        this.sAssetId = sAssetId;
    }

    public String getsPhoneNumber() {
        return sPhoneNumber;
    }

    public void setsPhoneNumber(String sPhoneNumber) {
        this.sPhoneNumber = sPhoneNumber;
    }

    public String getsUserId() {
        return sUserId;
    }

    public void setsUserId(String sUserId) {
        this.sUserId = sUserId;
    }
}
