package com.stvn.nscreen.common;

import java.util.ArrayList;

/**
 * Created by leejunghoon on 15. 11. 6..
 */
public class KeyWordDataObject {

    private String totalCount;
    private String transactionId;
    private ArrayList<String> searchWordList = new ArrayList<String>();

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public ArrayList<String> getSearchWordList() {
        return searchWordList;
    }

    public void setSearchWordList(ArrayList<String> searchWordList) {
        this.searchWordList = searchWordList;
    }
}
