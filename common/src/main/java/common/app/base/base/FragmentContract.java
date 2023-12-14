

package common.app.base.base;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;


public interface FragmentContract {


    String CLASS = "CLASS";
    String DATA = "DATA";
    String TYPE = "TYPE";


    int NONO = 0;
    int BA_TPARCELABLE = 1;
    int BA_TSTRING = 2;
    int BA_TSTRINGLIST = 3;
    int BA_TPARCELABLELIST = 4;
    int BA_SERIALIZABLE = 5;

    @IntDef({NONO, BA_TPARCELABLE, BA_TSTRING, BA_TSTRINGLIST, BA_TPARCELABLELIST, BA_SERIALIZABLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ArgsType {
    }

    public interface Contract4Activity {


        
        @Nullable
        void replaceFragment(BaseFragment fragment);


        
        void replaceFragment4S(@Nullable BaseFragment fragment, @Nullable String value);


        
        void replaceFragment4P(@Nullable BaseFragment fragment, @Nullable Parcelable value);

        
        void replaceFragment4PList(@Nullable BaseFragment fragment, @Nullable ArrayList<? extends Parcelable> value);

        
        void replaceFragment4SList(@Nullable BaseFragment fragment, @Nullable ArrayList<String> value);


        BaseFragment setFragment(@Nullable String clazz);

        BaseFragment setFragment4P(@Nullable String clazz, @Nullable Parcelable value);

        BaseFragment setFragment4S(@Nullable String clazz, @Nullable String value);

        BaseFragment setFragment4PList(@Nullable String clazz, @Nullable ArrayList<? extends Parcelable> value);

        BaseFragment setFragment4SList(@Nullable String clazz, @Nullable ArrayList<String> value);

    }


    public interface Contract4Fragment {
        
        Bundle get4SBundle(@Nullable String value);

        
        Bundle get4PBundle(@Nullable Parcelable value);

        
        Bundle get4PListBundle(@Nullable ArrayList<? extends Parcelable> value);

        
        Bundle get4SListBundle(@Nullable ArrayList<String> value);


        void targetFragment(@Nullable String clsFragment);

        
        void targetFragment4P(@Nullable String clsFragment, @Nullable Parcelable value);

        void targetFragment4P(@Nullable String clsFragment, @Nullable Parcelable value,@Nullable int from);

        void targetFragment4S(@Nullable String clsFragment, @Nullable Serializable value);

        void targetFragment4S(@Nullable String clsFragment, @Nullable String value);


        void targetFragment4Result(@Nullable String clsFragment);

        


        void targetFragment4PList(@Nullable String clsFragment, @Nullable ArrayList<? extends Parcelable> value);

        void targetFragment4SList(@Nullable String clsFragment, @Nullable ArrayList<String> value);

        void targetFragment4PForResult(@Nullable String clsFragment, @Nullable Parcelable value);

        void targetFragment4SForResult(@Nullable String clsFragment, @Nullable String value);

        void targetFragment4PListForResult(@Nullable String clsFragment, @Nullable ArrayList<? extends Parcelable> value);

        void targetFragment4SListForResult(@Nullable String clsFragment, @Nullable ArrayList<String> value);

    }


}
