

package common.app.im.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class Share2Con implements Parcelable {


    @SerializedName(value = "mIocLink", alternate = {"img"})
    public String mIocLink;
    @SerializedName(value = "mLink", alternate = {"link"})
    public String mLink;
    @SerializedName(value = "mTitle", alternate = {"title"})
    public String mTitle;
    @SerializedName(value = "mDigst", alternate = {"content"})
    public String mDigst;
    @SerializedName(value = "mTime", alternate = {"time"})
    public Long mTime;
    @SerializedName(value = "mType", alternate = {"type"})
    public String mType;

    public Share2Con(String iocLink, String link, String title, String digst) {
        mIocLink = iocLink;
        mLink = link;
        mTitle = title;
        mDigst = digst;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mIocLink);
        dest.writeString(this.mLink);
        dest.writeString(this.mTitle);
        dest.writeString(this.mDigst);
    }

    public Share2Con() {
    }

    protected Share2Con(Parcel in) {
        this.mIocLink = in.readString();
        this.mLink = in.readString();
        this.mTitle = in.readString();
        this.mDigst = in.readString();
    }

    public static final Creator<Share2Con> CREATOR = new Creator<Share2Con>() {
        @Override
        public Share2Con createFromParcel(Parcel source) {
            return new Share2Con(source);
        }

        @Override
        public Share2Con[] newArray(int size) {
            return new Share2Con[size];
        }
    };

    @Override
    public String toString() {
        return "Share2Con{" +
                "mIocLink='" + mIocLink + '\'' +
                ", mLink='" + mLink + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mDigst='" + mDigst + '\'' +
                ", mTime=" + mTime +
                ", mType='" + mType + '\'' +
                '}';
    }
}
