package com.app.root.wallet;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.LinearInterpolator;


class ViewAnmiUtils {

    
    public static ObjectAnimator bubbleFloat(View view, int duration, int offset, int repeatCount) {
        float path = (float) (Math.sqrt(3) / 2 * offset);
        PropertyValuesHolder translateX = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset / 2),
                Keyframe.ofFloat(2 / 12f, path),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, path),
                Keyframe.ofFloat(5 / 12f, offset / 2),
                Keyframe.ofFloat(6 / 12f, 0),
                Keyframe.ofFloat(7 / 12f, -offset / 2),
                Keyframe.ofFloat(8 / 12f, -path),
                Keyframe.ofFloat(9 / 12f, -offset),
                Keyframe.ofFloat(10 / 12f, -path),
                Keyframe.ofFloat(11 / 12f, -offset / 2),
                Keyframe.ofFloat(1f, 0)
        );

        PropertyValuesHolder translateY = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_Y,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset - path),
                Keyframe.ofFloat(2 / 12f, offset / 2),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, offset * 3 / 2),
                Keyframe.ofFloat(5 / 12f, offset + path),
                Keyframe.ofFloat(6 / 12f, offset * 2),
                Keyframe.ofFloat(7 / 12f, offset + path),
                Keyframe.ofFloat(8 / 12f, offset * 3 / 2),
                Keyframe.ofFloat(9 / 12f, offset),
                Keyframe.ofFloat(10 / 12f, offset / 2),
                Keyframe.ofFloat(11 / 12f, offset - path),
                Keyframe.ofFloat(1f, 0)
        );



        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, translateX, translateY).
                setDuration(duration);
        animator.setRepeatCount(repeatCount);
        animator.setInterpolator(new LinearInterpolator());
        return animator;
    }
}
