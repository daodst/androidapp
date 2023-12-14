

package common.app.ui.view.update;

import common.app.base.base.BaseDialogView;
import common.app.base.base.BasePresenter;


public class UpdateContract {

    public interface View extends BaseDialogView<Presenter> {

    }

    public interface Presenter extends BasePresenter {

        void getNewApp();
    }
}
