package com.example.administrator.myapplication;

import android.net.Uri;

import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        



        String[] testUrls = {"www.baidu.com", "192.168.0.135", "http://192.168.0.123", "http://ajkjalkjak", "https://www.baidu.coms:234", "http://192.168.0.135:123", "192.168.0.132:2343", "wusjlskj"};
        for (String url : testUrls) {
            try {
                Uri testuri = Uri.parse(url);
                String testhost = testuri.getHost();
                int port = testuri.getPort();
                String scheme = testuri.getScheme();
                System.out.println(scheme+"---host:"+testhost+"----prot:"+port);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(url+" "+e.getMessage());
            }

        }

        
        
        
        
        
        
        
        
        

    }
}
