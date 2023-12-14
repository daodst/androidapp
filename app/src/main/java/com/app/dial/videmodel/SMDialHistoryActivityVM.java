

package com.app.dial.videmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.app.dial.entity.SMDialHistoryEntity;

import java.util.ArrayList;
import java.util.List;

import common.app.base.BaseViewModel;


public class SMDialHistoryActivityVM extends BaseViewModel {
    public MutableLiveData<List<SMDialHistoryEntity>> mRecordList;

    
    public SMDialHistoryActivityVM(@NonNull Application application) {
        super(application);
        mRecordList = new MutableLiveData<>();
    }

    
    public void getDialRecord(int type) {
        List<SMDialHistoryEntity> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new SMDialHistoryEntity("138" + i + "789" + i + "123"
                    , i % 3 == 0 ? "" : ""
                    , i % 3 == 0
                    , type != 2 && i % 2 == 0
                    , System.currentTimeMillis() + ""));
        }
        mRecordList.setValue(list);
    }
}
