

package com.wallet.ctc.ui.blockchain.transactionrecord;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.blockchain.BlockChainApi;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.TransactionRecordBean;
import com.wallet.ctc.util.LogUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class TransactionRecordAdapter extends BaseAdapter {
    private List<TransactionRecordBean> list = new ArrayList<TransactionRecordBean>();
    private String myWalletAddress = "";
    private Context context;
    private int type;
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();

    private int i = 1;
    private int decimal = 0;

    public TransactionRecordAdapter(Context context) {
        this.context = context;
    }

    public void bindData(List<TransactionRecordBean> list, int type) {
        this.list = list;
        this.type = type;
    }

    public void bindAddress(String myWalletAddress) {
        this.myWalletAddress = myWalletAddress;
    }

    
    public boolean isSendTransfer(String fromAddress) {
        if (!TextUtils.isEmpty(myWalletAddress) && myWalletAddress.equalsIgnoreCase(fromAddress)) {
            return true;
        } else {
            return false;
        }
    }

    
    public int getDecimal() {
        if (decimal > 0) {
            return decimal;
        }
        List<AssertBean> assets = WalletDBUtil.getInstent(context).getMustWallet(type);
        decimal = assets.get(0).getDecimal();
        if (decimal == 0) {
            
            decimal = 18;
        }
        return decimal;
    }

    
    public String getTransferAmount(String amount) {
        if (TextUtils.isEmpty(amount)) {
            return amount;
        }
        int decimal = getDecimal();
        try {
            String transferAmount = new BigDecimal(amount).divide(new BigDecimal(Math.pow(10, decimal)), 6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
            return transferAmount;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return amount;
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
                    R.layout.item_transaction_record, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TransactionRecordBean mBean = list.get(position);
        holder.timeTxt.setVisibility(View.GONE);
        if (position == 0) {
            holder.timeTxt.setText(mBean.getDay());
            holder.timeTxt.setVisibility(View.VISIBLE);
        } else {
            if (!mBean.getDay().equals(list.get(position - 1).getDay())) {
                holder.timeTxt.setText(mBean.getDay());
                holder.timeTxt.setVisibility(View.VISIBLE);
            }
        }

        if (isSendTransfer(mBean.getFromAllAddress())) {


            String amount = getTransferAmount(mBean.getBigIntTransferAmount());
            String showTips = "";
            if (!TextUtils.isEmpty(amount)) {
                if (!amount.contains(mBean.getCoin_name())) {
                    showTips = amount + " " + mBean.getCoin_name().toUpperCase();
                } else {
                    showTips = amount;
                }
            }

            
            holder.walletName.setText(mBean.getToAddress());
            holder.assetsLogo.setImageResource(R.mipmap.zhuanchu);
            holder.ether.setText("-" + showTips);
            holder.ether.setTextColor(ContextCompat.getColor(context, R.color.default_tip_color));
        } else {
            String amount = getTransferAmount(mBean.getBigIntTransferAmount());
            String showTips = "";
            if (!TextUtils.isEmpty(amount)) {
                if (!amount.contains(mBean.getCoin_name())) {
                    showTips = amount + " " + mBean.getCoin_name().toUpperCase();
                } else {
                    showTips = amount;
                }
            }
            
            holder.walletName.setText(mBean.getFromAddress());
            holder.assetsLogo.setImageResource(R.mipmap.zhuanru);
            holder.ether.setText("+" + showTips);
            holder.ether.setTextColor(ContextCompat.getColor(context, R.color.default_theme_color));
        }

        holder.orderState.setVisibility(View.GONE);
        holder.statusProgressBar.setVisibility(View.GONE);
        if (mBean.getStatus() == 2) {
            holder.assetsLogo.setImageResource(R.mipmap.shibai);
            holder.ether.setTextColor(0xff778899);
        } else if (mBean.getType() == 1 || mBean.getStatus() == 0 || mBean.getStatus() == 3) {
            holder.assetsLogo.setImageResource(R.mipmap.chuangjian_token);
            holder.ether.setTextColor(0xff7C69C0);
            if (mBean.getStatus() == 0 || mBean.getStatus() == 3) {
                holder.orderState.setText(context.getString(R.string.pending_package));
                holder.statusProgressBar.setProgress(0);
            } else {
                holder.orderState.setText(context.getString(R.string.publish_tokens));
                holder.statusProgressBar.setProgress(100);
            }

        } else {
            if (mBean.getShowFlash() == 1) {
                holder.orderState.setVisibility(View.VISIBLE);
                holder.statusProgressBar.setVisibility(View.VISIBLE);
                int po = mBean.getJindu();
                if (po == 0) {
                    showFalsh();
                    holder.orderState.setText(context.getString(R.string.packed));
                } else if (po == 20) {
                    holder.orderState.setText("1/5");
                } else if (po == 40) {
                    holder.orderState.setText("2/5");
                } else if (po == 60) {
                    holder.orderState.setText("3/5");
                } else if (po == 80) {
                    holder.orderState.setText("4/5");
                } else {
                    holder.orderState.setText(context.getString(R.string.completed));
                }
                holder.statusProgressBar.setProgress(po);
            } else {
                holder.orderState.setText(context.getString(R.string.completed));
                holder.statusProgressBar.setProgress(100);
            }
        }

        holder.kuanggong.setVisibility(View.GONE);

        
        holder.noticeJieshou.setText(mBean.getTransferTime());

        return convertView;
    }

    class ViewHolder {
        @BindView(R2.id.time_txt)
        TextView timeTxt;
        @BindView(R2.id.assets_logo)
        ImageView assetsLogo;
        @BindView(R2.id.wallet_name)
        TextView walletName;
        @BindView(R2.id.ether)
        TextView ether;
        @BindView(R2.id.kuanggong)
        TextView kuanggong;
        @BindView(R2.id.notice_jieshou)
        TextView noticeJieshou;
        @BindView(R2.id.order_state)
        TextView orderState;
        @BindView(R2.id.status_progressBar)
        ProgressBar statusProgressBar;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void showFalsh() {
        time = 1000;
    }

    private List<Integer> xiabiao = new ArrayList<>();
    private int time = 15000;
    private BlockChainApi mApi = new BlockChainApi();
    int po = 0;
    
    private Runnable timeRunable = new Runnable() {
        @Override
        public void run() {
            LogUtil.d("" + time);
            if (time == 15000) {
                loadDetail();
                mhandle.postDelayed(this, time);
            } else if (time == 1000) {
                if (po >= 100) {
                    time = 15000;
                    list.get(xiabiao.get(0)).setJindu(100);
                    po = 0;
                    xiabiao.clear();
                    notifyDataSetChanged();
                } else {
                    po = po + 20;
                    list.get(xiabiao.get(0)).setJindu(po);
                    notifyDataSetChanged();

                    mhandle.postDelayed(this, time);
                }
            }

        }
    };
    
    private Handler mhandle = new Handler();

    
    private void loadDetail() {
        Map<String, Object> params = new TreeMap();
        params.put("method", "txrst");
        params.put("tx", list.get(xiabiao.get(0)).getAllTransaction_no());
        mApi.getTransList(params, type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(context) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {
                            TransactionRecordBean mBean;
                            if (type == WalletUtil.DM_COIN || type == WalletUtil.MCC_COIN) {
                                mBean = gson.fromJson(gson.toJson(baseEntity.getData()), TransactionRecordBean.class);
                            } else {
                                List<TransactionRecordBean> list = gson.fromJson(gson.toJson(baseEntity.getData()), new TypeToken<List<TransactionRecordBean>>() {
                                }.getType());
                                if (null != list && list.size() > 0) {
                                    mBean = list.get(0);
                                } else {
                                    return;
                                }
                            }
                            if (xiabiao.size() < 1 || list.size() < 1) {
                                mhandle.removeCallbacks(timeRunable);
                                return;
                            }
                            if (mBean.getStatus() == 1 || mBean.getStatus() == 4) {
                                list.get(xiabiao.get(0)).setStatus(mBean.getStatus());
                                list.get(xiabiao.get(0)).setShowFlash(1);
                                time = 1000;
                                mhandle.removeCallbacks(timeRunable);
                                notifyDataSetChanged();
                            }
                        } else {
                            LogUtil.d("");
                            mhandle.removeCallbacks(timeRunable);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mhandle.removeCallbacks(timeRunable);
                    }
                });
    }

}

