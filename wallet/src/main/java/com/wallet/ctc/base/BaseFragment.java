

package com.wallet.ctc.base;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;

import com.wallet.ctc.util.ACache;



public class BaseFragment extends Fragment {
    public ACache mAcache;
    public Activity context;
    public void getData(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = (Activity)context;
    }


}
