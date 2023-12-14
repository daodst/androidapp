package com.app.view.groupinformation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.app.R;
import com.app.anim.AnimationAssistanceHelper;
import com.app.anim.AnimationType;
import com.app.databinding.ActivityGroupInformationBinding;
import com.app.me.destory_group.DestoryGroupActivity;
import com.app.view.dialog.HashrateAuthorizationPopup;
import com.app.view.dialog.RateModifyType;
import com.app.view.groupinformation.ratio.commission.CommissionActivity;
import com.app.view.groupinformation.ratio.contribution.ContributionActivity;
import com.app.view.groupinformation.vote.votelist.GovernancePoolsVoteListActivity;
import com.lxj.xpopup.XPopup;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.ui.pay.TransferControlApi;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.view.TitleBarView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.function.Consumer;

import common.app.base.BaseActivity;
import common.app.mall.util.ToastUtil;
import common.app.pojo.ConsumerData;
import im.vector.app.provide.ChatStatusProvide;
import im.wallet.router.listener.TranslationListener;
import im.wallet.router.wallet.pojo.EvmosDaoParams;
import im.wallet.router.wallet.pojo.EvmosGroupDataBean;
import im.wallet.router.wallet.pojo.EvmosMyGroupDataBean;


public class GroupInformationActivity extends BaseActivity<GroupInformationActivityVM> {
    private ActivityGroupInformationBinding mBinding;

    private boolean isGroupLeader;
    private String groupId;
    private String clusterVotePolicy;
    
    private String gasDay, funding;

    
    private EvmosDaoParams.NumRange brokerageRate, salaryRate;

    private PrivateDVMAdapter mDVMAdapter;

    
    public static Intent getIntent(Context context, String groupId, boolean isGroupLeader) {
        Intent intent = new Intent(context, GroupInformationActivity.class);
        intent.putExtra("groupId", groupId);
        intent.putExtra("isGroupLeader", isGroupLeader);
        return intent;
    }

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mBinding = ActivityGroupInformationBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void initParam() {
        super.initParam();
        isGroupLeader = getIntent().getBooleanExtra("isGroupLeader", false);
        groupId = getIntent().getStringExtra("groupId");
    }

    @Override
    public void initView(@Nullable View view) {
        mBinding.titleBarView.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {
                
            }
        });
        
        mBinding.tvEdit.setVisibility(View.VISIBLE);
        mBinding.tvWagesEdit.setVisibility(View.VISIBLE);
        mBinding.tvGetSalary.setVisibility(isGroupLeader ? View.VISIBLE : View.GONE);



        
        mDVMAdapter = new PrivateDVMAdapter(new ArrayList<>());
        mBinding.rvList.setAdapter(mDVMAdapter);
        mDVMAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            EvmosMyGroupDataBean dataBean = (EvmosMyGroupDataBean) adapter.getData().get(position);
            switch (view1.getId()) {
                case R.id.btnReward://Dvm
                case R.id.btnReward2:
                    String address = ChatStatusProvide.getAddress(this);
                    AnimationAssistanceHelper.getInstance(this, address, address, groupId)
                            .setAnimationType(AnimationType.TYPE_DVM_VIRTUAL)
                            .setCallbackConsumer((pS, p2) -> {
                                refreshData();
                            })
                            .init();
                    break;
                case R.id.btnPower:
                    
                    if (!dataBean.data.isHasDvm()) {
                        showToast(R.string.auth_no_dvm_alert);
                        return;
                    }

                    HashrateAuthorizationPopup authPopup = new HashrateAuthorizationPopup(GroupInformationActivity.this, new Consumer<ConsumerData>() {
                        @Override
                        public void accept(ConsumerData consumerData) {
                            String contractAddress = consumerData.value1;
                            String authHeight = consumerData.value2;
                            
                            getViewModel().myDvmApprove(GroupInformationActivity.this, contractAddress, dataBean.data.cluster_chat_id, authHeight);
                        }
                    });
                    new XPopup.Builder(this).dismissOnTouchOutside(true).asCustom(authPopup).show();
                    break;
                case R.id.tvPowerContract:
                case R.id.tvPowerContract2:
                    AllUtils.copyText(dataBean.data.auth_contract);
                    showToast(R.string.copy_success);
                    break;
                case R.id.btnPowerDetail:
                default:
                    break;
            }
        });

        
        mBinding.tvApplyFunds.setOnClickListener(v -> {
            Intent intent = new Intent(this, GovernancePoolsActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("clusterVotePolicy", clusterVotePolicy);
            intent.putExtra("amount", funding);
            startActivity(intent);
        });
        
        mBinding.tvApplyPower.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrganizationAuthorizationActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("clusterVotePolicy", clusterVotePolicy);
            intent.putExtra("amount", gasDay);
            startActivity(intent);
        });
        
        mBinding.tvGetSalary.setOnClickListener(v -> {
            String address = ChatStatusProvide.getAddress(this);
            AnimationAssistanceHelper.getInstance(this, address, address, groupId)
                    .setAnimationType(AnimationType.TYPE_SALARY)
                    .setCallbackConsumer((pS, p2) -> {
                        refreshData();
                    })
                    .init();
        });
        
        mBinding.button2.setOnClickListener(v -> {
            String address = ChatStatusProvide.getAddress(this);
            startActivity(DestoryGroupActivity.getIntent(this, address, groupId));
        });

        
        mBinding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radial) {
                mBinding.radial.setTextColor(ContextCompat.getColor(this, R.color.white));
                mBinding.radial.setTypeface(Typeface.DEFAULT_BOLD);
                mBinding.radia2.setTextColor(ContextCompat.getColor(this, R.color.default_text_three_color));
                mBinding.radia2.setTypeface(Typeface.DEFAULT);

                mBinding.llDao.setVisibility(View.VISIBLE);
                mBinding.rvList.setVisibility(View.GONE);
            } else if (checkedId == R.id.radia2) {
                mBinding.radial.setTextColor(ContextCompat.getColor(this, R.color.default_text_three_color));
                mBinding.radial.setTypeface(Typeface.DEFAULT);
                mBinding.radia2.setTextColor(ContextCompat.getColor(this, R.color.white));
                mBinding.radia2.setTypeface(Typeface.DEFAULT_BOLD);

                mBinding.llDao.setVisibility(View.GONE);
                mBinding.rvList.setVisibility(View.VISIBLE);

            }
        });

        
        mBinding.rlVoteList.setOnClickListener(v -> {
            Intent intent = new Intent(this, GovernancePoolsVoteListActivity.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
        });
    }

    @Override
    public void initData() {
        super.initData();
        getViewModel().getInputLimit();
        getViewModel().observe(viewModel.mLiveData, this::setData);
        
        getViewModel().observe(viewModel.mLimitData, pEvmosDaoParams -> {
            brokerageRate = pEvmosDaoParams.data.device_range;
            salaryRate = pEvmosDaoParams.data.salary_range;
        });

        getViewModel().observe(viewModel.mListData, pObjects -> {
            mDVMAdapter.setNewData(pObjects);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    
    private void refreshData() {
        getViewModel().getInfoData(groupId);
    }

    @SuppressLint("SetTextI18n")
    private void setData(EvmosGroupDataBean pData) {
        if (null == pData || null == pData.data) return;
        EvmosGroupDataBean.Data data = pData.data;
        this.clusterVotePolicy = data.cluster_vote_policy;
        String onlineRate = new BigDecimal(data.online_ratio).multiply(new BigDecimal(100)).toPlainString();
        mBinding.tAliveRate.setText(onlineRate + "%");
        mBinding.tvAliveQuantity.setText(data.cluster_active_device);
        mBinding.tvDevicesTotal.setText(data.cluster_device_amount);
        
        String deviceConnectivityRate = new BigDecimal(data.device_connectivity_rate).multiply(new BigDecimal(100)).toPlainString();
        mBinding.tvConnectRate.setText(deviceConnectivityRate + "%");
        
        String clusterDeviceRatio = new BigDecimal(data.cluster_device_ratio).multiply(new BigDecimal(100)).toPlainString();
        mBinding.tvBrokerage.setText(clusterDeviceRatio + "%");
        
        String clusterSalaryRatio = (new BigDecimal(1).subtract(new BigDecimal(data.cluster_salary_ratio))).multiply(new BigDecimal(100)).toPlainString();
        mBinding.tvWages.setText(clusterSalaryRatio + "%");


        
        mBinding.tvEdit.setOnClickListener(v -> {
            Intent intent = CommissionActivity.getIntent(this, clusterDeviceRatio, clusterVotePolicy, groupId);
            startActivity(intent);

        });
        
        mBinding.tvWagesEdit.setOnClickListener(v -> {
            Intent intent = ContributionActivity.getIntent(this, clusterSalaryRatio, clusterVotePolicy, groupId);
            startActivity(intent);
        });

        
        mBinding.tvGAS.setText(assetsExchange(data.cluster_day_free_gas, 3));
        String gasStr = BuildConfig.EVMOS_FAKE_UNINT + getString(R.string.group_information_string_22);
        mBinding.tvGASEdit.setText(gasStr);

        EvmosGroupDataBean.LevelInfo levelInfo = data.level_info;
        mBinding.tvLevel.setText(levelInfo.level + "");
        mBinding.ivLevel.setImageResource(getLevelDrawableRes(levelInfo.level));

        int progress1 = new BigDecimal(data.cluster_active_device).divide(new BigDecimal(levelInfo.active_amount_next_level), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).intValue();
        int progress2 = new BigDecimal(data.cluster_all_burn).divide(new BigDecimal(levelInfo.burn_amount_next_level), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).intValue();


        mBinding.progressBar1.setProgress(progress1, true);
        mBinding.progressBar2.setProgress(progress2, true);
        mBinding.tvActivePeople.setText(getString(R.string.group_information_string_10) + data.cluster_active_device + "/" + levelInfo.active_amount_next_level);

        String plegAllText = getString(R.string.group_information_string_11) + assetsExchange(data.cluster_all_burn, 2) + "/" + assetsExchange(levelInfo.burn_amount_next_level, 2);
        float needWidth = mBinding.tvTotalPledge.getPaint().measureText(plegAllText);
        float viewWidth = mBinding.tvTotalPledge.getMeasuredWidth();
        if (viewWidth <= needWidth) {
            plegAllText = getString(R.string.group_information_string_11) + "\n" + assetsExchange(data.cluster_all_burn, 2) + "/" + assetsExchange(levelInfo.burn_amount_next_level, 2);
            mBinding.tvTotalPledge.setText(plegAllText);
        }
        mBinding.tvTotalPledge.setText(plegAllText);

        mBinding.rvRemark1.setText(getString(R.string.group_information_remark) + getString(R.string.rvremark1_content));

        
        mBinding.tvDaoPool.setText(assetsExchange(data.cluster_dao_pool_power, 3));
        gasDay = assetsExchange(data.dao_pool_day_free_gas, 3);
        mBinding.tvDaoPoolGas.setText(gasDay);
        funding = assetsExchange(data.dao_pool_available_amount, 4);
        mBinding.tvCanApplyFunds.setText(funding);
        
        String day = " " + BuildConfig.EVMOS_FAKE_UNINT + getString(R.string.group_information_string_22);
        mBinding.tvDaoPowerDay.setText(day);
        mBinding.isPower.setText(data.isDaoHasContact() ? R.string.group_information_string_27 : R.string.group_information_string_26);
        mBinding.isPower.setTextColor(ContextCompat.getColor(this, data.isDaoHasContact() ? R.color.default_theme_color : R.color.default_text_color));
        
        mBinding.tvPowerContract.setText(data.dao_licensing_contract);
        mBinding.tvBlockHeight.setText(data.dao_licensing_height);

        mBinding.tvRemark2.setText(getString(R.string.group_information_remark) + getString(R.string.tvremark2_content));
    }

    
    private String assetsExchange(String bigAmount, int scale) {
        String dstCoinName = getString(R.string.default_token_name2);
        AssertBean assertBean = WalletDBUtil.getInstent(this).getWalletAssets(WalletUtil.MCC_COIN, dstCoinName);
        int decimal = 18;
        if (null != assertBean) {
            decimal = assertBean.getDecimal();
        }
        return AllUtils.getTenDecimalValue(bigAmount, decimal, scale);
    }

    
    private void changeRatio(@RateModifyType int type, String ratio, Runnable pRunnable) {
        TransferControlApi controlApi = new TransferControlApi();
        String address = ChatStatusProvide.getAddress(this);
        TranslationListener listener = new TranslationListener() {
            @Override
            public void onFail(String errorInfo) {
                ToastUtil.showToast(errorInfo);
            }

            @Override
            public void onTransSuccess() {
                pRunnable.run();
                refreshData();
            }
        };
        if (type == RateModifyType.TYPE_BROKERAGE) {
            controlApi.changeDeviceRatio(this, address, groupId, ratio, listener);
        } else if (type == RateModifyType.TYPE_SALARY) {
            controlApi.changeSalaryRatio(this, address, groupId, ratio, listener);
        }
    }

    
    private int getLevelDrawableRes(int level) {
        String resName = "sm_pledge" + level;
        String packageName = getPackageName();
        return getResources().getIdentifier(resName, "mipmap", packageName);
    }
}
