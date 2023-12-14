package com.app.home.ui.vote.detial;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.R;
import com.app.databinding.ActivityVoteDetialBinding;
import com.app.databinding.ActivityVoteDetialHeaderBinding;
import com.app.home.pojo.VoteDetial;
import com.app.home.pojo.VoteInfoDetialListWapper;
import com.app.home.pojo.rpc.DposVoteParam;
import com.app.home.ui.utils.TimeUtils;
import com.app.home.ui.vote.detial.adapter.VoteDetialAdapter;
import com.app.home.ui.vote.detial.adapter.VoteDetialCunKuanAdapter;
import com.app.home.ui.vote.detial.adapter.VoteDetialUpdateAdapter;
import com.app.home.ui.vote.detial.dialog.VoteDetialDialogFragment;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.view.dialog.TransConfirmDialogBuilder;
import com.wallet.ctc.view.view.LoadMoreFooter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import common.app.base.BaseActivity;
import common.app.mall.util.ToastUtil;
import common.app.utils.DisplayUtils;


public class VoteDetialActivity extends BaseActivity<VoteDetialVM> {


    private ActivityVoteDetialBinding mBinding;

    private VoteDetialAdapter mAdapter;


    private static String VOTE_ID = "id";

    public static Intent getIntent(Context context, String id) {
        Intent intent = new Intent(context, VoteDetialActivity.class);
        intent.putExtra(VOTE_ID, id);
        return intent;
    }

    public String mID;
    
    private int mDecimal = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBinding = ActivityVoteDetialBinding.inflate(getLayoutInflater());
        
        mID = getIntent().getStringExtra(VOTE_ID);
        super.onCreate(savedInstanceState);
        setContentView(mBinding.getRoot());
    }

    private VoteDetialCunKuanAdapter mCunKuanAdapter;
    private VoteDetialUpdateAdapter mUpdateAdapter;

    @Override
    public void initData() {
        List<AssertBean> assets = WalletDBUtil.getInstent(this).getMustWallet(WalletUtil.MCC_COIN);
        mDecimal = assets.get(0).getDecimal();
        if (mDecimal == 0) {
            mDecimal = 18;
        }

        mBinding.voteDetialTopbar.setLeftTv(v -> {
            finish();
        }).setMiddleTv(R.string.vote_detial_title, R.color.default_titlebar_title_color);

        
        mBinding.voteDetialBt.setOnClickListener(v -> {
            
            WalletEntity walletEntity = WalletDBUtil.getInstent(this).getWalletInfo(WalletUtil.MCC_COIN);
            if (null == walletEntity) {
                ToastUtil.showToast(R.string.get_wallet_address_fail);
                return;
            }
            showVoteDialog(walletEntity);
        });


        
        bulidRv();


        
        bulidHeader();

        getViewModel().mGasLiveData.observe(this, info -> {
            DposVoteParam param = null;
            if (info.param instanceof DposVoteParam) {
                param = (DposVoteParam) info.param;
            } else {
                return;
            }
            TransConfirmDialogBuilder.builder(this, info.mWalletEntity).amount(info.consume)
                    
                    .fromAddress(param.voter)
                    
                    .toAddress("")
                    .type(WalletUtil.MCC_COIN)
                    .orderDesc(getString(R.string.vote_detial_bt_tips))
                    
                    .gasFeeWithToken(info.getShowFee(BuildConfig.EVMOS_FAKE_UNINT))
                    
                    .goTransferListener(pwd -> {
                        getViewModel().vote(info, info.mWalletEntity, pwd);
                    }).show();
        });
        getViewModel().mVoteSuccess.observe(this, aBoolean -> {
            if (aBoolean) {
                getViewModel().getVoteDetial(mID);
                getViewModel().getVoteInfo(mID, null);
            }
        });
    }


    private void showVoteDialog(WalletEntity walletEntity) {
        WalletEntity walletInfo = WalletDBUtil.getInstent(this).getWalletInfo(WalletUtil.MCC_COIN);
        String address = "";
        if (null != walletInfo) {
            address = walletInfo.getAllAddress();
        }
        if (TextUtils.isEmpty(address)) {
            ToastUtil.showToast(getString(R.string.get_wallet_address_fail));
            return;
        }
        VoteDetialDialogFragment dialog = new VoteDetialDialogFragment();
        dialog.show(getSupportFragmentManager(), dialog.getTag());
        String finalAddress = address;
        dialog.setIConsume(type -> {
            VoteDetial value = getViewModel().mLiveData.getValue();
            if (null == value || null == value.getProposalContent() || null == value.getProposalContent().getMessages()) {
                return;
            }
            DposVoteParam param = new DposVoteParam();
            param.option = type;
            
            param.proposal_id = value.getProposalContent().id;
            
            param.voter = finalAddress;

            
            getViewModel().vote(param, walletEntity);

        });
    }

    private LoadMoreFooter mLoadMoreFooter;

    private VoteInfoDetialListWapper mPageList;

    private void bulidRv() {
        mAdapter = new VoteDetialAdapter();
        mBinding.voteDetialRv.setAdapter(mAdapter);

        mLoadMoreFooter = new LoadMoreFooter(this, mBinding.voteDetialRv, () -> {
            getViewModel().getVoteInfo(mID, mPageList);
        });

        getViewModel().mWapperLiveData.observe(this, wapper -> {
            
            mPageList = wapper;
            if (null != mAdapter) {
                mAdapter.setLists(wapper.result);
                mLoadMoreFooter.setState(wapper.isEnd ? LoadMoreFooter.STATE_FINISHED : LoadMoreFooter.STATE_ENDLESS);
            }
        });

        getViewModel().getVoteInfo(mID, null);
    }

    private void bulidHeader() {
        initHeader();

        mCunKuanAdapter = new VoteDetialCunKuanAdapter(mDecimal);
        mHeaderBinding.voteDetialCunkuan.setAdapter(mCunKuanAdapter);

        mUpdateAdapter = new VoteDetialUpdateAdapter();
        mHeaderBinding.voteDetialUpdate.setAdapter(mUpdateAdapter);

        getViewModel().mLiveData.observe(this, this::showTopInfo);
        getViewModel().getVoteDetial(mID);
    }

    private void showTopInfo(VoteDetial detial) {
        

        mHeaderBinding.hvoteDetialAuthor.setText(detial.proposer);
        if (null != detial.deposit_amount) {
            mHeaderBinding.hvoteDetialBalance.setText(detial.deposit_amount.getAmount(mDecimal) + " " + detial.deposit_amount.denom);
        } else {
            mHeaderBinding.hvoteDetialBalance.setText("--");
        }
        mBinding.voteDetialBt.setEnabled(false);
        mBinding.voteDetialBt.setText("");
        VoteDetial.ProposalDetail result = detial.getProposalContent();
        if (null != result) {



            String status = result.status;
            if (TextUtils.equals(status, "0") || TextUtils.equals(status, "1")) {
                
                mBinding.voteDetialBt.setEnabled(false);
                mBinding.voteDetialBt.setText(R.string.vote_detial_bt_tips_un_begin);
            } else if (TextUtils.equals(status, "2")) {
                
                mBinding.voteDetialBt.setText(getString(R.string.vote_detial_bt_tips));
                mBinding.voteDetialBt.setEnabled(true);
            } else {
                
                mBinding.voteDetialBt.setText(R.string.vote_detial_bt_tips_over);
                mBinding.voteDetialBt.setEnabled(false);
            }

            mHeaderBinding.hvoteDetialId.setText(String.valueOf(result.id));
            mHeaderBinding.hvoteDetialTime.setText(TimeUtils.format3(result.submit_time));
            mHeaderBinding.hvoteDetialTimeVote.setText(TimeUtils.format3(result.voting_start_time) + getString(R.string.voting_start_time_tips) + TimeUtils.format3(result.voting_end_time));
            VoteDetial.ProposalDetailResultContent content = result.getMessages();
            if (null != content) {
                mHeaderBinding.hvoteDetialType.setText(content.getType());
                if (null != content.value) {
                    mHeaderBinding.hvoteDetialTitle.setText(content.value.getTitle(content.getRealType()));
                    mHeaderBinding.hvoteDetialDesc.setText(content.value.getDescription(content.getRealType()));
                }
            }
        }

        
        if (null != mUpdateAdapter && null != detial.getProposalContent() && null != detial.getProposalContent().getMessages()) {
            mUpdateAdapter.setInfos(detial.getProposalContent().getMessages().getInfos());
        }
        
        if (null != mCunKuanAdapter) {
            mCunKuanAdapter.setDeposits(detial.deposits);
        }
        BigDecimal yes = new BigDecimal("0");
        BigDecimal abstain = new BigDecimal("0");
        BigDecimal no = new BigDecimal("0");
        BigDecimal no_with_veto = new BigDecimal("0");
        BigDecimal all = new BigDecimal("0");
        if (null != detial.tally) {
            VoteDetial.Tally tally = detial.tally;
            if (!TextUtils.isEmpty(tally.yes)) {
                yes = new BigDecimal(tally.yes);
            }
            if (!TextUtils.isEmpty(tally.abstain)) {
                abstain = new BigDecimal(tally.abstain);
            }
            if (!TextUtils.isEmpty(tally.no)) {
                no = new BigDecimal(tally.no);
            }
            if (!TextUtils.isEmpty(tally.no_with_veto)) {
                no_with_veto = new BigDecimal(tally.no_with_veto);
            }

            all = yes.add(no).add(abstain).add(no_with_veto);
        }


        
        mHeaderBinding.voteDetialVote1.setText(getPer(yes, all));
        mHeaderBinding.voteDetialVote2.setText(getPer(no, all));
        mHeaderBinding.voteDetialVote3.setText(getPer(no_with_veto, all));
        mHeaderBinding.voteDetialVote4.setText(getPer(abstain, all));
        
        ViewGroup.LayoutParams params = mHeaderBinding.voteDetialVote1.getLayoutParams();
        params.width = DisplayUtils.dp2px(this, 50);
        params = mHeaderBinding.voteDetialVote2.getLayoutParams();
        params.width = DisplayUtils.dp2px(this, 50);
        params = mHeaderBinding.voteDetialVote3.getLayoutParams();
        params.width = DisplayUtils.dp2px(this, 50);
        params = mHeaderBinding.voteDetialVote4.getLayoutParams();
        params.width = DisplayUtils.dp2px(this, 50);
        BigDecimal finalAll = all;
        BigDecimal finalYes = yes;
        BigDecimal finalAbstain = abstain;
        BigDecimal finalNo_with_veto = no_with_veto;
        BigDecimal finalNo = no;
        mHeaderBinding.voteDetialVoteParent.post(() -> {

            int canUseSize = mHeaderBinding.voteDetialVoteParent.getWidth() - DisplayUtils.dp2px(this, 15) * 2;

            
            canUseSize = canUseSize - DisplayUtils.dp2px(this, 50) * 4;

            
            ViewGroup.LayoutParams paramsTemp = mHeaderBinding.voteDetialVote1.getLayoutParams();
            paramsTemp.width = (int) (DisplayUtils.dp2px(this, 50) + canUseSize * getPer2(finalYes, finalAll));
            mHeaderBinding.voteDetialVote1.setLayoutParams(paramsTemp);
            paramsTemp = mHeaderBinding.voteDetialVote2.getLayoutParams();
            paramsTemp.width = (int) (DisplayUtils.dp2px(this, 50) + canUseSize * getPer2(finalNo, finalAll));
            mHeaderBinding.voteDetialVote2.setLayoutParams(paramsTemp);
            paramsTemp = mHeaderBinding.voteDetialVote3.getLayoutParams();
            paramsTemp.width = (int) (DisplayUtils.dp2px(this, 50) + canUseSize * getPer2(finalNo_with_veto, finalAll));
            mHeaderBinding.voteDetialVote3.setLayoutParams(paramsTemp);
            paramsTemp = mHeaderBinding.voteDetialVote4.getLayoutParams();
            paramsTemp.width = (int) (DisplayUtils.dp2px(this, 50) + canUseSize * getPer2(finalAbstain, finalAll));
            mHeaderBinding.voteDetialVote4.setLayoutParams(paramsTemp);

        });
    }

    private ActivityVoteDetialHeaderBinding mHeaderBinding;

    public float getPer2(BigDecimal in, BigDecimal all) {
        if (new BigDecimal("0").equals(all)) {
            return 0.25f;
        }
        if (new BigDecimal("0").equals(in)) {
            return 0;
        }
        try {
            return in.divide(all, 2, RoundingMode.DOWN).floatValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getPer(BigDecimal in, BigDecimal all) {
        if (new BigDecimal("0").equals(all)) {
            return "0%";
        }
        if (new BigDecimal("0").equals(in)) {
            return "0%";
        }
        try {
            return in.multiply(new BigDecimal("100")).divide(all, 0, RoundingMode.DOWN) + "%";
        } catch (Exception e) {
            e.printStackTrace();
            return "--";
        }
    }

    private void initHeader() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.activity_vote_detial_header, mBinding.voteDetialRv.getHeaderContainer(), false);
        mHeaderBinding = ActivityVoteDetialHeaderBinding.bind(view);
        mBinding.voteDetialRv.addHeaderView(view);
    }
}
