package org.matrix.android.sdk.internal.session.sync.job.ws.pojo;

public class StreamingToken {
    int PDUPosition;
    int TypingPosition;
    int ReceiptPosition;
    int SendToDevicePosition;
    int InvitePosition;
    int AccountDataPosition;
    int DeviceListPosition;
    int NotificationDataPosition;
    int PresencePosition;

    public StreamingToken(int PDUPosition, int typingPosition, int receiptPosition, int sendToDevicePosition, int invitePosition, int accountDataPosition, int deviceListPosition, int notificationDataPosition, int presencePosition) {
        this.PDUPosition = PDUPosition;
        TypingPosition = typingPosition;
        ReceiptPosition = receiptPosition;
        SendToDevicePosition = sendToDevicePosition;
        InvitePosition = invitePosition;
        AccountDataPosition = accountDataPosition;
        DeviceListPosition = deviceListPosition;
        NotificationDataPosition = notificationDataPosition;
        PresencePosition = presencePosition;
    }

    public StreamingToken() {
    }

    public int getPDUPosition() {
        return PDUPosition;
    }

    public void setPDUPosition(int PDUPosition) {
        this.PDUPosition = PDUPosition;
    }

    public int getTypingPosition() {
        return TypingPosition;
    }

    public void setTypingPosition(int typingPosition) {
        TypingPosition = typingPosition;
    }

    public int getReceiptPosition() {
        return ReceiptPosition;
    }

    public void setReceiptPosition(int receiptPosition) {
        ReceiptPosition = receiptPosition;
    }

    public int getSendToDevicePosition() {
        return SendToDevicePosition;
    }

    public void setSendToDevicePosition(int sendToDevicePosition) {
        SendToDevicePosition = sendToDevicePosition;
    }

    public int getInvitePosition() {
        return InvitePosition;
    }

    public void setInvitePosition(int invitePosition) {
        InvitePosition = invitePosition;
    }

    public int getAccountDataPosition() {
        return AccountDataPosition;
    }

    public void setAccountDataPosition(int accountDataPosition) {
        AccountDataPosition = accountDataPosition;
    }

    public int getDeviceListPosition() {
        return DeviceListPosition;
    }

    public void setDeviceListPosition(int deviceListPosition) {
        DeviceListPosition = deviceListPosition;
    }

    public int getNotificationDataPosition() {
        return NotificationDataPosition;
    }

    public void setNotificationDataPosition(int notificationDataPosition) {
        NotificationDataPosition = notificationDataPosition;
    }

    public int getPresencePosition() {
        return PresencePosition;
    }

    public void setPresencePosition(int presencePosition) {
        PresencePosition = presencePosition;
    }

    @Override
    public String toString() {
        return "s"+this.PDUPosition+"_"+this.TypingPosition+"_"+this.ReceiptPosition+"_"+this.SendToDevicePosition+"_"+
                this.InvitePosition+"_"+this.AccountDataPosition+"_"+this.DeviceListPosition+"_"+this.NotificationDataPosition+"_"+
                this.PresencePosition;
    }
}
