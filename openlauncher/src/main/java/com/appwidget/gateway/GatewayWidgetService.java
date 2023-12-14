package com.appwidget.gateway;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import com.appwidget.GatewayWidgetProvider;
import com.benny.openlauncher.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


@Deprecated
public class GatewayWidgetService extends Service {
    protected static final String TAG = "jues_WidgetUpdate";

    private static final DateFormat df = new SimpleDateFormat("hh:mm:ss");

    protected static final String BROADCAST_ACTION = "update_gateway_widget";

    private String mBlockHeight;
    private long mPing;
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;

    public GatewayWidgetService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
        getBlockHeight();
        getPing();
        
        if (null == mAlarmManager) mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent receiverIntent = new Intent(this, AlarmReceiver.class);
        receiverIntent.setAction(BROADCAST_ACTION);
        mPendingIntent = PendingIntent.getBroadcast(this, 0, receiverIntent, PendingIntent.FLAG_IMMUTABLE);
        
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 5 * 1000, mPendingIntent);
        return super.onStartCommand(receiverIntent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mAlarmManager && null != mPendingIntent) {
            mAlarmManager.cancel(mPendingIntent);
        }
    }

    
    protected void getBlockHeight(){};

    
    protected void getPing(){};

    protected void setBlockHeight(String blockHeight) {
        this.mBlockHeight = blockHeight;
        updateWidgetUi();
    }

    protected void setPing(long ping) {
        this.mPing = ping;
        updateWidgetUi();
    }

    private void updateWidgetUi() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] idLs = appWidgetManager.getAppWidgetIds(new ComponentName(getPackageName(), GatewayWidgetProvider.class.getName()));
        
        
        for (int widgetId : idLs) {
            String currentTime = df.format(new Date());

            @SuppressLint("RemoteViewLayout") RemoteViews updateViews = new RemoteViews(getPackageName(), R.layout.widget_gateway);
            updateViews.setTextViewText(R.id.widget_gateway_subtitle, getString(R.string.widget_gateway_block_height, mBlockHeight));
            
            
            String ping = null;
            int resource = R.mipmap.icon_gateway_wifi0;
            if (0 < mPing && mPing <= 500) {
                ping = getString(R.string.widget_gateway_signal_3) + " Ping ";
                resource = R.mipmap.icon_gateway_wifi3;
            } else if (500 < mPing && mPing <= 1000) {
                ping = getString(R.string.widget_gateway_signal_2) + " Ping ";
                resource = R.mipmap.icon_gateway_wifi2;
            } else if (mPing > 1000) {
                ping = getString(R.string.widget_gateway_signal_1) + " Ping ";
                resource = R.mipmap.icon_gateway_wifi1;
            } else ping = "-- Ping ";

            updateViews.setTextViewText(R.id.widget_gateway_signal, ping + mPing);
            updateViews.setImageViewResource(R.id.widget_gateway_signal_img, resource);
            appWidgetManager.updateAppWidget(widgetId, updateViews);
        }
    }

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Objects.equals(action, BROADCAST_ACTION)) {
                
                
                try {
                    Class<?> aClass = Class.forName("com.app.appwidget.GatewayService");
                    Intent service = new Intent(context, aClass);
                    context.startService(service);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
