

package com.wallet.ctc.model.blockchain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class FilTransRecordBean {


    

    private int total;
    private boolean status;
    

    private List<DocsBean> docs;
    

    private ErrorBean error;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<DocsBean> getDocs() {
        return docs;
    }

    public void setDocs(List<DocsBean> docs) {
        this.docs = docs;
    }

    public ErrorBean getError() {
        return error;
    }

    public void setError(ErrorBean error) {
        this.error = error;
    }

    
    public static DocsBean toDoc(EvmosTokenRecordsBean.Data data) {
        if (null == data) {
            return null;
        }
        DocsBean doc = new DocsBean();
        doc.setId(data.Txhash);
        doc.setFrom(data.Facc);
        doc.setTo(data.Tacc);
        doc.setBlock(data.BlockNum);
        if(data.Status == 0) {
            doc.setStatus("ing");
        } else if(data.Status == 1) {
            doc.setStatus("completed");
        } else if(data.Status == 2){
            doc.setStatus("fail");
        } else {
            doc.setStatus(data.Status+"");
        }
        if (data.feeList != null && data.feeList.size() > 0) {
            List<DocsBean.EvmosTokenFee> fees = new ArrayList<>();
            for (EvmosTokenRecordsBean.Fee fee : data.feeList) {
                fees.add(new DocsBean.EvmosTokenFee(fee.coinname, fee.value));
            }
            doc.setFees(fees);
        }
        doc.setDate(data.CreateTime);
        doc.setDirection(data.direction);
        doc.setType(data.TradeType);
        doc.setMemo(data.Memo);

        DocsBean.MetadataBean metaData = new DocsBean.MetadataBean();
        
        metaData.setValue(data.Amount);
        metaData.setSymbol(data.tokenName);
        metaData.setDecimals(data.tokenDecimal);
        metaData.setToken_id(data.Token);
        doc.setMetadata(metaData);
        return doc;

    }

    public static class DocsBean implements Parcelable {
        private String id;
        private int coin;
        private String from;
        private String to;
        private String fee;
        private long date;
        private long block;
        private String status;
        private int sequence;
        private String type;
        private String direction;

        

        private MetadataBean metadata;
        private String memo;
        private List<EvmosTokenFee> fees;

        public DocsBean() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getCoin() {
            return coin;
        }

        public void setCoin(int coin) {
            this.coin = coin;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }

        public long getBlock() {
            return block;
        }

        public void setBlock(long block) {
            this.block = block;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getSequence() {
            return sequence;
        }

        public void setSequence(int sequence) {
            this.sequence = sequence;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public MetadataBean getMetadata() {
            return metadata;
        }

        public void setMetadata(MetadataBean metadata) {
            this.metadata = metadata;
        }

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }

        public List<EvmosTokenFee> getFees() {
            return fees;
        }

        public void setFees(List<EvmosTokenFee> fees) {
            this.fees = fees;
        }

        public static class EvmosTokenFee implements Parcelable{
            public String coinname;
            public String value;
            public EvmosTokenFee(String coinname, String value) {
                this.coinname = coinname;
                this.value = value;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.coinname);
                dest.writeString(this.value);
            }

            public void readFromParcel(Parcel source) {
                this.coinname = source.readString();
                this.value = source.readString();
            }

            protected EvmosTokenFee(Parcel in) {
                this.coinname = in.readString();
                this.value = in.readString();
            }

            public static final Creator<EvmosTokenFee> CREATOR = new Creator<EvmosTokenFee>() {
                @Override
                public EvmosTokenFee createFromParcel(Parcel source) {
                    return new EvmosTokenFee(source);
                }

                @Override
                public EvmosTokenFee[] newArray(int size) {
                    return new EvmosTokenFee[size];
                }
            };
        }

        public static class MetadataBean implements Parcelable {
            private String value;
            private String symbol;
            private int decimals;
            private String asset_id;
            private String token_id;
            private String from;
            private String to;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getSymbol() {
                return symbol;
            }

            public void setSymbol(String symbol) {
                this.symbol = symbol;
            }

            public int getDecimals() {
                return decimals;
            }

            public void setDecimals(int decimals) {
                this.decimals = decimals;
            }

            public String getAsset_id() {
                return asset_id;
            }

            public void setAsset_id(String asset_id) {
                this.asset_id = asset_id;
            }

            public String getToken_id() {
                return token_id;
            }

            public void setToken_id(String token_id) {
                this.token_id = token_id;
            }

            public String getFrom() {
                return from;
            }

            public void setFrom(String from) {
                this.from = from;
            }

            public String getTo() {
                return to;
            }

            public void setTo(String to) {
                this.to = to;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.value);
                dest.writeString(this.symbol);
                dest.writeInt(this.decimals);
                dest.writeString(this.asset_id);
                dest.writeString(this.token_id);
                dest.writeString(this.from);
                dest.writeString(this.to);
            }

            public MetadataBean() {
            }

            protected MetadataBean(Parcel in) {
                this.value = in.readString();
                this.symbol = in.readString();
                this.decimals = in.readInt();
                this.asset_id = in.readString();
                this.token_id = in.readString();
                this.from = in.readString();
                this.to = in.readString();
            }

            public static final Creator<MetadataBean> CREATOR = new Creator<MetadataBean>() {
                @Override
                public MetadataBean createFromParcel(Parcel source) {
                    return new MetadataBean(source);
                }

                @Override
                public MetadataBean[] newArray(int size) {
                    return new MetadataBean[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeInt(this.coin);
            dest.writeString(this.from);
            dest.writeString(this.to);
            dest.writeString(this.fee);
            dest.writeLong(this.date);
            dest.writeLong(this.block);
            dest.writeString(this.status);
            dest.writeInt(this.sequence);
            dest.writeString(this.type);
            dest.writeString(this.direction);
            dest.writeParcelable(this.metadata, flags);
            dest.writeString(this.memo);
            dest.writeTypedList(this.fees);
        }

        public void readFromParcel(Parcel source) {
            this.id = source.readString();
            this.coin = source.readInt();
            this.from = source.readString();
            this.to = source.readString();
            this.fee = source.readString();
            this.date = source.readLong();
            this.block = source.readLong();
            this.status = source.readString();
            this.sequence = source.readInt();
            this.type = source.readString();
            this.direction = source.readString();
            this.metadata = source.readParcelable(MetadataBean.class.getClassLoader());
            this.memo = source.readString();
            this.fees = source.createTypedArrayList(EvmosTokenFee.CREATOR);
        }

        protected DocsBean(Parcel in) {
            this.id = in.readString();
            this.coin = in.readInt();
            this.from = in.readString();
            this.to = in.readString();
            this.fee = in.readString();
            this.date = in.readLong();
            this.block = in.readLong();
            this.status = in.readString();
            this.sequence = in.readInt();
            this.type = in.readString();
            this.direction = in.readString();
            this.metadata = in.readParcelable(MetadataBean.class.getClassLoader());
            this.memo = in.readString();
            this.fees = in.createTypedArrayList(EvmosTokenFee.CREATOR);
        }

        public static final Creator<DocsBean> CREATOR = new Creator<DocsBean>() {
            @Override
            public DocsBean createFromParcel(Parcel source) {
                return new DocsBean(source);
            }

            @Override
            public DocsBean[] newArray(int size) {
                return new DocsBean[size];
            }
        };
    }

    public static class ErrorBean {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
