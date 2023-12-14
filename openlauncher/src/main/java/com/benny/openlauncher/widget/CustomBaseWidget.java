package com.benny.openlauncher.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.benny.openlauncher.activity.HomeActivity;
import com.benny.openlauncher.model.Item;


public class CustomBaseWidget extends FrameLayout {

    public CustomBaseWidget(Context context, Item item) {
        super(context,null);
    }

    public CustomBaseWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomBaseWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void seItemLongListener(OnLongClickListener listener) {
    }

    public void updateWidgetOption(Item item) {
        int cellWidth = HomeActivity.Companion.getLauncher().getDesktop().getCurrentPage().getCellWidth();
        int cellHeight = HomeActivity.Companion.getLauncher().getDesktop().getCurrentPage().getCellHeight();

        if (cellWidth < 1 || cellHeight < 1) {
            
            return;
        }

        Bundle newOps = new Bundle();
        newOps.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, item.getSpanX() * cellWidth);
        newOps.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, item.getSpanX() * cellWidth);
        newOps.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, item.getSpanY() * cellHeight);
        newOps.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, item.getSpanY() * cellHeight);
        HomeActivity._appWidgetManager.updateAppWidgetOptions(item.getWidgetValue(), newOps);
    }
}
