

package com.wallet.ctc.crypto;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.wallet.ctc.DMTransaction.DMTransactionEncoder;
import com.wallet.ctc.R;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.BtcFeesBean;
import com.wallet.ctc.model.blockchain.BtcTransferBean;
import com.wallet.ctc.model.blockchain.CreateEthBean;
import com.wallet.ctc.model.blockchain.FilGasMsgBean;
import com.wallet.ctc.model.blockchain.GasPriceBean;
import com.wallet.ctc.model.blockchain.GoTransBean;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.model.blockchain.XrpTransFee;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.view.dialog.TransferDialog;
import com.wallet.ctc.view.dialog.TransferEthDialog;

import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;

import common.app.AppApplication;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.InputPwdDialog;
import io.reactivex.Observable;



public class WalletTransctionUtil {
    private Context mContext;
    private TransferDialog transferDialog;
    private InputPwdDialog mDialog;
    private TransctionListen transctionListen;
    private EtcTransctionListen etctransctionListen;
    private FilTransctionListen filtransctionListen;
    private DotTransctionListen dottransctionListen;
    private SgbTransctionListen sgbtransctionListen;
    private BchTransctionListen bchtransctionListen;
    private ZecTransctionListen zectransctionListen;
    private LtcTransctionListen ltctransctionListen;
    private DogeTransctionListen dogetransctionListen;
    private DMTransctionListen dmTransctionListen;
    private BtcTransctionListen btcTransctionListen;
    private XrpTransctionListen xrpTransctionListen;
    private TrxTransctionListen trxTransctionListen;
    private SolTransctionListen solTransctionListen;

    public WalletTransctionUtil(Context context) {
        this.mContext = context;
        mDialog = new InputPwdDialog(mContext, mContext.getString(R.string.place_edit_password));
    }

    public WalletTransctionUtil(Context context, boolean showNewpwdDialog) {
        this.mContext = context;
        mDialog = new InputPwdDialog(mContext, mContext.getString(R.string.place_edit_password));
    }

    
    public void doTransction(TransferBean mBean, String pwd) {
        trans(pwd, mBean);
    }

    
    public void DoTransction(TransferBean mBean, boolean show) {
        if (null == mBean) {
            ToastUtil.showToast("transfer data is error, please check info!!");
            return;
        }
        LogUtil.d(mBean.toString());
        if (show) {
            showDia(mBean);
        } else {
            mDialog.setonclick(new InputPwdDialog.Onclick() {
                @Override
                public void Yes(String pwd) {
                    mDialog.dismiss();
                    if (!WalletDBUtil.getInstent(mContext).getWalletInfoByAddress(mBean.getPayaddress(), mBean.getType()).getmPassword().equals(DecriptUtil.MD5(pwd))) {
                        ToastUtil.showToast(mContext.getResources().getString(R.string.password_error2));
                        return;
                    }
                    if (null == mContext) {
                        return;
                    }

                    trans(pwd, mBean);

                }

                @Override
                public void No() {
                    mDialog.dismiss();
                }
            });
            mDialog.show();
        }
    }

    public void setHasShowPwd(boolean hasShowPwd) {
        this.hasShowPwd = hasShowPwd;
    }

    
    private boolean hasShowPwd;
    private String transPwd;

    public void DoTransctionMore(boolean isOnlyFirstShowPwdDialog, TransferBean mBean, boolean show) {
        if (null == mBean) {
            ToastUtil.showToast(" ");
            return;
        }
        LogUtil.d(mBean.toString());
        if (show) {
            showDia(mBean);
        } else {
            if (isOnlyFirstShowPwdDialog) {
                if (!hasShowPwd) {
                    transPwd = "";
                    mDialog.setonclick(new InputPwdDialog.Onclick() {
                        @Override
                        public void Yes(String pwd) {
                            mDialog.dismiss();
                            transPwd = pwd;
                            hasShowPwd = true;
                            if (!WalletDBUtil.getInstent(mContext).getWalletInfoByAddress(mBean.getPayaddress(), mBean.getType()).getmPassword().equals(DecriptUtil.MD5(pwd))) {
                                ToastUtil.showToast(mContext.getResources().getString(R.string.password_error2));
                                return;
                            }
                            if (null == mContext) {
                                return;
                            }
                            trans(pwd, mBean);
                        }

                        @Override
                        public void No() {
                            mDialog.dismiss();
                        }
                    });
                    mDialog.show();
                } else {
                    trans(transPwd, mBean);
                }


            } else {
                mDialog.setonclick(new InputPwdDialog.Onclick() {
                    @Override
                    public void Yes(String pwd) {
                        mDialog.dismiss();
                        if (!WalletDBUtil.getInstent(mContext).getWalletInfoByAddress(mBean.getPayaddress(), mBean.getType()).getmPassword().equals(DecriptUtil.MD5(pwd))) {
                            ToastUtil.showToast(mContext.getResources().getString(R.string.password_error2));
                            return;
                        }
                        if (null == mContext) {
                            return;
                        }
                        trans(pwd, mBean);
                    }

                    @Override
                    public void No() {
                        mDialog.dismiss();
                    }
                });
                mDialog.show();
            }


        }
    }

    private ETHTransctionUtil ethTransctionUtil;
    private ETCTransctionUtil etcTransctionUtil;
    private FILTransctionUtil filTransctionUtil;
    private DotTransctionUtil dotTransctionUtil;
    private BCHTransctionUtil bchTransctionUtil;
    private ZECTransctionUtil zecTransctionUtil;
    private LTCTransctionUtil ltcTransctionUtil;
    private BtcTransctionUtil btcTransctionUtil;
    private DOGETransctionUtil dogeTransctionUtil;
    private XrpTransctionUtil xrpTransctionUtil;
    private TrxTransctionUtil trxTransctionUtil;
    private SgbTransctionUtil sgbTransctionUtil;

    private void trans(String pwd, TransferBean mBean) {
        if (mBean.getType() == WalletUtil.ETH_COIN || mBean.getType() == WalletUtil.ETF_COIN || mBean.getType() == WalletUtil.DMF_COIN ||
                mBean.getType() == WalletUtil.DMF_BA_COIN || mBean.getType() == WalletUtil.HT_COIN || mBean.getType() == WalletUtil.BNB_COIN ||
                (mBean.getType() == WalletUtil.MCC_COIN && !TextUtils.isEmpty(mBean.getTokenType()))) {
            
            if (ethTransctionUtil == null) {
                ethTransctionUtil = new ETHTransctionUtil(mContext, transctionListen);
            }
            ethTransctionUtil.getNonce(pwd, mBean);

        } else if (mBean.getType() == WalletUtil.BTC_COIN) {
            if (null == btcTransctionListen) {
                return;
            }
            if (mBean.getTokenName().toUpperCase().equals("USDT")) {
                if (btcTransctionUtil == null) {
                    btcTransctionUtil = new BtcTransctionUtil(mContext, btcTransctionListen);
                }
                btcTransctionUtil.getNonce(pwd, mBean, new BtcTransferBean.TxoutsBean(mBean.getAllAddress(), mBean.getPrice()));
            } else {
                if (btcTransctionUtil == null) {
                    btcTransctionUtil = new BtcTransctionUtil(mContext, btcTransctionListen);
                }
                btcTransctionUtil.getNonce(pwd, mBean, null);
            }
        } else if (mBean.getType() == WalletUtil.XRP_COIN) {
            if (null == xrpTransctionListen) {
                return;
            }
            if (xrpTransctionUtil == null) {
                xrpTransctionUtil = new XrpTransctionUtil(mContext, xrpTransctionListen);
            }
            xrpTransctionUtil.getNonce(pwd, mBean);

        } else if (mBean.getType() == WalletUtil.EOS_COIN) {

        } else if (mBean.getType() == WalletUtil.TRX_COIN) {
            if (null == trxTransctionListen) {
                return;
            }
            if (trxTransctionUtil == null) {
                trxTransctionUtil = new TrxTransctionUtil(mContext, mBean, trxTransctionListen);
            }
            trxTransctionUtil.getnowblock(pwd, true);
        } else if (mBean.getType() == WalletUtil.DM_COIN || mBean.getType() == WalletUtil.MCC_COIN || mBean.getType() == WalletUtil.OTHER_COIN) {
            if (null == dmTransctionListen) {
                return;
            }
            new DmTransctionUtil(mContext, mBean, dmTransctionListen).getNonce(pwd);
        } else if (mBean.getType() == WalletUtil.ETC_COIN) {
            if (null == etctransctionListen) {
                return;
            }
            if (etcTransctionUtil == null) {
                etcTransctionUtil = new ETCTransctionUtil(mContext, etctransctionListen);
            }
            etcTransctionUtil.getNonce(pwd, mBean);
        } else if (mBean.getType() == WalletUtil.FIL_COIN) {
            if (null == filtransctionListen) {
                return;
            }
            if (filTransctionUtil == null) {
                filTransctionUtil = new FILTransctionUtil(mContext, filtransctionListen);
            }
            filTransctionUtil.getNonce(pwd, mBean);
        } else if (mBean.getType() == WalletUtil.DOT_COIN) {
            if (null == dottransctionListen) {
                return;
            }
            if (dotTransctionUtil == null) {
                dotTransctionUtil = new DotTransctionUtil(mContext, dottransctionListen);
            }
            dotTransctionUtil.getNonce(pwd, mBean);
        } else if (mBean.getType() == WalletUtil.SGB_COIN) {
            if (null == sgbtransctionListen) {
                return;
            }
            if (sgbTransctionUtil == null) {
                sgbTransctionUtil = new SgbTransctionUtil(mContext, sgbtransctionListen);
            }
            sgbTransctionUtil.getNonce(pwd, mBean);
        } else if (mBean.getType() == WalletUtil.BCH_COIN) {
            if (null == bchtransctionListen) {
                return;
            }
            if (bchTransctionUtil == null) {
                bchTransctionUtil = new BCHTransctionUtil(mContext, bchtransctionListen);
            }
            bchTransctionUtil.getBchTxId(mBean, new String(WalletDBUtil.getInstent(AppApplication.getContext()).getWalletInfo().getmPublicKey()), pwd);
        } else if (mBean.getType() == WalletUtil.ZEC_COIN) {
            if (null == zectransctionListen) {
                return;
            }
            if (zecTransctionUtil == null) {
                zecTransctionUtil = new ZECTransctionUtil(mContext, zectransctionListen);
            }
            zecTransctionUtil.getZecTxId(mBean, new String(WalletDBUtil.getInstent(AppApplication.getContext()).getWalletInfo().getmPublicKey()), pwd);
        } else if (mBean.getType() == WalletUtil.LTC_COIN) {
            if (null == ltctransctionListen) {
                return;
            }
            if (ltcTransctionUtil == null) {
                ltcTransctionUtil = new LTCTransctionUtil(mContext, ltctransctionListen);
            }
            ltcTransctionUtil.getLTCTxId(mBean, new String(WalletDBUtil.getInstent(AppApplication.getContext()).getWalletInfo().getmPublicKey()), pwd);
        } else if (mBean.getType() == WalletUtil.DOGE_COIN) {
            if (null == dogetransctionListen) {
                return;
            }
            if (dogeTransctionUtil == null) {
                dogeTransctionUtil = new DOGETransctionUtil(mContext, dogetransctionListen);
            }
            dogeTransctionUtil.getDogeTxId(mBean, new String(WalletDBUtil.getInstent(AppApplication.getContext()).getWalletInfo().getmPublicKey()), pwd);
        } else {
            if (null == dmTransctionListen) {
                return;
            }



            dmTransctionListen.onFail("");


            
        }
    }

    
    public Observable<String> getBalance(WalletEntity wallet) {
        if (null == mContext || null == wallet) {
            return null;
        }

        if (null == transctionListen) {
            return null;
        }
        int type = wallet.getType();
        if (type == WalletUtil.ETH_COIN || type == WalletUtil.ETF_COIN || type == WalletUtil.DMF_COIN ||
                type == WalletUtil.DMF_BA_COIN || type == WalletUtil.HT_COIN || type == WalletUtil.BNB_COIN ||
                type == WalletUtil.MCC_COIN) {
            return new ETHTransctionUtil(mContext, transctionListen).getBalance(wallet);
        } else {
            return null;
        }
    }

    
    public void DoTransctionDapp(TransferBean mBean, int type) {

        TransferEthDialog transferDialog = new TransferEthDialog(mContext);
        transferDialog.setOnDismiss(new TransferEthDialog.Dismiss() {
            @Override
            public void dismiss() {
                if (null == mContext || null == transctionListen) {
                    return;
                }
                transctionListen.onFail("Canceled");
            }
        });
        transferDialog.setTrans(new TransferEthDialog.goTransfer() {
            @Override
            public void goTransfer(String pwd) {
                if (null == mContext) {
                    return;
                }

                if (null == transctionListen) {
                    return;
                }
                if (type == WalletUtil.ETH_COIN || type == WalletUtil.ETF_COIN || type == WalletUtil.DMF_COIN ||
                        type == WalletUtil.DMF_BA_COIN || type == WalletUtil.HT_COIN || type == WalletUtil.BNB_COIN ||
                        type == WalletUtil.MCC_COIN) {
                    new ETHTransctionUtil(mContext, transctionListen).getNonce(pwd, mBean);
                }
            }
        });
        transferDialog.show(mBean);
    }

    
    public void DoTrxTransctionDapp(TransferBean mBean, int type) {

        TransferDialog transferDialog = new TransferDialog(mContext);
        transferDialog.setOnDismiss(new TransferDialog.Dismiss() {
            @Override
            public void dismiss() {
                if (null == mContext || null == trxTransctionListen) {
                    return;
                }
                trxTransctionListen.onFail("Canceled");
            }
        });
        transferDialog.setTrans(new TransferDialog.goTransfer() {
            @Override
            public void goTransfer(String pwd) {
                if (null == mContext || null == trxTransctionListen) {
                    return;
                }
                new TrxTransctionUtil(mContext, mBean, trxTransctionListen).getnowblock(pwd, false);
            }
        });
        transferDialog.show(mBean);
    }

    
    public void doSgbTransctionDapp(String request, String type) {

        InputPwdDialog mPwdDialog = new InputPwdDialog(mContext, mContext.getString(R.string.place_edit_password));
        mPwdDialog.setonclick(new InputPwdDialog.Onclick() {
            @Override
            public void Yes(String pwd) {
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.showToast(R.string.place_edit_password);
                    return;
                }
                mPwdDialog.dismiss();
                
                new SgbTransctionUtil(mContext, sgbtransctionListen).sign(type, request, pwd);
            }

            @Override
            public void No() {
                mPwdDialog.dismiss();
            }
        });
        mPwdDialog.show();
    }


    
    public void DoTransctionBtcUsdt(TransferBean mBean) {
        BtcTransferBean.TxoutsBean txoutsBean = new BtcTransferBean.TxoutsBean();
        txoutsBean.setAddress(mBean.getAllAddress());
        txoutsBean.setAmount(mBean.getPrice());
        transferDialog = new TransferDialog(mContext);
        transferDialog.setTrans(new TransferDialog.goTransfer() {
            @Override
            public void goTransfer(String pwd) {
                if (null == mContext || null == btcTransctionListen) {
                    return;
                }
                new BtcTransctionUtil(mContext, btcTransctionListen).getNonce(pwd, mBean, txoutsBean);
            }
        });
        transferDialog.show(mBean);

    }

    private void showDia(TransferBean mBean) {
        if (mBean.getType() == WalletUtil.ETH_COIN || mBean.getType() == WalletUtil.ETF_COIN || mBean.getType() == WalletUtil.DMF_COIN ||
                mBean.getType() == WalletUtil.DMF_BA_COIN || mBean.getType() == WalletUtil.HT_COIN || mBean.getType() == WalletUtil.BNB_COIN ||
                (mBean.getType() == WalletUtil.MCC_COIN && !TextUtils.isEmpty(mBean.getTokenType()))) {
            
            TransferEthDialog transferDialog = new TransferEthDialog(mContext);
            transferDialog.setTrans(pwd -> {
                if (null == mContext) {
                    return;
                }

                if (null == transctionListen) {
                    return;
                }
                new ETHTransctionUtil(mContext, transctionListen).getNonce(pwd, mBean);

            });
            transferDialog.show(mBean);
        } else {
            transferDialog = new TransferDialog(mContext);
            transferDialog.setTrans(pwd -> {
                if (null == mContext) {
                    return;
                }
                if (mBean.getType() == WalletUtil.BTC_COIN) {
                    if (null == btcTransctionListen) {
                        return;
                    }
                    if (mBean.getTokenName().toUpperCase().equals("USDT")) {
                        new BtcTransctionUtil(mContext, btcTransctionListen).getNonce(pwd, mBean, new BtcTransferBean.TxoutsBean(mBean.getAllAddress(), mBean.getPrice()));
                    } else {
                        new BtcTransctionUtil(mContext, btcTransctionListen).getNonce(pwd, mBean, null);
                    }
                } else if (mBean.getType() == WalletUtil.XRP_COIN) {
                    if (xrpTransctionListen == null) {
                        return;
                    }
                    new XrpTransctionUtil(mContext, xrpTransctionListen).getNonce(pwd, mBean);
                } else if (mBean.getType() == WalletUtil.EOS_COIN) {

                } else if (mBean.getType() == WalletUtil.TRX_COIN) {
                    if (null == trxTransctionListen) {
                        return;
                    }
                    new TrxTransctionUtil(mContext, mBean, trxTransctionListen).getnowblock(pwd, true);
                } else if (mBean.getType() == WalletUtil.ETC_COIN) {
                    if (null == etctransctionListen) {
                        return;
                    }
                    new ETCTransctionUtil(mContext, etctransctionListen).getNonce(pwd, mBean);
                } else if (mBean.getType() == WalletUtil.FIL_COIN) {
                    if (null == filtransctionListen) {
                        return;
                    }
                    new FILTransctionUtil(mContext, filtransctionListen).getNonce(pwd, mBean);
                } else if (mBean.getType() == WalletUtil.DOT_COIN) {
                    if (null == dottransctionListen) {
                        return;
                    }
                    new DotTransctionUtil(mContext, dottransctionListen).getNonce(pwd, mBean);
                } else if (mBean.getType() == WalletUtil.SGB_COIN) {
                    if (null == sgbtransctionListen) {
                        return;
                    }
                    new SgbTransctionUtil(mContext, sgbtransctionListen).getNonce(pwd, mBean);
                } else if (mBean.getType() == WalletUtil.DM_COIN || mBean.getType() == WalletUtil.MCC_COIN || mBean.getType() == WalletUtil.OTHER_COIN) {
                    
                    if (null == dmTransctionListen) {
                        return;
                    }
                    new DmTransctionUtil(mContext, mBean, dmTransctionListen).getNonce(pwd);
                } else if (mBean.getType() == WalletUtil.BCH_COIN) {
                    if (null == bchtransctionListen) {
                        return;
                    }
                    new BCHTransctionUtil(mContext, bchtransctionListen).getBchTxId(mBean, new String(WalletDBUtil.getInstent(AppApplication.getContext()).getWalletInfo().getmPublicKey()), pwd);

                } else if (mBean.getType() == WalletUtil.ZEC_COIN) {
                    if (null == zectransctionListen) {
                        return;
                    }
                    new ZECTransctionUtil(mContext, zectransctionListen).getZecTxId(mBean, new String(WalletDBUtil.getInstent(AppApplication.getContext()).getWalletInfo().getmPublicKey()), pwd);
                } else if (mBean.getType() == WalletUtil.LTC_COIN) {
                    if (null == ltctransctionListen) {
                        return;
                    }
                    new LTCTransctionUtil(mContext, ltctransctionListen).getLTCTxId(mBean, new String(WalletDBUtil.getInstent(AppApplication.getContext()).getWalletInfo().getmPublicKey()), pwd);
                } else if (mBean.getType() == WalletUtil.DOGE_COIN) {
                    if (null == dogetransctionListen) {
                        return;
                    }
                    new DOGETransctionUtil(mContext, dogetransctionListen).getDogeTxId(mBean, new String(WalletDBUtil.getInstent(AppApplication.getContext()).getWalletInfo().getmPublicKey()), pwd);
                } else if (mBean.getType() == WalletUtil.SOL_COIN) {
                    if (null == solTransctionListen) {
                        return;
                    }
                    new SolTransctionUtil(mContext, solTransctionListen).getDefFee(pwd, mBean);
                } else {
                    if (null == dmTransctionListen) {
                        return;
                    }


                    dmTransctionListen.onFail("transfer fail");
                }
            });
            transferDialog.show(mBean);
        }
    }

    
    public void getEthGas(String fromAddress, String tokenType, int walletType, String data, String hexBigAmountValue) {
        if (TextUtils.isEmpty(fromAddress) || null == transctionListen) {
            return;
        }
        new ETHTransctionUtil(mContext, transctionListen).getKGPrice(fromAddress, tokenType, walletType, data, hexBigAmountValue);
    }


    public void getMccGas(String fromAddr, String toAddress, String coinName) {
        if (TextUtils.isEmpty(fromAddr) || TextUtils.isEmpty(coinName) || null == dmTransctionListen) {
            LogUtil.w("mcc gas info error");
            return;
        }
        new DmTransctionUtil(mContext, null, dmTransctionListen).getGas(fromAddr, toAddress, coinName);
    }

    
    public void getEthEIP1559Gas(String fromAddress, String tokenType, String basefee, int walletType) {
        if (TextUtils.isEmpty(fromAddress) || null == transctionListen) {
            return;
        }
        new ETHTransctionUtil(mContext, transctionListen).getGasLimit(fromAddress, tokenType, basefee, walletType);
    }

    
    public void getMaxPriorityFeePerGas(int wallettype) {
        if (null == transctionListen) {
            return;
        }
        new ETHTransctionUtil(mContext, transctionListen).getMaxPriorityFeePerGas(wallettype);
    }

    
    public void getEthDefPrice(int wallettype) {
        if (null == transctionListen) {
            return;
        }
        new ETHTransctionUtil(mContext, transctionListen).getDefGasprice(wallettype);
    }

    
    public void getEtcGasCount(TransferBean bean) {
        if (TextUtils.isEmpty(bean.getPayaddress()) || null == etctransctionListen) {
            return;
        }
        new ETCTransctionUtil(mContext, etctransctionListen).getGasCount(bean);
    }

    
    public void getEtcDefPrice(int wallettype) {
        if (null == etctransctionListen) {
            return;
        }
        new ETCTransctionUtil(mContext, etctransctionListen).getDefGasprice(wallettype);
    }

    public void getFilDefPrice(TransferBean transferBean) {
        if (null == filtransctionListen) {
            return;
        }
        new FILTransctionUtil(mContext, filtransctionListen).getDefGasprice(transferBean);
    }

    public void getGasEstimateMessageGas(TransferBean transferBean) {
        if (null == filtransctionListen) {
            return;
        }
        new FILTransctionUtil(mContext, filtransctionListen).getGasEstimateMessageGas(transferBean);
    }

    public void getDotestimateFee(TransferBean transferBean, String tx) {
        if (null == dottransctionListen) {
            return;
        }
        new DotTransctionUtil(mContext, dottransctionListen).getDotestimateFee(transferBean, tx);
    }

    public void getBchGasFee(TransferBean transferBean) {
        if (null == bchtransctionListen) {
            return;
        }
        new BCHTransctionUtil(mContext, bchtransctionListen).getBCHestimateFee(transferBean);
    }

    public void getZecestimateFee(TransferBean transferBean) {
        if (null == zectransctionListen) {
            return;
        }
        new ZECTransctionUtil(mContext, zectransctionListen).getZecestimateFee(transferBean);
    }

    public void getLTCestimateFee(TransferBean transferBean) {
        if (null == ltctransctionListen) {
            return;
        }
        new LTCTransctionUtil(mContext, ltctransctionListen).getLTCestimateFee(transferBean);
    }

    public void getDogeGasFee(TransferBean transferBean) {
        if (null == dogetransctionListen) {
            return;
        }
        new DOGETransctionUtil(mContext, dogetransctionListen).getDogeestimateFee(transferBean);
    }


    public void getXrpFee(String fromAddress, String tokenType) {
        new XrpTransctionUtil(mContext, xrpTransctionListen).getDefFee();
    }

    
    public void getBtcFee() {
        if (null == btcTransctionListen) {
            return;
        }
        new BtcTransctionUtil(mContext, btcTransctionListen).getDefFee();
    }

    
    public void setOnTransctionListen(TransctionListen mListen) {
        transctionListen = mListen;
    }

    public void setOnDMTransctionListen(DMTransctionListen mListen) {
        dmTransctionListen = mListen;
    }

    public void setOnBtcTransctionListen(BtcTransctionListen mListen) {
        btcTransctionListen = mListen;
    }

    public void setOnXrpTransctionListen(XrpTransctionListen mListen) {
        xrpTransctionListen = mListen;
    }

    public void setTrxTransctionListen(TrxTransctionListen trxTransctionListen) {
        this.trxTransctionListen = trxTransctionListen;
    }

    public void setEtctransctionListen(EtcTransctionListen etctransctionListen) {
        this.etctransctionListen = etctransctionListen;
    }

    public void setFiltransctionListen(FilTransctionListen filtransctionListen) {
        this.filtransctionListen = filtransctionListen;
    }

    public void setSoltransctionListen(SolTransctionListen soltransctionListen) {
        this.solTransctionListen = soltransctionListen;
    }

    public void setDottransctionListen(DotTransctionListen dottransctionListen) {
        this.dottransctionListen = dottransctionListen;
    }

    public void setSgbTransctionListen(SgbTransctionListen sgbtransctionListen) {
        this.sgbtransctionListen = sgbtransctionListen;
    }

    public void setBchtransctionListen(BchTransctionListen bchtransctionListen) {
        this.bchtransctionListen = bchtransctionListen;
    }

    public void setZectransctionListen(ZecTransctionListen zectransctionListen) {
        this.zectransctionListen = zectransctionListen;
    }

    public void setLtctransctionListen(LtcTransctionListen ltctransctionListen) {
        this.ltctransctionListen = ltctransctionListen;
    }

    public void setDogetransctionListen(DogeTransctionListen dogetransctionListen) {
        this.dogetransctionListen = dogetransctionListen;
    }

    
    public interface TransctionListen {
        void showLoading();

        void showGasCount(String gasCount);

        void showGasprice(GasPriceBean gasPriceBean);

        void showEip1559(String baseFeePerGas);

        void showDefGasprice(String defGasprice);

        void showTransctionSuccess(String hash);

        void onFail(String msg);
    }

    
    public interface EtcTransctionListen {
        void showLoading();

        void showGasCount(String gasCount);

        void showDefGasprice(String defGasprice);

        void showTransctionSuccess(String hash);

        void onFail(String msg);
    }

    
    public interface FilTransctionListen {
        void showLoading();


        void showGasEstimateFeeCap(String gasprice);

        void showGasmsg(FilGasMsgBean.ResultBean gasprice);

        void showTransctionSuccess(String hash);

        void onFail(String msg);
    }

    public interface DotTransctionListen {
        void showLoading();


        void showGasEstimateFee(String fee);

        void showTransctionSuccess(String hash);

        void onFail(String msg);
    }

    public interface SgbTransctionListen {
        void showLoading();

        void onSuccess(String hash);

        void onFail(String msg);
    }

    public interface BchTransctionListen {
        void showLoading();


        void showGasEstimateFee(String fee);

        void showTransctionSuccess(String hash);

        void onFail(String msg);
    }

    public interface ZecTransctionListen {
        void showLoading();


        void showGasEstimateFee(String fee);

        void showTransctionSuccess(String hash);

        void onFail(String msg);
    }

    public interface LtcTransctionListen {
        void showLoading();


        void showGasEstimateFee(String fee);

        void showTransctionSuccess(String hash);

        void onFail(String msg);
    }

    public interface DogeTransctionListen {
        void showLoading();

        void showGasEstimateFee(String fee);

        void showTransctionSuccess(String hash);

        void onFail(String msg);
    }

    
    public interface DMTransctionListen {
        void showLoading();

        void showTransctionSuccess(String hash);

        void onFail(String msg);

        void showGasInfo(String gasPrice, String gasLimit);
    }

    
    public interface BtcTransctionListen {
        void showLoading();

        void showDefDee(BtcFeesBean mbean);

        void showTransctionSuccess(String hash);

        void onFail(String msg);
    }

    public interface XrpTransctionListen {
        void showLoading();

        void showDefDee(XrpTransFee mbean);

        void showTransctionSuccess(String hash);

        void onFail(String msg);
    }

    public interface TrxTransctionListen {
        void showLoading();

        void showTransctionSuccess(String hash);

        void onFail(String msg);
    }

    
    public interface SolTransctionListen {
        void showLoading();

        void showTransctionSuccess(String hash);

        void onFail(String msg);
    }

    
    public String sign(String msg, WalletEntity WalletEntity, String pwd) {
        try {
            
            ECKeyPair pair = ECKeyPair.create(new BigInteger(WalletUtil.getDecryptionKey(WalletEntity.getmPrivateKey(), pwd), 16));
            byte[] s = Numeric.toHexStringNoPrefix(msg.getBytes()).getBytes();
            String hexValue = DMTransactionEncoder.signMessage(new String(s), pair);
            if (hexValue.startsWith("0x")) {
                hexValue = hexValue.substring(2, hexValue.length());
            }
            return hexValue;
        } catch (Exception e) {
            LogUtil.d(e.toString());
        }
        return "";
    }

    
    public String signETHTransfer(TransferBean mBean, String pwd, BigInteger nonce, WalletEntity WalletEntity) {
        try {
            GoTransBean goTransBean = new GoTransBean();
            BigDecimal amount = new BigDecimal(mBean.getPrice().trim()).multiply(new BigDecimal(Math.pow(10, mBean.getDecimal())));
            goTransBean.setGasprice(new BigDecimal(mBean.getGasprice()).intValue() + "000000000");
            goTransBean.setGaslimit(mBean.getGascount());
            goTransBean.setAmount(amount.toPlainString());
            goTransBean.setNonce(nonce.intValue());
            goTransBean.setPrivatekey(WalletUtil.getDecryptionKey(WalletEntity.getmPrivateKey(), pwd));
            goTransBean.setToaddress(mBean.getAllAddress());
            goTransBean.setTokenaddress(mBean.getTokenType());
            
            String hexValue = WalletUtil.getTrandsSign(new Gson().toJson(goTransBean), WalletEntity.getType());
            return hexValue;

        } catch (Exception e) {
            ToastUtil.showToast(mContext.getResources().getString(R.string.fee_payment_failed));
            return "";
        }
    }

    
    public String getCreateEthTokenSign(WalletEntity WalletEntity, int gasCount, int nonce, String gasPrice, String pwd, String data) {
        CreateEthBean goTransBean = new CreateEthBean();
        goTransBean.setAmount("0");
        goTransBean.setGaslimit(gasCount);
        goTransBean.setNonce(nonce);
        goTransBean.setGasprice(gasPrice);
        goTransBean.setPrivatekey(WalletUtil.getDecryptionKey(WalletEntity.getmPrivateKey(), pwd));
        goTransBean.setData(data);
        
        String hexValue = WalletUtil.getCreateEthTokenSign(goTransBean);
        return hexValue;
    }

    
    public void getContractEthGas(String fromAddress, String contractAddr, int walletType, String data) {
        if (TextUtils.isEmpty(fromAddress) || null == transctionListen) {
            return;
        }
        new ETHTransctionUtil(mContext, transctionListen).getContractKGPrice(fromAddress, contractAddr, walletType, data);
    }
}
