package com.benny.openlauncher.widget;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import in.championswimmer.sfg.lib.SimpleFingerGestures;


public interface MyViewGroup {

    public abstract void addView(View view);

    public abstract void removeView(View view);

    public abstract void removeAllViews();

    public abstract ViewPropertyAnimator animate();

    public abstract boolean performClick();

    public abstract int getCellWidth();

    public abstract int getChildCount();

    public abstract int getCellHeight();

    public abstract int getCellSpanV();

    public abstract int getCellSpanH();

    public abstract void setBlockTouch(boolean v);

    public abstract void setGestures(@Nullable SimpleFingerGestures v);

    public abstract List<View> getAllCells();

    public abstract void setGridSize(int x, int y);

    public abstract void setHideGrid(boolean hideGrid);

    public abstract void resetOccupiedSpace();

    public abstract void projectImageOutlineAt(@NonNull Point newCoordinate, @Nullable Bitmap bitmap);

    public abstract void clearCachedOutlineBitmap();

    public abstract CellContainer.DragState peekItemAndSwap(@NonNull DragEvent event, @NonNull Point coordinate);

    public abstract CellContainer.DragState peekItemAndSwap(int x, int y, Point coordinate);

    public abstract void init();

    public abstract void animateBackgroundShow();

    public abstract void animateBackgroundHide();

    public abstract Point findFreeSpace();

    public abstract Point findFreeSpace(int spanX, int spanY);

    public abstract void addViewToGrid(@NonNull View view, int x, int y, int xSpan, int ySpan);

    public abstract void addViewToGrid(@NonNull View view);

    public abstract void setOccupied(boolean b, @NonNull CellContainer.LayoutParams lp);

    public abstract boolean checkOccupied(Point start, int spanX, int spanY);

    public abstract View coordinateToChildView(Point pos);

    public abstract CellContainer.LayoutParams coordinateToLayoutParams(int mX, int mY, int xSpan, int ySpan);

    public abstract void touchPosToCoordinate(@NonNull Point coordinate, int mX, int mY, int xSpan, int ySpan, boolean checkAvailability);

    public abstract void touchPosToCoordinate(Point coordinate, int mX, int mY, int xSpan, int ySpan, boolean checkAvailability, boolean checkBoundary);

    
    public int bottomHeight();
}
