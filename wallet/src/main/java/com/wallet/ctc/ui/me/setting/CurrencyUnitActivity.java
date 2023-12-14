

package com.wallet.ctc.ui.me.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.view.popwindow.CurrencyUnitAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.base.base.BaseActivity;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.pojo.CurrencyBean;
import common.app.ui.view.TitleBarView;
import common.app.utils.SpUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class CurrencyUnitActivity extends BaseActivity {

    @BindView(R2.id.title_bar)
    TitleBarView titleBar;
    @BindView(R2.id.yuyan_list)
    ListView yuyanList;
    private CurrencyUnitAdapter mAdapter;
    private List<CurrencyBean> yuyan = new ArrayList<>();
    MeApi mApi=new MeApi();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_unit);
        ButterKnife.bind(this);
        titleBar.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });
        yuyan.add(new CurrencyBean("CNY","¥"));
        yuyan.add(new CurrencyBean("USD","$"));
        mAdapter = new CurrencyUnitAdapter(this, yuyan);
        yuyanList.setAdapter(mAdapter);
        yuyanList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("yuyan", yuyan.get(position).getCurrency());
                SpUtil.saveDcu(gson.toJson(yuyan.get(position)));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        getData();
    }
    private Gson gson=new Gson();
    private void getData(){
        mApi.getCurrencys(new TreeMap<>())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {
                            yuyan.clear();
                            yuyan.addAll(gson.fromJson(gson.toJson(baseEntity.getData()),new TypeToken<List<CurrencyBean>>(){}.getType()));
                            mAdapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

}
