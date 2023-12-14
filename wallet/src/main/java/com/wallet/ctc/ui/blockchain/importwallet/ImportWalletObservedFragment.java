

package com.wallet.ctc.ui.blockchain.importwallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseFragment;
import com.wallet.ctc.base.BaseWebViewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;



public class ImportWalletObservedFragment extends BaseFragment {


    Unbinder mUnbinder;
    @BindView(R2.id.password)
    EditText password;
    @BindView(R2.id.import_wallet)
    TextView importWallet;
    @BindView(R2.id.what_is_mnemonic)
    TextView whatIsMnemonic;
    private Intent intent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import_wallet_observice, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        initView(view, inflater);
        return view;
    }

    private void initView(View view, LayoutInflater inflater) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick({ R2.id.import_wallet, R2.id.what_is_mnemonic})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.import_wallet) {

        } else if (i == R.id.what_is_mnemonic) {
            intent = new Intent(getActivity(), BaseWebViewActivity.class);
            intent.putExtra("url", "http://www.baidu.com");
            intent.putExtra("title", getString(R.string.what_id_observed));
            startActivity(intent);

        } else {
        }
    }
}
