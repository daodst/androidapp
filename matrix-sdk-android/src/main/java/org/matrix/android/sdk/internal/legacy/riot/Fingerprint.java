

package org.matrix.android.sdk.internal.legacy.riot;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class Fingerprint {
    public enum HashType {
        SHA1,
        SHA256
    }

    private final HashType mHashType;
    private final byte[] mBytes;

    public Fingerprint(HashType hashType, byte[] bytes) {
        mHashType = hashType;
        mBytes = bytes;
    }

    public HashType getType() {
        return mHashType;
    }

    public byte[] getBytes() {
        return mBytes;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("bytes", Base64.encodeToString(getBytes(), Base64.DEFAULT));
        obj.put("hash_type", mHashType.toString());
        return obj;
    }

    public static Fingerprint fromJson(JSONObject obj) throws JSONException {
        String hashTypeStr = obj.getString("hash_type");
        byte[] fingerprintBytes = Base64.decode(obj.getString("bytes"), Base64.DEFAULT);

        final HashType hashType;
        if ("SHA256".equalsIgnoreCase(hashTypeStr)) {
            hashType = HashType.SHA256;
        } else if ("SHA1".equalsIgnoreCase(hashTypeStr)) {
            hashType = HashType.SHA1;
        } else {
            throw new JSONException("Unrecognized hash type: " + hashTypeStr);
        }

        return new Fingerprint(hashType, fingerprintBytes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Fingerprint that = (Fingerprint) o;

        if (!Arrays.equals(mBytes, that.mBytes)) return false;
        return mHashType == that.mHashType;

    }

    @Override
    public int hashCode() {
        int result = mBytes != null ? Arrays.hashCode(mBytes) : 0;
        result = 31 * result + (mHashType != null ? mHashType.hashCode() : 0);
        return result;
    }
}
