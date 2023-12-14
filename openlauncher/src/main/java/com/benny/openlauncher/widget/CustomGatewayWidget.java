package com.benny.openlauncher.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.HomeActivity;
import com.benny.openlauncher.model.Item;

import common.app.RxBus;
import common.app.im.event.Notice;
import common.app.utils.AppWidgetUtils;
import common.app.utils.SpUtil;

public class CustomGatewayWidget extends CustomBaseWidget {

    public CustomGatewayWidget(Context context, Item item) {
        super(context, item);
        initView(context, item);
    }

    public CustomGatewayWidget(@NonNull Context context, Item item, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, item);
    }


    @SuppressLint("SetTextI18n")
    private void initView(Context context, Item item) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View widgetView = layoutInflater.inflate(R.layout.widget_gateway, this);
        TextView title = widgetView.findViewById(R.id.widget_gateway_title);
        TextView blockView = widgetView.findViewById(R.id.widget_gateway_subtitle);
        TextView gatewaySignal = widgetView.findViewById(R.id.widget_gateway_signal);
        ImageView signalImage = widgetView.findViewById(R.id.widget_gateway_signal_img);

        widgetView.setOnClickListener(v -> {
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
}
