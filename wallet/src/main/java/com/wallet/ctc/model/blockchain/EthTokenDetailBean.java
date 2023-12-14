

package com.wallet.ctc.model.blockchain;



public class EthTokenDetailBean {


    

    private OverviewBean overview;
    private LinkBean links;
    private InitialPriceBean initial_price;
    private String name;
    private String symbol;
    private String decimals;
    private String address;
    private String logo;
    private String website;
    private String email;
    private String whitepaper;
    private String state;
    private String published_on;



    public OverviewBean getOverview() {
        if(null==overview){
            return new OverviewBean();
        }
        return overview;
    }

    public void setOverview(OverviewBean Overview) {
        this.overview = Overview;
    }

    public LinkBean getLink() {
        if(null==links){
            return new LinkBean();
        }
        return links;
    }

    public void setLink(LinkBean Link) {
        this.links = Link;
    }

    public InitialPriceBean getInitial_price() {
        if(null==initial_price){
            return new InitialPriceBean();
        }
        return initial_price;
    }

    public void setInitial_price(InitialPriceBean initial_price) {
        this.initial_price = initial_price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDecimals() {
        return decimals;
    }

    public void setDecimals(String decimals) {
        this.decimals = decimals;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWhitepaper() {
        return whitepaper;
    }

    public void setWhitepaper(String whitepaper) {
        this.whitepaper = whitepaper;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPublished_on() {
        return published_on;
    }

    public void setPublished_on(String published_on) {
        this.published_on = published_on;
    }

    public static class OverviewBean {
        

        private String en;
        private String zh;

        public String getEn() {
            return en;
        }

        public void setEn(String En) {
            this.en = En;
        }

        public String getZh() {
            return zh;
        }

        public void setZh(String Zh) {
            this.zh = Zh;
        }
    }

    public static class LinkBean {
        

        private String blog;
        private String twitter;
        private String telegram;
        private String github;
        private String facebook;
        private String reddit;
        private String slack;
        private String medium;

        public String getBlog() {
            return blog;
        }

        public void setBlog(String Blog) {
            this.blog = Blog;
        }

        public String getTwitter() {
            return twitter;
        }

        public void setTwitter(String Twitter) {
            this.twitter = Twitter;
        }

        public String getTelegram() {
            return telegram;
        }

        public void setTelegram(String Telegram) {
            this.telegram = Telegram;
        }

        public String getGithub() {
            return github;
        }

        public void setGithub(String Github) {
            this.github = Github;
        }

        public String getFacebook() {
            return facebook;
        }

        public void setFacebook(String Facebook) {
            this.facebook = Facebook;
        }

        public String getReddit() {
            return reddit;
        }

        public void setReddit(String Reddit) {
            this.reddit = Reddit;
        }

        public String getSlack() {
            return slack;
        }

        public void setSlack(String Slack) {
            this.slack = Slack;
        }

        public String getMedium() {
            return medium;
        }

        public void setMedium(String Medium) {
            this.medium = Medium;
        }
    }

    public static class InitialPriceBean {
        

        private String ETH;
        private String USD;
        private String BTC;

        public String getETH() {
            return ETH;
        }

        public void setETH(String ETH) {
            this.ETH = ETH;
        }

        public String getUSD() {
            return USD;
        }

        public void setUSD(String USD) {
            this.USD = USD;
        }

        public String getBTC() {
            return BTC;
        }

        public void setBTC(String BTC) {
            this.BTC = BTC;
        }
    }
}
