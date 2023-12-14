

package common.app.ui.view.update;


public class UpdatePresenter implements UpdateContract.Presenter {


    private UpdateContract.View mView;

    public UpdatePresenter(UpdateContract.View view) {
        this.mView = view;
        this.mView.setPresenter(this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        this.mView = null;
    }

    @Override
    public void getNewApp() {

    }
}
