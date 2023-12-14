

package com.wallet.ctc.ui.blockchain.home;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.util.GlideUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.my.view.CircularImage;
import common.app.pojo.CurrencyBean;
import common.app.utils.SpUtil;



public class HomeWalletListAdapter extends BaseAdapter {
    private List<AssertBean> list = new ArrayList<AssertBean>();
    private Context context;
    private ColorMatrixColorFilter filter;
    private WalletDBUtil walletDBUtil;
    public HomeWalletListAdapter(Context context) {
        this.context = context;
        walletDBUtil=WalletDBUtil.getInstent(context);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0f);
        filter = new ColorMatrixColorFilter(matrix);
    }

    public void bindData(List<AssertBean> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_home_wallet, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AssertBean mBean=list.get(position);
        holder.walletLogo.clearColorFilter();
        if(mBean.getImg_path().length()>2){
            GlideUtil.showImg(context, mBean.getImg_path(), holder.walletLogo);
        }else if(mBean.getLogo()!=0){
            GlideUtil.showImg(context, mBean.getLogo(), holder.walletLogo);
        }else {
            holder.walletLogo.setColorFilter(filter);
            GlideUtil.showImg(context, walletDBUtil.getMustWallet(mBean.getType()).get(0).getLogo(), holder.walletLogo);
        }
        holder.walletName.setText(mBean.getShort_name().toUpperCase());
        holder.walletNumber.setText(mBean.getAssertsNum());
        String dcu= SpUtil.getDcu();
        if(dcu.equals(common.app.BuildConfig.CURRENCY_UNIT)) {
            dcu= BuildConfig.CURRENCY_SYMBOL;
        }else {
            Gson gson=new Gson();
            CurrencyBean bean=gson.fromJson(dcu, CurrencyBean.class);
            if(null!=bean){
                dcu=bean.getCurrency_symbol();
            }
        }
        holder.walletAddress.setText("≈ "+dcu+" "+mBean.getAssertsSumPrice());
        return convertView;
    }
    class ViewHolder {
        @BindView(R2.id.wallet_logo)
        CircularImage walletLogo;
        @BindView(R2.id.wallet_name)
        TextView walletName;
        @BindView(R2.id.wallet_number)
        TextView walletNumber;
        @BindView(R2.id.wallet_address)
        TextView walletAddress;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

