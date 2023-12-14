

package common.app.base.fragment.mall.model;



public class AdvertEntity {


    private String name;
    private String title;
    private String subtitle;
    private String image;
    private String url;
    private String link_in;
    private String link_objid;
    private String backcolo;
    private String voide;
    private String id;

    public AdvertEntity(){

    }

    public AdvertEntity(String image) {
        this.image = image;
    }

    public String getVoide() {
        return voide;
    }

    public void setVoide(String voide) {
        this.voide = voide;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        if(image==null){
            image="";
        }
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        if(url==null){
            url="";
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLink_in() {
        if(link_in==null){
            link_in="";
        }
        return link_in;
    }

    public void setLink_in(String link_in) {
        this.link_in = link_in;
    }

    public String getLink_objid() {
        if(link_objid==null){
            link_objid="";
        }
        return link_objid;
    }

    public void setLink_objid(String link_objid) {
        this.link_objid = link_objid;
    }

    public String getBackcolo() {
        return backcolo;
    }

    public void setBackcolo(String backcolo) {
        this.backcolo = backcolo;
    }

    public String getSubtitle() {
        if(subtitle==null){
            subtitle="";
        }
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
