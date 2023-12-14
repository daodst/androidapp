package com.benny.openlauncher.receivers;

import static android.content.Intent.ACTION_WALLPAPER_CHANGED;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;


public class WallpaperReceiver extends BroadcastReceiver {
    private final OnWallPaperChange mPaperChange;

    public WallpaperReceiver(OnWallPaperChange paperChange) {
        mPaperChange = paperChange;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), ACTION_WALLPAPER_CHANGED)) {
            mPaperChange.onWallPaperChange();
        }
    }

    public interface OnWallPaperChange {
        void onWallPaperChange();
    }
}
