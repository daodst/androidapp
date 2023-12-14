package com.app.lg4e.ui;

import static common.app.utils.LanguageUtil.TYPE_LAGUAGE_ENGLISH;
import static common.app.utils.LanguageUtil.TYPE_LAGUAGE_TAIWAN_CHINESE;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;

import com.app.R;
import com.app.databinding.PopupLanguageChangeBinding;
import com.lxj.xpopup.impl.FullScreenPopupView;

import java.util.Locale;

import common.app.utils.LanguageUtil;
import common.app.utils.SpUtil;
import im.vector.app.features.configuration.VectorConfiguration;
import im.vector.app.features.settings.VectorLocale;


public class LanguagePopup extends FullScreenPopupView {
    private PopupLanguageChangeBinding binding;
    private Consumer<Void> consumer;

    public LanguagePopup(@NonNull Context context,Consumer<Void> consumer) {
        super(context);
        this.consumer = consumer;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_language_change;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        binding = PopupLanguageChangeBinding.bind(getPopupImplView());
        binding.titleBarView2.setLeftBtnVisable(false);

        binding.tvEnglish.setOnClickListener(v -> {
            
            LanguageUtil.setNewLanguage(getContext(), TYPE_LAGUAGE_ENGLISH);
            setLanguage();
            setCurrentLanguage(false);
        });
        binding.tvChina.setOnClickListener(v -> {
            
            LanguageUtil.setNewLanguage(getContext(), TYPE_LAGUAGE_TAIWAN_CHINESE);
            setLanguage();

            setCurrentLanguage(true);
        });
    }

    private void setCurrentLanguage(boolean isChinese) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.mipmap.icon_language_sel);
        int colorSelect = ContextCompat.getColor(getContext(), R.color.default_theme_color);
        int colorDefault = ContextCompat.getColor(getContext(), R.color.default_text_color);

        binding.tvEnglish.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, isChinese ? null : drawable, null);
        binding.tvEnglish.setTextColor(isChinese ? colorDefault : colorSelect);
        binding.tvChina.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, !isChinese ? null : drawable, null);
        binding.tvChina.setTextColor(!isChinese ? colorDefault : colorSelect);
    }

    private void setLanguage() {
        Locale nowLocal = LanguageUtil.getNowLocal(getContext());
        VectorLocale.INSTANCE.saveApplicationLocale(nowLocal);
        VectorConfiguration configuration = new VectorConfiguration(getContext());
        configuration.applyToApplicationContext();
        
        SpUtil.setAppLanguageHasSet(true);
        consumer.accept(null);
    }
}
