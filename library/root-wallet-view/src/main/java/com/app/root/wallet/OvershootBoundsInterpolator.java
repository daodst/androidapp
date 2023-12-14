package com.app.root.wallet;

import android.view.animation.Interpolator;


class OvershootBoundsInterpolator implements Interpolator {
    @Override
    public float getInterpolation(float t) {
        if(t < 0.3f) return (1.12f/0.3f)*t;
        else if(t < 0.6f) return (-0.8f*t)+1.36f;
        else if(t < 0.9f) return (0.6f*t)+0.52f;
        else return (-0.6f*t)+1.6f;
    }
}
