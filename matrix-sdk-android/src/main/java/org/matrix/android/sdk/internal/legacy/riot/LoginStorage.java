

package org.matrix.android.sdk.internal.legacy.riot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class LoginStorage {
    private static final String PREFS_LOGIN = "Vector.LoginStorage";

    
    private static final String PREFS_KEY_CONNECTION_CONFIGS = "PREFS_KEY_CONNECTION_CONFIGS";

    private final Context mContext;

    public LoginStorage(Context appContext) {
        mContext = appContext.getApplicationContext();

    }

    
    public List<HomeServerConnectionConfig> getCredentialsList() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_LOGIN, Context.MODE_PRIVATE);

        String connectionConfigsString = prefs.getString(PREFS_KEY_CONNECTION_CONFIGS, null);

        Timber.d("Got connection json: ");

        if (connectionConfigsString == null) {
            return new ArrayList<>();
        }

        try {
            JSONArray connectionConfigsStrings = new JSONArray(connectionConfigsString);

            List<HomeServerConnectionConfig> configList = new ArrayList<>(
                    connectionConfigsStrings.length()
            );

            for (int i = 0; i < connectionConfigsStrings.length(); i++) {
                configList.add(
                        HomeServerConnectionConfig.fromJson(connectionConfigsStrings.getJSONObject(i))
                );
            }

            return configList;
        } catch (JSONException e) {
            Timber.e(e, "Failed to deserialize accounts");
            throw new RuntimeException("Failed to deserialize accounts");
        }
    }

    
    public void addCredentials(HomeServerConnectionConfig config) {
        if (null != config && config.getCredentials() != null) {
            SharedPreferences prefs = mContext.getSharedPreferences(PREFS_LOGIN, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            List<HomeServerConnectionConfig> configs = getCredentialsList();

            configs.add(config);

            List<JSONObject> serialized = new ArrayList<>(configs.size());

            try {
                for (HomeServerConnectionConfig c : configs) {
                    serialized.add(c.toJson());
                }
            } catch (JSONException e) {
                throw new RuntimeException("Failed to serialize connection config");
            }

            String ser = new JSONArray(serialized).toString();

            Timber.d("Storing " + serialized.size() + " credentials");

            editor.putString(PREFS_KEY_CONNECTION_CONFIGS, ser);
            editor.apply();
        }
    }

    
    public void removeCredentials(HomeServerConnectionConfig config) {
        if (null != config && config.getCredentials() != null) {
            Timber.d("Removing account: " + config.getCredentials().userId);

            SharedPreferences prefs = mContext.getSharedPreferences(PREFS_LOGIN, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            List<HomeServerConnectionConfig> configs = getCredentialsList();
            List<JSONObject> serialized = new ArrayList<>(configs.size());

            boolean found = false;
            try {
                for (HomeServerConnectionConfig c : configs) {
                    if (c.getCredentials().userId.equals(config.getCredentials().userId)) {
                        found = true;
                    } else {
                        serialized.add(c.toJson());
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException("Failed to serialize connection config");
            }

            if (!found) return;

            String ser = new JSONArray(serialized).toString();

            Timber.d("Storing " + serialized.size() + " credentials");

            editor.putString(PREFS_KEY_CONNECTION_CONFIGS, ser);
            editor.apply();
        }
    }

    
    public void replaceCredentials(HomeServerConnectionConfig config) {
        if (null != config && config.getCredentials() != null) {
            SharedPreferences prefs = mContext.getSharedPreferences(PREFS_LOGIN, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            List<HomeServerConnectionConfig> configs = getCredentialsList();
            List<JSONObject> serialized = new ArrayList<>(configs.size());

            boolean found = false;
            try {
                for (HomeServerConnectionConfig c : configs) {
                    if (c.getCredentials().userId.equals(config.getCredentials().userId)) {
                        serialized.add(config.toJson());
                        found = true;
                    } else {
                        serialized.add(c.toJson());
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException("Failed to serialize connection config");
            }

            if (!found) return;

            String ser = new JSONArray(serialized).toString();

            Timber.d("Storing " + serialized.size() + " credentials");

            editor.putString(PREFS_KEY_CONNECTION_CONFIGS, ser);
            editor.apply();
        }
    }

    
    @SuppressLint("ApplySharedPref")
    public void clear() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PREFS_KEY_CONNECTION_CONFIGS);
        
        editor.commit();
    }
}
