package com.jjiya.android.common;

import android.view.View;
import android.widget.ImageView;

import com.stvn.nscreen.R;

import java.text.DecimalFormat;

/**
 * Created by swlim on 2015. 10. 21..
 */
public class UiUtil {

    /**
     * 별점이미지 셋팅하기.
     * @param rating
     * @param starImage1
     * @param starImage2
     * @param starImage3
     * @param starImage4
     * @param starImage5
     */
    public static void setStarRating(Long rating, ImageView starImage1, ImageView starImage2, ImageView starImage3, ImageView starImage4, ImageView starImage5) {
        if ( rating <= 0l ) {
            starImage1.setImageResource(R.mipmap.series_star_off);
            starImage2.setImageResource(R.mipmap.series_star_off);
            starImage3.setImageResource(R.mipmap.series_star_off);
            starImage4.setImageResource(R.mipmap.series_star_off);
            starImage5.setImageResource(R.mipmap.series_star_off);
        } else if ( rating > 0l && rating <= 20l ) {
            starImage1.setImageResource(R.mipmap.series_star_on);
            starImage2.setImageResource(R.mipmap.series_star_off);
            starImage3.setImageResource(R.mipmap.series_star_off);
            starImage4.setImageResource(R.mipmap.series_star_off);
            starImage5.setImageResource(R.mipmap.series_star_off);
        } else if ( rating > 20l && rating <= 40l ) {
            starImage1.setImageResource(R.mipmap.series_star_on);
            starImage2.setImageResource(R.mipmap.series_star_on);
            starImage3.setImageResource(R.mipmap.series_star_off);
            starImage4.setImageResource(R.mipmap.series_star_off);
            starImage5.setImageResource(R.mipmap.series_star_off);
        } else if ( rating > 40l && rating <= 60l ) {
            starImage1.setImageResource(R.mipmap.series_star_on);
            starImage2.setImageResource(R.mipmap.series_star_on);
            starImage3.setImageResource(R.mipmap.series_star_on);
            starImage4.setImageResource(R.mipmap.series_star_off);
            starImage5.setImageResource(R.mipmap.series_star_off);
        } else if ( rating > 60l && rating <= 80l ) {
            starImage1.setImageResource(R.mipmap.series_star_on);
            starImage2.setImageResource(R.mipmap.series_star_on);
            starImage3.setImageResource(R.mipmap.series_star_on);
            starImage4.setImageResource(R.mipmap.series_star_on);
            starImage5.setImageResource(R.mipmap.series_star_off);
        } else if ( rating > 80l ) {
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

        if ( image6 != null ) {
            image6.setVisibility(View.GONE);
            image6.setImageResource(R.mipmap.indicator_off);
        }
        if ( image7 != null ) {
            image7.setVisibility(View.GONE);
            image7.setImageResource(R.mipmap.indicator_off);
        }
        if ( image8 != null ) {
            image8.setVisibility(View.GONE);
            image8.setImageResource(R.mipmap.indicator_off);
        }
        if ( image9 != null ) {
            image9.setVisibility(View.GONE);
            image9.setImageResource(R.mipmap.indicator_off);
        }
        if ( image10 != null ) {
            image10.setVisibility(View.GONE);
            image10.setImageResource(R.mipmap.indicator_off);
        }

        if ( iTotalPage == 0 ||  iTotalPage == 1 ) {
            image1.setVisibility(View.VISIBLE);
        } else if ( iTotalPage == 2 ) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
        } else if ( iTotalPage == 3 ) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
        } else if ( iTotalPage == 4 ) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
            image4.setVisibility(View.VISIBLE);
        } else if ( iTotalPage == 5 ) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
            image4.setVisibility(View.VISIBLE);
            image5.setVisibility(View.VISIBLE);
        } else if ( iTotalPage == 6 ) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
            image4.setVisibility(View.VISIBLE);
            image5.setVisibility(View.VISIBLE);
            if ( image6 != null ) { image6.setVisibility(View.VISIBLE); }
        } else if ( iTotalPage == 7 ) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
            image4.setVisibility(View.VISIBLE);
            image5.setVisibility(View.VISIBLE);
            if ( image6 != null ) { image6.setVisibility(View.VISIBLE); }
            if ( image7 != null ) { image7.setVisibility(View.VISIBLE); }
        } else if ( iTotalPage == 8 ) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
            image4.setVisibility(View.VISIBLE);
            image5.setVisibility(View.VISIBLE);
            if ( image6 != null ) { image6.setVisibility(View.VISIBLE); }
            if ( image7 != null ) { image7.setVisibility(View.VISIBLE); }
            if ( image8 != null ) { image8.setVisibility(View.VISIBLE); }
        } else if ( iTotalPage == 9 ) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
            image4.setVisibility(View.VISIBLE);
            image5.setVisibility(View.VISIBLE);
            if ( image6 != null ) { image6.setVisibility(View.VISIBLE); }
            if ( image7 != null ) { image7.setVisibility(View.VISIBLE); }
            if ( image8 != null ) { image8.setVisibility(View.VISIBLE); }
            if ( image9 != null ) { image9.setVisibility(View.VISIBLE); }
        } else if ( iTotalPage == 10 ) {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            image3.setVisibility(View.VISIBLE);
            image4.setVisibility(View.VISIBLE);
            image5.setVisibility(View.VISIBLE);
            if ( image6 != null ) { image6.setVisibility(View.VISIBLE); }
            if ( image7 != null ) { image7.setVisibility(View.VISIBLE); }
            if ( image8 != null ) { image8.setVisibility(View.VISIBLE); }
            if ( image9 != null ) { image9.setVisibility(View.VISIBLE); }
            if ( image10 != null ) { image10.setVisibility(View.VISIBLE); }
        }
        switch ( iCurrPage ) {
            case 0: {
                image1.setImageResource(R.mipmap.indicator_on);
            } break;
            case 1: {
                image2.setImageResource(R.mipmap.indicator_on);
            } break;
            case 2: {
                image3.setImageResource(R.mipmap.indicator_on);
            } break;
            case 3: {
                image4.setImageResource(R.mipmap.indicator_on);
            } break;
            case 4: {
                image5.setImageResource(R.mipmap.indicator_on);
            } break;
            case 5: {
                if ( image6 != null ) { image6.setImageResource(R.mipmap.indicator_on); }
            } break;
            case 6: {
                if ( image7 != null ) {image7.setImageResource(R.mipmap.indicator_on); }
            } break;
            case 7: {
                if ( image8 != null ) {image8.setImageResource(R.mipmap.indicator_on); }
            } break;
            case 8: {
                if ( image9 != null ) {image9.setImageResource(R.mipmap.indicator_on); }
            } break;
            case 9: {
                if ( image10 != null ) {image10.setImageResource(R.mipmap.indicator_on); }
            } break;
        }
    }

    /**
     * 숫자에 천단위마다 콤마 넣기
     * @param int
     * @return String
     * */
    public static String toNumFormat(int num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num);
    }

    /**
     *
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

        String commafiedNum="";
        Character firstChar= inputNum.charAt(0);

        //If there is a positive or negative number sign,
        //then put the number sign to the commafiedNum and remove the sign from inputNum.
        if(firstChar=='+' || firstChar=='-')
        {
            commafiedNum = commafiedNum + Character.toString(firstChar);
            inputNum=inputNum.replaceAll("[-\\+]", "");
        }

        //If the input number has decimal places,
        //then split it into two, save the first part to inputNum
        //and save the second part to decimalNum which will be appended to the final result at the end.
        String [] splittedNum = inputNum.split("\\.");
        String decimalNum="";
        if(splittedNum.length==2)
        {
            inputNum=splittedNum[0];
            decimalNum="."+splittedNum[1];
        }

        //The main logic for adding commas to the number.
        int numLength = inputNum.length();
        for (int i=0; i<numLength; i++) {
            if ((numLength-i)%3 == 0 && i != 0) {
                commafiedNum += ",";
            }
            commafiedNum += inputNum.charAt(i);
        }

        return commafiedNum+decimalNum;
    }


    //Adding comma in between every 3 digits to a large number by using regex.
    public static String regexCommafy(String inputNum)
    {
        String regex = "(\\d)(?=(\\d{3})+$)";
        String [] splittedNum = inputNum.split("\\.");
        if(splittedNum.length==2)
        {
            return splittedNum[0].replaceAll(regex, "$1,")+"."+splittedNum[1];
        }
        else
        {
            return inputNum.replaceAll(regex, "$1,");
        }
    }

    //Adding comma in between every 3 digits to a large number by using DecimalFormat.
    public static String decimalFormatCommafy(String inputNum)
    {
        //If the input number has decimal places,
        //then split it into two, save the first part to inputNum
        //and save the second part to decimalNum which will be appended to the final result at the end.
        String [] splittedNum = inputNum.split("\\.");
        String decimalNum="";
        if(splittedNum.length==2)
        {
            inputNum=splittedNum[0];
            decimalNum="."+splittedNum[1];
        }

        Double inputDouble=Double.parseDouble(inputNum);
        DecimalFormat myFormatter = new DecimalFormat("###,###");
        String output = myFormatter.format(inputDouble);


        return output+decimalNum;
    }
}
