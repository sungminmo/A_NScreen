package com.jjiya.android.common;

/**
 * Created by swlim on 2015. 9. 11..
 */
public class ListViewDataObject {
    public int    iPosition;    // 어레이의 순서 번호
    public int    iKey;         // 데이터 고유 키
    public String sJson;        // 실제 데이터 JSON

    public long remainMinute; // 구매목록 정렬을 위한 남은 기간 분으로 환산한 변수
    public long puchaseSecond; // 구매목록 정렬을 위한 구매일자 조 환산한 변수
    public ListViewDataObject(int iPosition, int iKey, String sJson) {
        this.iPosition = iPosition;
        this.iKey      = iKey;
        this.sJson     = sJson;
    }


}
