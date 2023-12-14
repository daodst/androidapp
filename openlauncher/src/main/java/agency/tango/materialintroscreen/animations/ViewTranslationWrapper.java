package agency.tango.materialintroscreen.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.AnimRes;

import agency.tango.materialintroscreen.animations.translations.NoTranslation;

@SuppressWarnings("WeakerAccess")
public class ViewTranslationWrapper {
    private View view;

    private IViewTranslation enterTranslation;
    private IViewTranslation exitTranslation;
    private IViewTranslation defaultTranslation;
    private Animation errorAnimation;

    public ViewTranslationWrapper(View view) {
        this.view = view;

        enterTranslation = new NoTranslation();
        exitTranslation = new NoTranslation();
        setErrorAnimation(0);
    }

    
    public ViewTranslationWrapper setEnterTranslation(IViewTranslation enterTranslation) {
        this.enterTranslation = enterTranslation;
        return this;
    }

    
    public ViewTranslationWrapper setExitTranslation(IViewTranslation exitTranslation) {
        this.exitTranslation = exitTranslation;
        return this;
    }

    
    public ViewTranslationWrapper setDefaultTranslation(IViewTranslation defaultTranslation) {
        this.defaultTranslation = defaultTranslation;
        return this;
    }

    
    public ViewTranslationWrapper setErrorAnimation(@AnimRes int errorAnimation) {
        if (errorAnimation != 0) {
            this.errorAnimation = AnimationUtils.loadAnimation(view.getContext(), errorAnimation);
        }
        return this;
    }

    public void enterTranslate(float percentage) {
        enterTranslation.translate(view, percentage);
    }

    public void exitTranslate(float percentage) {
        exitTranslation.translate(view, percentage);
    }

    public void defaultTranslate(float percentage) {
        defaultTranslation.translate(view, percentage);
    }

    public void error() {
        if (errorAnimation != null) {
            view.startAnimation(errorAnimation);
        }
    }
}