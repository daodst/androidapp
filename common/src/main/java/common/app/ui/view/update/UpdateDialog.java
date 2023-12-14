

package common.app.ui.view.update;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import common.app.R;
import common.app.R2;
import common.app.base.base.BaseDialogFragment;
import common.app.ui.view.update.adapter.UpdateAdapter;


public class UpdateDialog extends BaseDialogFragment implements UpdateContract.View {


    @BindView(R2.id.update_info)
    ListView mUpdateInfo;
    @BindView(R2.id.bt_update)
    Button mBtUpdate;


    private List<String> mData;
    private Unbinder mUnbinder;

    private static final String PARAM_TIPS = "PARAM_TIPS";
    private UpdateAdapter mAdapter;

    public static UpdateDialog newInstance(ArrayList<String> data) {
        UpdateDialog dialog = new UpdateDialog();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(PARAM_TIPS, data);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void getArgument() {
        Bundle bundle = this.getArguments();
        if (null != bundle) {
            mData = bundle.getStringArrayList(PARAM_TIPS);
        }
        if (null == bundle && null == mData) {
            this.dismiss();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_update, container, false);
        super.onCreateView(inflater, container, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initViews() {
        
        new UpdatePresenter(this);
        mAdapter = new UpdateAdapter(mData);
    }

    @Override
    protected void initEvents() {
        mBtUpdate.setOnClickListener(v -> {
            
            
            mPresenter.getNewApp();
            UpdateDialog.this.dismiss();
        });
    }


    private UpdateContract.Presenter mPresenter;

    @Override
    public void setPresenter(UpdateContract.Presenter presenter) {
        this.mPresenter = presenter;
    }


}
