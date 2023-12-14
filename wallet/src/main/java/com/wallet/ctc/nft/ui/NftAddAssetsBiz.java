

package com.wallet.ctc.nft.ui;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wallet.ctc.R;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.NftBean;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.nft.http.NftObserver;
import com.wallet.ctc.nft.http.api.NFTApi;

import common.app.base.model.http.bean.Result;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import retrofit2.HttpException;

public class NftAddAssetsBiz extends BaseBiz {

    
    public MutableLiveData<Result<Boolean>> doAddAssetsLiveData = null;

    private WalletDBUtil walletDBUtil = null;

    private WalletEntity currentWallet = null;

    
    public NftAddAssetsBiz(@NonNull Application application) {
        super(application);
        doAddAssetsLiveData = new MutableLiveData<>();
        walletDBUtil = new WalletDBUtil(application);

    }

    
    public void addNFtAssets(String address) {
        showLoadingDialog("");
        Observable<Result<Boolean>> ob = Observable.create(new ObservableOnSubscribe<NftBean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<NftBean> e) throws Exception {
                if (null == currentWallet) {
                    currentWallet = walletDBUtil.getWalletInfo();
                }

                NftBean bean = walletDBUtil.getNftBeanByAddress(currentWallet.getAllAddress(), address, currentWallet.getType());
                if (null == bean) {
                    
                    bean = walletDBUtil.getNftBeanByAddress(currentWallet.getAllAddress(), address.toLowerCase(), currentWallet.getType());
                }

                if (null == bean) {
                    bean = new NftBean();
                }

                e.onNext(bean);
            }
        }).flatMap(new Function<NftBean, ObservableSource<Result<Boolean>>>() {
            @Override
            public ObservableSource<Result<Boolean>> apply(@NonNull NftBean nftBean) throws Exception {
                if (TextUtils.isEmpty(nftBean.getToken_address())) {
                    
                    return api(NFTApi.class).getNftMetadata(address, WalletUtil.getNftChain(currentWallet.getType())).map(new Function<NftBean, Result<Boolean>>() {
                        @Override
                        public Result<Boolean> apply(@NonNull NftBean nftBean) throws Exception {
                            Result<Boolean> rs = new Result<>();
                            if (TextUtils.isEmpty(nftBean.token_address)) {
                                
                                rs.setStatus(0);
                                rs.setInfo(getApplication().getString(R.string.nft_wrong_contract));
                            } else {
                                
                                walletDBUtil.addNftToken(nftBean, currentWallet);
                                rs.setStatus(1);
                            }
                            return rs;
                        }
                    });
                } else {
                    
                    return Observable.create(new ObservableOnSubscribe<Result<Boolean>>() {
                        @Override
                        public void subscribe(@NonNull ObservableEmitter<Result<Boolean>> emitter) throws Exception {
                            Result<Boolean> rs = new Result<>();
                            rs.setStatus(0);
                            rs.setInfo(getApplication().getString(R.string.nft_wrong_exist_contract));
                            emitter.onNext(rs);
                            emitter.onComplete();
                        }
                    });
                }
            }
        });

        http(ob, new NftObserver<Result<Boolean>>() {
            @Override
            public void onNext(@NonNull Result<Boolean> data) {
                dismissLoadingDialog();
                doAddAssetsLiveData.setValue(data);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                super.onError(e);
                dismissLoadingDialog();
                Result<Boolean> rs = new Result<>();
                rs.setStatus(0);
                if (e instanceof HttpException) {
                    Log.d("fenghl", ((HttpException) e).code() + "");
                }
                if (e instanceof HttpException) {
                    HttpException he = (HttpException) e;
                    if (he.code() == 404) {
                        rs.setInfo(getApplication().getString(R.string.nft_wrong_contract));
                    } else if (he.code() == 404) {
                        rs.setInfo(getApplication().getString(R.string.nft_wrong_contract));
                    } else {
                        rs.setInfo(getApplication().getString(R.string.nft_net_error));
                    }
                } else {
                    rs.setInfo(getApplication().getString(R.string.nft_net_error));
                }

                doAddAssetsLiveData.setValue(rs);
            }
        });
    }

}
