

package com.wallet.ctc.view.dialog.choosewallet;

import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.BTC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.WalletAppProvider;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.util.SettingPrefUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.mall.util.ToastUtil;



public class ChooseWalletDialogAdapter extends BaseAdapter {
    private List<WalletEntity> list = new ArrayList<WalletEntity>();
    private Context context;
    private int all = 1;
    
    
    private String[] colors = {"#575d7b", "#575d7b", "#e6973c", "#2c2c2e", "#010000", "#575d7b", "#3c81bd", "#af333d", "#575d7b", "#575d7b", "#575d7b", "#1f2044", "#f9ba12",
            "#458ff7", "#edc147", "#d02d79", "#425c95", "#75c75a", "#e1b75c", "#000000", "#3f8129", "#d02d79", "#000000", "#784ddd"};

    public ChooseWalletDialogAdapter(Context context) {
        this.context = context;
    }

    public void bindData(List<WalletEntity> list) {
        if (null != list && list.size() > 0) {
            Map<Integer, String> typeAddrMap = new HashMap<>();
            for (int i = 0; i < list.size(); i++) {
                WalletEntity wallet = list.get(i);
                int walletType = wallet.getType();
                String walletAddre = wallet.getAllAddress();
                String typeDefAddr = "";
                if (typeAddrMap.containsKey(walletType)) {
                    typeDefAddr = typeAddrMap.get(walletType);
                } else {
                    typeDefAddr = SettingPrefUtil.getWalletTypeAddress(context, walletType);
                    if (!TextUtils.isEmpty(typeDefAddr)) {
                        typeAddrMap.put(walletType, typeDefAddr);
                    }
                }
                if (!TextUtils.isEmpty(walletAddre) && walletAddre.equalsIgnoreCase(typeDefAddr)) {
                    list.get(i).select_flag = 1;
                } else {
                    list.get(i).select_flag = 0;
                }
            }
        }
        this.list = list;
        notifyDataSetChanged();
    }

    public void refresh(){
        if (this.list != null && this.list.size() > 0){
            bindData(this.list);
        }
    }

    public void bindType(int type) {
        this.all = type;
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
                    R.layout.item_choose_wallet_dialog, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        WalletEntity mEntity = list.get(position);
        List<AssertBean> assertEntityList = WalletDBUtil.getInstent(context).getMustWallet(mEntity.getType());
        holder.addWalletLin.setVisibility(View.GONE);
        holder.walletTypeName.setVisibility(View.GONE);
        if (position == 0) {
            holder.walletTypeName.setVisibility(View.VISIBLE);

            holder.addWalletLin.setBackgroundResource(R.drawable.default_btn_bg);
            holder.addWalletName.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.addWalletName.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(context, R.mipmap.jiatianjia), null, null, null);

            if (all == 1) {
                holder.addWalletLin.setVisibility(View.VISIBLE);
                holder.walletTypeName.setText(context.getString(R.string.identity_wallet));
                holder.addWalletName.setText(context.getString(R.string.add_currency));
            } else {
                holder.walletTypeName.setText(assertEntityList.get(0).getFull_name().toUpperCase()+context.getString(R.string.wallet));
                if (mEntity.getType() == WalletUtil.HT_COIN) {
                    holder.walletTypeName.setText("HECO");
                }
            }
        } else if (mEntity.getLevel() != 1 && list.get(position - 1).getLevel() == 1 && all == 1) {
            holder.walletTypeName.setVisibility(View.VISIBLE);
            holder.addWalletLin.setVisibility(View.VISIBLE);
            holder.walletTypeName.setText(context.getString(R.string.other_wallets));
            holder.addWalletName.setText(context.getString(R.string.add_wallet));


            holder.addWalletLin.setBackgroundResource(R.drawable.round_d7f_c5);
            holder.addWalletName.setTextColor(ContextCompat.getColor(context, R.color.default_theme_color));
            holder.addWalletName.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(context, R.mipmap.jiatianjia_theme), null, null, null);
        }

        holder.addWalletLin.setOnClickListener(v -> {
            if (null != mChooseWallet) {
                mChooseWallet.addWallet(mEntity.getLevel());
            }
        });
        holder.walletInfo.setOnClickListener(v -> {
            if (null != mChooseWallet) {
                mChooseWallet.onChangeWallet(mEntity.getAllAddress(), mEntity.getType());
            }
        });
        holder.walletAddress.setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            
            cm.setText(mEntity.getAllAddress());
            ToastUtil.showToast(context.getResources().getString(R.string.copy_success));
        });
        if (TextUtils.isEmpty(mEntity.getAllAddress())) {
            holder.walletInfo.setVisibility(View.GONE);
        } else {
            holder.walletInfo.setVisibility(View.VISIBLE);
        }
        if (mEntity.getLevel() == 1) {
            
            holder.walletName.setText(assertEntityList.get(0).getShort_name().toUpperCase());
        } else {
            
            holder.walletName.setText(mEntity.getName());
        }
        if (mEntity.getLevel() == 0) {
            holder.tvWalletLevel.setVisibility(View.GONE);
        } else {
            holder.tvWalletLevel.setVisibility(View.VISIBLE);
            holder.tvWalletLevel.setText(mEntity.getLevel() == -1 ? R.string.wallet_level_ob : R.string.wallet_level_hd);
        }
        if (mEntity.select_flag == 1) {
            holder.selectFlag.setVisibility(View.VISIBLE);
        } else {
            holder.selectFlag.setVisibility(View.GONE);
        }

        WalletAppProvider provider = (WalletAppProvider) context.getApplicationContext();
        holder.wallet_login_logo.setVisibility(TextUtils.equals(provider.getLoginAccount(), mEntity.getAllAddress()) ? View.VISIBLE : View.GONE);
        
        
        
        

        if (mEntity.getType() == BTC_COIN) {
            holder.walletInfo.setBackgroundResource(R.mipmap.btc_wallet_bg);
        } else if (mEntity.getType() == ETH_COIN) {
            holder.walletInfo.setBackgroundResource(R.mipmap.eth_wallet_bg);
        } else if (mEntity.getType() == BNB_COIN) {
            holder.walletInfo.setBackgroundColor(Color.parseColor("#212121"));
        } else if (mEntity.getType() == MCC_COIN) {
            holder.walletInfo.setBackgroundResource(R.mipmap.mcc_wallet_bg);
        } else if (mEntity.getType() >= colors.length) {
            holder.walletInfo.setBackgroundColor(Color.parseColor(colors[0]));
        } else {
            holder.walletInfo.setBackgroundColor(Color.parseColor(colors[mEntity.getType()]));
        }

        if (mEntity.getType() == BNB_COIN) {
            holder.walletName.setTextColor(ContextCompat.getColor(context, R.color.goin_theme_color));
            holder.tvWalletLevel.setTextColor(ContextCompat.getColor(context, R.color.goin_theme_color));
            holder.tvWalletLevel.setBackgroundResource(R.drawable.bg_wallet_level_yellow);
            holder.selectFlag.setImageResource(R.mipmap.w_xuanzhong_yellow);
            holder.walletAddress.setTextColor(ContextCompat.getColor(context, R.color.goin_theme_color));
            holder.walletAddress.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.mipmap.copy_wallet_address_yellow), null);
        } else {
            holder.walletName.setTextColor(ContextCompat.getColor(context, R.color.default_button_text_color));
            holder.tvWalletLevel.setTextColor(ContextCompat.getColor(context, R.color.default_button_text_color));
            holder.tvWalletLevel.setBackgroundResource(R.drawable.bg_wallet_level);
            holder.selectFlag.setImageResource(R.mipmap.w_xuanzhong);
            holder.walletAddress.setTextColor(ContextCompat.getColor(context, R.color.default_button_text_color));
            holder.walletAddress.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.mipmap.copy_wallet_address), null);
        }

        
        holder.walletAddress.setText(mEntity.getmAddress());
        return convertView;
    }

    private ChooseWallet mChooseWallet;

    public interface ChooseWallet {
        void onChangeWallet(String address, int type);

        void addWallet(int type);
    }

    public void setChooseWallet(ChooseWallet chooseWallet) {
        mChooseWallet = chooseWallet;
    }

    class ViewHolder {
        @BindView(R2.id.wallet_type_name)
        TextView walletTypeName;
        @BindView(R2.id.add_wallet_name)
        TextView addWalletName;
        @BindView(R2.id.add_wallet_lin)
        LinearLayout addWalletLin;
        @BindView(R2.id.wallet_name)
        TextView walletName;
        @BindView(R2.id.wallet_address)
        TextView walletAddress;
        @BindView(R2.id.tv_wallet_level)
        TextView tvWalletLevel;
        @BindView(R2.id.wallet_info)
        View walletInfo;
        @BindView(R2.id.now_flag)
        ImageView selectFlag;
        @BindView(R2.id.wallet_login_logo)
        ImageView wallet_login_logo;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

