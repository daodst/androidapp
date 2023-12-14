

package com.wallet.ctc.nft.bean;

import java.util.List;

public class NtfPage<T> {
    public int total;
    public int page;
    public int page_size;
    public String cursor;
    public List<T> result;
    public String status;

    @Override
    public String toString() {
        return "NtfPage{" +
                "total=" + total +
                ", page=" + page +
                ", page_size=" + page_size +
                ", cursor='" + cursor + '\'' +
                ", result=" + result +
                ", status='" + status + '\'' +
                '}';
    }
}
