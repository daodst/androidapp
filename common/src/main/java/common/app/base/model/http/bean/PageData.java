

package common.app.base.model.http.bean;

import java.util.ArrayList;
import java.util.List;



public class PageData<T> {

    

    private int total;
    private int per_page;
    private int current_page;
    private List<T> data;

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

    @Override
    public String toString() {
        return "PageData{" +
                "total=" + total +
                ", per_page='" + per_page + '\'' +
                ", current_page=" + current_page +
                ", data=" + data +
                '}';
    }

    public List<T> getData() {
        if (null == data) {
            data = new ArrayList<>();
        }
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
