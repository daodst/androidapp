

package com.wallet.ctc.crypto;

import static wallet.core.jni.CoinType.TRON;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.ByteString;
import com.wallet.ctc.R;
import com.wallet.ctc.api.blockchain.TrxApi;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.model.blockchain.TrxBlockHeardBean;
import com.wallet.ctc.model.blockchain.TrxTransSignBean;
import com.wallet.ctc.model.blockchain.TrxTransactionResultBean;
import com.wallet.ctc.model.blockchain.TrxTransferBean;
import com.wallet.ctc.model.blockchain.TrxTrc20TransferBean;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.HexUtils;
import com.wallet.ctc.util.LogUtil;

import org.web3j.utils.Numeric;

import java.math.BigDecimal;

import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import wallet.core.java.AnySigner;
import wallet.core.jni.proto.Tron;



public class TrxTransctionUtil {
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private Context mContext;
    private TrxApi mApi = new TrxApi();
    private WalletDBUtil walletDBUtil;
    private TransferBean mBean;
    private int type;
    private WalletTransctionUtil.TrxTransctionListen mListen;
    public TrxTransctionUtil(Context context, TransferBean mBean, WalletTransctionUtil.TrxTransctionListen mListen){
        walletDBUtil=WalletDBUtil.getInstent(context);
        this.type=mBean.getType();
        this.mContext=context;
        this.mBean=mBean;
        this.mListen=mListen;
    }

    public void pushTranfer(String pwd) {
        String amountStr=new BigDecimal(Math.pow(10,mBean.getDecimal())).multiply(new BigDecimal(mBean.getPrice())).setScale(0).toPlainString();
        WalletEntity walletEntity=WalletDBUtil.getInstent(mContext).getWalletInfoByAddress(mBean.getPayaddress());
        String pubKey=WalletUtil.getDecryptionKey(walletEntity.getmPrivateKey(),pwd);
        String sign="";

        if(!TextUtils.isEmpty(mBean.getData())){
            TrxTransferBean transferBean=new TrxTransferBean(mBean.getAllAddress(),amountStr,pubKey);
            transferBean.setData(mBean.getData());
            transferBean.setNotsend(true);
            sign=gson.toJson(transferBean);
        }else if(TextUtils.isEmpty(mBean.getTokenAddress())){
            TrxTransferBean transferBean=new TrxTransferBean(mBean.getAllAddress(),amountStr,pubKey);
            sign=gson.toJson(transferBean);
        }else {
            TrxTrc20TransferBean transferBean=new TrxTrc20TransferBean(mBean.getAllAddress(),mBean.getTokenAddress(),amountStr,pubKey);
            sign=gson.toJson(transferBean);
        }
        LogUtil.d(""+sign);
        String ss=WalletUtil.getTrandsSign(sign, WalletUtil.TRX_COIN);
        LogUtil.d(""+ss);
        try {
            if(!TextUtils.isEmpty(ss)&&ss.startsWith("{")){
                TrxTransSignBean signBean = gson.fromJson(ss, TrxTransSignBean.class);
                if(gson.toJson(signBean.getError()).length()>4){
                    mListen.onFail(gson.toJson(signBean.getError()));
                }else {
                    mListen.showTransctionSuccess(signBean.getResult()+"");
                }
            }else {
                mListen.showTransctionSuccess(ss);
            }

            LogUtil.d(""+ss);
        }catch (Exception e){
            LogUtil.d(""+e.getMessage());
        }
    }

    
    public void getnowblock(String pwd,boolean needsend){
        LogUtil.d("getnowblock");
        if(null!=mListen){
            mListen.showLoading();
        }
        WalletEntity walletEntity = walletDBUtil.getWalletInfoByAddress(mBean.getPayaddress(), mBean.getType());
        if (!walletEntity.getmPassword().equals(DecriptUtil.MD5(pwd))) {
            if (null != mListen)
                mListen.onFail(mContext.getString(R.string.password_error2));
            return;
        }
        mApi.getnowblock().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<TrxBlockHeardBean>(mContext) {
                    @Override
                    public void onNexts(TrxBlockHeardBean baseEntity) {
                        if(null==baseEntity||null==baseEntity.getBlock_header()||null==baseEntity.getBlock_header().getRaw_data()){
                            error(mContext.getString(R.string.param_check_error));
                        }else {
                            getSign(baseEntity,pwd,needsend);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d("getnowblock");
                       error("");
                    }
                });
    }

    
    private void pushNewTranfer(String data){
        mApi.sendTransaction(data).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<TrxTransactionResultBean>(mContext) {
                    @Override
                    public void onNexts(TrxTransactionResultBean baseEntity) {
                        if(baseEntity.isResult()){
                            if(null!=mListen){
                                mListen.showTransctionSuccess(baseEntity.getTxid());
                            }
                        }else {
                            error(baseEntity.getCode());
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        error("");
                    }
                });
    }

    
    private void getSign(TrxBlockHeardBean heardBean,String pwd,boolean needsend){

        String fromAddress=mBean.getPayaddress();
        String toAddress=mBean.getAllAddress();
        String contractAddress=mBean.getTokenAddress();
        String amount=mBean.getPrice();
        WalletEntity mWallet=walletDBUtil.getWalletInfoByAddress(fromAddress, WalletUtil.TRX_COIN);
        if(null==mWallet){
            error(mContext.getString(R.string.param_check_error));
            return;
        }
        String privateKey=WalletUtil.getDecryptionKey(mWallet.getmPrivateKey(),pwd);
        Tron.BlockHeader blockHeader= Tron.BlockHeader.newBuilder()
                .setTimestamp(heardBean.getBlock_header().getRaw_data().getTimestamp())
                .setTxTrieRoot(ByteString.copyFrom(Numeric.hexStringToByteArray(heardBean.getBlock_header().getRaw_data().getTxTrieRoot())))
                .setParentHash(ByteString.copyFrom(Numeric.hexStringToByteArray(heardBean.getBlock_header().getRaw_data().getParentHash())))
                .setNumber(heardBean.getBlock_header().getRaw_data().getNumber())
                .setWitnessAddress(ByteString.copyFrom(Numeric.hexStringToByteArray(heardBean.getBlock_header().getRaw_data().getWitness_address())))
                .setVersion(heardBean.getBlock_header().getRaw_data().getVersion())
                .build();
        Tron.Transaction transaction;
        if(mBean.getInfo().equalsIgnoreCase("FreezeBalanceContract")){
            String rece=mBean.getReceiver_address();
            if(TextUtils.isEmpty(rece)){
                rece=fromAddress;
            }
            Tron.FreezeBalanceContract.Builder mSmartContract=Tron.FreezeBalanceContract.newBuilder()
                    .setOwnerAddress(fromAddress)
                    .setFrozenDuration(mBean.getFrozen_duration())
                    .setFrozenBalance(mBean.getFrozen_balance())
                    .setReceiverAddress(rece)
                    .setResource(mBean.getResource());
            transaction = Tron.Transaction.newBuilder()
                    .setFreezeBalance(mSmartContract)
                    .setExpiration(mBean.getExpiration())
                    .setTimestamp(mBean.getTimestamp())
                    .setBlockHeader(blockHeader)
                    .build();
        }else if(mBean.getInfo().equalsIgnoreCase("UnfreezeBalanceContract")){
            String rece=mBean.getReceiver_address();
            if(TextUtils.isEmpty(rece)){
                rece=fromAddress;
            }
            Tron.UnfreezeBalanceContract.Builder mSmartContract=Tron.UnfreezeBalanceContract.newBuilder()
                    .setOwnerAddress(fromAddress)
                    .setReceiverAddress(rece)
                    .setResource(mBean.getResource());
            transaction = Tron.Transaction.newBuilder()
                    .setUnfreezeBalance(mSmartContract)
                    .setExpiration(mBean.getExpiration())
                    .setTimestamp(mBean.getTimestamp())
                    .setBlockHeader(blockHeader)
                    .build();
        }else if(TextUtils.isEmpty(contractAddress)){
            String amountStr=new BigDecimal(Math.pow(10,mBean.getDecimal())).multiply(new BigDecimal(amount)).setScale(0).toPlainString();
            Tron.TransferContract.Builder mContract=Tron.TransferContract.newBuilder()
                    .setOwnerAddress(fromAddress)
                    .setToAddress(toAddress)
                    .setAmount(new BigDecimal(amountStr).longValue());
            transaction = Tron.Transaction.newBuilder()
                    .setTransfer(mContract)
                    .setExpiration(mBean.getExpiration())
                    .setTimestamp(mBean.getTimestamp())
                    .setBlockHeader(blockHeader)
                    .build();
        }else if(!TextUtils.isEmpty(mBean.getData())){
            long feelimit=100000000L;
            if(mBean.getFee_limit()>0){
                feelimit=mBean.getFee_limit();
            }
            Tron.TriggerSmartContract.Builder mSmartContract=Tron.TriggerSmartContract.newBuilder()
                    .setOwnerAddress(fromAddress)
                    .setContractAddress(contractAddress)
                    .setData(ByteString.copyFrom(Numeric.hexStringToByteArray(mBean.getData())));
            transaction = Tron.Transaction.newBuilder()
                    .setTriggerSmartContract(mSmartContract)
                    .setFeeLimit(feelimit)
                    .setExpiration(mBean.getExpiration())
                    .setTimestamp(mBean.getTimestamp())
                    .setBlockHeader(blockHeader)
                    .build();
        }else {
            String amountStr=new BigDecimal(Math.pow(10,mBean.getDecimal())).multiply(new BigDecimal(amount)).setScale(0).toPlainString();
            amountStr=HexUtils.tenToHex(amountStr);
            Tron.TransferTRC20Contract.Builder trc20Contract=Tron.TransferTRC20Contract.newBuilder()
                    .setOwnerAddress(fromAddress)
                    .setContractAddress(contractAddress)
                    .setToAddress(toAddress)
                    
                    .setAmount(ByteString.copyFrom(Numeric.hexStringToByteArray(amountStr)));
            transaction = Tron.Transaction.newBuilder()
                    .setTransferTrc20Contract(trc20Contract)
                    .setFeeLimit(100000000L)
                    .setExpiration(mBean.getExpiration())
                    .setTimestamp(mBean.getTimestamp())
                    .setBlockHeader(blockHeader)
                    .build();
        }
        Tron.SigningInput.Builder signingInput = Tron.SigningInput.newBuilder()
                .setTransaction(transaction)
                .setPrivateKey(ByteString.copyFrom(Numeric.hexStringToByteArray(privateKey)));
        try {
            Tron.SigningOutput output = AnySigner.sign(signingInput.build(), TRON, Tron.SigningOutput.parser());
            String singString=output.getJson();
            if(mBean.getInfo().equalsIgnoreCase("FreezeBalanceContract")){
                if(null!=mListen){
                    mListen.showTransctionSuccess(singString);
                }
                needsend=true;
            }
            if(needsend){
                pushNewTranfer(singString);
            }else {
                if(null!=mListen){
                    mListen.showTransctionSuccess(singString);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void error(String msg){
        if(null!=mListen){
            mListen.onFail(msg);
        }
    }
}
