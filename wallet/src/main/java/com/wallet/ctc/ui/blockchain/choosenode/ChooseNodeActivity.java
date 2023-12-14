

package com.wallet.ctc.ui.blockchain.choosenode;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.blockchain.BlockChainApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.ChooseNodeBean;
import com.wallet.ctc.ui.blockchain.blockchainlogin.BlockchainLoginActivity;
import com.wallet.ctc.util.ACache;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.util.SettingPrefUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.ActivityRouter;
import common.app.AppApplication;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.utils.NetWorkUtils;
import common.app.utils.ThreadManager;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class ChooseNodeActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.node_sum)
    TextView nodeSum;
    @BindView(R2.id.choose_node_list)
    ListView chooseNodeList;
    private ChooseNodeAdapter mAdapter;
    private BlockChainApi mApi=new BlockChainApi();
    private List<ChooseNodeBean> mList=new ArrayList<>();
    private List<ChooseNodeBean> mDefList=new ArrayList<>();
    private Gson gson=new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private int type=0;
    private List<ChooseNodeBean> list=new ArrayList<>();

    @Override
    public int initContentView() {
        return R.layout.activity_choosenode;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        type=getIntent().getIntExtra("type",0);
        mDefList=walletDBUtil.canChooseNode();
        tvTitle.setText(R.string.choosenode);
        mAcache = ACache.get(this);
        mAdapter=new ChooseNodeAdapter(this);
        String data=mAcache.getAsString("jiedian");
        if(null!=data&&data.length()>6){
            List<ChooseNodeBean> mList=gson.fromJson(data, new TypeToken<List<ChooseNodeBean>>() {
            }.getType());
            showData(mList);
        }else{
            showData(mDefList);
        }
        chooseNodeList.setAdapter(mAdapter);
        chooseNodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mList.get(position).getType()==0) {
                    AlertDialog dialog = new AlertDialog.Builder(ChooseNodeActivity.this)
                            .setNegativeButton(getString(R.string.cancel), null).setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    chooseNode(position);
                                }
                            }).setMessage(getString(R.string.is_normal_not_transefer2)).create();
                    dialog.show();
                }else {
                    chooseNode(position);
                }
            }
        });
    }

    private void chooseNode(int position){
        SettingPrefUtil.setNodeType(this,mList.get(position).getType());
        SettingPrefUtil.setHostUrl(ChooseNodeActivity.this,mList.get(position).getUrl());
        if(type==1){
            HttpMethods.mMccRetrofit=null;
            Intent intent = ActivityRouter.getEmptyContentIntent(this, ActivityRouter.Wallet.A_NewHomeFragment);
            startActivity(intent);
            AppApplication.finishAllActivity();
        }else {
            List<WalletEntity> mWallName=walletDBUtil.getWallName();
            if(mWallName==null||mWallName.size()<1) {
                Intent intent = new Intent(ChooseNodeActivity.this, BlockchainLoginActivity.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = ActivityRouter.getEmptyContentIntent(this, ActivityRouter.Wallet.A_NewHomeFragment);
                startActivity(intent);
                finish();
            }

        }
    }

    @Override
    public void initData() {
        Map<String, Object> params = new TreeMap();
        mLoadingDialog.show();
        mApi.getNode(params).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {
                            List<ChooseNodeBean> mList=gson.fromJson(gson.toJson(baseEntity.getData()), new TypeToken<List<ChooseNodeBean>>() {
                            }.getType());
                            mLoadingDialog.dismiss();
                            mAcache.put("jiedian",gson.toJson(mList));
                            LogUtil.d(mList.size()+"");
                            showData(mList);
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mLoadingDialog.dismiss();
                    }
                });
    }
    @OnClick(R2.id.tv_back)
    public void onViewClicked() {
        finish();
    }



    private void showData(List<ChooseNodeBean> mdat){
        this.mList=mdat;
        for(int i=0;i<mDefList.size();i++){
            String ip=mDefList.get(i).getSmallUrl().replaceAll(" ","");
            boolean cun=false;
            for(int j=0;j<mList.size();j++){
                String ip2=mList.get(j).getSmallUrl().replaceAll(" ","");
                if(ip.equals(ip2)){
                    LogUtil.d(ip+"|||"+ip2);
                    cun=true;
                    break;
                }
            }
            if(!cun){
                mList.add(mDefList.get(i));
            }
        }

        list.clear();
        nodeSum.setText(getString(R.string.nodesum,mList.size()+""));
        mAdapter.bindData(mList);
        mAdapter.notifyDataSetChanged();
        mLoadingDialog.show();
        ThreadManager.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<mList.size();i++){
                    int ws=ispinged(mList.get(i));
                    if(ws==0){
                        ws=pingIpAddress(mList.get(i).getSmallUrl());
                        mList.get(i).setDelay(ws);
                        list.add(mList.get(i));
                    }
                    if(ws==1000){
                        mList.remove(i);
                        i--;
                    }else {
                        mList.get(i).setDelay(ws);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingDialog.dismiss();
                        nodeSum.setText(getString(R.string.nodesum,mList.size()+""));
                        Collections.sort(mList);
                        mAdapter.bindData(mList);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });


    }

    private int ispinged(ChooseNodeBean chooseNode){
        for(int i=0;i<list.size();i++){
            if(list.get(i).getSmallUrl().equals(chooseNode.getSmallUrl())){
                return list.get(i).getDelay();
            }
        }
        return 0;
    }

    private int pingIpAddress(String ipAddress) {
        try {
            if(NetWorkUtils.getNetWorkStatus(this)==NetWorkUtils.NET_WORK_STATE_UN_CONNECT){
                return 1000;
            }
            Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 -w 1 " + ipAddress);
            int status = process.waitFor();
            
            BufferedReader buf =new BufferedReader(new InputStreamReader(process.getInputStream()));
            String str ="";
            String delay="";
            while((str=buf.readLine())!=null) {
                if (str.contains("avg")) {
                    int i = str.indexOf("/", 20);
                    int j = str.indexOf(".", i);
                    System.out.println(":" + str.substring(i + 1, j));
                    delay = str.substring(i + 1, j);
                }
            }
            if(delay.equals("")){
                return 1000;
            }else{
                return Integer.parseInt(delay);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1000;
    }




}
