package common.app.pojo;


public class NameIdBean {
    public String id;
    public String name;
    public String logo;
    public int logoRes;

    public NameIdBean() {

    }

    public NameIdBean(String id, String name, String logo) {
        this.id = id;
        this.name = name;
        this.logo = logo;
    }

    public NameIdBean(String id, String name, int logoRes) {
        this.id = id;
        this.name = name;
        this.logoRes = logoRes;
    }
}
