

package common.app.im.event;

import android.os.Parcelable;


public class NetNotices {


    private String action;
    private Parcelable extra;
    private int imState = -1;

    public NetNotices(int imState) {
        this.imState = imState;
    }

    public NetNotices(Parcelable extra, String action) {
        this.extra = extra;
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public Parcelable getExtra() {
        return extra;
    }

    public int getImState() {
        return imState;
    }

    public void setImState(int imState) {
        this.imState = imState;
    }
}
