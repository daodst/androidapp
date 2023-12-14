package com.benny.openlauncher.util;

import static com.benny.openlauncher.util.TabActionsHelper.FLAG_ACQUISITION_OF_VOTING_RIGHTS;
import static com.benny.openlauncher.util.TabActionsHelper.FLAG_ADDRESS;
import static com.benny.openlauncher.util.TabActionsHelper.FLAG_CHOOSEPOINT;
import static com.benny.openlauncher.util.TabActionsHelper.FLAG_COLLECT_MONEY;
import static com.benny.openlauncher.util.TabActionsHelper.FLAG_COMPUTING_POWER_MANAGEMENT;
import static com.benny.openlauncher.util.TabActionsHelper.FLAG_CROSS_CHAIN_BRIDGE;
import static com.benny.openlauncher.util.TabActionsHelper.FLAG_DAO;
import static com.benny.openlauncher.util.TabActionsHelper.FLAG_DAPP;
import static com.benny.openlauncher.util.TabActionsHelper.FLAG_DAPP_STORE;
import static com.benny.openlauncher.util.TabActionsHelper.FLAG_DST_NUMBER;
import static com.benny.openlauncher.util.TabActionsHelper.FLAG_DST_REDUCE_PLAN;
import static com.benny.openlauncher.util.TabActionsHelper.FLAG_EXCHANGE;
import static com.benny.openlauncher.util.TabActionsHelper.FLAG_ONE_KEY_PUBLISH_COIN;
import static com.benny.openlauncher.util.TabActionsHelper.FLAG_RECRUIT;
import static com.benny.openlauncher.util.TabActionsHelper.FLAG_SHARE;
import static com.benny.openlauncher.util.TabActionsHelper.FLAG_TRANSFER;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.HomeActivity;
import com.benny.openlauncher.manager.Setup;
import com.benny.openlauncher.model.App;
import com.benny.openlauncher.model.Item;

import net.gsantner.opoc.preference.OtherSpUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import common.app.utils.AllUtils;
import common.app.utils.LogUtil;
import io.reactivex.functions.Action;


public class DesktopAtomHelper {
    @SuppressLint("StaticFieldLeak")
    private static DesktopAtomHelper sAtomHelper;

    public Context mContext;
    public DatabaseHelper _db;

    
    private ConcurrentHashMap<String, App> currentAllApps = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Item> databaseAllApps = new ConcurrentHashMap<>();
    
    private Map<String, Item> differenceSet = new ConcurrentHashMap<>();

    private DesktopAtomHelper(Context context) {
        mContext = context;
        _db = Setup.dataManager();
    }

    public static DesktopAtomHelper instance(Context context) {
        if (null == sAtomHelper) {
            sAtomHelper = new DesktopAtomHelper(context);
        }
        return sAtomHelper;
    }

    
    public void arrangeDockAtom(int mode) {
        
        if (mode == OtherSpUtils.HOME_PAGE_APP_LOAD_MODE_TILED) {
            
            initDefaultTabs(TabActionsHelper.ACTION_CHAT, 0, R.mipmap.tab_chat);
            initDefaultTabs(TabActionsHelper.ACTION_WALLET, 1, R.mipmap.tab_wallet);
            initDefaultTabs(TabActionsHelper.ACTION_MINE, 2, R.mipmap.tab_mine);
        } else {
            
            initDefaultTabs(TabActionsHelper.ACTION_CHAT, 1, R.mipmap.tab_chat);
            initDefaultTabs(TabActionsHelper.ACTION_DEFAULT, 2, R.drawable.item_drawer);
            initDefaultTabs(TabActionsHelper.ACTION_WALLET, 3, R.mipmap.tab_wallet);
            initDefaultTabs(TabActionsHelper.ACTION_MINE, 4, R.mipmap.tab_mine);
        }
    }

    
    public void initDefaultApps() {
        Log.d("jues", "CurrentItem:" + 0);
        int _x = AppSettings.get().getDesktopColumnCount();
        int _y = AppSettings.get().getDesktopRowCount() ;

        String[] names = {mContext.getString(R.string.desktop_computing_power_management), mContext.getString(R.string.desktop_dao), mContext.getString(R.string.desktop_dst_number),
                mContext.getString(R.string.desktop_transfer), mContext.getString(R.string.desktop_collect_money), mContext.getString(R.string.desktop_cross_chain_bridge),
                mContext.getString(R.string.desktop_one_key_publish_coin), mContext.getString(R.string.desktop_acquisition_of_voting_rights),
                mContext.getString(R.string.desktop_share), mContext.getString(R.string.desktop_dapp), mContext.getString(R.string.desktop_choosepoint), mContext.getString(R.string.desktop_address),
                mContext.getString(R.string.desktop_exchange), mContext.getString(R.string.desktop_dapp_store),
                mContext.getString(R.string.super_node_recruit), mContext.getString(R.string.dst_reduce_plan)};
        
        String[] labels = {"desktop_computing_power_management", "desktop_dao", "desktop_dst_number",
                "desktop_cross_chain_bridge", "desktop_one_key_publish_coin",
                "desktop_transfer", "desktop_collect_money", "desktop_acquisition_of_voting_rights",
                "desktop_share", "desktop_dapp", "desktop_choosepoint", "desktop_address",
                "desktop_exchange", "desktop_dapp_store", "super_node_recruit", "dst_reduce_plan"};
        int[] flags = {FLAG_COMPUTING_POWER_MANAGEMENT, FLAG_DAO, FLAG_DST_NUMBER,
                FLAG_CROSS_CHAIN_BRIDGE, FLAG_ONE_KEY_PUBLISH_COIN,
                FLAG_TRANSFER, FLAG_COLLECT_MONEY, FLAG_ACQUISITION_OF_VOTING_RIGHTS,
                FLAG_SHARE, FLAG_DAPP, FLAG_CHOOSEPOINT, FLAG_ADDRESS,
                FLAG_EXCHANGE, FLAG_DAPP_STORE, FLAG_RECRUIT, FLAG_DST_REDUCE_PLAN};
        int[] drawables = {R.mipmap.computing_power_management, R.mipmap.dao, R.mipmap.dst_number,
                R.mipmap.cross_chain_bridge, R.mipmap.one_key_publish_coin,
                R.mipmap.transfer, R.mipmap.collect_money, R.mipmap.acquisition_of_voting_rights,
                R.mipmap.share, R.mipmap.dapp, R.mipmap.choosepoint, R.mipmap.address,
                R.mipmap.jiaoyi_exchange, R.mipmap.icon_dapp_store, R.mipmap.icon_super_node_recruit,
                R.mipmap.ico_dst_reduce_plan};
        
        int currentX = 0, currentY = 0;
        for (int i = 0, j = labels.length; i < j; i++) {
            if (currentX >= _x) {
                currentX = 0;
                currentY++;
            }
            
            if (currentY >= _y) return;
            addStables(drawables[i], labels[i], currentX, currentY, flags[i]);
            currentX++;
        }
    }


    
    public void updateDockAtom(int mode) {
        int oldMode = OtherSpUtils.getInstance().getHomePageAppLoadMode();
        if (oldMode == mode) return;
        List<Item> dockItems = _db.getDock();
        if (mode == OtherSpUtils.HOME_PAGE_APP_LOAD_MODE_TILED) {
            if (dockItems.size() == 5) {
                Iterator<Item> iterator = dockItems.iterator();
                
                while (iterator.hasNext()) {
                    Item item = iterator.next();
                    if (item._x == 2) {
                        iterator.remove();
                        _db.deleteItem(item, true);
                    }
                }
                for (Item item : dockItems) {
                    if (item._x > 2) item._x--;
                    _db.saveItem(item);
                }
                
                Setup.appSettings().setInt(mContext.getString(R.string.pref_key__dock_columns), 4);
                OtherSpUtils.getInstance().putHomePageAppLoaded(false);
                
                
            } else return;
        } else if (mode == OtherSpUtils.HOME_PAGE_APP_LOAD_MODE_DRAWER) {
            if (dockItems.size() == 4) {
                
                for (Item item : dockItems) {
                    if (item._x > 1) item._x++;
                    _db.saveItem(item);
                }
                
                initDefaultTabs(TabActionsHelper.ACTION_DEFAULT, 2, R.drawable.item_drawer);
                
                Setup.appSettings().setInt(mContext.getString(R.string.pref_key__dock_columns), 5);
                
                List<App> allApps = Setup.appLoader().getAllApps(mContext, false);
                for (App app : allApps) _db.deleteItems(app);
            } else return;
        }
        OtherSpUtils.getInstance().putHomePageAppLoadMode(mode);
        try {
            HomeActivity._launcher.recreate();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("updateDockAtom:", "HomeActivity._launcher ");
        }
    }

    
    public void updateDesktopAtom() {
        if (!OtherSpUtils.getInstance().getHomeDefaultAppNeedChange()) return;
        List<List<Item>> items = _db.getDesktop();

        int page = AppSettings.get().getDesktopPageCurrent();
        Iterator<Item> iterator = items.get(page).listIterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            iterator.remove();
            _db.deleteItem(item, false);
        }
        Iterator<Item> iterator2 = items.get(0).listIterator();
        while (iterator2.hasNext()) {
            Item item = iterator2.next();
            if (item._type == Item.Type.STABLE || item._type == Item.Type.APP || item._type == Item.Type.CUSTOM_WIDGET) {
                iterator2.remove();
                _db.deleteItem(item, false);
            }
        }
        OtherSpUtils.getInstance().putHomeDefaultAppNeedChange(false);
        
        initDefaultApps();
        
        List<Item> dockItems = _db.getDock();
        Iterator<Item> dockIterator = dockItems.listIterator();
        while (dockIterator.hasNext()) {
            Item item = dockIterator.next();
            dockIterator.remove();
            _db.deleteItem(item, false);
        }
        
        arrangeDockAtom(1);
    }

    
    private void initDefaultTabs(int action, int x, int resource) {
        Item appDrawerBtnItem = Item.newActionItem(action);
        appDrawerBtnItem._x = x;
        appDrawerBtnItem._icon = ContextCompat.getDrawable(mContext, resource);
        appDrawerBtnItem._icon.setBounds(0, 0, appDrawerBtnItem._icon.getIntrinsicWidth(), appDrawerBtnItem._icon.getIntrinsicHeight());

        int page = AppSettings.get().getDesktopPageCurrent();
        _db.saveItem(appDrawerBtnItem, page, Definitions.ItemPosition.Dock);
    }

    
    private void addStables(int resourceId, String name, int x, int y, int actionValue) {
        Item stableBtnItem = Item.newStableItem(ContextCompat.getDrawable(mContext, resourceId), name, actionValue);
        stableBtnItem._x = x;
        stableBtnItem._y = y;
        int page = AppSettings.get().getDesktopPageCurrent();
        _db.saveItem(stableBtnItem, page, Definitions.ItemPosition.Desktop);
    }

    
    public void initAllApps(List<App> allApps, Action action) {
        new Thread(() -> {
            int _x = AppSettings.get().getDesktopColumnCount(); 
            int _y = Math.abs(AppSettings.get().getDesktopRowCount());    
            int page = AppSettings.get().getDesktopPageCurrent();
            int currentPage = page + 1;
            int tempPage = 1;
            LogUtil.d("DesktopAtomHelper", "page = " + currentPage);
            
            int x = 0, y = 0;
            for (int i = 0, j = allApps.size(); i < j; i++) {
                x = i % _x;
                
                y = (i - (tempPage - 1) * (_x * _y)) / _x;

                if (i >= tempPage * (_x * _y)) {
                    currentPage++;
                    tempPage++;
                    x = y = 0;
                }
                
                App app = allApps.get(i);
                Item appItem = Item.newAppItem(app);
                appItem._x = x;
                appItem._y = y;
                _db.saveItem(appItem, currentPage, Definitions.ItemPosition.Desktop);
            }
            OtherSpUtils.getInstance().putHomePageAppLoaded(true);
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    action.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }

    
    private final Executor mUpdateExecutor = Executors.newSingleThreadExecutor();

    public synchronized void updateDesktopApps(List<App> apps, Action action) {
        mUpdateExecutor.execute(() -> {
            updateDesktopApps(apps);
            
            if (null != action) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        action.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });

        
    }

    
    public synchronized void updateDesktopApps(List<App> apps) {
        currentAllApps.clear();
        databaseAllApps.clear();
        differenceSet.clear();
        for (App app : apps) {
            currentAllApps.put(AllUtils.getMD5(app._packageName + app._label), app);
        }

        List<List<Item>> items = _db.getDesktop();
        
        for (List<Item> pages : items) circleItems(pages);
        Set<String> oneSet = currentAllApps.keySet();
        Set<String> twoSet = databaseAllApps.keySet();
        for (String oneKey : oneSet) {
            
            App app = currentAllApps.get(oneKey);
            if (!twoSet.contains(oneKey)) {
                Item item = Item.newAppItem(app);
                item._tag = "new";
                differenceSet.put(oneKey, item);
            }
        }
        for (String twoKey : twoSet) {
            
            Item item = databaseAllApps.get(twoKey);
            if (!oneSet.contains(twoKey)) differenceSet.put(twoKey, item);
        }
        
        Set<String> differSet = differenceSet.keySet();
        for (String differKey : differSet) {
            Item item = differenceSet.get(differKey);
            if (item != null && !TextUtils.isEmpty(item.get_tag())) {
                
                addAppToDataBase(item);
            } else {
                
                _db.deleteItem(item, false);
            }
        }
    }

    private void circleItems(List<Item> items) {
        for (Item item : items) {
            
            if (item._type == Item.Type.APP) {
                databaseAllApps.put(item._id, item);
            } else if (item._type == Item.Type.GROUP) {
                
                circleItems(item._items);
            }
        }
    }

    private void addAppToDataBase(Item item) {
        item._tag = "";
        List<List<Item>> items = _db.getDesktop();
        if (items.size() == 2) {
            item._x = 0;
            item._y = 0;
            _db.saveItem(item, 2, Definitions.ItemPosition.Desktop);
            return;
        }

        
        List<Item> last = items.get(items.size() - 1);

        int _x = AppSettings.get().getDesktopColumnCount(); 
        int _y = Math.abs(AppSettings.get().getDesktopRowCount());    
        if (last.size() > 0) {
            
            Item lastItem = last.get(last.size() - 1);
            int curX = lastItem._x;
            int curY = lastItem._y;
            if ((curX + 1) * (curY + 1) == _x * _y) {
                
                item._x = 0;
                item._y = 0;
                _db.saveItem(item, items.size(), Definitions.ItemPosition.Desktop);
            } else {
                
                if (curX + 1 < _x) {
                    item._x = curX + 1;
                    item._y = curY;
                } else {
                    item._x = 0;
                    item._y = curY + 1;
                }
                _db.saveItem(item, items.size() - 1, Definitions.ItemPosition.Desktop);
            }
        } else {
            item._x = 0;
            item._y = 0;
            _db.saveItem(item, items.size() - 1, Definitions.ItemPosition.Desktop);
        }

    }
}
