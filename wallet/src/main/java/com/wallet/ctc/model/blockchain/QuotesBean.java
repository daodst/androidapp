

package com.wallet.ctc.model.blockchain;

import java.io.Serializable;



public class QuotesBean implements Serializable{


    



    private String Close;
    private String Cny;
    private String Vary;
    private String Name;
    private String Base;
    private String Quote;

    public String getClose() {
        return Close;
    }

    public void setClose(String Close) {
        this.Close = Close;
    }

    public String getCny() {
        return Cny;
    }

    public void setCny(String Cny) {
        this.Cny = Cny;
    }

    public String getVary() {
        return Vary;
    }

    public void setVary(String vary) {
        Vary = vary;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getBase() {
        return Base;
    }

    public void setBase(String Base) {
        this.Base = Base;
    }

    public String getQuote() {
        return Quote;
    }

    public void setQuote(String Quote) {
        this.Quote = Quote;
    }
}
