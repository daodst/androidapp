

package common.app.pojo;



public class CurrencyBean {


    

    private String currency;
    private String currency_symbol;

    public CurrencyBean(){

    }

    public CurrencyBean(String currency,String currency_symbol){
        this.currency=currency;
        this.currency_symbol=currency_symbol;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency_symbol() {
        return currency_symbol;
    }

    public void setCurrency_symbol(String currency_symbol) {
        this.currency_symbol = currency_symbol;
    }
}
