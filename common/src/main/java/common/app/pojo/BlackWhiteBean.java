package common.app.pojo;

import java.util.Arrays;
import java.util.List;


public class BlackWhiteBean {
    public List<String> blacks;
    public List<String> whites;

    public boolean isBlacksEmpty() {
        return blacks == null || blacks.size() == 0;
    }

    public boolean isWhitesEmpty() {
        return whites == null || whites.size() == 0;
    }

    public boolean isEmpty() {
        return isBlacksEmpty() && isWhitesEmpty();
    }

    @Override
    public String toString() {
        return "BlackWhiteBean{" +
                "blacks=" + listToString(blacks) +
                ", whites=" + listToString(whites) +
                '}';
    }

    public String listToString(List<String> list) {
        if (null == list) {
            return null;
        }
        return Arrays.toString(list.toArray());
    }
}
