

package com.app.dial.videmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import common.app.base.BaseViewModel;
import im.vector.app.features.call.phone.popup.SMPhoneNumberEntity;


public class SMDialFragmentVM extends BaseViewModel {
    public MutableLiveData<List<SMPhoneNumberEntity>> mMobileList = new MutableLiveData<>();

    
    public SMDialFragmentVM(@NonNull Application application) {
        super(application);
    }

    
    public void getMobileList() {
        List<SMPhoneNumberEntity> mobiles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mobiles.add(new SMPhoneNumberEntity("1866666666" + i, false));
        }
        mMobileList.setValue(mobiles);
    }
}
