package com.jjiya.android.common;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.stvn.nscreen.R;
import com.stvn.nscreen.util.CMAlertUtil;

import java.text.DecimalFormat;

/**
 * Created by swlim on 2015. 10. 21..
 */
public class UiUtil {

    /**
     * 별점이미지 셋팅하기.
     *
     * @param rating
     * @param starImage1
     * @param starImage2
     * @param starImage3
     * @param starImage4
     * @param starImage5
     */
    public static void setStarRating(Float rating, ImageView starImage1, ImageView starImage2, ImageView starImage3, ImageView starImage4, ImageView starImage5) {
        if ( rating == 0 ) {
            starImage1.setImageResource(R.mipmap.series_star_off);
            starImage2.setImageResource(R.mipmap.series_star_off);
            starImage3.setImageResource(R.mipmap.series_star_off);
            starImage4.setImageResource(R.mipmap.series_star_off);
            starImage5.setImageResource(R.mipmap.series_star_off);
        } else if ( rating > 0 && rating <= 0.5 ) {
            starImage1.setImageResource(R.mipmap.series_star_on_half);
            starImage2.setImageResource(R.mipmap.series_star_off);
            starImage3.setImageResource(R.mipmap.series_star_off);
            starImage4.setImageResource(R.mipmap.series_star_off);
            starImage5.setImageResource(R.mipmap.series_star_off);
        } else if ( rating > 0.5 && rating <= 1.0 ) {
            starImage1.setImageResource(R.mipmap.series_star_on);
            starImage2.setImageResource(R.mipmap.series_star_off);
            starImage3.setImageResource(R.mipmap.series_star_off);
            starImage4.setImageResource(R.mipmap.series_star_off);
            starImage5.setImageResource(R.mipmap.series_star_off);
        } else if ( rating > 1.0 && rating <= 1.5 ) {
            starImage1.setImageResource(R.mipmap.series_star_on);
            starImage2.setImageResource(R.mipmap.series_star_on_half);
            starImage3.setImageResource(R.mipmap.series_star_off);
            starImage4.setImageResource(R.mipmap.series_star_off);
            starImage5.setImageResource(R.mipmap.series_star_off);
        } else if ( rating > 1.5 && rating <= 2.0 ) {
            starImage1.setImageResource(R.mipmap.series_star_on);
            starImage2.setImageResource(R.mipmap.series_star_on);
            starImage3.setImageResource(R.mipmap.series_star_off);
            starImage4.setImageResource(R.mipmap.series_star_off);
            starImage5.setImageResource(R.mipmap.series_star_off);
        } else if ( rating > 2.0 && rating <= 2.5 ) {
            starImage1.setImageResource(R.mipmap.series_star_on);
            starImage2.setImageResource(R.mipmap.series_star_on);
            starImage3.setImageResource(R.mipmap.series_star_on_half);
            starImage4.setImageResource(R.mipmap.series_star_off);
            starImage5.setImageResource(R.mipmap.series_star_off);
        } else if ( rating > 2.5 && rating <= 3.0 ) {
            starImage1.setImageResource(R.mipmap.series_star_on);
            starImage2.setImageResource(R.mipmap.series_star_on);
            starImage3.setImageResource(R.mipmap.series_star_on);
            starImage4.setImageResource(R.mipmap.series_star_off);
            starImage5.setImageResource(R.mipmap.series_star_off);
        } else if ( rating > 3.0 && rating <= 3.5 ) {
            starImage1.setImageResource(R.mipmap.series_star_on);
            starImage2.setImageResource(R.mipmap.series_star_on);
            starImage3.setImageResource(R.mipmap.series_star_on);
            starImage4.setImageResource(R.mipmap.series_star_on_half);
            starImage5.setImageResource(R.mipmap.series_star_off);
        } else if ( rating > 3.5 && rating <= 4.0 ) {
            starImage1.setImageResource(R.mipmap.series_star_on);
            starImage2.setImageResource(R.mipmap.series_star_on);
            starImage3.setImageResource(R.mipmap.series_star_on);
            starImage4.setImageResource(R.mipmap.series_star_on);
            starImage5.setImageResource(R.mipmap.series_star_off);
        } else if ( rating > 4.0 && rating < 4.5 ) {
            starImage1.setImageResource(R.mipmap.series_star_on);
            starImage2.setImageResource(R.mipmap.series_star_on);
            starImage3.setImageResource(R.mipmap.series_star_on);
            starImage4.setImageResource(R.mipmap.series_star_on);
            starImage5.setImageResource(R.mipmap.series_star_on_half);
        } else if ( rating > 4.5 ) {
            starImage1.setImageResource(R.mipmap.series_star_on);
            starImage2.setImageResource(R.mipmap.series_star_on);
            starImage3.setImageResource(R.mipmap.series_star_on);
            starImage4.setImageResource(R.mipmap.series_star_on);
            starImage5.setImageResource(R.mipmap.series_star_on);
        }
    }

    public static void setIndicatorImage(int iCurrPage, int iTotalPage,
                                         ImageView image1, ImageView image2, ImageView image3, ImageView image4, ImageView image5,
                                         ImageView image6, ImageView image7, ImageView image8, ImageView image9, ImageView image10) {
        image1.setVisibility(View.GONE);
        image2.setVisibility(View.GONE);
        image3.setVisibility(View.GONE);
        image4.setVisibility(View.GONE);
        image5.setVisibility(View.GONE);
        image1.setImageResource(R.mipmap.indicator_off);
        image2.setImageResource(R.mipmap.indicator_off);
        image3.setImageResource(R.mipmap.indicator_off);
        image4.setImageResource(R.mipmap.indicator_off);
        image5.setImageResource(R.mipmap.indicator_off);

        if (image6 != null) {
            image6.setVisibility(View.GONE);
            image6.setImageResource(R.mipmap.indicator_off);
        }
        if (image7 != null) {
            image7.setVisibility(View.GONE);
            image7.setImageResource(R.mipmap.indicator_off);
        }
        if (image8 != null) {
            image8.setVisibility(View.GONE);
            image8.setImageResource(R.mipmap.indicator_off);
        }
        if (image9 != null) {
            image9.setVisibility(View.GONE);
            image9.setImageResource(R.mipmap.indicator_off);
        }
        if (image10 != null) {
            image10.setVisibility(View.GONE);
            image10.setImageResource(R.mipmap.indicator_off);
        }

        if (iTotalPage == 0 || iTotalPage == 1) {
            image1.setVisibility(View.VISIBLE);
        } else if (iTotalPage == 2) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
        } else if (iTotalPage == 3) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
        } else if (iTotalPage == 4) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
            image4.setVisibility(View.VISIBLE);
        } else if (iTotalPage == 5) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
            image4.setVisibility(View.VISIBLE);
            image5.setVisibility(View.VISIBLE);
        } else if (iTotalPage == 6) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
            image4.setVisibility(View.VISIBLE);
            image5.setVisibility(View.VISIBLE);
            if (image6 != null) {
                image6.setVisibility(View.VISIBLE);
            }
        } else if (iTotalPage == 7) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
            image4.setVisibility(View.VISIBLE);
            image5.setVisibility(View.VISIBLE);
            if (image6 != null) {
                image6.setVisibility(View.VISIBLE);
            }
            if (image7 != null) {
                image7.setVisibility(View.VISIBLE);
            }
        } else if (iTotalPage == 8) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
            image4.setVisibility(View.VISIBLE);
            image5.setVisibility(View.VISIBLE);
            if (image6 != null) {
                image6.setVisibility(View.VISIBLE);
            }
            if (image7 != null) {
                image7.setVisibility(View.VISIBLE);
            }
            if (image8 != null) {
                image8.setVisibility(View.VISIBLE);
            }
        } else if (iTotalPage == 9) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
            image4.setVisibility(View.VISIBLE);
            image5.setVisibility(View.VISIBLE);
            if (image6 != null) {
                image6.setVisibility(View.VISIBLE);
            }
            if (image7 != null) {
                image7.setVisibility(View.VISIBLE);
            }
            if (image8 != null) {
                image8.setVisibility(View.VISIBLE);
            }
            if (image9 != null) {
                image9.setVisibility(View.VISIBLE);
            }
        } else if (iTotalPage == 10) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
            image4.setVisibility(View.VISIBLE);
            image5.setVisibility(View.VISIBLE);
            if (image6 != null) {
                image6.setVisibility(View.VISIBLE);
            }
            if (image7 != null) {
                image7.setVisibility(View.VISIBLE);
            }
            if (image8 != null) {
                image8.setVisibility(View.VISIBLE);
            }
            if (image9 != null) {
                image9.setVisibility(View.VISIBLE);
            }
            if (image10 != null) {
                image10.setVisibility(View.VISIBLE);
            }
        }
        switch (iCurrPage) {
            case 0: {
                image1.setImageResource(R.mipmap.indicator_on);
            }
            break;
            case 1: {
                image2.setImageResource(R.mipmap.indicator_on);
            }
            break;
            case 2: {
                image3.setImageResource(R.mipmap.indicator_on);
            }
            break;
            case 3: {
                image4.setImageResource(R.mipmap.indicator_on);
            }
            break;
            case 4: {
                image5.setImageResource(R.mipmap.indicator_on);
            }
            break;
            case 5: {
                if (image6 != null) {
                    image6.setImageResource(R.mipmap.indicator_on);
                }
            }
            break;
            case 6: {
                if (image7 != null) {
                    image7.setImageResource(R.mipmap.indicator_on);
                }
            }
            break;
            case 7: {
                if (image8 != null) {
                    image8.setImageResource(R.mipmap.indicator_on);
                }
            }
            break;
            case 8: {
                if (image9 != null) {
                    image9.setImageResource(R.mipmap.indicator_on);
                }
            }
            break;
            case 9: {
                if (image10 != null) {
                    image10.setImageResource(R.mipmap.indicator_on);
                }
            }
            break;
        }
    }

    /**
     * 숫자에 천단위마다 콤마 넣기
     *
     * @param int
     * @return String
     */
    public static String toNumFormat(int num) {
        DecimalFormat df = new DecimalFormat("###,###,###");
        return df.format(num);
    }

    /**
     * @param inputNum
     * @return
     */
    /*
    stringParserCommafy("+123456")  = +123,456
    regexCommafy("+123456")         = +123,456
    decimalFormatCommafy("+123456") = 123,456

    stringParserCommafy("+123456")  = 1,234,567
    regexCommafy("+123456")         = 1,234,567
    decimalFormatCommafy("+123456") = 1,234,567

    stringParserCommafy("+123456")  = -100,000,000
    regexCommafy("+123456")         = -100,000,000
    decimalFormatCommafy("+123456") = -100,000,000

    stringParserCommafy("+123456")  = +2,345,676,558,947,589.2546815
    regexCommafy("+123456")         = +2,345,676,558,947,589.2546815
    decimalFormatCommafy("+123456") = 2,345,676,558,947,589.2546815
     */

    //Adding comma in between every 3 digits to a large number by parsing the number string.
    public static String stringParserCommafy(String inputNum) {

        String commafiedNum = "";
        Character firstChar = inputNum.charAt(0);

        //If there is a positive or negative number sign,
        //then put the number sign to the commafiedNum and remove the sign from inputNum.
        if (firstChar == '+' || firstChar == '-') {
            commafiedNum = commafiedNum + Character.toString(firstChar);
            inputNum = inputNum.replaceAll("[-\\+]", "");
        }

        //If the input number has decimal places,
        //then split it into two, save the first part to inputNum
        //and save the second part to decimalNum which will be appended to the final result at the end.
        String[] splittedNum = inputNum.split("\\.");
        String decimalNum = "";
        if (splittedNum.length == 2) {
            inputNum = splittedNum[0];
            decimalNum = "." + splittedNum[1];
        }

        //The main logic for adding commas to the number.
        int numLength = inputNum.length();
        for (int i = 0; i < numLength; i++) {
            if ((numLength - i) % 3 == 0 && i != 0) {
                commafiedNum += ",";
            }
            commafiedNum += inputNum.charAt(i);
        }

        return commafiedNum + decimalNum;
    }


    //Adding comma in between every 3 digits to a large number by using regex.
    public static String regexCommafy(String inputNum) {
        String regex = "(\\d)(?=(\\d{3})+$)";
        String[] splittedNum = inputNum.split("\\.");
        if (splittedNum.length == 2) {
            return splittedNum[0].replaceAll(regex, "$1,") + "." + splittedNum[1];
        } else {
            return inputNum.replaceAll(regex, "$1,");
        }
    }

    //Adding comma in between every 3 digits to a large number by using DecimalFormat.
    public static String decimalFormatCommafy(String inputNum) {
        //If the input number has decimal places,
        //then split it into two, save the first part to inputNum
        //and save the second part to decimalNum which will be appended to the final result at the end.
        String[] splittedNum = inputNum.split("\\.");
        String decimalNum = "";
        if (splittedNum.length == 2) {
            inputNum = splittedNum[0];
            decimalNum = "." + splittedNum[1];
        }

        Double inputDouble = Double.parseDouble(inputNum);
        DecimalFormat myFormatter = new DecimalFormat("###,###");
        String output = myFormatter.format(inputDouble);


        return output + decimalNum;
    }

    /**
     * 프로모션스티커 적용하기.
     *
     * @param promotionSticker
     * @param isNew
     * @param assetNew
     * @param hot
     * @param assetHot
     * @param PromotionSticker
     */
    public static void setPromotionSticker(String promotionSticker, Boolean isNew, Boolean hot, String assetNew, String assetHot, ImageView PromotionSticker) {

        // string < - > int
//        String str = "1234";
//        int    num = 5678;
//
//        String str2 = String.valueOf(num);
//        int    num2 = Integer.parseInt(str);
        if ( isNew == true ) {
            PromotionSticker.setImageResource(R.mipmap.vod_01);
        } else if ( assetNew != "0" ) {
            PromotionSticker.setImageResource(R.mipmap.vod_01);
        } else if ("11".equals(promotionSticker)) {
            PromotionSticker.setImageResource(R.mipmap.vod_09);
        } else if ("12".equals(promotionSticker)) {
            PromotionSticker.setImageResource(R.mipmap.vod_07);
        } else if ("13".equals(promotionSticker)) {
            PromotionSticker.setImageResource(R.mipmap.vod_03);
        } else if ("14".equals(promotionSticker)) {
            PromotionSticker.setImageResource(R.mipmap.vod_08);
        } else if ("15".equals(promotionSticker)) {
            PromotionSticker.setImageResource(R.mipmap.vod_04);
        } else if ("16".equals(promotionSticker)) {
            PromotionSticker.setImageResource(R.mipmap.vod_02);
        } else if ("17".equals(promotionSticker)) {
            PromotionSticker.setImageResource(R.mipmap.vod_05);
        } else if ("18".equals(promotionSticker)) {
            PromotionSticker.setImageResource(R.mipmap.vod_06);
        } else if ( hot == true ) {
            PromotionSticker.setImageResource(R.mipmap.vod_02);
        } else if ( assetHot != "0" ) {
            PromotionSticker.setImageResource(R.mipmap.vod_02);
        } else {
            PromotionSticker.setVisibility(View.GONE);
        }
    }

    public static void setPromotionSticker(String promotionSticker, ImageView PromotionSticker) {

        // string < - > int
//        String str = "1234";
//        int    num = 5678;
//
//        String str2 = String.valueOf(num);
//        int    num2 = Integer.parseInt(str);

        if ("0".equals(promotionSticker)) {
            PromotionSticker.setImageResource(R.mipmap.vod_01);
        } else if ("11".equals(promotionSticker)) {
            PromotionSticker.setImageResource(R.mipmap.vod_09);
        } else if ("12".equals(promotionSticker)) {
            PromotionSticker.setImageResource(R.mipmap.vod_07);
        } else if ("13".equals(promotionSticker)) {
            PromotionSticker.setImageResource(R.mipmap.vod_03);
        } else if ("14".equals(promotionSticker)) {
            PromotionSticker.setImageResource(R.mipmap.vod_08);
        } else if ("15".equals(promotionSticker)) {
            PromotionSticker.setImageResource(R.mipmap.vod_04);
        } else if ("16".equals(promotionSticker)) {
            PromotionSticker.setImageResource(R.mipmap.vod_02);
        } else if ("17".equals(promotionSticker)) {
            PromotionSticker.setImageResource(R.mipmap.vod_05);
        } else if ("18".equals(promotionSticker)) {
            PromotionSticker.setImageResource(R.mipmap.vod_06);
        } else {
            PromotionSticker.setVisibility(View.GONE);
        }
    }

    /**
     * viewpager indicator 설정
     * */
    public static void initializePageIndicator(Context context, int totalSize, ViewGroup indicatorView, int initialzeIndex) {
        if (totalSize <= 0 || context == null || indicatorView == null) {
            return;
        }

        if (initialzeIndex < 0) {
            initialzeIndex = 0;
        }

        for (int i = 0; i < totalSize; i++) {
            ImageView iv = new ImageView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.rightMargin = 6;
            iv.setLayoutParams(lp);
            iv.setBackgroundResource(R.mipmap.indicator_off);
            indicatorView.addView(iv);
        }
        indicatorView.getChildAt(initialzeIndex).setBackgroundResource(R.mipmap.indicator_on);
    }

    public static void changePageIndicator(ViewGroup indicatorView, int beforeIndex,int changeIndex) {
        if (changeIndex < indicatorView.getChildCount()) {
            indicatorView.getChildAt(beforeIndex).setBackgroundResource(R.mipmap.indicator_off);
            indicatorView.getChildAt(changeIndex).setBackgroundResource(R.mipmap.indicator_on);
        }
    }

    public static boolean checkSTBStateCode(String code, Context ctx) {

        boolean isSuccess = false;

        //  공통
        //  조회목록이 없는 경우(205)도 정상 처리임.
        if ("100".equals(code) || "205".equals(code)) {
            isSuccess = true;
        } else if ("200".equals(code)) {

            String alertTitle = "씨앤앰 모바일 TV";
            String alertMessage1 = "알수없는 에러";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("201".equals(code)) {
            String alertTitle = "씨앤앰 모바일 TV";
            String alertMessage1 = "지원하지 않는 프로토콜";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("202".equals(code)) {
            String alertTitle = "씨앤앰 모바일 TV";
            String alertMessage1 = "인증 실패";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("203".equals(code)) {
            String alertTitle = "씨앤앰 모바일 TV";
            String alertMessage1 = "지원하지 않는 프로파일";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("204".equals(code)) {
            String alertTitle = "씨앤앰 모바일 TV";
            String alertMessage1 = "잘못된 파라미터값";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        }
//    else if ([code isEqualToString:@"205"])
//    {
//        [SIAlertView alert:@"씨앤앰 모바일 TV" message:@"녹화물 목록을 받을 수 없습니다."];
//    }
        else if ("206".equals(code) || "028".equals(code)) {
            String alertTitle = "씨앤앰 모바일 TV";
            String alertMessage1 = "셋탑박스와 통신이 끊어졌습니다.\n전원을 확인해주세요.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("207".equals(code)) {
            String alertTitle = "씨앤앰 모바일 TV";
            String alertMessage1 = "내부 프로세싱 에러";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("211".equals(code)) {
            String alertTitle = "씨앤앰 모바일 TV";
            String alertMessage1 = "일반 DB 에러";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("221".equals(code)) {
            String alertTitle = "씨앤앰 모바일 TV";
            String alertMessage1 = "이미 처리 되었음";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("223".equals(code)) {
            String alertTitle = "씨앤앰 모바일 TV";
            String alertMessage1 = "이미 추가된 항목";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("231".equals(code)) {
            String alertTitle = "씨앤앰 모바일 TV";
            String alertMessage1 = "인증코드발급 실패";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("232".equals(code)) {
            String alertTitle = "씨앤앰 모바일 TV";
            String alertMessage1 = "만료된 인증코드";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        }

        //  녹화요청
        else if ("001".equals(code)) {
            String alertTitle = "녹화 불가";
            String alertMessage1 = "MAC주소가 불일치 합니다.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("002".equals(code)) {
            String alertTitle = "녹화 불가";
            String alertMessage1 = "고객님의 셋탑박스는 해당시간에 다른 채널이 녹화예약되어있습니다. 녹화예약을 취소해주세요.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("003".equals(code)) {
            String alertTitle = "녹화 불가";
            String alertMessage1 = "셋탑박스의 저장공간이 부족합니다. 녹화물 목록을 확인해주세요.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("004".equals(code)) {
            String alertTitle = "녹화 불가";
            String alertMessage1 = "튜너를 모두 사용하고 있습니다.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("005".equals(code)) {
            String alertTitle = "녹화 불가";
            String alertMessage1 = "선택 하신 채널은 녹화하실 수 없습니다.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("006".equals(code)) {
            String alertTitle = "녹화 불가";
            String alertMessage1 = "이미 녹화가 예약되었습니다.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("007".equals(code)) {
            String alertTitle = "녹화 불가";
            String alertMessage1 = "프로그램 정보가 없습니다.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("008".equals(code)) {
            String alertTitle = "녹화예약취소 불가";
            String alertMessage1 = "녹화물 재생중엔 채널변경이 불가능합니다.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("009".equals(code)) {
            String alertTitle = "녹화 불가";
            String alertMessage1 = "고객님의 셋탑박스에서 제공되지 않는 채널입니다.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("010".equals(code)) {
            String alertTitle = "녹화 불가";
            String alertMessage1 = "셋탑박스에서 동시화면 기능을 사용중인 경우 즉시 녹화가 불가능합니다.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("011".equals(code)) {
            String alertTitle = "녹화 불가";
            String alertMessage1 = "고객님의 셋탑박스는 현재 다른 채널을 녹화중입니다. 녹화를 중지해주세요.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("012".equals(code)) {
            String alertTitle = "녹화 불가";
            String alertMessage1 = "고객님의 셋탑박스 설정에 의한 시청제한으로 녹화가 불가합니다. 셋탑박스 설정을 확인해주세요.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("013".equals(code)) {
            String alertTitle = "녹화 불가";
            String alertMessage1 = "제한채널로 녹화가 불가합니다.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("014".equals(code)) {
            String alertTitle = "녹화 불가";
            String alertMessage1 = "셋탑박스의 뒷 전원이 꺼져있거나, 통신이 고르지 못해 녹화가 불가합니다. 셋탑박스의 상태를 확인해주세요.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("015".equals(code)) {
            String alertTitle = "녹화 불가";
            String alertMessage1 = "이미 녹화 중입니다.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("016".equals(code)) {
            String alertTitle = "녹화 불가";
            String alertMessage1 = "삭제 오류입니다.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("017".equals(code)) {
            String alertTitle = "녹화 불가";
            String alertMessage1 = "이름변경 오류입니다.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("018".equals(code)) {
            String alertTitle = "녹화 불가";
            String alertMessage1 = "VOD상세 화면 띄우기 오류입니다.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("019".equals(code)) {
            String alertTitle = "녹화 불가";
            String alertMessage1 = "개인 미디어 재생중입니다.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("020".equals(code)) {
            String alertTitle = "녹화 불가";
            String alertMessage1 = "독립형(데이터 서비스) 실행중입니다.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("021".equals(code)) {
            String alertTitle = "녹화예약취소 불가";
            String alertMessage1 = "VOD 시청중엔 채널변경이 불가능합니다.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        } else if ("023".equals(code)) {
            String alertTitle = "녹화 불가";
            String alertMessage1 = "고객님의 셋탑박스에서 제공되지 않는 채널입니다.";
            String alertMessage2 = "";
            CMAlertUtil.Alert(ctx, alertTitle, alertMessage1, alertMessage2, true, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }, true);
        }


        return isSuccess;
    }
}
