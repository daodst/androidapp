

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;


public class DatePicker extends WheelPicker {
    
    public static final int YEAR_MONTH_DAY = 0;
    
    public static final int YEAR_MONTH = 1;
    
    public static final int MONTH_DAY = 2;
    private ArrayList<String> years = new ArrayList<String>();
    private ArrayList<String> months = new ArrayList<String>();
    private ArrayList<String> days = new ArrayList<String>();
    private OnDatePickListener onDatePickListener;
    private String yearLabel = "", monthLabel = "", dayLabel = "";
    private int startYear = 2010, startMonth = 1, startDay = 1;
    private int endYear = 2050, endMonth = 12, endDay = 31;
    private int selectedYearIndex = 0, selectedMonthIndex = 0, selectedDayIndex = 0;
    private int mode = YEAR_MONTH_DAY;

    
    @IntDef(flag = false, value = {YEAR_MONTH_DAY, YEAR_MONTH, MONTH_DAY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    
    public DatePicker(Activity activity) {
        this(activity, YEAR_MONTH_DAY);
    }

    
    public DatePicker(Activity activity, @Mode int mode) {
        super(activity);
        this.mode = mode;
    }

    
    public void setLabel(String yearLabel, String monthLabel, String dayLabel) {
        this.yearLabel = yearLabel;
        this.monthLabel = monthLabel;
        this.dayLabel = dayLabel;
    }

    
    @Deprecated
    public void setRange(int startYear, int endYear) {
        this.startYear = startYear;
        this.endYear = endYear;
        changeYearData();
    }

    
    public void setRangeStart(int startYear, int startMonth, int startDay) {
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDay = startDay;
    }

    
    public void setRangeEnd(int endYear, int endMonth, int endDay) {
        this.endYear = endYear;
        this.endMonth = endMonth;
        this.endDay = endDay;
    }

    
    public void setRangeStart(int startYearOrMonth, int startMonthOrDay) {
        if (mode == YEAR_MONTH_DAY) {
            throw new IllegalArgumentException();
        }
        if (mode == YEAR_MONTH) {
            this.startYear = startYearOrMonth;
            this.startMonth = startMonthOrDay;
        } else {
            this.startMonth = startYearOrMonth;
            this.startDay = startMonthOrDay;
        }
    }

    
    public void setRangeEnd(int endYearOrMonth, int endMonthOrDay) {
        if (mode == YEAR_MONTH_DAY) {
            throw new IllegalArgumentException();
        }
        if (mode == YEAR_MONTH) {
            this.endYear = endYearOrMonth;
            this.endMonth = endMonthOrDay;
        } else {
            this.endMonth = endYearOrMonth;
            this.endDay = endMonthOrDay;
        }
    }

    private int findItemIndex(ArrayList<String> items, int item) {
        
        int index = Collections.binarySearch(items, item, new Comparator<Object>() {
            @Override
            public int compare(Object lhs, Object rhs) {
                String lhsStr = lhs.toString();
                String rhsStr = rhs.toString();
                lhsStr = lhsStr.startsWith("0") ? lhsStr.substring(1) : lhsStr;
                rhsStr = rhsStr.startsWith("0") ? rhsStr.substring(1) : rhsStr;
                return Integer.parseInt(lhsStr) - Integer.parseInt(rhsStr);
            }
        });
        if (index < 0) {
            index = 0;
        }
        return index;
    }

    
    public void setSelectedItem(int year, int month, int day) {
        changeYearData();
        changeMonthData(year);
        changeDayData(year, month);
        selectedYearIndex = findItemIndex(years, year);
        selectedMonthIndex = findItemIndex(months, month);
        selectedDayIndex = findItemIndex(days, day);
    }

    
    public void setSelectedItem(int yearOrMonth, int monthOrDay) {
        int year = Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR);
        changeYearData();
        if (mode == MONTH_DAY) {
            changeMonthData(year);
            changeDayData(year, yearOrMonth);
            selectedMonthIndex = findItemIndex(months, yearOrMonth);
            selectedDayIndex = findItemIndex(days, monthOrDay);
        } else {
            changeMonthData(year);
            selectedYearIndex = findItemIndex(years, yearOrMonth);
            selectedMonthIndex = findItemIndex(months, monthOrDay);
        }
    }

    public void setOnDatePickListener(OnDatePickListener listener) {
        this.onDatePickListener = listener;
    }

    @Override
    @NonNull
    protected View makeCenterView() {
        if (months.size() == 0) {
            
            int year = Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR);
            changeYearData();
            changeDayData(year, changeMonthData(year));
        }
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        WheelView yearView = new WheelView(activity.getBaseContext());
        yearView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        yearView.setTextSize(textSize);
        yearView.setTextColor(textColorNormal, textColorFocus);
        yearView.setLineVisible(lineVisible);
        yearView.setLineColor(lineColor);
        yearView.setOffset(offset);
        layout.addView(yearView);
        TextView yearTextView = new TextView(activity);
        yearTextView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        yearTextView.setTextSize(textSize);
        yearTextView.setTextColor(textColorFocus);
        if (!TextUtils.isEmpty(yearLabel)) {
            yearTextView.setText(yearLabel);
        }
        layout.addView(yearTextView);
        final WheelView monthView = new WheelView(activity.getBaseContext());
        monthView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        monthView.setTextSize(textSize);
        monthView.setTextColor(textColorNormal, textColorFocus);
        monthView.setLineVisible(lineVisible);
        monthView.setLineColor(lineColor);
        monthView.setOffset(offset);
        layout.addView(monthView);
        TextView monthTextView = new TextView(activity);
        monthTextView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        monthTextView.setTextSize(textSize);
        monthTextView.setTextColor(textColorFocus);
        if (!TextUtils.isEmpty(monthLabel)) {
            monthTextView.setText(monthLabel);
        }
        layout.addView(monthTextView);
        final WheelView dayView = new WheelView(activity.getBaseContext());
        dayView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        dayView.setTextSize(textSize);
        dayView.setTextColor(textColorNormal, textColorFocus);
        dayView.setLineVisible(lineVisible);
        dayView.setLineColor(lineColor);
        dayView.setOffset(offset);
        layout.addView(dayView);
        TextView dayTextView = new TextView(activity);
        dayTextView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        dayTextView.setTextSize(textSize);
        dayTextView.setTextColor(textColorFocus);
        if (!TextUtils.isEmpty(dayLabel)) {
            dayTextView.setText(dayLabel);
        }
        layout.addView(dayTextView);
        if (mode == YEAR_MONTH) {
            
            dayView.setVisibility(View.GONE);
            dayTextView.setVisibility(View.GONE);
        } else if (mode == MONTH_DAY) {
            
            yearView.setVisibility(View.GONE);
            yearTextView.setVisibility(View.GONE);
        }
        if (mode != MONTH_DAY) {
            if (!TextUtils.isEmpty(yearLabel)) {
                yearTextView.setText(yearLabel);
            }
            changeYearData();
            if (selectedYearIndex == 0) {
                yearView.setItems(years);
            } else {
                yearView.setItems(years, selectedYearIndex);
            }
            yearView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                @Override
                public void onSelected(boolean isUserScroll, int selectedIndex, String item) {
                    selectedYearIndex = selectedIndex;
                    
                    int year = DateUtils.trimZero(item);
                    changeDayData(year, changeMonthData(year));
                    monthView.setItems(months, selectedMonthIndex);
                    dayView.setItems(days, selectedDayIndex);
                }
            });
        }
        if (!TextUtils.isEmpty(monthLabel)) {
            monthTextView.setText(monthLabel);
        }
        if (selectedMonthIndex == 0) {
            monthView.setItems(months);
        } else {
            monthView.setItems(months, selectedMonthIndex);
        }
        monthView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(boolean isUserScroll, int selectedIndex, String item) {
                selectedMonthIndex = selectedIndex;
                if (mode != YEAR_MONTH) {
                    changeDayData(DateUtils.trimZero(years.get(selectedYearIndex)), DateUtils.trimZero(item));
                    dayView.setItems(days, selectedDayIndex);
                }
            }
        });
        if (mode != YEAR_MONTH) {
            if (!TextUtils.isEmpty(dayLabel)) {
                dayTextView.setText(dayLabel);
            }
            dayView.setItems(days, selectedDayIndex);
            dayView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                @Override
                public void onSelected(boolean isUserScroll, int selectedIndex, String item) {
                    selectedDayIndex = selectedIndex;
                }
            });
        }
        return layout;
    }

    private void changeYearData() {
        years.clear();
        if (startYear < endYear) {
            
            for (int i = startYear; i <= endYear; i++) {
                years.add(String.valueOf(i));
            }
        } else {
            
            for (int i = startYear; i >= endYear; i--) {
                years.add(String.valueOf(i));
            }
        }
    }

    private int changeMonthData(int year) {
        String preSelectMonth = months.size() > selectedMonthIndex ? months.get(selectedMonthIndex) : null;
        months.clear();
        if (year == startYear) {
            for (int i = startMonth; i <= 12; i++) {
                months.add(DateUtils.fillZero(i));
            }
        } else if (year == endYear) {
            for (int i = 1; i <= endMonth; i++) {
                months.add(DateUtils.fillZero(i));
            }
        } else {
            for (int i = 1; i <= 12; i++) {
                months.add(DateUtils.fillZero(i));
            }
        }
        selectedMonthIndex = (preSelectMonth == null || !months.contains(preSelectMonth)) ? 0 : months.indexOf(preSelectMonth);
        return DateUtils.trimZero(months.get(selectedMonthIndex));
    }

    private void changeDayData(int year, int month) {
        String preSelectDay = days.size() > selectedDayIndex ? days.get(selectedDayIndex) : null;
        days.clear();
        int maxDays = DateUtils.calculateDaysInMonth(year, month);
        if (year == startYear && month == startMonth) {
            for (int i = startDay; i <= maxDays; i++) {
                days.add(DateUtils.fillZero(i));
            }
            selectedDayIndex = (preSelectDay == null || !days.contains(preSelectDay)) ? 0 : days.indexOf(preSelectDay);
        } else if (year == endYear && month == endMonth) {
            for (int i = 1; i <= endDay; i++) {
                days.add(DateUtils.fillZero(i));
            }
            selectedDayIndex = (preSelectDay == null || !days.contains(preSelectDay)) ? 0 : days.indexOf(preSelectDay);
        } else {
            for (int i = 1; i <= maxDays; i++) {
                days.add(DateUtils.fillZero(i));
            }
            if (selectedDayIndex >= maxDays) {
                
                selectedDayIndex = days.size() - 1;
            }
        }
    }

    @Override
    protected void onSubmit() {
        if (onDatePickListener == null) {
            return;
        }
        String year = getSelectedYear();
        String month = getSelectedMonth();
        String day = getSelectedDay();
        switch (mode) {
            case YEAR_MONTH:
                ((OnYearMonthPickListener) onDatePickListener).onDatePicked(year, month);
                break;
            case MONTH_DAY:
                ((OnMonthDayPickListener) onDatePickListener).onDatePicked(month, day);
                break;
            default:
                ((OnYearMonthDayPickListener) onDatePickListener).onDatePicked(year, month, day);
                break;
        }
    }

    public String getSelectedYear() {
        return years.get(selectedYearIndex);
    }

    public String getSelectedMonth() {
        return months.get(selectedMonthIndex);
    }

    public String getSelectedDay() {
        return days.get(selectedDayIndex);
    }

    protected interface OnDatePickListener {

    }

    public interface OnYearMonthDayPickListener extends OnDatePickListener {

        void onDatePicked(String year, String month, String day);

    }

    public interface OnYearMonthPickListener extends OnDatePickListener {

        void onDatePicked(String year, String month);

    }

    public interface OnMonthDayPickListener extends OnDatePickListener {

        void onDatePicked(String month, String day);

    }

}
