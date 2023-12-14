

package com.app.dial.entity;


public class SMDialHistoryEntity {

    public String phoneNumber;
    public boolean isCallOut;
    public boolean turnedOn;
    public String remark;
    public String timestamp;

    public SMDialHistoryEntity() {
    }

    public SMDialHistoryEntity(String phoneNumber, String remark, boolean isCallOut, boolean turnedOn, String timestamp) {
        this.phoneNumber = phoneNumber;
        this.isCallOut = isCallOut;
        this.turnedOn = turnedOn;
        this.remark = remark;
        this.timestamp = timestamp;
    }
}
