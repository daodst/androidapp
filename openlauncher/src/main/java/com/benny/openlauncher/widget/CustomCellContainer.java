package com.benny.openlauncher.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.app.root.wallet.NxnWalletLayoutView;
import com.app.root.wallet.OnClickDetectorListener;
import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.HomeActivity;
import com.benny.openlauncher.blurring.BitmapUtils;
import com.benny.openlauncher.databinding.WidgetChatCustomBinding;
import com.benny.openlauncher.databinding.WidgetGatewayBinding;
import com.benny.openlauncher.manager.Setup;
import com.benny.openlauncher.util.TabActionsHelper;
import com.benny.openlauncher.util.Tool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.app.RxBus;
import common.app.im.event.Notice;
import common.app.pojo.ChatWidgetItemEntity;
import common.app.utils.AppWidgetUtils;
import common.app.utils.SpUtil;
import in.championswimmer.sfg.lib.SimpleFingerGestures;


public class CustomCellContainer extends LinearLayout implements MyViewGroup {
    public CustomCellContainer(Context context) {
        super(context);
        initView(context, null);
    }

    public CustomCellContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public CustomCellContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @Override
    public void init() {
    }

    private void initView(Context context, AttributeSet attrs) {
        setOrientation(VERTICAL);
        inflate(context, R.layout.view_custom_cell_container, this);
        
        WidgetGatewayBinding gatewayBinding = WidgetGatewayBinding.bind(findViewById(R.id.includeGateway));
        WidgetChatCustomBinding chatBinding = WidgetChatCustomBinding.bind(findViewById(R.id.includeChat));

        
        View softView = findViewById(R.id.soft);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) softView.getLayoutParams();
        params.height = bottomHeight();
        softView.setLayoutParams(params);

        
        initGatewayView(context, gatewayBinding);
        initChatView(context, chatBinding);
        initRootWalletView(findViewById(R.id.rootWalletView));
    }

    private void initRootWalletView(NxnWalletLayoutView rootWalletView) {
        AppWidgetUtils.walletCountLD.observe(HomeActivity._launcher, countMap->{
            if (countMap == null) {
                return;
            }
            int dstCount = countMap.get(AppWidgetUtils.KEY_DST);
            int ethCount = countMap.get(AppWidgetUtils.KEY_ETH);
            int bscCount = countMap.get(AppWidgetUtils.KEY_BSC);
            rootWalletView.updateWalletCount(dstCount, ethCount, bscCount);
        });
        rootWalletView.setOnClickDetectorListener(new OnClickDetectorListener() {
            @Override
            public void onClick(String type) {
                Map<String,String> args = new HashMap<>();
                args.put("from", type);
                TabActionsHelper.action(rootWalletView, TabActionsHelper.ACTION_ROOT_WALLET, args);
            }

            @Override
            public void onLongClick(String type) {
            }

            @Override
            public void onHit(String fromType, String toType) {
                Map<String,String> args = new HashMap<>();
                args.put("from", fromType);
                args.put("to", toType);
                TabActionsHelper.action(rootWalletView, TabActionsHelper.ACTION_ROOT_WALLET, args);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initGatewayView(Context context, WidgetGatewayBinding gatewayBinding) {
        TextView title = gatewayBinding.widgetGatewayTitle;
        TextView blockView = gatewayBinding.widgetGatewaySubtitle;
        TextView gatewaySignal = gatewayBinding.widgetGatewaySignal;
        ImageView signalImage = gatewayBinding.widgetGatewaySignalImg;

        gatewayBinding.getRoot().setOnClickListener(v -> {
            RxBus.getInstance().post(new Notice(Notice.TEST_NET_STATUS));
            
            
        });
        AppWidgetUtils.gatewayBlock.observe(HomeActivity._launcher, block -> {
            blockView.setText(context.getString(R.string.widget_gateway_block_height, block));
        });
        String nodeName = SpUtil.getNodeName();
        String name = context.getString(R.string.widget_gateway_name);
        title.setText(name + nodeName);
        AppWidgetUtils.gatewayPing.observe(HomeActivity._launcher, mPing -> {
            
            
            String ping = null;
            int resource = R.mipmap.icon_gateway_wifi0;
            if (0 < mPing && mPing <= 500) {
                ping = context.getString(R.string.widget_gateway_signal_3) + " Ping ";
                resource = R.mipmap.icon_gateway_wifi3;
            } else if (500 < mPing && mPing <= 1000) {
                ping = context.getString(R.string.widget_gateway_signal_2) + " Ping ";
                resource = R.mipmap.icon_gateway_wifi2;
            } else if (mPing > 1000) {
                ping = context.getString(R.string.widget_gateway_signal_1) + " Ping ";
                resource = R.mipmap.icon_gateway_wifi1;
            } else {
                
                RxBus.getInstance().post(new Notice(Notice.PING_FAIL));
                ping = "-- Ping ";
            }

            gatewaySignal.setText(ping + mPing);
            signalImage.setImageResource(resource);
        });

        
        AppWidgetUtils.gatewayName.observe(HomeActivity._launcher, newNodeName -> {
            title.setText(SpUtil.getNodeName());
        });
    }

    @SuppressLint("SetTextI18n")
    private void initChatView(Context context, WidgetChatCustomBinding chatBinding) {
        ImageView mImageView = chatBinding.imageView4;
        mImageView.setOnClickListener(v -> TabActionsHelper.action(mImageView, TabActionsHelper.ACTION_CHAT));

        RecyclerView recyclerView = chatBinding.recyclerView;
        Adapter mAdapter = new Adapter(new ArrayList<>());
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

        
        AppWidgetUtils.chatUnreadLiveData.observe(HomeActivity._launcher, mAdapter::setNewData);
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
            
            if (item.izServerNotice) {
                RequestOptions options = RequestOptions.circleCropTransform();
                Glide.with(mContext).asBitmap().load(R.drawable.cus_server_ico).apply(options).into(image);
            } else {
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
        }

        public int getUserColorByIndex(int index) {
            int[] colors = {R.color.launcher_name_02, R.color.launcher_name_03, R.color.launcher_name_04,
                    R.color.launcher_name_05, R.color.launcher_name_06, R.color.launcher_name_07,
                    R.color.launcher_name_08, R.color.launcher_name_01};
            return colors[index % 8];
        }
    }

    
    @Override
    public int bottomHeight() {
        
        int iconSize = Setup.appSettings().getDockIconSize();
        int bottomHeight = Tool.dp2px((iconSize + 50)); 
        if (Setup.appSettings().getDockShowLabel()) bottomHeight += Tool.dp2px(20);
        return bottomHeight;
    }

    @Override
    public int getCellWidth() {
        return 0;
    }

    @Override
    public int getCellHeight() {
        return 0;
    }

    @Override
    public int getCellSpanV() {
        return 0;
    }

    @Override
    public int getCellSpanH() {
        return 0;
    }

    @Override
    public void setBlockTouch(boolean v) {

    }

    @Override
    public int getChildCount() {
        return super.getChildCount();
    }

    @Override
    public void setGestures(@Nullable SimpleFingerGestures v) {

    }

    @Override
    public List<View> getAllCells() {
        return new ArrayList<>();
    }

    @Override
    public void setGridSize(int x, int y) {

    }

    @Override
    public void setHideGrid(boolean hideGrid) {

    }

    @Override
    public void resetOccupiedSpace() {

    }

    @Override
    public void projectImageOutlineAt(@NonNull Point newCoordinate, @Nullable Bitmap bitmap) {

    }

    @Override
    public void clearCachedOutlineBitmap() {

    }

    @Override
    public CellContainer.DragState peekItemAndSwap(@NonNull DragEvent event, @NonNull Point coordinate) {
        return CellContainer.DragState.OutOffRange;
    }

    @Override
    public CellContainer.DragState peekItemAndSwap(int x, int y, Point coordinate) {
        return CellContainer.DragState.OutOffRange;
    }

    @Override
    public void animateBackgroundShow() {

    }

    @Override
    public void animateBackgroundHide() {

    }

    @Override
    public Point findFreeSpace() {
        return null;
    }

    @Override
    public Point findFreeSpace(int spanX, int spanY) {
        return null;
    }

    @Override
    public void addViewToGrid(@NonNull View view, int x, int y, int xSpan, int ySpan) {

    }

    @Override
    public void addViewToGrid(@NonNull View view) {

    }

    @Override
    public void setOccupied(boolean b, @NonNull CellContainer.LayoutParams lp) {

    }

    @Override
    public boolean checkOccupied(Point start, int spanX, int spanY) {
        return false;
    }

    @Override
    public View coordinateToChildView(Point pos) {
        return null;
    }

    @Override
    public CellContainer.LayoutParams coordinateToLayoutParams(int mX, int mY, int xSpan, int ySpan) {
        return null;
    }

    @Override
    public void touchPosToCoordinate(@NonNull Point coordinate, int mX, int mY, int xSpan, int ySpan, boolean checkAvailability) {

    }

    @Override
    public void touchPosToCoordinate(Point coordinate, int mX, int mY, int xSpan, int ySpan, boolean checkAvailability, boolean checkBoundary) {

    }


}
