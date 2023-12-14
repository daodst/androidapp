


package common.app.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.TextAppearanceSpan;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import common.app.Injection;
import common.app.ui.view.TouchableSpan;



public class SpanString {



    
    public static SpannableString getSpannableString(String content, int startIndex, int endIndex, int textSize, @ColorRes int colorID, String... args) {
        ColorStateList color = ColorStateList.valueOf(ContextCompat.getColor(Injection.provideContext(),colorID));
        String family = (args == null || args.length == 0 || args[0] == null) ? "blod" : args[0];
        SpannableString spanStr = new SpannableString(content);
        spanStr.setSpan(new TextAppearanceSpan(family, Typeface.NORMAL, textSize, color, null), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanStr;
    }

    public static SpannableString getSpannableString(Context context,@StringRes int strId, int textSize, @ColorRes int colorID, String... args) {
        String content = context.getString(strId);
        return getSpannableString(content, 0, content.length(), textSize, colorID, args);
    }

    public static SpannableString getSpannableString(String content, int textSize, @ColorRes int colorID, String... args) {
        content = null == content ? "" : content;
        return getSpannableString(content, 0, content.length(), textSize, colorID, args);
    }

    
    public static SpannableString getHintSpannableString(Context context,@StringRes int strId, @DimenRes int dimen) {
        int size = (int) (context.getResources().getDimension(dimen) + 0.5f);
        String content = context.getString(strId);
        SpannableString spanString = new SpannableString(content);
        spanString.setSpan(new AbsoluteSizeSpan(size, false), 0, spanString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }


    
    public static SpannableString getTouchableSpanString(String content, int startIndex, int endIndex, @ColorRes int normal, @ColorRes int pressed, final TouchableSpanClick click) {
        SpannableString spanString = new SpannableString(content);
        int normalColor = ContextCompat.getColor(Injection.provideContext(),normal);
        int pressedColor = ContextCompat.getColor(Injection.provideContext(),pressed);
        final int bgPressedColor =ContextCompat.getColor(Injection.provideContext(),android.R.color.transparent);
        spanString.setSpan(new TouchableSpan(normalColor, pressedColor, bgPressedColor) {

            @Override
            public void onClick(View view) {
                if (null != click) {
                    click.onClick(view);
                }
            }
        }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }


    public static SpannableString getTouchableSpanString(Context context,@StringRes int strId, @ColorRes int normal, @ColorRes int pressed, final TouchableSpanClick click) {

        String content = context.getString(strId);
        return getTouchableSpanString(content, 0, content.length(), normal, pressed, click);

    }

    public interface TouchableSpanClick {
        void onClick(View view);
    }


    
    public static SpannableString getIMSpannString(String content, @ColorRes int color, int middle, int topSize, int netSize) {
        ColorStateList colorStateList = ColorStateList.valueOf(ContextCompat.getColor(Injection.provideContext(), color));
        SpannableString spanStr = new SpannableString(content);
        spanStr.setSpan(new TextAppearanceSpan("blod", Typeface.NORMAL, topSize, colorStateList, null), 0, middle, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new TextAppearanceSpan("blod", Typeface.NORMAL, netSize, colorStateList, null), middle, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spanStr;
    }

    public static SpannableString getSpannString(String content, ColorStateList colorStateList, int middle, @ColorRes int color2, int topSize, int netSize) {
        ColorStateList colorStateList2 = ColorStateList.valueOf(ContextCompat.getColor(Injection.provideContext(), color2));
        SpannableString spanStr = new SpannableString(content);
        
        spanStr.setSpan(new TextAppearanceSpan("default", Typeface.NORMAL, topSize, colorStateList, null), 0, middle, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStr.setSpan(new TextAppearanceSpan("default", Typeface.NORMAL, netSize, colorStateList2, null), middle, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spanStr;
    }

}
