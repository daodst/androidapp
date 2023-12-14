

package common.app.base.share;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.StringDef;

import com.google.gson.Gson;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.TreeMap;

import common.app.R;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.base.fragment.mall.model.BaseEntity;
import common.app.base.share.api.ShareApi;
import common.app.base.share.bean.ShareData;
import common.app.ui.view.MyProgressDialog;
import common.app.utils.LogUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class AppShare {
    private final String TAG = "AppShare";
    private Context mContext;
    private ShareApi mApi;
    private Gson gson = new Gson();
    private MyProgressDialog Progress;

    private int types=0;


    
    public interface CBGetShareData {
        public void onGet(ShareData data);
    }

    
    public void getData(String type, String target, CBGetShareData cb) {
        mApi = new ShareApi();
        Map<String, Object> params = new TreeMap();
        params.put("type", type);
        params.put("target", target);
        Progress.show();
        mApi.getShare(params).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(mContext) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        Progress.dismiss();
                        if (baseEntity.getStatus() == 1) {
                            LogUtil.d(TAG, new Gson().toJson(baseEntity.getData()));
                            ShareData shareData = gson.fromJson(gson.toJson(baseEntity.getData()), ShareData.class);
                            shareData.type=types;
                            cb.onGet(shareData);
                        } else {
                            Toast.makeText(mContext, baseEntity.getInfo(), Toast.LENGTH_SHORT);
                            cb.onGet(null);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Progress.dismiss();
                    }
                });
    }

}
