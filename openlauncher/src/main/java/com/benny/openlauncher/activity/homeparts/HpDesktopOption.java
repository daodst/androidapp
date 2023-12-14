package com.benny.openlauncher.activity.homeparts;

import static com.benny.openlauncher.activity.HomeActivity.REQUEST_CREATE_APPWIDGET;
import static com.benny.openlauncher.activity.HomeActivity.REQUEST_PICK_APPWIDGET;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.HomeActivity;
import com.benny.openlauncher.interfaces.DialogListener;
import com.benny.openlauncher.manager.Setup;
import com.benny.openlauncher.model.Item;
import com.benny.openlauncher.util.AppSettings;
import com.benny.openlauncher.util.Definitions;
import com.benny.openlauncher.util.Tool;
import com.benny.openlauncher.widget.Desktop;
import com.benny.openlauncher.widget.DesktopOptionView;
import com.benny.openlauncher.widget.MyViewGroup;

import java.util.List;

public class HpDesktopOption implements DesktopOptionView.DesktopOptionViewListener, DialogListener.OnActionDialogListener {
    private HomeActivity _homeActivity;
    private OperateCallback mOperateCallback;

    public HpDesktopOption(HomeActivity homeActivity) {
        _homeActivity = homeActivity;
    }

    public void setOperateCallback(OperateCallback operateCallback) {
        mOperateCallback = operateCallback;
    }

    public void onRemovePage() {
        if (_homeActivity.getDesktop().isCurrentPageEmpty()) {
            _homeActivity.getDesktop().removeCurrentPage();
            return;
        }
    }

    public void onSetHomePage() {
        AppSettings appSettings = Setup.appSettings();
        appSettings.setDesktopPageCurrent(_homeActivity.getDesktop().getCurrentItem());
    }

    public void onPickWidget() {
        _homeActivity.ignoreResume = true;
        int appWidgetId = _homeActivity._appWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent("android.appwidget.action.APPWIDGET_PICK");
        pickIntent.putExtra("appWidgetId", appWidgetId);
        _homeActivity.startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);
    }

    public void onPickAction() {
        Setup.eventHandler().showPickAction(_homeActivity, this);
    }

    public void onLaunchSettings() {
        Setup.eventHandler().showLauncherSettings(_homeActivity);
    }

    public void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt("appWidgetId", -1);
        AppWidgetProviderInfo appWidgetInfo = _homeActivity._appWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            Intent intent = new Intent("android.appwidget.action.APPWIDGET_CONFIGURE");
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra("appWidgetId", appWidgetId);
            _homeActivity.startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
        } else {
            createWidget(data);
        }
    }

    public void createWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = _homeActivity._appWidgetManager.getAppWidgetInfo(appWidgetId);
        Item item = Item.newWidgetItem(appWidgetInfo.provider, appWidgetId);
        Desktop desktop = _homeActivity.getDesktop();
        List<MyViewGroup> pages = desktop.getPages();
        item._spanX = (appWidgetInfo.minWidth - 1) / pages.get(desktop.getCurrentItem()).getCellWidth() + 1;
        item._spanY = (appWidgetInfo.minHeight - 1) / pages.get(desktop.getCurrentItem()).getCellHeight() + 1;
        Point point = desktop.getCurrentPage().findFreeSpace(item._spanX, item._spanY);
        if (point != null) {
            item._x = point.x;
            item._y = point.y;

            
            _homeActivity._db.saveItem(item, desktop.getCurrentItem(), Definitions.ItemPosition.Desktop);
            desktop.addItemToPage(item, desktop.getCurrentItem());
            if (null != mOperateCallback) mOperateCallback.onCallback(true);
        } else {
            if (null != mOperateCallback) mOperateCallback.onCallback(true);
            Tool.toast(_homeActivity, R.string.toast_not_enough_space);
        }
    }

    @Deprecated
    public void createCustomWidget(int widgetType) {
        Item item = Item.newCustomWidgetItem(widgetType);
        Desktop desktop = _homeActivity.getDesktop();
        List<MyViewGroup> pages = desktop.getPages();
        item._spanX = 4;
        item._spanY = 1;
        Point point = desktop.getCurrentPage().findFreeSpace(item._spanX, item._spanY);
        if (point != null) {
            item._x = point.x;
            item._y = point.y;

            
            _homeActivity._db.saveItem(item, 1, Definitions.ItemPosition.Desktop);
            desktop.addItemToPage(item, 1);
            
        } else {
            
            Tool.toast(_homeActivity, R.string.toast_not_enough_space);
        }
    }

    @Override
    public void onAdd(int type) {
        Point pos = _homeActivity.getDesktop().getCurrentPage().findFreeSpace();
        if (pos != null) {
            _homeActivity.getDesktop().addItemToCell(Item.newActionItem(type), pos.x, pos.y);
        } else {
            Tool.toast(_homeActivity, R.string.toast_not_enough_space);
        }
    }

    
    public interface OperateCallback {
        void onCallback(boolean result);
    }
}
