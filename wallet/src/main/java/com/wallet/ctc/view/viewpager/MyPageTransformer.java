

package com.wallet.ctc.view.viewpager;

import android.view.View;

import androidx.viewpager.widget.ViewPager;


public class MyPageTransformer implements ViewPager.PageTransformer {


    private static final float MIN_SCALE = 0.8f;

    
    public Float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }

    
    public void transformPage(View view, float position) {


        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();


        if (position < -1) { 
            
            view.setScaleX(MIN_SCALE);
            view.setScaleY(MIN_SCALE);
            view.setPivotY(MIN_SCALE);
            view.setPivotX(MIN_SCALE);

        } else if (position <= 1) { 
            if (position == 0){

                view.setPivotX(1.0f);
                view.setPivotY(1.0f);
                view.setScaleX(1);
                view.setScaleY(1);
                view.setAlpha(1);
                return;
            }

            float p = Math.abs(position);
            float scaleFactor = evaluate(p, 1, MIN_SCALE);
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }

            
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            view.setPivotX(scaleFactor);
            view.setPivotY(scaleFactor);



        } else { 
            
            view.setScaleX(MIN_SCALE);
            view.setScaleY(MIN_SCALE);
            view.setPivotY(MIN_SCALE);
            view.setPivotY(MIN_SCALE);
        }

    }

}

