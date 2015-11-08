package com.stvn.nscreen.common;

import java.util.ArrayList;

/**
 * Created by leejunghoon on 15. 10. 9..
 */

public class SearchVodDataObject {

    private String resultCode;
    private SearchResultList searchResultList;
    private String version;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public SearchResultList getSearchResultList() {
        return searchResultList;
    }

    public void setSearchResultList(SearchResultList searchResultList) {
        this.searchResultList = searchResultList;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public class ContentGroup{
        private String assetNew;
        private String imageFileName;
        private String runningTime;
        private String smallImageFileName;
        private String primaryAssetId;
        private String title;
        private String synopsis;
        private String assetBundle;
        private String likedCount;
        private String mobilePublicationRight;
        private String starring;
        private String assetFree;
        private String episodePeerExistence;
        private String assetSeriesLink;
        private String isLiked;
        private String production;
        private String UHDAssetCount;
        private String isFavorite;
        private String director;
        private String SDAssetCount;
        private String HDAssetCount;
        private String genre;
        private String promotionSticker;
        private String reviewRating;
        private String assetHot;
        private String categoryId;
        private String contentGroupId;

        public String getAssetNew() {
            return assetNew;
        }

        public void setAssetNew(String assetNew) {
            this.assetNew = assetNew;
        }

        public String getImageFileName() {
            return imageFileName;
        }

        public void setImageFileName(String imageFileName) {
            this.imageFileName = imageFileName;
        }

        public String getRunningTime() {
            return runningTime;
        }

        public void setRunningTime(String runningTime) {
            this.runningTime = runningTime;
        }

        public String getSmallImageFileName() {
            return smallImageFileName;
        }

        public void setSmallImageFileName(String smallImageFileName) {
            this.smallImageFileName = smallImageFileName;
        }

        public String getPrimaryAssetId() {
            return primaryAssetId;
        }

        public void setPrimaryAssetId(String primaryAssetId) {
            this.primaryAssetId = primaryAssetId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSynopsis() {
            return synopsis;
        }

        public void setSynopsis(String synopsis) {
            this.synopsis = synopsis;
        }

        public String getAssetBundle() {
            return assetBundle;
        }

        public void setAssetBundle(String assetBundle) {
            this.assetBundle = assetBundle;
        }

        public String getLikedCount() {
            return likedCount;
        }

        public void setLikedCount(String likedCount) {
            this.likedCount = likedCount;
        }

        public String getMobilePublicationRight() {
            return mobilePublicationRight;
        }

        public void setMobilePublicationRight(String mobilePublicationRight) {
            this.mobilePublicationRight = mobilePublicationRight;
        }

        public String getStarring() {
            return starring;
        }

        public void setStarring(String starring) {
            this.starring = starring;
        }

        public String getAssetFree() {
            return assetFree;
        }

        public void setAssetFree(String assetFree) {
            this.assetFree = assetFree;
        }

        public String getEpisodePeerExistence() {
            return episodePeerExistence;
        }

        public void setEpisodePeerExistence(String episodePeerExistence) {
            this.episodePeerExistence = episodePeerExistence;
        }

        public String getAssetSeriesLink() {
            return assetSeriesLink;
        }

        public void setAssetSeriesLink(String assetSeriesLink) {
            this.assetSeriesLink = assetSeriesLink;
        }

        public String getIsLiked() {
            return isLiked;
        }

        public void setIsLiked(String isLiked) {
            this.isLiked = isLiked;
        }

        public String getProduction() {
            return production;
        }

        public void setProduction(String production) {
            this.production = production;
        }

        public String getUHDAssetCount() {
            return UHDAssetCount;
        }

        public void setUHDAssetCount(String UHDAssetCount) {
            this.UHDAssetCount = UHDAssetCount;
        }

        public String getIsFavorite() {
            return isFavorite;
        }

        public void setIsFavorite(String isFavorite) {
            this.isFavorite = isFavorite;
        }

        public String getDirector() {
            return director;
        }

        public void setDirector(String director) {
            this.director = director;
        }

        public String getSDAssetCount() {
            return SDAssetCount;
        }

        public void setSDAssetCount(String SDAssetCount) {
            this.SDAssetCount = SDAssetCount;
        }

        public String getHDAssetCount() {
            return HDAssetCount;
        }

        public void setHDAssetCount(String HDAssetCount) {
            this.HDAssetCount = HDAssetCount;
        }

        public String getGenre() {
            return genre;
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }

        public String getPromotionSticker() {
            return promotionSticker;
        }

        public void setPromotionSticker(String promotionSticker) {
            this.promotionSticker = promotionSticker;
        }

        public String getReviewRating() {
            return reviewRating;
        }

        public void setReviewRating(String reviewRating) {
            this.reviewRating = reviewRating;
        }

        public String getAssetHot() {
            return assetHot;
        }

        public void setAssetHot(String assetHot) {
            this.assetHot = assetHot;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getContentGroupId() {
            return contentGroupId;
        }

        public void setContentGroupId(String contentGroupId) {
            this.contentGroupId = contentGroupId;
        }
    }
    public class ContentGroupList{
        private ArrayList<ContentGroup> contentGroup = new ArrayList<ContentGroup>();
        private String totalCount;
        private String totalPage;
        private String searchCategory;

        public ArrayList<ContentGroup> getContentGroup() {
            return contentGroup;
        }

        public void setContentGroup(ArrayList<ContentGroup> contentGroup) {
            this.contentGroup = contentGroup;
        }

        public String getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(String totalCount) {
            this.totalCount = totalCount;
        }

        public String getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(String totalPage) {
            this.totalPage = totalPage;
        }

        public String getSearchCategory() {
            return searchCategory;
        }

        public void setSearchCategory(String searchCategory) {
            this.searchCategory = searchCategory;
        }
    }
    public class SearchResult{
        private ContentGroupList contentGroupList;

        public ContentGroupList getContentGroupList() {
            return contentGroupList;
        }

        public void setContentGroupList(ContentGroupList contentGroupList) {
            this.contentGroupList = contentGroupList;
        }
    }
    public class SearchResultList
    {
        private SearchResult searchResult;

        public SearchResult getSearchResult() {
            return searchResult;
        }

        public void setSearchResult(SearchResult searchResult) {
            this.searchResult = searchResult;
        }
    }
}
