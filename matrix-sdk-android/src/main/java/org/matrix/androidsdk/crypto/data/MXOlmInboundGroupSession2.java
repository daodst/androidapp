

package org.matrix.androidsdk.crypto.data;

import org.matrix.olm.OlmInboundGroupSession;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MXOlmInboundGroupSession2 implements Serializable {
    
    private static final long serialVersionUID = 201702011617L;

    
    public OlmInboundGroupSession mSession;

    
    public String mRoomId;

    
    public String mSenderKey;

    
    public Map<String, String> mKeysClaimed;

    
    public List<String> mForwardingCurve25519KeyChain = new ArrayList<>();
}