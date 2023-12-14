

package com.wallet.ctc.ui.blockchain.addressbook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.db.AddressBookEntity;
import com.wallet.ctc.db.DBManager;
import com.wallet.ctc.util.LogUtil;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.ActivityRouter;
import common.app.mall.util.ToastUtil;



public class AddressBookAddActivity extends BaseActivity {
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_action)
    TextView tvAction;
    @BindView(R2.id.lxr_username)
    EditText lxrUsername;
    @BindView(R2.id.lxr_remark)
    EditText lxrRemark;
    @BindView(R2.id.lxr_address)
    EditText lxrAddress;
    @BindView(R2.id.lxr_addordel)
    ImageView lxrAddordel;
    private int from = 0;
    private Intent intent;
    private AddressBookEntity addressBookEntity=new AddressBookEntity();

    @Override
    public int initContentView() {
        from=getIntent().getIntExtra("type",0);
        return R.layout.activity_add_addressbook;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        if (from == 0) {
            tvTitle.setText(getString(R.string.addaddressbook));
            lxrAddordel.setImageResource(R.mipmap.saoyisao_blue);
        } else {
            String data=getIntent().getStringExtra("data");
            LogUtil.d(data);
            addressBookEntity=new Gson().fromJson(data,AddressBookEntity.class);
            tvTitle.setText(getString(R.string.updaddressbook));
            lxrAddordel.setImageResource(R.mipmap.delete);
            if(null==addressBookEntity){
                return;
            }
            lxrUsername.setText(addressBookEntity.getName());
            lxrAddress.setText(addressBookEntity.getAddress());
            lxrRemark.setText(addressBookEntity.getRemark());
        }
        tvAction.setVisibility(View.VISIBLE);
        tvAction.setText(getString(R.string.save));
    }

    @Override
    public void initData() {

    }

    @OnClick({R2.id.tv_back, R2.id.tv_action,R2.id.lxr_addordel})
    public void onViewClicked(View view) {
        int i1 = view.getId();
        if (i1 == R.id.tv_back) {
            finish();

        } else if (i1 == R.id.tv_action) {
            String username = lxrUsername.getText().toString().trim();
            String remark = lxrRemark.getText().toString().trim();
            String address = lxrAddress.getText().toString().trim();
            if (TextUtils.isEmpty(username)) {
                ToastUtil.showToast(getString(R.string.enter_lxradd));
                return;
            }
            if (TextUtils.isEmpty(address)) {
                ToastUtil.showToast(getString(R.string.enter_lxr_address_error));
                return;
            }

            addressBookEntity.setName(username);
            addressBookEntity.setRemark(remark);
            addressBookEntity.setAddress(address);
            if (from == 0) {
                Random rand = new Random();
                int i = rand.nextInt(5);
                addressBookEntity.setLogo(i);

                List li = DBManager.getInstance(this).queryAdressBookByAddress(address);
                if (null == li || li.size() < 1) {
                    DBManager.getInstance(this).insertAddress(addressBookEntity);
                } else {
                    ToastUtil.showToast(getString(R.string.address_is_save));
                    return;
                }
            } else {
                DBManager.getInstance(this).updateAddress(addressBookEntity);
            }
            ToastUtil.showToast(getString(R.string.caozuo_success));
            finish();

        } else if (i1 == R.id.lxr_addordel) {
            if (from == 0) {
                Intent intent = ActivityRouter.getEmptyContentIntent(this,ActivityRouter.Common.F_QRCodeFragment);
                startActivityForResult(intent, 1000);
            } else {
                AlertDialog alertDialog4 = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.deladdressbook))
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (null == addressBookEntity) {
                                    return;
                                }
                                DBManager.getInstance(AddressBookAddActivity.this).deleteAddressBook(addressBookEntity);
                                ToastUtil.showToast(getString(R.string.caozuo_success));
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create();
                alertDialog4.show();
            }

        } else {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null) {
            String add = data.getStringExtra("toAddress");
            lxrAddress.setText(add);
        }
    }
}
