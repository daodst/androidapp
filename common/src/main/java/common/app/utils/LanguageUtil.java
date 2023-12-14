

package common.app.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.tencent.mmkv.MMKV;

import java.util.Locale;

import common.app.AppApplication;
import common.app.R;
import common.app.base.view.SelectLanguageListView;


public class LanguageUtil {

    public static final String KEY_LAGUAGE_SETTING = "app_language";
    public static final String TYPE_LAGUAGE_AUTO = "auto";
    public static final String TYPE_LAGUAGE_SIMPLE_CHINESE = "zh-cn";
    public static final String TYPE_LAGUAGE_TAIWAN_CHINESE = "zh-tw";
    public static final String TYPE_LAGUAGE_ENGLISH = "en";
    public static final String TYPE_LAGUAGE_FRENCH = "fra";
    public static final String TYPE_LANGUAGE_KO = "ko";
    public static final String TYPE_LANGUAGE_VI = "vie";
    
    public static final String DEFAULT_SETTING_LANGUAGE = TYPE_LAGUAGE_ENGLISH;

    public static interface LanguageChangeListener {
        public void onLanguageChange();
    }


    
    public static void showSettingDialog(Context context, LanguageChangeListener listener) {
        if (null == context) {
            return;
        }
        String[] languages = {
                TYPE_LAGUAGE_TAIWAN_CHINESE,
                TYPE_LAGUAGE_ENGLISH};
        int[] languageStrId = {
                R.string.language_zh_tw,
                R.string.language_english
        };
        String nowLanguage = getNowSettingLaguage(context);
        SelectLanguageListView languageListView = new SelectLanguageListView(context, nowLanguage, languages, languageStrId, newLanguage -> {
            setNewLanguage(context, newLanguage);
            if (null != listener) {
                
                
                listener.onLanguageChange();
            }
        });
        languageListView.show();
    }


    
    public static Locale getNowLocal(Context context) {
        if (null == context) {
            return null;
        }
        
        Locale locale = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
        return locale;
    }


    
    public static String getNowSettingLaguage(Context context) {
        if (null == context) {
            return null;
        }

        String languae = MMKV.defaultMMKV().decodeString(KEY_LAGUAGE_SETTING, DEFAULT_SETTING_LANGUAGE);
        return languae;
    }

    
    public static String getNowLocalStr(Context context) {
        if (context == null) {
            return null;
        }
        Locale locale = getNowLocal(context);
        String language = locale.getLanguage();
        String country = locale.getCountry().toLowerCase();
        if ((language.equals(Locale.SIMPLIFIED_CHINESE.getLanguage()) || language.equals(Locale.CHINESE.getLanguage())) && country.equals("cn")) {
            return TYPE_LAGUAGE_SIMPLE_CHINESE;
        } else if (language.equals(Locale.TRADITIONAL_CHINESE.getLanguage()) && country.equals("tw")) {
            return TYPE_LAGUAGE_TAIWAN_CHINESE;
        } else if (language.equals(Locale.FRENCH.getLanguage())) {
            return TYPE_LAGUAGE_FRENCH;
        } else if (language.equals(Locale.KOREA.getLanguage()) && country.equals("kr")) {
            return TYPE_LANGUAGE_KO;
        } else if (language.equals("vi") && country.equals("vn")) {
            return TYPE_LANGUAGE_VI;
        } else {
            return TYPE_LAGUAGE_ENGLISH;
        }
    }

    
    public static String getNowLanguageStr(Context context) {
        if (context == null) {
            return null;
        }
        String languae = getNowSettingLaguage(context);
        if (!TextUtils.isEmpty(languae)) {
            switch (languae) {
                case TYPE_LAGUAGE_AUTO:
                    return context.getString(R.string.language_auto);
                case TYPE_LAGUAGE_SIMPLE_CHINESE:
                    return context.getString(R.string.language_zh_cn);
                case TYPE_LAGUAGE_TAIWAN_CHINESE:
                    return context.getString(R.string.language_zh_tw);
                case TYPE_LAGUAGE_ENGLISH:
                    return context.getString(R.string.language_english);
                case TYPE_LAGUAGE_FRENCH:
                    return context.getString(R.string.language_french);
                case TYPE_LANGUAGE_KO:
                    return context.getString(R.string.language_ko);
                case TYPE_LANGUAGE_VI:
                    return context.getString(R.string.language_vi);
                default:
                    return context.getString(R.string.language_auto);
            }
        } else {
            return context.getString(R.string.language_auto);
        }
    }

    
    public static void initLanguage(Context context) {
        if (null == context) {
            return;
        }

        String languae = MMKV.defaultMMKV().decodeString(KEY_LAGUAGE_SETTING, DEFAULT_SETTING_LANGUAGE);
        doSetLanguage(context, languae);

    }

    
    public static void setNewLanguage(Context context, String newLanguage) {
        if (null == context) {
            return;
        }
        doSetLanguage(context, newLanguage);
        if (AppApplication.getInstance().getApplicationContext() != null) {
            doSetLanguage(AppApplication.getInstance().getApplicationContext(), newLanguage);
        }
        MMKV.defaultMMKV().encode(KEY_LAGUAGE_SETTING, newLanguage);
    }


    
    private static void doSetLanguage(Context context, String language) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        Locale locale = null;
        switch (language) {
            case TYPE_LAGUAGE_AUTO:
                locale = Locale.getDefault();
                break;
            case TYPE_LAGUAGE_SIMPLE_CHINESE:
                locale = Locale.SIMPLIFIED_CHINESE;
                break;
            case TYPE_LAGUAGE_TAIWAN_CHINESE:
                locale = Locale.TRADITIONAL_CHINESE;
                break;
            case TYPE_LAGUAGE_ENGLISH:
                locale = Locale.ENGLISH;
                break;
            case TYPE_LAGUAGE_FRENCH:
                locale = Locale.FRENCH;
                break;
            case TYPE_LANGUAGE_KO:
                locale = Locale.KOREA;
                break;
            case TYPE_LANGUAGE_VI:
                locale = new Locale("vi", "vn");
                break;
        }
        if (null != locale) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(locale);
            } else {
                configuration.locale = locale;
            }
        }
        resources.updateConfiguration(configuration, displayMetrics);
    }


}
