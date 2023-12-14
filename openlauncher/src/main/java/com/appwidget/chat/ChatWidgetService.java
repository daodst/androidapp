package com.appwidget.chat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.amulyakhare.textdrawable.TextDrawable;
import com.appwidget.ChatWidgetProvider;
import com.benny.openlauncher.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import common.app.pojo.ChatWidgetItemEntity;
import common.app.utils.AppWidgetUtils;


@Deprecated
public class ChatWidgetService extends RemoteViewsService {
    protected static final String TAG = "jues_ChatWidgetService";
    public static final String ACTION_CHAT_WIDGET = "action_chat_widget";

    private ChatWidgetViewFactory mViewFactory;

    private BroadcastReceiver mBroadcastReceiver;
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;

    public ChatWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(TAG, "Chat:  RemoteViewsFactory");
        mViewFactory = new ChatWidgetViewFactory(this.getApplicationContext(), intent);
        return mViewFactory;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        
        registerReceiver(mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getBundleExtra("bundle");
                String json = bundle.getString("data");
                Type type = new TypeToken<List<ChatWidgetItemEntity>>() {
                }.getType();
                List<ChatWidgetItemEntity> entityList = new GsonBuilder().create().fromJson(json, type);
                gotoUpdate(entityList);
                Log.d(TAG, " filter：" + AppWidgetUtils.ChatWidgetDataFilter);
            }
        }, new IntentFilter(AppWidgetUtils.ChatWidgetDataFilter));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
        
        

        
        if (null == mAlarmManager)
            mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent receiverIntent = new Intent(this, ChatWidgetReceiver.class);
        receiverIntent.setAction(ACTION_CHAT_WIDGET);
        mPendingIntent = PendingIntent.getBroadcast(this, 0, receiverIntent, PendingIntent.FLAG_IMMUTABLE);
        
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 5 * 1000, mPendingIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    public void gotoUpdate(List<ChatWidgetItemEntity> entityList) {
        
        
        if (null == mViewFactory) return;
        mViewFactory.setNewData(entityList);
        
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int[] appWidgetID = appWidgetManager.getAppWidgetIds(new ComponentName(getPackageName(), ChatWidgetProvider.class.getName()));

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetID, R.id.widget_chat_grid);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBroadcastReceiver) unregisterReceiver(mBroadcastReceiver);
        if (null != mAlarmManager && null != mPendingIntent) {
            mAlarmManager.cancel(mPendingIntent);
        }
    }
}



@Deprecated
class ChatWidgetViewFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = "jues_ChatWidgetService";
    private static final int mCount = 5;
    private List<ChatWidgetItemEntity> mWidgetItems = new ArrayList<>();
    private final Context mContext;
    private final int mAppWidgetId;

    public ChatWidgetViewFactory(Context context, Intent intent) {
        
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    
    public void setNewData(List<ChatWidgetItemEntity> newData) {
        this.mWidgetItems = newData;
    }

    @Override
    public void onCreate() {
        Log.w(TAG, "Chat: onCreate ");
    }

    @Override
    public void onDataSetChanged() {
        
        Log.d(TAG, "Chat: nDataSetChanged  mWidgetItems.size() = " + mWidgetItems.size());
    }

    @Override
    public void onDestroy() {
        mWidgetItems.clear();
    }

    @Override
    public int getCount() {
        int count = Math.min(mWidgetItems.size(), mCount);
        
        return count;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        ChatWidgetItemEntity entity = mWidgetItems.get(position);

        @SuppressLint("RemoteViewLayout")
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_chat_item);
        if (entity.number <= 0) {
            remoteViews.setViewVisibility(R.id.widget_chat_num, View.INVISIBLE);
        } else {
            remoteViews.setViewVisibility(R.id.widget_chat_num, View.VISIBLE);
        }
        String num = Math.abs(entity.number) > 99 ? "……" : "" + Math.abs(entity.number);
        remoteViews.setTextViewText(R.id.widget_chat_num, num);
        AppWidgetTarget image = new AppWidgetTarget(mContext, R.id.widget_chat_avatar, remoteViews, R.id.widget_chat_avatar);
        Log.e(TAG, "：=========================================================");
        Log.e(TAG, "：" + entity.image);
        Log.e(TAG, "：==========================================================");
        if (!TextUtils.isEmpty(entity.image)) {
            RequestOptions options = RequestOptions.circleCropTransform();
            Glide.with(mContext).asBitmap().load(entity.image).apply(options).into(image);
        } else {
            Glide.with(mContext).asBitmap().load(
                    TextDrawable.builder().beginConfig().bold().endConfig()
                            .buildRect(entity.displayName, R.color.default_theme_color)
            ).into(image);
            
        }

        Intent fillInIntent = new Intent();
        
        fillInIntent.putExtra("position", position);
        fillInIntent.putExtra("roomId", entity.roomId);
        remoteViews.setOnClickFillInIntent(R.id.widget_chat_root, fillInIntent);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

