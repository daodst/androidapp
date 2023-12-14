package com.appwidget;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.appwidget.chat.ChatWidgetClickReceiver;
import com.appwidget.chat.ChatWidgetService;
import com.benny.openlauncher.R;


@Deprecated
public class ChatWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "jues_ChatWidgetProvider";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {

            
            
            Intent intent = new Intent(context, ChatWidgetService.class);
            
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            
            @SuppressLint("RemoteViewLayout")
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_chat);
            
            
            
            
            rv.setRemoteAdapter(R.id.widget_chat_grid, intent);

            
            
            
            rv.setEmptyView(R.id.widget_chat_grid, R.id.widget_chat_empty);

            
            
            

            Intent receiverIntent = new Intent(ChatWidgetClickReceiver.ACTION_CHAT_WIDGET_CLICK);
            
            
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, receiverIntent, PendingIntent.FLAG_IMMUTABLE);
            rv.setOnClickPendingIntent(R.id.widget_chat_ll, pendingIntent);

            Intent receiverIntent2 = new Intent(ChatWidgetClickReceiver.ACTION_CHAT_WIDGET_CLICK);
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, receiverIntent2, PendingIntent.FLAG_IMMUTABLE);
            rv.setPendingIntentTemplate(R.id.widget_chat_grid, pendingIntent2);

            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }

        try {
            Intent intent = new Intent(context, ChatWidgetService.class);
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
