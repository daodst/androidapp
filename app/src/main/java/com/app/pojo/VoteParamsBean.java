package com.app.pojo;

import android.os.Parcel;
import android.os.Parcelable;


public class VoteParamsBean implements Parcelable {
    public String subspace;
    public String key;
    public String value;
    public VoteParamsBean(String subspace, String key, String value){
        this.subspace = subspace;
        this.key = key;
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.subspace);
        dest.writeString(this.key);
        dest.writeString(this.value);
    }

    public void readFromParcel(Parcel source) {
        this.subspace = source.readString();
        this.key = source.readString();
        this.value = source.readString();
    }

    protected VoteParamsBean(Parcel in) {
        this.subspace = in.readString();
        this.key = in.readString();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<VoteParamsBean> CREATOR = new Parcelable.Creator<VoteParamsBean>() {
        @Override
        public VoteParamsBean createFromParcel(Parcel source) {
            return new VoteParamsBean(source);
        }

        @Override
        public VoteParamsBean[] newArray(int size) {
            return new VoteParamsBean[size];
        }
    };
}
