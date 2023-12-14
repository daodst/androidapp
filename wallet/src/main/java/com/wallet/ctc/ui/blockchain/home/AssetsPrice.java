

package com.wallet.ctc.ui.blockchain.home;



public class AssetsPrice {

    private String number;

    private String price;

    public String getNumber() {
        if(number==null||number.equals("")){
            number="0";
        }
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPrice() {
        if(price==null||price.equals("")){
            price="0";
        }
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
