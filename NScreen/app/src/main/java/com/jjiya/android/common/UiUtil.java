package com.jjiya.android.common;

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
