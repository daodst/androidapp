

package com.wallet.ctc.ui.blockchain.addquotes;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.ui.blockchain.seach.SeachActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class AddQuotesActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.img_action)
    ImageView imgAction;
    @BindView(R2.id.assets_list)
    ListView assetsList;
    @BindView(R2.id.nodata)
    LinearLayout nodata;
    @BindView(R2.id.title_bar)
    RelativeLayout titleBar;
    private AddQuotesAdapter mAdapter;

    @Override
    public int initContentView() {
        return R.layout.activity_addquotes;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        mAdapter = new AddQuotesAdapter(this);
        assetsList.setAdapter(mAdapter);
        tvTitle.setText(getString(R.string.add_market));
    }

    @Override
    public void initData() {

    }


    @OnClick({R2.id.tv_back, R2.id.img_action})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.img_action) {
            Intent intent = new Intent(this, SeachActivity.class);
            startActivity(intent);

        } else {
        }
    }
}
