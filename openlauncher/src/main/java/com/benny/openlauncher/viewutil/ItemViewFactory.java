package com.benny.openlauncher.viewutil;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.benny.openlauncher.activity.HomeActivity;
import com.benny.openlauncher.manager.Setup;
import com.benny.openlauncher.model.App;
import com.benny.openlauncher.model.Item;
import com.benny.openlauncher.notifications.ActionNotificationListener;
import com.benny.openlauncher.notifications.MDNotificationListener;
import com.benny.openlauncher.util.Definitions;
import com.benny.openlauncher.util.DragAction;
import com.benny.openlauncher.util.DragHandler;
import com.benny.openlauncher.util.Tool;
import com.benny.openlauncher.widget.AppItemView;
import com.benny.openlauncher.widget.CustomBaseWidget;
import com.benny.openlauncher.widget.CustomChatWidget;
import com.benny.openlauncher.widget.CustomGatewayWidget;
import com.benny.openlauncher.widget.WidgetContainer;
import com.benny.openlauncher.widget.WidgetView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemViewFactory {
    private static Logger LOG = LoggerFactory.getLogger("ItemViewFactory");

    public static View getItemView(final Context context, final DesktopCallback callback, final DragAction.Action type, final Item item, Boolean showLabel) {
        View view = null;
        if (item.getType().equals(Item.Type.WIDGET)) {
            view = getWidgetView(context, callback, type, item);
        } else if (item.getType().equals(Item.Type.CUSTOM_WIDGET)) {
            view = getCustomWidgetView(context, callback, type, item);
        } else {
            AppItemView.Builder builder = new AppItemView.Builder(context);
            builder.setIconSize(Setup.appSettings().getIconSize());
            builder.vibrateWhenLongPress(Setup.appSettings().getGestureFeedback());
            if (item.getType() != Item.Type.ACTION) {
                builder.withOnLongClick(item, type, callback);
            }
            switch (type) {
                case DRAWER:
                    builder.setLabelVisibility(Setup.appSettings().getDrawerShowLabel());
                    builder.setTextColor(Setup.appSettings().getDrawerLabelColor());
                    break;
                case GROUP:
                case DESKTOP:
                default:
                    builder.setLabelVisibility(Setup.appSettings().getDesktopShowLabel());
                    builder.setTextColor(Color.WHITE);
                    break;
            }
            if (showLabel != null) {
                boolean labelVisibility = showLabel.booleanValue();
                builder.setLabelVisibility(labelVisibility);
            }

            switch (item.getType()) {
                case APP:
                    final App app = Setup.appLoader().findItemApp(item);
                    if (app == null) break;
                    view = builder.setAppItem(item).getView();

                    if (Setup.appSettings().getNotificationStatus()) {
                        MDNotificationListener.setNotificationCallback(app.getPackageName(), (MDNotificationListener.NotificationCallback) view);
                    }
                    break;
                case SHORTCUT:
                    view = builder.setShortcutItem(item).getView();
                    break;
                case STABLE:
                    view = builder.setStableItem(item).getView();
                    ActionNotificationListener.getInstance().setNotificationCallback("action" + item._actionValue, (MDNotificationListener.NotificationCallback) view);
                    break;
                case GROUP:
                    view = builder.setGroupItem(context, callback, item).getView();
                    view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    break;
                case ACTION:
                    view = builder.setActionItem(item).getView();
                    
                    
                    ActionNotificationListener.getInstance().setNotificationCallback("action" + item._actionValue, (MDNotificationListener.NotificationCallback) view);
                    
                    break;
            }
        }

        
        if (view != null) {
            view.setTag(item);
        }

        return view;
    }

    public static View getItemView(final Context context, final DesktopCallback callback, final DragAction.Action type, final Item item) {
        return getItemView(context, callback, type, item, null);
    }

    public static View getWidgetView(final Context context, final DesktopCallback callback, final DragAction.Action type, final Item item) {
        if (HomeActivity._appWidgetHost == null) return null;

        AppWidgetProviderInfo appWidgetInfo = HomeActivity._appWidgetManager.getAppWidgetInfo(item.getWidgetValue());

        
        if (appWidgetInfo == null) {
            if (item._label.contains(Definitions.DELIMITER)) {
                String[] cnSplit = item._label.split(Definitions.DELIMITER);
                ComponentName cn = new ComponentName(cnSplit[0], cnSplit[1]);

                
                int appWidgetId = item._widgetValue;
                if (HomeActivity._appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, cn)) {
                    appWidgetInfo = HomeActivity._appWidgetManager.getAppWidgetInfo(appWidgetId);
                    
                    HomeActivity._db.updateItem(item);
                } else {
                    LOG.error("Unable to bind app widget id: {}; removing from database", cn);
                    HomeActivity._appWidgetHost.deleteAppWidgetId(appWidgetId);
                    HomeActivity._db.deleteItem(item, false);
                    return null;
                }
            } else {
                
                LOG.debug("Unable to identify Widget for rehydration; removing from database");
                HomeActivity._db.deleteItem(item, false);
                return null;
            }
        }

        final WidgetView widgetView = (WidgetView) HomeActivity._appWidgetHost.createView(context, item.getWidgetValue(), appWidgetInfo);
        widgetView.setAppWidget(item.getWidgetValue(), appWidgetInfo);

        final WidgetContainer widgetContainer = new WidgetContainer(context, widgetView, item);

        
        
        widgetView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (Setup.appSettings().getDesktopLock()) {
                    return false;
                }
                if (Setup.appSettings().getGestureFeedback()) {
                    Tool.vibrate(view);
                }
                DragHandler.startDrag(widgetContainer, item, DragAction.Action.DESKTOP, callback);
                return true;
            }
        });

        widgetView.post(new Runnable() {
            @Override
            public void run() {
                widgetContainer.updateWidgetOption(item);
            }
        });

        return widgetContainer;
    }

    
    public static View getCustomWidgetView(final Context context, final DesktopCallback callback, final DragAction.Action type, final Item item) {
        CustomBaseWidget widgetView = new CustomBaseWidget(context,item);
        switch (item._customWidgetType) {
            case Item.GATEWAY:
                widgetView = new CustomGatewayWidget(context, item);
                break;
            case Item.CHAT:
                widgetView = new CustomChatWidget(context, item);
                break;
        }

        final CustomBaseWidget widgetContainer = widgetView;
        widgetContainer.seItemLongListener(view -> {
            if (Setup.appSettings().getDesktopLock()) {
                return false;
            }
            if (Setup.appSettings().getGestureFeedback()) {
                Tool.vibrate(view);
            }
            DragHandler.startDrag(widgetContainer, item, DragAction.Action.DESKTOP, callback);
            return true;
        });
        widgetContainer.setOnLongClickListener(view -> {
            if (Setup.appSettings().getDesktopLock()) {
                return false;
            }
            if (Setup.appSettings().getGestureFeedback()) {
                Tool.vibrate(view);
            }
            DragHandler.startDrag(widgetContainer, item, DragAction.Action.DESKTOP, callback);
            return true;
        });
        

        return widgetContainer;
    }
}
