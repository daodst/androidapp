

package im.vector.app.features.call.phone.popup;


public class SMPhoneNumberEntity {
    public String phoneNumber;
    public boolean checked = false;

    public SMPhoneNumberEntity() {

    }

    public SMPhoneNumberEntity(String phoneNumber, boolean checked) {
        this.phoneNumber = phoneNumber;
        this.checked = checked;
    }
}
