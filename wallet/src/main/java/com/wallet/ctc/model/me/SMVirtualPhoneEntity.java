

package com.wallet.ctc.model.me;


public class SMVirtualPhoneEntity {
    public String phoneNumber;
    public boolean checked = false;

    public SMVirtualPhoneEntity() {
    }

    public SMVirtualPhoneEntity(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
