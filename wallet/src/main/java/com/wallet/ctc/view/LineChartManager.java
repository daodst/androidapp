

package com.wallet.ctc.view;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;



public class LineChartManager {

    private LineChart lineChart;
    private YAxis leftAxis;   
    private YAxis rightAxis;  
    private XAxis xAxis;      

    public LineChartManager(LineChart mLineChart) {
        this.lineChart = mLineChart;
        leftAxis = lineChart.getAxisLeft();
        rightAxis = lineChart.getAxisRight();
        xAxis = lineChart.getXAxis();
    }

    
    private void initLineChart() {
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawMarkers(false);
        
        lineChart.setDrawBorders(false);
        
        lineChart.getAxisLeft().setDrawGridLines(false);
        
        lineChart.getAxisRight().setDrawGridLines(false);

        lineChart.getAxisLeft().setEnabled(false);
        
        
        lineChart.animateY(1000, Easing.EasingOption.Linear);
        lineChart.animateX(1000, Easing.EasingOption.Linear);
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(9f);
        legend.setFormSize(0.5f);
        legend.setFormLineWidth(0.5f);

        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        
        
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(0f);
        
        leftAxis.setAxisMinimum(0f);
        rightAxis.setAxisMinimum(0f);
    }

    
    public void init() {
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawMarkers(false);
        
        lineChart.setDrawBorders(false);
        
        lineChart.getAxisLeft().setDrawGridLines(false);
        
        lineChart.getAxisRight().setDrawGridLines(false);

        lineChart.getAxisLeft().setEnabled(false);
        
        lineChart.getXAxis().setDrawGridLines(false);
        
        lineChart.animateY(1000, Easing.EasingOption.Linear);
        lineChart.animateX(1000, Easing.EasingOption.Linear);
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(9f);
        legend.setFormSize(0.5f);
        legend.setFormLineWidth(0.5f);

        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        
        
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(0f);
        
        leftAxis.setAxisMinimum(0f);
        rightAxis.setAxisMinimum(0f);
    }

    
    private void initLineDataSet(LineDataSet lineDataSet, int color, boolean mode) {
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setLineWidth(1f);
        
        lineDataSet.setCircleRadius(0.5f);
        
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(9f);
        
        lineDataSet.setDrawFilled(mode);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(8.f);
        
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawCircleHole(false);
        
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
    }

    
    public void showLineChart(List<Float> xAxisValues, List<Float> yAxisValues, String label, int color) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < xAxisValues.size(); i++) {
            entries.add(new Entry(xAxisValues.get(i), yAxisValues.get(i)));
        }
        
        LineDataSet lineDataSet = new LineDataSet(entries, label);
        initLineDataSet(lineDataSet, color, false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);
        
        xAxis.setLabelCount(10, false);
        lineChart.setData(data);
    }

    
    public void showLineChart(List<Float> xAxisValues, List<List<Float>> yAxisValues, List<String> labels, List<Integer> colours,ArrayList<String> trdata) {
        initLineChart();
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        for (int i = 0; i < yAxisValues.size(); i++) {
            ArrayList<Entry> entries = new ArrayList<>();
            for (int j = 0; j < yAxisValues.get(i).size(); j++) {
                if (j >= xAxisValues.size()) {
                    j = xAxisValues.size() - 1;
                }
                entries.add(new Entry(xAxisValues.get(j), yAxisValues.get(i).get(j)));
            }
            LineDataSet lineDataSet = new LineDataSet(entries, labels.get(i));
            if(i==0){
                initLineDataSet(lineDataSet, colours.get(i), true);
            }else{
                initLineDataSet(lineDataSet, colours.get(i), false);
            }
            dataSets.add(lineDataSet);
        }
        LineData data = new LineData(dataSets);
        
        xAxis.setAxisMaximum(6f);
        
        xAxis.setAxisMinimum(0f);
        data.setDrawValues(false);
        xAxis.setLabelCount(7, true);
        ArrayList<String> xValues=trdata;
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int i=(int) value % xValues.size();
                if(i>=xValues.size()){
                    i=xValues.size()-1;
                }
                return xValues.get(i);
            }
        });
        lineChart.setData(data);
    }

    
    public void showLineChart(List<Float> xAxisValues, List<List<Float>> yAxisValues, List<String> labels, List<Integer> colours) {
        initLineChart();
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        for (int i = 0; i < yAxisValues.size(); i++) {
            ArrayList<Entry> entries = new ArrayList<>();
            for (int j = 0; j < yAxisValues.get(i).size(); j++) {
                if (j >= xAxisValues.size()) {
                    j = xAxisValues.size() - 1;
                }
                entries.add(new Entry(xAxisValues.get(j), yAxisValues.get(i).get(j)));
            }
            LineDataSet lineDataSet = new LineDataSet(entries, labels.get(i));
            if(i==0){
                initLineDataSet(lineDataSet, colours.get(i), true);
            }else{
                initLineDataSet(lineDataSet, colours.get(i), false);
            }
            dataSets.add(lineDataSet);
        }
        LineData data = new LineData(dataSets);
        
        xAxis.setAxisMaximum(6f);
        
        xAxis.setAxisMinimum(0f);
        data.setDrawValues(false);
        xAxis.setLabelCount(7, true);
        ArrayList<String> xValues=getX();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xValues.get((int) value % xValues.size());
            }
        });
        lineChart.setData(data);
    }
    private ArrayList<String> getX(){
        ArrayList<String> xValues=new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int num=6;
        int time=Calendar.DAY_OF_MONTH;
            String month=cal.get(Calendar.MONTH)+1+"";
            if(month.length()<2){
                month="0"+month;
            }
            String day=cal.get(Calendar.DAY_OF_MONTH)+"";
            if(day.length()<2){
                day="0"+day;
            }
            xValues.add(month+"-"+day);
            for(int i=0;i<num;i++){
                cal.add(time, -1);
                month=cal.get(Calendar.MONTH)+1+"";
                if(month.length()<2){
                    month="0"+month;
                }
                day=cal.get(Calendar.DAY_OF_MONTH)+"";
                if(day.length()<2){
                    day="0"+day;
                }
                xValues.add(month+"-"+day);
            }
        Collections.reverse(xValues);
        return xValues;
    }

    
    public void setYAxis(float max, float min, int labelCount) {
        if (max < min) {
            return;
        }
        leftAxis.setAxisMaximum(max);
        leftAxis.setAxisMinimum(min);
        leftAxis.setLabelCount(labelCount, false);

        rightAxis.setAxisMaximum(max);
        rightAxis.setAxisMinimum(min);
        rightAxis.setLabelCount(labelCount, false);
        lineChart.invalidate();
    }

    
    public void setXAxis(float max, float min, int labelCount) {
        xAxis.setAxisMaximum(max);
        xAxis.setAxisMinimum(min);
        xAxis.setLabelCount(labelCount, true);

        lineChart.invalidate();
    }

    
    public void setHightLimitLine(float high, String name, int color) {
        if (name == null) {
            name = "";
        }
        LimitLine hightLimit = new LimitLine(high, name);
        hightLimit.setLineWidth(2f);
        hightLimit.setTextSize(10f);
        hightLimit.setLineColor(color);
        hightLimit.setTextColor(color);
        leftAxis.addLimitLine(hightLimit);
        lineChart.invalidate();
    }

    
    public void setLowLimitLine(int low, String name) {
        if (name == null) {
            name = "";
        }
        LimitLine hightLimit = new LimitLine(low, name);
        hightLimit.setLineWidth(4f);
        hightLimit.setTextSize(10f);
        leftAxis.addLimitLine(hightLimit);
        lineChart.invalidate();
    }
    
    public void setDescription(String str) {
        Description description = new Description();
        description.setText(str);
        lineChart.setDescription(description);
        lineChart.invalidate();
    }
}
