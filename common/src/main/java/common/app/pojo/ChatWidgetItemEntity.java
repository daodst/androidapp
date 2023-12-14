package common.app.pojo;


public class ChatWidgetItemEntity {
    public int colorIdTag;
    public String roomId;
    public int number;
    public String image;
    public String displayName;
    
    public boolean izServerNotice;

    public ChatWidgetItemEntity() {
    }

    public ChatWidgetItemEntity(int number, String image) {
        this.number = number;
        this.image = image;
    }

    public ChatWidgetItemEntity(int colorIdTag, String displayName, String roomId, int number, String image,boolean izServerNotice) {
        this.colorIdTag = colorIdTag;
        this.displayName = displayName;
        this.roomId = roomId;
        this.number = number;
        this.image = image;
        this.izServerNotice = izServerNotice;
    }
}
