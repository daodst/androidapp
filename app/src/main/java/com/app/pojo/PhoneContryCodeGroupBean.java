

package com.app.pojo;

import java.util.List;



public class PhoneContryCodeGroupBean {

    public String key;
    public int index;
    public List<PhoneContryCode> dict;

    public static class PhoneContryCode {
        public String area;
        public String country;
        public String country_code;
        public String id;
        public String mobile_prefix;
    }
}
