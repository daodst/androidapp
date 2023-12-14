
package org.matrix.android.sdk.internal.legacy.riot;

import android.text.TextUtils;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;


public class Credentials {
    public String userId;

    
    @Deprecated
    public String homeServer;

    public String accessToken;

    public String refreshToken;

    public String deviceId;

    
    public WellKnown wellKnown;

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("user_id", userId);
        json.put("home_server", homeServer);
        json.put("access_token", accessToken);
        json.put("refresh_token", TextUtils.isEmpty(refreshToken) ? JSONObject.NULL : refreshToken);
        json.put("device_id", deviceId);

        return json;
    }

    public static Credentials fromJson(JSONObject obj) throws JSONException {
        Credentials creds = new Credentials();
        creds.userId = obj.getString("user_id");
        creds.homeServer = obj.getString("home_server");
        creds.accessToken = obj.getString("access_token");

        if (obj.has("device_id")) {
            creds.deviceId = obj.getString("device_id");
        }

        
        if (obj.has("refresh_token")) {
            try {
                creds.refreshToken = obj.getString("refresh_token");
            } catch (Exception e) {
                creds.refreshToken = null;
            }
        } else {
            throw new RuntimeException("refresh_token is required.");
        }

        return creds;
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "userId='" + userId + '\'' +
                ", homeServer='" + homeServer + '\'' +
                ", refreshToken.length='" + (refreshToken != null ? refreshToken.length() : "null") + '\'' +
                ", accessToken.length='" + (accessToken != null ? accessToken.length() : "null") + '\'' +
                '}';
    }

    @Nullable
    public String getUserId() {
        return userId;
    }

    @Nullable
    public String getHomeServer() {
        return homeServer;
    }

    @Nullable
    public String getAccessToken() {
        return accessToken;
    }

    @Nullable
    public String getDeviceId() {
        return deviceId;
    }
}
