package agency.tango.materialintroscreen.animations.translations;

import android.view.View;

import androidx.annotation.FloatRange;

import com.benny.openlauncher.R;

import agency.tango.materialintroscreen.animations.IViewTranslation;

public class EnterDefaultTranslation implements IViewTranslation {
    @Override
    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
        view.setTranslationY((1f - percentage) * view.getResources().getDimensionPixelOffset(R.dimen.y_offset));
    }
}
