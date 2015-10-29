/*
 * (c)Copyright 2011 Widevine Technologies, Inc
 */

package com.widevine.sampleplayer;

public class AssetItem {
    private String assetPath;
    private String imagePath;
    private String title;

    public AssetItem() {
        assetPath = null;
        imagePath = null;
        title = null;
    }

    //test
    public AssetItem(String assetPath, int index) {
        this.assetPath = assetPath;
        this.imagePath = "https://common.worksmobile.com/gateway/image/view?path=/logo/20150117/33183/img_logo_ne_works_204124.png";
        this.title = "test_"+index;
    }

    public AssetItem(String assetPath, String imagePath, String title) {
        this.assetPath = assetPath;
        this.imagePath = imagePath;
        this.title = title;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getTitle() {
        return title;
    }
}
