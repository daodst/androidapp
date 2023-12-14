package common.app.utils;

import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;


public class SpannableUtils {

    
    public static Spannable styleMatchingText(Spannable spl, String match, int typeFace) {
        if (spl == null || TextUtils.isEmpty(spl.toString()) || TextUtils.isEmpty(match)) {
            return spl;
        }
        int start =  spl.toString().indexOf(match);
        if (start != -1) {
            spl.setSpan(new StyleSpan(typeFace), start, start + match.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spl;
    }

    
    public static Spannable colorizeMatchingText(Spannable spl, String match, int color) {
        if (spl == null || TextUtils.isEmpty(spl.toString()) || TextUtils.isEmpty(match)) {
            return spl;
        }
        int start =  spl.toString().indexOf(match);
        if (start != -1) {
            spl.setSpan(new ForegroundColorSpan(color), start, start + match.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spl;
    }

    
    public static Spannable sizeMatchingText(Spannable spl, String match, int textSizePx) {
        if (spl == null || TextUtils.isEmpty(spl.toString()) || TextUtils.isEmpty(match)) {
            return spl;
        }
        int start =  spl.toString().indexOf(match);
        if (start != -1) {
            spl.setSpan(new AbsoluteSizeSpan(textSizePx), start, start + match.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spl;
    }


    
    public static Spannable clickMatchingText(Spannable spl, String match, ClickableSpan clickableSpan) {
        if (spl == null || TextUtils.isEmpty(spl.toString()) || TextUtils.isEmpty(match)) {
            return spl;
        }
        int start =  spl.toString().indexOf(match);
        if (start != -1) {
            spl.setSpan(clickableSpan, start, start + match.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spl;
    }


    
    public static Spannable addImgMatchingText(Spannable spl, String match, Drawable drawable, int verticalAlignment) {
        if (spl == null || TextUtils.isEmpty(spl.toString()) || TextUtils.isEmpty(match)) {
            return spl;
        }
        int start =  spl.toString().indexOf(match);
        if (start != -1) {
            spl.setSpan(new ImageSpan(drawable, verticalAlignment), start, start + match.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spl;
    }

}
