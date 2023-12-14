

package com.app.lg4e.ui.fragment.splash;

import common.app.base.base.BaseFragmentView;
import common.app.base.base.BasePresenter;



public interface SplashContract {

    public interface View extends BaseFragmentView<Presenter> {

        
        void toLogInFragment();
        
        void toMainTab();


    }

    public interface Presenter extends BasePresenter {
        void pushLogFile();
    }


}
