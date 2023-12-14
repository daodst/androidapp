

package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;

import com.wallet.ctc.model.me.DefValueBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DefaultAssetsBean extends EvmosHttpBean {

    public Map<String, List<DefValueBean>> data;

    public List<DefValueBean> getAsserts() {
        if (null == data || data.size() == 0) {
            return null;
        }
        List<DefValueBean> all = new ArrayList<>();
        for (Map.Entry<String, List<DefValueBean>> entry : data.entrySet()) {
            List<DefValueBean> list = setKey(entry.getKey(), entry.getValue());
            if (null != list) {
                all.addAll(list);
            }
        }
        return all;
    }

    
    private List<DefValueBean> setKey(String key, List<DefValueBean> list) {
        if (null == list || list.size() == 0) {
            return null;
        }
        if (TextUtils.isEmpty(key)) {
            return list;
        }
        for (int i=0; i<list.size(); i++) {
            list.get(i).setKey(key);
        }
        return list;
    }


}
