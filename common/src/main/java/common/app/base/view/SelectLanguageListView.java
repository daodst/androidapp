

package common.app.base.view;

import android.content.Context;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

import common.app.R;
import common.app.base.model.RadioListItemBean;



public class SelectLanguageListView {

    private Context mContext;
    
    private String mNowSettingLaguage;

    private String[] mLanguages;
    private int[] mLanguageStrId;

    List<RadioListItemBean> mLanguagesList = new ArrayList<>();

    
    public SelectLanguageListView(Context context, String nowLaguage, String[] languages, int[] titleIds, LanguageChangeListener listener) {
        this.mContext = context;
        mNowSettingLaguage = nowLaguage;
        this.mLanguages = languages;
        this.mLanguageStrId = titleIds;
        this.mChangeListener = listener;
    }

    private LanguageChangeListener mChangeListener;

    public interface LanguageChangeListener {
        public void onLanguageChange(String newLanguage);
    }

    public void show() {
        this.mLanguagesList.clear();
        for (int i=0; i<mLanguages.length; i++) {
            String languageType = mLanguages[i];
            RadioListItemBean languageItem = new RadioListItemBean();
            languageItem.id = languageType;
            if (languageType.equals(mNowSettingLaguage)) {
                languageItem.checked = true;
            } else {
                languageItem.checked = false;
            }
            languageItem.content = mContext.getString(mLanguageStrId[i]);
            mLanguagesList.add(languageItem);
        }
        SimpleRadioListDialog dialog = new SimpleRadioListDialog(mContext, mContext.getString(R.string.change_language),mLanguagesList);
        dialog.setOnItemClickListener((parent, view, position, id) -> {

            RadioListItemBean selectItem = mLanguagesList.get(position);
            if (!selectItem.id.equals(mNowSettingLaguage)) {
                if (null != view) {
                    RadioButton radio = (RadioButton) view.findViewById(R.id.radio_btn);
                    if (null != radio) {
                        radio.setChecked(true);
                    }
                }

                mNowSettingLaguage = selectItem.id;
                if (null != mChangeListener) {
                    mChangeListener.onLanguageChange(mNowSettingLaguage);
                }
            }
            dialog.dismiss();
        });
        dialog.show();
    }
}
