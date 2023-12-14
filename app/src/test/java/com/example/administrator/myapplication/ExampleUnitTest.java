package com.example.administrator.myapplication;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        System.out.println("10="+getLevel(10));
        System.out.println("100="+getLevel(100));
        System.out.println("150="+getLevel(150));
        System.out.println("199="+getLevel(199));
        System.out.println("200="+getLevel(200));
        System.out.println("209="+getLevel(209));
        System.out.println("290.6="+getLevel(290.6));
        System.out.println("300="+getLevel(300));
        System.out.println("360="+getLevel(360));
        System.out.println("400="+getLevel(400));
        System.out.println("900="+getLevel(900));
        System.out.println("1000="+getLevel(1000));
        System.out.println("1600="+getLevel(1600));
        System.out.println("3200="+getLevel(3200));
        System.out.println("6400="+getLevel(6400));
        System.out.println("6907="+getLevel(6907));
        System.out.println("12800="+getLevel(12800));
        System.out.println("25600="+getLevel(25600));
        System.out.println("134654564="+getLevel(134654564));

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        


    }

    
    private int getLevel(double amount) {
        int level = 0;
        int pledgeNum = (int) amount;
        if (pledgeNum < 100) {
            level = 0;
        } else if(pledgeNum < 200) {
            level = 1;
        } else {

            int index = pledgeNum/100;
            level = (int) (Math.log(index) / Math.log(2));
        }
        if (level > 33) {
            level = 33;
        }
        return level;
    }

    @Test
    public void urltest2() throws Exception {
        String[] testUrls = {"www.baidu.com", "www.163.com", "192.168.0.135", "wusjlskj"};
        for (String url : testUrls) {
            isIp(url);
        }


    }


    public void isIp(String url) {
        String rexIp = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";
        Pattern pattern = Pattern.compile(rexIp);
        Matcher matcher = pattern.matcher(url);
        System.out.println(url+" is ip "+matcher.matches());
    }

    
    public void isDomainOrIp(String url) {
        String rexDomain = "[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?.*";
        String rexIp = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";

        Pattern pattern = Pattern.compile(rexDomain);
        Matcher matcher = pattern.matcher(url); 
        if(matcher.matches()){
            System.out.println(url+" is domain");
        } else {
            pattern = Pattern.compile(rexIp);
            matcher = pattern.matcher(url); 
            System.out.println(url+" is ip "+matcher.matches());
        }
    }
}
