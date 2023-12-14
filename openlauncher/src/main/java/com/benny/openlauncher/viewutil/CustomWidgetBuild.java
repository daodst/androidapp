package com.benny.openlauncher.viewutil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.benny.openlauncher.R;


@Deprecated
public class CustomWidgetBuild {

    public static View getGatewayWidget(Context context, int layoutId) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View widgetView = layoutInflater.inflate(R.layout.widget_gateway, null);
        TextView blockView = widgetView.findViewById(R.id.widget_gateway_subtitle);
        TextView gatewaySignal = widgetView.findViewById(R.id.widget_gateway_signal);
        ImageView signalImage = widgetView.findViewById(R.id.widget_gateway_signal_img);
        return widgetView;
    }
}
