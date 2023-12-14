package common.app.pojo;

import java.util.List;


public class FriendsRemarkBean {

    public List<FRemark> remarks;
    public List<String> friends;

    public static class FRemark {
        public String id;
        public String name;
        public FRemark(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "FRemark{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    
    public boolean isFriendsEmpty() {
        return friends == null || friends.size() == 0;
    }

    
    public boolean isRemarksEmpty() {
        return remarks == null || remarks.size() ==0;
    }

    public boolean isEmpty() {
        return isFriendsEmpty() && isRemarksEmpty();
    }
}
