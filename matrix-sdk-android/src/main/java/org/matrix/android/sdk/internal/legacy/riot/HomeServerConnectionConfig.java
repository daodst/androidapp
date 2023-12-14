

package org.matrix.android.sdk.internal.legacy.riot;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import okhttp3.CipherSuite;
import okhttp3.TlsVersion;
import timber.log.Timber;


public class HomeServerConnectionConfig {

    
    private Uri mHomeServerUri;
    
    @Nullable
    private Uri mJitsiServerUri;
    
    @Nullable
    private Uri mIdentityServerUri;
    
    private Uri mAntiVirusServerUri;
    
    private List<Fingerprint> mAllowedFingerprints = new ArrayList<>();
    
    private Credentials mCredentials;
    
    private boolean mPin;
    
    private List<TlsVersion> mTlsVersions;
    
    private List<CipherSuite> mTlsCipherSuites;
    
    private boolean mShouldAcceptTlsExtensions = true;
    
    private boolean mForceUsageTlsVersions;
    
    private String mProxyHostname;
    
    private int mProxyPort = -1;


    
    private HomeServerConnectionConfig() {
        
    }

    
    public void setHomeserverUri(Uri uri) {
        mHomeServerUri = uri;
    }

    
    public Uri getHomeserverUri() {
        return mHomeServerUri;
    }

    
    public Uri getJitsiServerUri() {
        return mJitsiServerUri;
    }

    
    @Nullable
    public Uri getIdentityServerUri() {
        return mIdentityServerUri;
    }

    
    public Uri getAntiVirusServerUri() {
        if (null != mAntiVirusServerUri) {
            return mAntiVirusServerUri;
        }
        
        return mHomeServerUri;
    }

    
    public List<Fingerprint> getAllowedFingerprints() {
        return mAllowedFingerprints;
    }

    
    public Credentials getCredentials() {
        return mCredentials;
    }

    
    public void setCredentials(Credentials credentials) {
        mCredentials = credentials;

        
        if (credentials.wellKnown != null) {
            if (credentials.wellKnown.homeServer != null) {
                String homeServerUrl = credentials.wellKnown.homeServer.baseURL;

                if (!TextUtils.isEmpty(homeServerUrl)) {
                    
                    if (homeServerUrl.endsWith("/")) {
                        homeServerUrl = homeServerUrl.substring(0, homeServerUrl.length() - 1);
                    }

                    Timber.d("Overriding homeserver url to " + homeServerUrl);
                    mHomeServerUri = Uri.parse(homeServerUrl);
                }
            }

            if (credentials.wellKnown.identityServer != null) {
                String identityServerUrl = credentials.wellKnown.identityServer.baseURL;

                if (!TextUtils.isEmpty(identityServerUrl)) {
                    
                    if (identityServerUrl.endsWith("/")) {
                        identityServerUrl = identityServerUrl.substring(0, identityServerUrl.length() - 1);
                    }

                    Timber.d("Overriding identity server url to " + identityServerUrl);
                    mIdentityServerUri = Uri.parse(identityServerUrl);
                }
            }

            if (credentials.wellKnown.jitsiServer != null) {
                String jitsiServerUrl = credentials.wellKnown.jitsiServer.preferredDomain;

                if (!TextUtils.isEmpty(jitsiServerUrl)) {
                    
                    if (!jitsiServerUrl.endsWith("/")) {
                        jitsiServerUrl = jitsiServerUrl + "/";
                    }

                    Timber.d("Overriding jitsi server url to " + jitsiServerUrl);
                    mJitsiServerUri = Uri.parse(jitsiServerUrl);
                }
            }
        }
    }

    
    public boolean shouldPin() {
        return mPin;
    }

    
    @Nullable
    public List<TlsVersion> getAcceptedTlsVersions() {
        return mTlsVersions;
    }

    
    @Nullable
    public List<CipherSuite> getAcceptedTlsCipherSuites() {
        return mTlsCipherSuites;
    }

    
    public boolean shouldAcceptTlsExtensions() {
        return mShouldAcceptTlsExtensions;
    }

    
    public boolean forceUsageOfTlsVersions() {
        return mForceUsageTlsVersions;
    }


    
    @Nullable
    public Proxy getProxyConfig() {
        if (mProxyHostname == null || mProxyHostname.length() == 0 || mProxyPort == -1) {
            return null;
        }

        return new Proxy(Proxy.Type.HTTP,
                new InetSocketAddress(mProxyHostname, mProxyPort));
    }


    @Override
    public String toString() {
        return "HomeserverConnectionConfig{" +
                "mHomeServerUri=" + mHomeServerUri +
                ", mJitsiServerUri=" + mJitsiServerUri +
                ", mIdentityServerUri=" + mIdentityServerUri +
                ", mAntiVirusServerUri=" + mAntiVirusServerUri +
                ", mAllowedFingerprints size=" + mAllowedFingerprints.size() +
                ", mCredentials=" + mCredentials +
                ", mPin=" + mPin +
                ", mShouldAcceptTlsExtensions=" + mShouldAcceptTlsExtensions +
                ", mProxyHostname=" + (null == mProxyHostname ? "" : mProxyHostname) +
                ", mProxyPort=" + (-1 == mProxyPort ? "" : mProxyPort) +
                ", mTlsVersions=" + (null == mTlsVersions ? "" : mTlsVersions.size()) +
                ", mTlsCipherSuites=" + (null == mTlsCipherSuites ? "" : mTlsCipherSuites.size()) +
                '}';
    }

    
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("home_server_url", mHomeServerUri.toString());
        Uri jitsiServerUri = getJitsiServerUri();
        if (jitsiServerUri != null) {
            json.put("jitsi_server_url", jitsiServerUri.toString());
        }
        Uri identityServerUri = getIdentityServerUri();
        if (identityServerUri != null) {
            json.put("identity_server_url", identityServerUri.toString());
        }

        if (mAntiVirusServerUri != null) {
            json.put("antivirus_server_url", mAntiVirusServerUri.toString());
        }

        json.put("pin", mPin);

        if (mCredentials != null) json.put("credentials", mCredentials.toJson());
        if (mAllowedFingerprints != null) {
            List<JSONObject> fingerprints = new ArrayList<>(mAllowedFingerprints.size());

            for (Fingerprint fingerprint : mAllowedFingerprints) {
                fingerprints.add(fingerprint.toJson());
            }

            json.put("fingerprints", new JSONArray(fingerprints));
        }

        json.put("tls_extensions", mShouldAcceptTlsExtensions);

        if (mTlsVersions != null) {
            List<String> tlsVersions = new ArrayList<>(mTlsVersions.size());

            for (TlsVersion tlsVersion : mTlsVersions) {
                tlsVersions.add(tlsVersion.javaName());
            }

            json.put("tls_versions", new JSONArray(tlsVersions));
        }

        json.put("force_usage_of_tls_versions", mForceUsageTlsVersions);

        if (mTlsCipherSuites != null) {
            List<String> tlsCipherSuites = new ArrayList<>(mTlsCipherSuites.size());

            for (CipherSuite tlsCipherSuite : mTlsCipherSuites) {
                tlsCipherSuites.add(tlsCipherSuite.javaName());
            }

            json.put("tls_cipher_suites", new JSONArray(tlsCipherSuites));
        }

        if (mProxyPort != -1) {
            json.put("proxy_port", mProxyPort);
        }

        if (mProxyHostname != null && mProxyHostname.length() > 0) {
            json.put("proxy_hostname", mProxyHostname);
        }

        return json;
    }

    
    public static HomeServerConnectionConfig fromJson(JSONObject jsonObject) throws JSONException {
        JSONObject credentialsObj = jsonObject.optJSONObject("credentials");
        Credentials creds = credentialsObj != null ? Credentials.fromJson(credentialsObj) : null;

        Builder builder = new Builder()
                .withHomeServerUri(Uri.parse(jsonObject.getString("home_server_url")))
                .withJitsiServerUri(jsonObject.has("jitsi_server_url") ? Uri.parse(jsonObject.getString("jitsi_server_url")) : null)
                .withIdentityServerUri(jsonObject.has("identity_server_url") ? Uri.parse(jsonObject.getString("identity_server_url")) : null)
                .withCredentials(creds)
                .withPin(jsonObject.optBoolean("pin", false));

        JSONArray fingerprintArray = jsonObject.optJSONArray("fingerprints");
        if (fingerprintArray != null) {
            for (int i = 0; i < fingerprintArray.length(); i++) {
                builder.addAllowedFingerPrint(Fingerprint.fromJson(fingerprintArray.getJSONObject(i)));
            }
        }

        
        if (jsonObject.has("antivirus_server_url")) {
            builder.withAntiVirusServerUri(Uri.parse(jsonObject.getString("antivirus_server_url")));
        }

        builder.withShouldAcceptTlsExtensions(jsonObject.optBoolean("tls_extensions", true));

        
        if (jsonObject.has("tls_versions")) {
            JSONArray tlsVersionsArray = jsonObject.optJSONArray("tls_versions");
            if (tlsVersionsArray != null) {
                for (int i = 0; i < tlsVersionsArray.length(); i++) {
                    builder.addAcceptedTlsVersion(TlsVersion.forJavaName(tlsVersionsArray.getString(i)));
                }
            }
        }

        builder.forceUsageOfTlsVersions(jsonObject.optBoolean("force_usage_of_tls_versions", false));

        
        if (jsonObject.has("tls_cipher_suites")) {
            JSONArray tlsCipherSuitesArray = jsonObject.optJSONArray("tls_cipher_suites");
            if (tlsCipherSuitesArray != null) {
                for (int i = 0; i < tlsCipherSuitesArray.length(); i++) {
                    builder.addAcceptedTlsCipherSuite(CipherSuite.forJavaName(tlsCipherSuitesArray.getString(i)));
                }
            }
        }

        
        if (jsonObject.has("proxy_hostname") && jsonObject.has("proxy_port")) {
            builder.withProxy(jsonObject.getString("proxy_hostname"), jsonObject.getInt("proxy_port"));
        }

        return builder.build();
    }

    
    public static class Builder {
        private HomeServerConnectionConfig mHomeServerConnectionConfig;

        
        public Builder() {
            mHomeServerConnectionConfig = new HomeServerConnectionConfig();
        }

        
        public Builder(HomeServerConnectionConfig from) {
            try {
                mHomeServerConnectionConfig = HomeServerConnectionConfig.fromJson(from.toJson());
            } catch (JSONException e) {
                
                throw new RuntimeException("Unable to create a HomeServerConnectionConfig", e);
            }
        }

        
        public Builder withHomeServerUri(final Uri homeServerUri) {
            if (homeServerUri == null || (!"http".equals(homeServerUri.getScheme()) && !"https".equals(homeServerUri.getScheme()))) {
                throw new RuntimeException("Invalid homeserver URI: " + homeServerUri);
            }

            
            if (homeServerUri.toString().endsWith("/")) {
                try {
                    String url = homeServerUri.toString();
                    mHomeServerConnectionConfig.mHomeServerUri = Uri.parse(url.substring(0, url.length() - 1));
                } catch (Exception e) {
                    throw new RuntimeException("Invalid homeserver URI: " + homeServerUri);
                }
            } else {
                mHomeServerConnectionConfig.mHomeServerUri = homeServerUri;
            }

            return this;
        }

        
        public Builder withJitsiServerUri(@Nullable final Uri jitsiServerUri) {
            if (jitsiServerUri != null
                    && !jitsiServerUri.toString().isEmpty()
                    && !"http".equals(jitsiServerUri.getScheme())
                    && !"https".equals(jitsiServerUri.getScheme())) {
                throw new RuntimeException("Invalid jitsi server URI: " + jitsiServerUri);
            }

            
            if ((null != jitsiServerUri) && !jitsiServerUri.toString().endsWith("/")) {
                try {
                    String url = jitsiServerUri.toString();
                    mHomeServerConnectionConfig.mJitsiServerUri = Uri.parse(url + "/");
                } catch (Exception e) {
                    throw new RuntimeException("Invalid jitsi server URI: " + jitsiServerUri);
                }
            } else {
                if (jitsiServerUri != null && jitsiServerUri.toString().isEmpty()) {
                    mHomeServerConnectionConfig.mJitsiServerUri = null;
                } else {
                    mHomeServerConnectionConfig.mJitsiServerUri = jitsiServerUri;
                }
            }

            return this;
        }

        
        public Builder withIdentityServerUri(@Nullable final Uri identityServerUri) {
            if (identityServerUri != null
                    && !identityServerUri.toString().isEmpty()
                    && !"http".equals(identityServerUri.getScheme())
                    && !"https".equals(identityServerUri.getScheme())) {
                throw new RuntimeException("Invalid identity server URI: " + identityServerUri);
            }

            
            if ((null != identityServerUri) && identityServerUri.toString().endsWith("/")) {
                try {
                    String url = identityServerUri.toString();
                    mHomeServerConnectionConfig.mIdentityServerUri = Uri.parse(url.substring(0, url.length() - 1));
                } catch (Exception e) {
                    throw new RuntimeException("Invalid identity server URI: " + identityServerUri);
                }
            } else {
                if (identityServerUri != null && identityServerUri.toString().isEmpty()) {
                    mHomeServerConnectionConfig.mIdentityServerUri = null;
                } else {
                    mHomeServerConnectionConfig.mIdentityServerUri = identityServerUri;
                }
            }

            return this;
        }

        
        public Builder withCredentials(@Nullable Credentials credentials) {
            mHomeServerConnectionConfig.mCredentials = credentials;
            return this;
        }

        
        public Builder addAllowedFingerPrint(@Nullable Fingerprint allowedFingerprint) {
            if (allowedFingerprint != null) {
                mHomeServerConnectionConfig.mAllowedFingerprints.add(allowedFingerprint);
            }

            return this;
        }

        
        public Builder withPin(boolean pin) {
            mHomeServerConnectionConfig.mPin = pin;

            return this;
        }

        
        public Builder withShouldAcceptTlsExtensions(boolean shouldAcceptTlsExtension) {
            mHomeServerConnectionConfig.mShouldAcceptTlsExtensions = shouldAcceptTlsExtension;

            return this;
        }

        
        public Builder addAcceptedTlsVersion(@NonNull TlsVersion tlsVersion) {
            if (mHomeServerConnectionConfig.mTlsVersions == null) {
                mHomeServerConnectionConfig.mTlsVersions = new ArrayList<>();
            }

            mHomeServerConnectionConfig.mTlsVersions.add(tlsVersion);

            return this;
        }

        
        public Builder forceUsageOfTlsVersions(boolean forceUsageOfTlsVersions) {
            mHomeServerConnectionConfig.mForceUsageTlsVersions = forceUsageOfTlsVersions;

            return this;
        }

        
        public Builder addAcceptedTlsCipherSuite(@NonNull CipherSuite tlsCipherSuite) {
            if (mHomeServerConnectionConfig.mTlsCipherSuites == null) {
                mHomeServerConnectionConfig.mTlsCipherSuites = new ArrayList<>();
            }

            mHomeServerConnectionConfig.mTlsCipherSuites.add(tlsCipherSuite);

            return this;
        }

        
        public Builder withAntiVirusServerUri(@Nullable Uri antivirusServerUri) {
            if ((null != antivirusServerUri) && (!"http".equals(antivirusServerUri.getScheme()) && !"https".equals(antivirusServerUri.getScheme()))) {
                throw new RuntimeException("Invalid antivirus server URI: " + antivirusServerUri);
            }

            mHomeServerConnectionConfig.mAntiVirusServerUri = antivirusServerUri;

            return this;
        }

        
        public Builder withTlsLimitations(boolean tlsLimitations, boolean enableCompatibilityMode) {
            if (tlsLimitations) {
                withShouldAcceptTlsExtensions(false);

                
                addAcceptedTlsVersion(TlsVersion.TLS_1_2);
                addAcceptedTlsVersion(TlsVersion.TLS_1_3);

                forceUsageOfTlsVersions(enableCompatibilityMode);

                
                addAcceptedTlsCipherSuite(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256);
                addAcceptedTlsCipherSuite(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256);
                addAcceptedTlsCipherSuite(CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256);
                addAcceptedTlsCipherSuite(CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256);
                addAcceptedTlsCipherSuite(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384);
                addAcceptedTlsCipherSuite(CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384);
                addAcceptedTlsCipherSuite(CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256);
                addAcceptedTlsCipherSuite(CipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256);

                if (enableCompatibilityMode) {
                    
                    
                    addAcceptedTlsCipherSuite(CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA);
                    addAcceptedTlsCipherSuite(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA);
                }
            }

            return this;
        }

        
        public Builder withProxy(@Nullable String proxyHostname, int proxyPort) {
            mHomeServerConnectionConfig.mProxyHostname = proxyHostname;
            mHomeServerConnectionConfig.mProxyPort = proxyPort;
            return this;
        }

        
        public HomeServerConnectionConfig build() {
            
            if (mHomeServerConnectionConfig.mHomeServerUri == null) {
                throw new RuntimeException("Homeserver URI not set");
            }

            return mHomeServerConnectionConfig;
        }

    }
}
