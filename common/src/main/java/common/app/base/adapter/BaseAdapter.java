
package common.app.base.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public abstract class BaseAdapter<T, V extends BaseHolder> extends RecyclerView.Adapter<V> {

    public interface OnItemClick<T> {
        public void onItemClick(T t, int position);
    }

    public interface OnItemLongClick<T> {
        public void onItemLongClick(T t, int position);
    }

    public interface OnViewClick<T> {
        public void onViewClick(View v, T t, int position);
    }

    private OnItemClick l = null;
    private OnItemLongClick lc = null;
    private OnViewClick vc = null;

    public void setLongClickListener(OnItemLongClick<T> lc) {
        this.lc = lc;
    }

    public void setItemClickListener(OnItemClick<T> lc) {
        this.l = lc;
    }

    public void setViewClickListener(OnViewClick<T> c) {
        this.vc = c;
    }

    public List<T> mDatas = null;

    public void setItems(List<T> items) {
        mDatas = items;
    }

    @Override
    public void onBindViewHolder(@NonNull V holder, int position) {
        holder.setItemListener(l);
        holder.setItemLongClick(lc);
        holder.setViewClick(vc);
        holder.bindData(mDatas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return null == mDatas ? 0 : mDatas.size();
    }

    
    protected <M extends ViewBinding> M createBinding(Class<M> cls, @NonNull ViewGroup parent) {
        if (null == cls) {
            return null;
        }
        try {
            Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            M m = (M) inflate.invoke(null, LayoutInflater.from(parent.getContext()), parent, false);
            return m;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
