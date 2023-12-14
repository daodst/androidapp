

package com.wallet.ctc.view.wheelview;

public interface WheelAdapter<T> {
	
	public int getItemsCount();
	
	
	public T getItem(int index);

	public T getItemImg(int index);
	
	
	public int indexOf(T o);
}
