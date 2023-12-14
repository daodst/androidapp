

package org.matrix.androidsdk.crypto.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public class MXDeviceInfo implements Serializable {
    private static final long serialVersionUID = 20129670646382964L;

    
    public static final int DEVICE_VERIFICATION_UNKNOWN = -1;

    
    public static final int DEVICE_VERIFICATION_UNVERIFIED = 0;

    
    public static final int DEVICE_VERIFICATION_VERIFIED = 1;

    
    public static final int DEVICE_VERIFICATION_BLOCKED = 2;

    
    public String deviceId;

    
    public String userId;

    
    public List<String> algorithms;

    
    public Map<String, String> keys;

    
    public Map<String, Map<String, String>> signatures;

    
    public Map<String, Object> unsigned;

    
    public int mVerified;

    
    public MXDeviceInfo() {
        mVerified = DEVICE_VERIFICATION_UNKNOWN;
    }
}
