

package com.wallet.ctc.view.choosetime.picker;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.wallet.ctc.view.choosetime.util.DateUtils;
import com.wallet.ctc.view.choosetime.widget.WheelView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;


public class TimePicker extends WheelPicker {
    
    @Deprecated
    public static final int HOUR_OF_DAY = 0;
    public static final int HOUR_24 = 0;
    
    @Deprecated
    public static final int HOUR = 1;
    public static final int HOUR_12 = 1;
    private OnTimePickListener onTimePickListener;
    private int mode;
    private String hourLabel = "", minuteLabel = "";
    private String selectedHour = "", selectedMinute = "";
    private int startHour, startMinute = 0;
    private int endHour, endMinute = 59;

    
    @IntDef(flag = false, value = {HOUR_OF_DAY, HOUR, HOUR_24, HOUR_12})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    public TimePicker(Activity activity) {
        this(activity, HOUR_24);
    }

    
    public TimePicker(Activity activity, @Mode int mode) {
        super(activity);
        this.mode = mode;
        if (mode == HOUR_12) {
            startHour = 1;
            endHour = 12;
        } else {
            startHour = 0;
            endHour = 23;
        }
        selectedHour = DateUtils.fillZero(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        selectedMinute = DateUtils.fillZero(Calendar.getInstance().get(Calendar.MINUTE));
    }

    
    public void setLabel(String hourLabel, String minuteLabel) {
        this.hourLabel = hourLabel;
        this.minuteLabel = minuteLabel;
    }

    
    public void setRangeStart(int startHour, int startMinute) {
        boolean illegal = false;
        if (startHour < 0 || startMinute < 0 || startMinute > 59) {
            illegal = true;
        }
        if (mode == HOUR_12 && (startHour == 0 || startHour > 12)) {
            illegal = true;
        }
        if (mode == HOUR_24 && startHour >= 24) {
            illegal = true;
        }
        if (illegal) {
            throw new IllegalArgumentException();
        }
        this.startHour = startHour;
        this.startMinute = startMinute;
    }

    
    public void setRangeEnd(int endHour, int endMinute) {
        boolean illegal = false;
        if (endHour < 0 || endMinute < 0 || endMinute > 59) {
            illegal = true;
        }
        if (mode == HOUR_12 && (endHour == 0 || endHour > 12)) {
            illegal = true;
        }
        if (mode == HOUR_24 && endHour >= 24) {
            illegal = true;
        }
        if (illegal) {
            throw new IllegalArgumentException();
        }
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    
    public void setSelectedItem(int hour, int minute) {
        selectedHour = DateUtils.fillZero(hour);
        selectedMinute = DateUtils.fillZero(minute);
    }

    public void setOnTimePickListener(OnTimePickListener listener) {
        this.onTimePickListener = listener;
    }

    @Override
    @NonNull
    protected View makeCenterView() {
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        WheelView hourView = new WheelView(activity);
        hourView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        hourView.setTextSize(textSize);
        hourView.setTextColor(textColorNormal, textColorFocus);
        hourView.setLineVisible(lineVisible);
        hourView.setLineColor(lineColor);
        layout.addView(hourView);
        TextView hourTextView = new TextView(activity);
        hourTextView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        hourTextView.setTextSize(textSize);
        hourTextView.setTextColor(textColorFocus);
        if (!TextUtils.isEmpty(hourLabel)) {
            hourTextView.setText(hourLabel);
        }
        layout.addView(hourTextView);
        final WheelView minuteView = new WheelView(activity);
        minuteView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        minuteView.setTextSize(textSize);
        minuteView.setTextColor(textColorNormal, textColorFocus);
        minuteView.setLineVisible(lineVisible);
        minuteView.setLineColor(lineColor);
        minuteView.setOffset(offset);
        layout.addView(minuteView);
        TextView minuteTextView = new TextView(activity);
        minuteTextView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        minuteTextView.setTextSize(textSize);
        minuteTextView.setTextColor(textColorFocus);
        if (!TextUtils.isEmpty(minuteLabel)) {
            minuteTextView.setText(minuteLabel);
        }
        layout.addView(minuteTextView);
        ArrayList<String> hours = new ArrayList<String>();
        for (int i = startHour; i <= endHour; i++) {
            hours.add(DateUtils.fillZero(i));
        }
        hourView.setItems(hours, selectedHour);
        minuteView.setItems(changeMinuteData(selectedHour), selectedMinute);
        hourView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(boolean isUserScroll, int selectedIndex, String item) {
                selectedHour = item;
                minuteView.setItems(changeMinuteData(item));
            }
        });
        minuteView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(boolean isUserScroll, int selectedIndex, String item) {
                selectedMinute = item;
            }
        });
        return layout;
    }

    private ArrayList<String> changeMinuteData(String hour) {
        ArrayList<String> minutes = new ArrayList<String>();
        int hourInt = DateUtils.trimZero(hour);
        if (hourInt == startHour) {
            for (int i = startMinute; i <= 59; i++) {
                minutes.add(DateUtils.fillZero(i));
            }
            selectedMinute = "00";
        } else if (hourInt == endHour) {
            for (int i = 0; i <= endMinute; i++) {
                minutes.add(DateUtils.fillZero(i));
            }
            selectedMinute = "00";
        } else {
            for (int i = 0; i <= 59; i++) {
                minutes.add(DateUtils.fillZero(i));
            }
        }
        return minutes;
    }

    @Override
    public void onSubmit() {
        if (onTimePickListener != null) {
            onTimePickListener.onTimePicked(selectedHour, selectedMinute);
        }
    }

    
    public String getSelectedHour() {
        return selectedHour;
    }

    
    public String getSelectedMinute() {
        return selectedMinute;
    }

    
    public interface OnTimePickListener {

        
        void onTimePicked(String hour, String minute);

    }

}
