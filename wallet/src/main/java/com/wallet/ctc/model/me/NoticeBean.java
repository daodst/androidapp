

package com.wallet.ctc.model.me;

import java.util.List;



public class NoticeBean {

    

    private int total;
    private int per_page;
    private int current_page;
    private List<DataBean> data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        

        private String id;
        private String title;
        private long w_time;
        private long u_time;

        public String getId() {
            if(id.endsWith(".0")){
                id=id.substring(0,id.length()-2);
            }
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public long getW_time() {
            return w_time;
        }

        public void setW_time(long w_time) {
            this.w_time = w_time;
        }

        public long getU_time() {
            return u_time;
        }

        public void setU_time(long u_time) {
            this.u_time = u_time;
        }
    }
}
