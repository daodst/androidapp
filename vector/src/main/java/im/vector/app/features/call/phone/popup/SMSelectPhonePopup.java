

package im.vector.app.features.call.phone.popup;

import android.content.Context;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.AttachPopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.XPopupUtils;

import java.util.ArrayList;
import java.util.List;

import im.vector.app.R;
import im.vector.app.databinding.SmPopupSelectPhoneNumberBinding;


public class SMSelectPhonePopup extends AttachPopupView {
    private SmPopupSelectPhoneNumberBinding mBinding;
    private List<SMPhoneNumberEntity> list = new ArrayList<>();
    private OnSelectListener onSelectListener;

    public void setList(List<SMPhoneNumberEntity> list) {
        this.list = list;
        if (null != adapter) {
            adapter.setData(list);
        }
    }

    public SMSelectPhonePopup(@NonNull Context context) {
        super(context);
    }

    public SMSelectPhonePopup(@NonNull Context context, List<SMPhoneNumberEntity> list, OnSelectListener onSelectListener) {
        super(context);
        this.list = list;
        this.onSelectListener = onSelectListener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.sm_popup_select_phone_number;
    }

    @Override
    protected int getMaxHeight() {
        return (int) (XPopupUtils.getAppHeight(getContext()) * 0.5);
    }

    SMSelectPhoneAdapter adapter;

    @Override
    protected void onCreate() {
        super.onCreate();
        mBinding = SmPopupSelectPhoneNumberBinding.bind(getPopupImplView());

        adapter = new SMSelectPhoneAdapter();
        mBinding.rvList.setAdapter(adapter);
        adapter.setData(list);
        adapter.setICall(call -> {
            for (SMPhoneNumberEntity entity : list) entity.checked = false;
            call.checked = true;
            if (null != onSelectListener) onSelectListener.onSelect(0, call.phoneNumber);
            smartDismiss();
        });

    }
}
