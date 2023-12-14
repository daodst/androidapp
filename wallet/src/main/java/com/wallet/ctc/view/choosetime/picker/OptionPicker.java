

package com.wallet.ctc.view.choosetime.picker;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.wallet.ctc.view.choosetime.widget.WheelView;

import java.util.ArrayList;
import java.util.Arrays;


public class OptionPicker extends WheelPicker {
    protected ArrayList<String> options = new ArrayList<String>();
    private OnOptionPickListener onOptionPickListener;
    private int selectedOption = 0;
    private String label = "";

    
    public OptionPicker(Activity activity, String[] options) {
        super(activity);
        this.options.addAll(Arrays.asList(options));
    }

    public OptionPicker(Activity activity, ArrayList<String> options) {
        super(activity);
        this.options.addAll(options);
    }

    
    public void setLabel(String label) {
        this.label = label;
    }

    
    public void setSelectedIndex(int index) {
        if (index >= 0 && index < options.size()) {
            selectedOption = index;
        }
    }

    
    public void setSelectedItem(String option) {
        setSelectedIndex(options.indexOf(option));
    }

    public void setOnOptionPickListener(OnOptionPickListener listener) {
        this.onOptionPickListener = listener;
    }

    @Override
    @NonNull
    protected View makeCenterView() {
        if (options.size() == 0) {
            throw new IllegalArgumentException("please initial options at first, can't be empty");
        }
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        WheelView optionView = new WheelView(activity);
        optionView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        optionView.setTextSize(textSize);
        optionView.setTextColor(textColorNormal, textColorFocus);
        optionView.setLineVisible(lineVisible);
        optionView.setLineColor(lineColor);
        optionView.setOffset(offset);
        layout.addView(optionView);
        TextView labelView = new TextView(activity);
        labelView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        labelView.setTextColor(textColorFocus);
        labelView.setTextSize(textSize);
        layout.addView(labelView);
        if (!TextUtils.isEmpty(label)) {
            labelView.setText(label);
        }
        optionView.setItems(options, selectedOption);
        optionView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(boolean isUserScroll, int selectedIndex, String item) {
                selectedOption = selectedIndex;
            }
        });
        return layout;
    }

    @Override
    public void onSubmit() {
        if (onOptionPickListener != null) {
            onOptionPickListener.onOptionPicked(selectedOption, options.get(selectedOption));
        }
    }

    
    public String getSelectedOption() {
        return options.get(selectedOption);
    }

    
    public int getSelectedPosition() {
        return selectedOption;
    }

    
    public interface OnOptionPickListener {

        
        void onOptionPicked(int position, String option);

    }

}
