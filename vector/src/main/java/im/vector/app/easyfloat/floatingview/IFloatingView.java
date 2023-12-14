package im.vector.app.easyfloat.floatingview;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;

public interface IFloatingView {

    FloatingView remove();

    FloatingView add();

    FloatingView attach(Activity activity);

    FloatingView attach(FrameLayout container);

    FloatingView detach(Activity activity);

    FloatingView detach(FrameLayout container);

    FloatingMagnetView getView();

    FloatingView icon(@DrawableRes int resId);

    FloatingView customView(FloatingMagnetView viewGroup);

    FloatingView customView(@LayoutRes int resource);

    FloatingView layoutParams(ViewGroup.LayoutParams params);

    FloatingView listener(MagnetViewListener magnetViewListener);

    FloatingView dragEnable(boolean dragEnable);

    FloatingView setAutoMoveToEdge(boolean autoMoveToEdge);

}
