

package com.app.my.interfaces;


import com.app.view.CountView;


public interface RiseNumberBaseListener {
    public void start();

    public CountView withNumber(float number);

    public CountView withNumber(float number, boolean flag);

    public CountView withNumber(int number);

    public CountView setDuration(long duration);

    public void setOnEnd(CountView.EndListener callback);
}

