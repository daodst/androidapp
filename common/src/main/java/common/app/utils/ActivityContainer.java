

package common.app.utils;

import android.app.Activity;

import java.util.ArrayList;


public class ActivityContainer {

    private ArrayList<Activity> mList = new ArrayList<>();

    public ArrayList<Activity> getList() {
        return mList;
    }

    public Activity getLastActivity() {
        return mList.get(mList.size() - 1);
    }


    private ActivityContainer() {
    }

    public static final class LoadActivityContainer {
        public static ActivityContainer INSTANCE = new ActivityContainer();
    }

    public static ActivityContainer getInstance() {
        return LoadActivityContainer.INSTANCE;
    }
}
