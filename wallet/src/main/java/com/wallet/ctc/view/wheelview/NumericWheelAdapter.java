

package com.wallet.ctc.view.wheelview;

import java.util.ArrayList;
import java.util.List;


public class NumericWheelAdapter implements WheelAdapter {
	private List<DemoBean> list=new ArrayList<>();

	public NumericWheelAdapter(List<DemoBean> list) {
		this.list=list;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position).getName();
	}

	@Override
	public Object getItemImg(int position) {
		return list.get(position).getImg();
	}

	@Override
	public int getItemsCount() {
		return list.size();
	}
	
	@Override
	public int indexOf(Object o){
		int num=0;
		for(int i=0;i<list.size();i++){
			if(list.get(i).getName().equals(o.toString())){
				num=i;
				break;
			}
		}
		return num;
	}

}
