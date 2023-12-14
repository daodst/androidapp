

package common.app.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class OnLineWallet {


    public String all;
    public List<WalletItem> wallet_list;


    public static class WalletItem implements Parcelable {
        public int id;
        public int coin_id;
        public String number;
        public String locked;
        public String all;
        public String name;
        public String logo;
        public int can_click;
        public int is_down;
        public int is_up;
        public int is_buy;
        public int is_sell;
        public String symbol;
        public String chain;

        @Override
        public String toString() {
            return "WalletItem{" +
                    "id=" + id +
                    ", coin_id=" + coin_id +
                    ", number='" + number + '\'' +
                    ", locked='" + locked + '\'' +
                    ", all='" + all + '\'' +
                    ", name='" + name + '\'' +
                    ", logo='" + logo + '\'' +
                    '}';
        }

        public WalletItem() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeInt(this.coin_id);
            dest.writeString(this.number);
            dest.writeString(this.locked);
            dest.writeString(this.all);
            dest.writeString(this.name);
            dest.writeString(this.logo);
        }

        protected WalletItem(Parcel in) {
            this.id = in.readInt();
            this.coin_id = in.readInt();
            this.number = in.readString();
            this.locked = in.readString();
            this.all = in.readString();
            this.name = in.readString();
            this.logo = in.readString();
        }

        public static final Creator<WalletItem> CREATOR = new Creator<WalletItem>() {
            @Override
            public WalletItem createFromParcel(Parcel source) {
                return new WalletItem(source);
            }

            @Override
            public WalletItem[] newArray(int size) {
                return new WalletItem[size];
            }
        };
    }

}
