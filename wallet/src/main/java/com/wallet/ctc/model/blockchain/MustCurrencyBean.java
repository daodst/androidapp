

package com.wallet.ctc.model.blockchain;

import java.io.Serializable;



public class MustCurrencyBean implements Serializable{
    private int logo;
    private String name;
    private String address;
    private double num;
    private double sumprice;
    private String gas;


    public MustCurrencyBean(int logo,String name,String address,String gas){
        this.logo=logo;
        this.name=name;
        this.address=address;
        this.gas=gas;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getNum() {
        return num;
    }

    public void setNum(double num) {
        this.num = num;
    }

    public double getSumprice() {
        return sumprice;
    }

    public void setSumprice(double sumprice) {
        this.sumprice = sumprice;
    }
}
