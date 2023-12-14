

package com.app.me.pledge;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.tabs.TabLayout;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.util.XPopupUtils;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.databinding.EduMainTabItemBinding;
import com.wallet.ctc.databinding.SmActivityPledgeBinding;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosPledgeConfigBean;
import com.wallet.ctc.model.blockchain.EvmosTotalPledgeBean;
import com.wallet.ctc.model.blockchain.SmChartInfo;
import com.wallet.ctc.model.blockchain.SmOuterChartInfo;
import com.wallet.ctc.ui.blockchain.addressbook.AddressBookActivity;
import com.wallet.ctc.ui.me.pledge.SMPledgeActivityVM;
import com.wallet.ctc.ui.me.pledge.UnPledgeDialog2;
import com.wallet.ctc.ui.me.pledge.utils.CusOnTabSelectedListener;
import com.wallet.ctc.ui.me.virtualphone.SMVirtualPhoneActivity;
import com.wallet.ctc.view.dialog.TransConfirmDialogBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import common.app.base.BaseActivity;
import common.app.base.view.SimpleListDialog;
import common.app.mall.util.ToastUtil;
import common.app.my.view.MyAlertDialog;
import common.app.ui.view.InputPwdDialog;
import common.app.utils.SpUtil;
import im.vector.app.provide.ChatSmPledgeModel;


public class SMPledgeActivity extends BaseActivity<SMPledgeActivityVM> {
    private static final String TAG = "SMPledgeActivity";
    private SmActivityPledgeBinding mBinding;
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_NICKNAME = "nikeName";
    private String mAddress, mNickName;
    private EvmosPledgeConfigBean mConfig;
    private InputPwdDialog mPwdDialog;
    private WalletEntity mSelecteWallet;

    public static Intent getIntent(Context from, String address, String nickName) {
        Intent intent = new Intent(from, SMPledgeActivity.class);
        intent.putExtra(KEY_ADDRESS, address);
        intent.putExtra(KEY_NICKNAME, nickName);
        return intent;
    }

    @Override
    public void initParam() {
        mAddress = getIntent().getStringExtra(KEY_ADDRESS);
        mNickName = getIntent().getStringExtra(KEY_NICKNAME);
        if (TextUtils.isEmpty(mAddress)) {
            showToast(R.string.data_error);
            finish();
            return;
        }
        if (TextUtils.isEmpty(mNickName)) {
            mNickName = mAddress.substring(0, 10) + "..." + mAddress.substring(mAddress.length() - 10);
        }

        mSelecteWallet = WalletDBUtil.getInstent(this).getWalletInfoByAddress(mAddress, WalletUtil.MCC_COIN);
        if (null == mSelecteWallet) {
            showToast(R.string.no_found_wallet_info);
            finish();
            return;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mBinding = SmActivityPledgeBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        super.onCreate(savedInstanceState);
    }

    private ChatSmPledgeModel mSmPledgeModel;

    public int getLevelDrawableRes(int level) {
        String resName = "sm_pledge" + level;
        return getResources().getIdentifier(resName, "mipmap", getPackageName());
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        mSmPledgeModel = ViewModelProviders.of(this).get(ChatSmPledgeModel.class);
        mBinding.ivBack.setOnClickListener(view1 -> {
            finish();
        });
        mSmPledgeModel.getLiveDataLevel().observe(this, level -> {
            mBinding.tvLevelName.setText(R.string.ziyoushijiang);
            mBinding.tvLevelCode.setText("LV." + level);
            if (level == 0) {
                mBinding.tvLevelRemark.setText(R.string.sm_pledge_tip_empty);
            } else {
                mBinding.tvLevelRemark.setText(R.string.sm_pledge_tip);
            }
            mBinding.tvLevel.setText("LV." + level);
            mBinding.ivReward.setImageResource(getLevelDrawableRes(level));
        });
        mSmPledgeModel.getLevel(this);

        
        mBinding.tvGetNow.setOnClickListener(view1 -> {
            
        });


        getViewModel().mWithdrawGasLiveData.observe(this, bean -> {

            TransConfirmDialogBuilder.builder(this, mSelecteWallet).amount(mConfig.canWithdrawNum)
                    
                    .fromAddress(mSelecteWallet.getAllAddress())
                    
                    .toAddress(mConfig.pledgeAddress)
                    .type(WalletUtil.MCC_COIN)
                    .orderDesc(getString(R.string.sm_pledge_string_16))
                    
                    .gasFeeWithToken(bean.gas.getShowFee(BuildConfig.EVMOS_FAKE_UNINT))
                    
                    .goTransferListener(pwd -> {
                        getViewModel().doWithdraw(bean, mAddress, mConfig, mSelecteWallet, pwd);
                    }).show();
        });
        
        mBinding.btnLingqu.setOnClickListener(view1 -> {
            if (null == mConfig || !mConfig.isSuccess) {
                return;
            }
            if (!TextUtils.isEmpty(mConfig.canWithdrawNum)) {
                BigDecimal canWithdrawNum = new BigDecimal(mConfig.canWithdrawNum);
                if (canWithdrawNum.compareTo(new BigDecimal(0)) <= 0) {
                    return;
                }

                getViewModel().getWithdrawGas(mAddress, mConfig);
            }
        });

        
        mBinding.btnSuhui.setOnClickListener(view1 -> {
            if (null == mConfig || !mConfig.isSuccess) {
                return;
            }
            if (!TextUtils.isEmpty(mConfig.canWithdrawNum)) {
                BigDecimal remainPledgeNum = new BigDecimal(mConfig.remainPledgeNum);
                if (remainPledgeNum.compareTo(new BigDecimal(0)) <= 0) {
                    return;
                }
            }
            Integer value = mSmPledgeModel.getLiveDataLevel().getValue();
            if (value == null) {
                ToastUtil.showToast(getString(com.app.R.string.get_rank_tips));
                return;
            }
            if (value <= 10) {
                ToastUtil.showToast(getString(com.app.R.string.shuhui_rank_tips));
                return;
            }
            getViewModel().getEvmosChatUnPledgeAvailable(mSelecteWallet.getAllAddress());

        });
        getViewModel().mUnPledgeGasLiveData.observe(this, bean -> {
            String plainString = "";
            try {
                plainString = new BigDecimal(bean.gas.getGasLimit()).divide(BigDecimal.valueOf(Math.pow(10, 18)), 18, RoundingMode.UP).stripTrailingZeros().toPlainString();
            } catch (Exception e) {
                plainString = bean.gas.getGasLimit();
            }
            TransConfirmDialogBuilder.builder(this, mSelecteWallet).amount(bean.mUnPledgeHashNum)
                    
                    .fromAddress(mSelecteWallet.getAllAddress())
                    
                    .toAddress(mConfig.pledgeAddress)
                    .type(WalletUtil.MCC_COIN)
                    .orderDesc(getString(R.string.sm_pledge_string_14))
                    
                    .gasFeeWithToken(bean.gas.getShowFee(BuildConfig.EVMOS_FAKE_UNINT))
                    
                    .goTransferListener(pwd -> {
                        EvmosTotalPledgeBean.Delegation delegation = mConfig.delegations.get(0);
                        getViewModel().doUnPledge(bean, delegation.delegation.delegator_address, delegation.delegation.validator_address, bean.mUnPledgeHashNum, mConfig, mSelecteWallet, pwd);
                    }).show();
        });
        getViewModel().mAvailableLiveData.observe(this, s -> {
            mConfig.available = s;
        });

        getViewModel().mHashPledgeGasLiveData.observe(this, bean -> {
            TransConfirmDialogBuilder.builder(this, mSelecteWallet).amount(bean.mHashPledgeNum)
                    
                    .fromAddress(mSelecteWallet.getAllAddress())
                    
                    .toAddress(mConfig.pledgeAddress)
                    .type(WalletUtil.MCC_COIN)
                    .orderDesc(getString(R.string.sm_pledge_string_1))
                    
                    .gasFeeWithToken(bean.gas.getShowFee(BuildConfig.EVMOS_FAKE_UNINT))
                    
                    .goTransferListener(pwd -> {
                        getViewModel().doHashPledge(bean, mSelecteWallet.getAllAddress(), bean.mHashPledgeNum, mConfig, mSelecteWallet, pwd, mSelecteWallet.getAllAddress());
                    }).show();
        });
        getViewModel().mHashPldgeNumLiveData.observe(this, s -> {
            if (TextUtils.isEmpty(s)) {
                ToastUtil.showToast(mConfig.tokenName + getString(com.app.R.string.no_enouth_coin));
                return;
            }
            BigDecimal remainPledgeNum = new BigDecimal(s);
            if (remainPledgeNum.compareTo(new BigDecimal(0)) <= 0) {
                
                return;
            }

            mConfig.hasNum = s;
            UnPledgeDialog2 dialog = new UnPledgeDialog2(this, mSelecteWallet, getViewModel());
            dialog.show(mConfig);
        });
        
        mBinding.btnHash.setOnClickListener(v -> {
            
            if (null == mConfig || !mConfig.isSuccess) {
                return;
            }
            getViewModel().getHashPldgeNum(mAddress, mConfig.tokenName);
        });
        
        ActivityResultLauncher<Intent> addressBookLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    int resultCode = result.getResultCode();
                    if (resultCode == RESULT_OK && null != data) {
                        String toAddress = data.getStringExtra("toAddress");
                        if (!TextUtils.isEmpty(toAddress)) {
                            mBinding.tvPledgeAddress.setText(toAddress);
                        }
                    }
                });
        
        mBinding.tvPledgeAddress.setOnClickListener(view1 -> {
            Intent intent = new Intent(this, AddressBookActivity.class);
            intent.putExtra("type", 1);
            addressBookLauncher.launch(intent);
        });

        
        mBinding.tvPledgePhoneStart.setOnClickListener(view1 -> {
            if (null == mConfig || !mConfig.isSuccess) {
                return;
            }
            List<String> phoneStarArrays = mConfig.phoneStartList;
            if (null != phoneStarArrays && phoneStarArrays.size() > 0) {
                List<String> list = new ArrayList<>();
                for (String number : phoneStarArrays) {
                    list.add(number + "XXXX");
                }
                new XPopup.Builder(this)
                        .maxHeight((int) (XPopupUtils.getScreenHeight(this) * 0.5))
                        .asBottomList("", list.toArray(new String[]{}), (position, text) -> {
                            mBinding.tvPledgePhoneStart.setText(text);
                        }).show();
            }

        });

        
        mBinding.tvMyDid.setOnClickListener(view1 -> {
            if (null == mConfig || !mConfig.isSuccess) {
                return;
            }
            List<String> mobiles = mConfig.myMobileList;
            if (null == mobiles || mobiles.size() == 0) {
                return;
            }
            SimpleListDialog listDialog = new SimpleListDialog(this, "", mobiles);
            listDialog.show();
        });

        
        mBinding.tvSeeDetail.setOnClickListener(view1 -> {
            startActivity(SMVirtualPhoneActivity.getIntent(this, mAddress));
        });


        getViewModel().mLiveData.observe(this, bean -> {
            String inputNum = mBinding.etMinNum.getText().toString().trim();
            String phonePrex = mBinding.tvPledgePhoneStart.getText().toString().trim();
            phonePrex = phonePrex.replaceAll("XXXX", "");
            String pledgeToAddress = mBinding.tvPledgeAddress.getText().toString().trim();

            if (TextUtils.isEmpty(inputNum)) {
                inputNum = "0";
            }

            String finalInputNum = inputNum;
            String finalPhonePrex = phonePrex;
            TransConfirmDialogBuilder.builder(this, mSelecteWallet).amount(inputNum)
                    
                    .fromAddress(pledgeToAddress)
                    
                    .toAddress(mConfig.pledgeAddress)
                    .type(WalletUtil.MCC_COIN)
                    .orderDesc(getString(R.string.btn_mint_tips))
                    
                    .gasFeeWithToken(bean.gas.getShowFee(BuildConfig.EVMOS_FAKE_UNINT))
                    
                    .goTransferListener(pwd -> {
                        getViewModel().doPledge(bean, mAddress, finalInputNum, finalPhonePrex, mConfig, mSelecteWallet, pwd, pledgeToAddress);
                    }).show();

        });
        
        mBinding.btnMint.setOnClickListener(view1 -> {
            if (null == mConfig || !mConfig.isSuccess) {
                return;
            }
            
            String inputNum = mBinding.etMinNum.getText().toString().trim();

            boolean unRegisted = !mConfig.isHasRegisted;
            boolean unPay = true;
            try {
                unPay = new BigDecimal("0").equals(new BigDecimal(mConfig.minPledgeNum));
            } catch (Exception e) {
            }


            if (TextUtils.isEmpty(inputNum)) {
                if (unRegisted && unPay) {
                    
                    inputNum = "0";
                } else {
                    showToast(R.string.please_input_min_num);
                    return;
                }

            }

            
            String pledgeToAddress = mBinding.tvPledgeAddress.getText().toString().trim();
            if (TextUtils.isEmpty(pledgeToAddress)) {
                showToast("");
                return;
            }

            
            String phonePrex = mBinding.tvPledgePhoneStart.getText().toString().trim();
            if (TextUtils.isEmpty(phonePrex)) {
                showToast(R.string.please_selecte_phone_prefix);
                return;
            }
            phonePrex = phonePrex.replaceAll("XXXX", "");

            
            if (!TextUtils.isEmpty(mConfig.tokenBalance) && new BigDecimal(mConfig.tokenBalance).compareTo(new BigDecimal(inputNum)) < 0) {
                showToast(R.string.balance_no_enaful);
                return;
            }

            getViewModel().doPledggeRegistGas(mAddress, inputNum, phonePrex, mConfig, mSelecteWallet, "", pledgeToAddress);
        });

        getViewModel().mSmOuterChartInfoLiveData.observe(this, info -> initChart(info, true));
        doChartRequst();

        mSmPledgeModel.getLiveData().observe(this, topMediaInfo -> {
            mBinding.smPledgePbText.setText((topMediaInfo.getCurUsedFlow() / 1024 / 1024) + "M /" + (topMediaInfo.getFlowLimit() / 1024 / 1024) + "M");
            mBinding.smPledgeProgressbar.setMax((int) (topMediaInfo.getFlowLimit() / 1024 / 1024));
            mBinding.smPledgeProgressbar.setProgress((int) (topMediaInfo.getCurUsedFlow() / 1024 / 1024));
        });
        mSmPledgeModel.getMediaInfo(this);
        mBinding.smPledgeSuanli.setText("0");

        List<String> data = new ArrayList<>();
        data.add(getString(com.app.R.string.sm_pledge_lasted_week));
        data.add(getString(com.app.R.string.sm_pledge_lasted_mounth));
        data.add(getString(com.app.R.string.sm_pledge_lasted_100day));

        for (int i = 0; i < data.size(); i++) {
            EduMainTabItemBinding binding = EduMainTabItemBinding.inflate(getLayoutInflater(), null, false);
            binding.eduMainName.setText(data.get(i));
            mBinding.smPledgeChartTab.addTab(mBinding.smPledgeChartTab.newTab().setCustomView(binding.getRoot()));
        }
        mBinding.smPledgeChartTab.addOnTabSelectedListener(new CusOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                doChartRequst();
            }
        });
        mBinding.smPledgeChartRaioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            SmOuterChartInfo value = getViewModel().mSmOuterChartInfoLiveData.getValue();
            if (null == value) {
                return;
            }
            boolean left = checkedId == mBinding.smPledgeChartAddressRadio.getId();
            initChart(value, left);
        });

    }


    private void initChart(SmOuterChartInfo info, boolean left) {

        

        LineChart chart = mBinding.smPledgeChart;
        SmChartInfo chartInfo = null;
        if (left) {
            chartInfo = info.getDestoryChartInfo();
        } else {
            chartInfo = info.getWalletChartInfo();
        }

        
        List<String> dataX = new ArrayList<>();
        List<Float> dataY = new ArrayList<>();
        if (null != chartInfo.chatX) {
            dataX.addAll(chartInfo.chatX);
        }
        if (null != chartInfo.chatY) {
            dataY.addAll(chartInfo.chatY);
        }

        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dataX.size(); i++) {
            entries.add(new Entry(i, dataY.get(i)));
        }
        int tabPosition = mBinding.smPledgeChartTab.getSelectedTabPosition();

        
        initChart(chart, dataX);
        if (entries.size() > 0) {
            drawLine(chart, entries, tabPosition == 2);
        }


        String tips;
        if (tabPosition == 0) {
            tips = getString(com.app.R.string.sm_pledge_week);
        } else if (tabPosition == 1) {
            tips = getString(com.app.R.string.sm_pledge_mounth);
        } else {
            tips = getString(com.app.R.string.sm_pledge_100day);
        }


        
        String begin = getString(com.app.R.string.sm_pledge_recent) + tips + getString(com.app.R.string.sm_pledge_destory) + BuildConfig.EVMOS_FAKE_UNINT + ":";


        String end = info.getDestoryWallet() + " " + BuildConfig.EVMOS_FAKE_UNINT;
        SpannableString spannableString = new SpannableString(begin + end);
        spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.default_theme_color)), begin.length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mBinding.smPledgeChartTips1.setText(spannableString);

        String begin2 = getString(com.app.R.string.sm_pledge_recent) + tips + getString(com.app.R.string.sm_pledge_destory_address);
        String end2 = info.getDestoryAdress();
        SpannableString spannableString2 = new SpannableString(begin2 + end2);
        spannableString2.setSpan(new ForegroundColorSpan(getColor(R.color.default_theme_color)), begin2.length(), spannableString2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mBinding.smPledgeChartTips2.setText(spannableString2);
    }


    private boolean first = true;

    
    private void doChartRequst() {
        if (first) {
            first = false;
            
            
            getViewModel().getChartInfo("1", "7");
        } else {
            int tabPosition = mBinding.smPledgeChartTab.getSelectedTabPosition();
            String page_size = "7";
            if (tabPosition == 0) {
                page_size = "7";
            } else if (tabPosition == 1) {
                page_size = "30";
            } else {
                page_size = "100";
            }
            getViewModel().getChartInfo("1", page_size);
        }


    }


    private void initChart(LineChart lineChart, List<String> datax) {
        
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);

        lineChart.setDrawGridBackground(false);
        
        lineChart.getAxisLeft().setEnabled(true);
        lineChart.getAxisRight().setEnabled(false);
        
        lineChart.setDragEnabled(true);
        
        lineChart.setScaleEnabled(false);
        lineChart.setHighlightPerTapEnabled(false);

        
        lineChart.setDrawBorders(false);
        
        Legend legend = lineChart.getLegend();
        
        legend.setEnabled(false);

        
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        
        
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setDrawGridLines(false);

        xAxis.setValueFormatter((value, axis) -> {
            if (datax == null || datax.size() == 0) {
                return "";
            }
            return datax.get((int) value % datax.size());
        });


        YAxis yAxis = lineChart.getAxisLeft();
        
        yAxis.setDrawAxisLine(false);
        
        yAxis.setDrawGridLines(true);


    }

    private void drawLine(LineChart chart, ArrayList<Entry> entries, boolean toMore) {
        
        LineDataSet lineDataSet = new LineDataSet(entries, "");
        Context context = chart.getContext();
        initLineDataSet(lineDataSet, context, toMore);
        
        lineDataSet.setDrawValues(false);


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.fade_red);
        lineDataSet.setFillDrawable(drawable);
        for (ILineDataSet dataSet : dataSets) {
            dataSet.setDrawFilled(true);
        }
        chart.invalidate();
        chart.setData(data);
        chart.invalidate();
        
        
        
        chart.moveViewToX(data.getEntryCount() - 3);
    }

    
    private void initLineDataSet(LineDataSet lineDataSet, Context context, boolean toMore) {

        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setColor(context.getResources().getColor(R.color.coin_line_color));
        lineDataSet.setCircleColor(context.getResources().getColor(R.color.coin_line_color));
        lineDataSet.setHighLightColor(context.getResources().getColor(R.color.coin_line_color));

        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        
        lineDataSet.setDrawFilled(false);
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        if (toMore) {
            lineDataSet.setLineWidth(1.5f);
            lineDataSet.setCircleRadius(1.5f);
            lineDataSet.setDrawCircles(false);
        } else {
            lineDataSet.setLineWidth(1.5f);
            lineDataSet.setCircleRadius(4f);
            lineDataSet.setDrawCircles(true);
        }

    }

    @Override
    public void initData() {
        super.initData();
        
        getViewModel().observe(getViewModel().mConfigLD, this::setInfoData);

        
        getViewModel().observe(getViewModel().mPledgeResultLD, bean -> {
            if (bean.success) {
                showToast(R.string.operate_success);
                getData();
            } else {
                showToast(bean.info);
            }
        });

        getViewModel().observe(getViewModel().mHashPledgeResultLD, bean -> {
            if (bean.success) {
                showToast(R.string.operate_success);
                getData();
            } else {
                showToast(bean.info);
            }
        });

        
        getViewModel().observe(getViewModel().mUnPledgeResultLD, bean -> {
            if (bean.success) {
                showToast(R.string.operate_success);
                getData();
            } else {
                showToast(bean.info);
            }
        });

        
        getViewModel().observe(getViewModel().mLingQuResultLD, bean -> {
            if (bean.success) {
                showToast(R.string.operate_success);
                getData();
            } else {
                showToast(bean.info);
            }
        });

        
        getViewModel().observe(getViewModel().mResultLD, bean -> {
            if (bean.success) {
                showToast(R.string.operate_success);
                finish();
            } else {
                showToast(bean.info);
            }
        });

        getData();
    }

    
    private void getData() {
        
        String noSegment = SpUtil.getNodeNoSegm();
        getViewModel().getConfig(mAddress, noSegment);
    }

    private void setInfoData(EvmosPledgeConfigBean evmosPledgeConfigBean) {
        if (null == evmosPledgeConfigBean || !evmosPledgeConfigBean.isSuccess) {
            if (null != evmosPledgeConfigBean && !TextUtils.isEmpty(evmosPledgeConfigBean.errorInfo)) {
                showToast(evmosPledgeConfigBean.errorInfo);
            }
            return;
        }
        mBinding.etMinNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String trim = s.toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    mBinding.smPledgeSuanli.setText("0");
                } else {
                    try {
                        BigDecimal decimal = new BigDecimal(evmosPledgeConfigBean.ratio);
                        BigDecimal input = new BigDecimal(trim);
                        mBinding.smPledgeSuanli.setText(decimal.multiply(input).stripTrailingZeros().toPlainString());
                    } catch (Exception e) {
                        mBinding.smPledgeSuanli.setText("");
                    }
                }
            }
        });
        mConfig = evmosPledgeConfigBean;

        String showTokenName = evmosPledgeConfigBean.getTokenName().toUpperCase();
        String showTokenName2 = evmosPledgeConfigBean.tokenNameDestory.toUpperCase();

        
        String wakuangyue = getString(R.string.sm_pledge_string_17) + "<font color=\"#111111\">" + evmosPledgeConfigBean.tokenBalance + showTokenName2 + "</font>";
        mBinding.tvPledgeWakuangYue.setText(Html.fromHtml(wakuangyue));

        

        mBinding.tvNickname.setText("" + mNickName);

        
        mBinding.tvMyAddress.setText(mAddress);

        
        if (null != evmosPledgeConfigBean.myMobileList && evmosPledgeConfigBean.myMobileList.size() > 0) {
            mBinding.tvMyDid.setText(evmosPledgeConfigBean.myMobileList.get(0) + "");
        } else {
            mBinding.tvMyDid.setText(" - - ");
        }


        
        mBinding.tvPledgePhoneStart.setText(evmosPledgeConfigBean.phoneStartList.get(0) + "");

        
        mBinding.tvPledgeYue.setText(evmosPledgeConfigBean.totalHasPledgeNum + " " + showTokenName);
        
        mBinding.tvPledgeChuangshiMoney.setText(evmosPledgeConfigBean.prePledgeNum + " " + showTokenName);


        
        mBinding.tvPledgeYizhiyaMoney.setText(evmosPledgeConfigBean.remainPledgeNum + " " + showTokenName);

        
        mBinding.tvPledgeShengxiMoney.setText(evmosPledgeConfigBean.canWithdrawNum + " " + showTokenName2);

        
        mBinding.tvPledgeAddress.setText(mAddress);

        
        if (!evmosPledgeConfigBean.isHasRegisted) {
            if (new BigDecimal("0").equals(new BigDecimal(evmosPledgeConfigBean.minPledgeNum))) {
                MyAlertDialog alertDialog = new MyAlertDialog(this, getString(R.string.min_pledge_destory_tips));
                alertDialog.setonclick(new MyAlertDialog.Onclick() {
                    @Override
                    public void Yes() {
                        alertDialog.dismiss();

                    }

                    @Override
                    public void No() {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setNoBtnGone();
                alertDialog.show();

            }
            mBinding.btnMint.setText(R.string.pledge_comm);
            mBinding.etMinNum.setHint(String.format(getString(R.string.min_pledge_num2), evmosPledgeConfigBean.minPledgeNum + " " + evmosPledgeConfigBean.tokenNameDestory.toUpperCase()));
        } else {
            mBinding.btnMint.setText(getString(R.string.sm_pledge_string_1));
            mBinding.etMinNum.setHint(getString(R.string.please_input_min_num2));
        }

        mBinding.etMinNum.setText("");

    }

    @Override
    protected void onDestroy() {
        getViewModel().onDestroy();
        super.onDestroy();
    }
}
