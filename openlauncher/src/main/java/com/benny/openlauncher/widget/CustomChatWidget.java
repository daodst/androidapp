package com.benny.openlauncher.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.HomeActivity;
import com.benny.openlauncher.blurring.BitmapUtils;
import com.benny.openlauncher.model.Item;
import com.benny.openlauncher.util.TabActionsHelper;
import com.benny.openlauncher.util.Tool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import common.app.pojo.ChatWidgetItemEntity;
import common.app.utils.AppWidgetUtils;


public class CustomChatWidget extends CustomBaseWidget {
    private Adapter mAdapter;
    private ImageView mImageView;

    public CustomChatWidget(Context context, Item item) {
        super(context, item);
        initView(context, item);
    }

    public CustomChatWidget(@NonNull Context context, Item item, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, item);
    }

    public void seItemLongListener(OnLongClickListener listener) {
        mAdapter.getEmptyView().setOnLongClickListener(listener);
        mImageView.setOnLongClickListener(listener);
        mAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            listener.onLongClick(view);
            return true;
        });
    }

    private void initView(Context context, Item item) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View widgetView = layoutInflater.inflate(R.layout.widget_chat_custom, this);
        mImageView = widgetView.findViewById(R.id.imageView4);
        mImageView.setOnClickListener(v -> TabActionsHelper.action(mImageView, TabActionsHelper.ACTION_CHAT));

        RecyclerView recyclerView = widgetView.findViewById(R.id.recyclerView);
        mAdapter = new Adapter(new ArrayList<>());
        mAdapter.setEmptyView(R.layout.widget_chat_empty, recyclerView);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            ChatWidgetItemEntity entity = (ChatWidgetItemEntity) adapter.getData().get(position);
            HashMap<String, String> map = new HashMap<>();
            map.put("roomId", entity.roomId);
            TabActionsHelper.action(view, TabActionsHelper.ACTION_CHAT_DETAIL, map);
        });
        mAdapter.getEmptyView().setOnClickListener(v -> {
            TabActionsHelper.action(mAdapter.getEmptyView(), TabActionsHelper.ACTION_CHAT);
        });
        
        recyclerView.setAdapter(mAdapter);

        AppWidgetUtils.chatUnreadLiveData.observe(HomeActivity._launcher, chatWidgetItemEntities -> {
            
            mAdapter.setNewData(chatWidgetItemEntities);
        });


        
    }

    static class Adapter extends BaseQuickAdapter<ChatWidgetItemEntity, BaseViewHolder> {
        public Adapter(@Nullable List<ChatWidgetItemEntity> data) {
            super(R.layout.widget_chat_item, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, ChatWidgetItemEntity item) {
            String num = Math.abs(item.number) > 99 ? "……" : "" + Math.abs(item.number);
            helper.setText(R.id.widget_chat_num, num).setVisible(R.id.widget_chat_num, item.number > 0);

            ImageView image = helper.getView(R.id.widget_chat_avatar);
            if (!TextUtils.isEmpty(item.image)) {
                RequestOptions options = RequestOptions.circleCropTransform();
                Glide.with(mContext).asBitmap().load(item.image).apply(options).into(image);
            } else {
                int color = getUserColorByIndex(item.colorIdTag);
                Drawable drawable = TextDrawable.builder().beginConfig().bold().endConfig().buildRect(item.displayName.substring(0, 1), color);
                Bitmap bitmap = BitmapUtils.drawableToBitmap(drawable, Tool.dp2px(50), Tool.dp2px(50));
                RequestOptions options = RequestOptions.circleCropTransform();
                Glide.with(mContext).asBitmap().load(bitmap).apply(options).into(image);
                
            }
        }

        public int getUserColorByIndex(int index) {
            int[] colors = {R.color.launcher_name_02, R.color.launcher_name_03, R.color.launcher_name_04,
                    R.color.launcher_name_05, R.color.launcher_name_06, R.color.launcher_name_07,
                    R.color.launcher_name_08, R.color.launcher_name_01};
            return colors[index % 8];
        }
    }
}
