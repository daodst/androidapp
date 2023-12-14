package com.appwidget;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.benny.openlauncher.R;


@Deprecated
public class GatewayWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "jues_WidgetUpdate";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "Updating Gateway Widgets.");

        for (int appWidgetId : appWidgetIds) {
            @SuppressLint("RemoteViewLayout")
            RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_gateway);
            updateViews.setTextViewText(R.id.widget_gateway_subtitle, context.getString(R.string.widget_gateway_block_height, "0"));
            updateViews.setTextViewText(R.id.widget_gateway_signal, "0");
            appWidgetManager.updateAppWidget(appWidgetId, updateViews);
        }

        try {
            Class<?> aClass = Class.forName("com.app.appwidget.GatewayService");
            Intent intent = new Intent(context, aClass);
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
