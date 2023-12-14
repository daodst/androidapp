

package com.wallet.ctc.view.choosetime.picker;

import android.app.Activity;


public class NumberPicker extends OptionPicker {

    public NumberPicker(Activity activity) {
        super(activity, new String[]{});
    }

    
    public void setRange(int startNumber, int endNumber) {
        setRange(startNumber, endNumber, 1);
    }

    
    public void setRange(int startNumber, int endNumber, int step) {
        for (int i = startNumber; i <= endNumber; i = i + step) {
            options.add(String.valueOf(i));
        }
    }

    
    public void setSelectedItem(int number) {
        setSelectedItem(String.valueOf(number));
    }

}

