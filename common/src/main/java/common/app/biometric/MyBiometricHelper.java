package common.app.biometric;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.provider.Settings;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.security.UnrecoverableKeyException;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;

import common.app.R;
import common.app.mall.util.ToastUtil;
import common.app.utils.SpUtil;


public class MyBiometricHelper {
    
    private boolean isOpenBiometric = false;
    private String mPassword;
    private BiometricCallback mBiometricCallback;
    private BiometricPrompt mPrompt;
    private KStoreUtils mKStoreUtils;
    private final String keyName;

    private BiometricPrompt.AuthenticationCallback mAuthenticationCallback;

    private MyBiometricHelper(boolean isOpenBiometric, String key) {
        this.isOpenBiometric = isOpenBiometric;
        this.mKStoreUtils = KStoreUtils.getInstance(key);
        this.keyName = key;
    }

    public static MyBiometricHelper getInstance(boolean isOpenBiometric, String key) {
        return new MyBiometricHelper(isOpenBiometric, key);
    }

    
    public void setPassword(String password) {
        this.mPassword = password;
    }

    
    public void setBiometricCallback(BiometricCallback biometricCallback) {
        mBiometricCallback = biometricCallback;
    }

    public void start(FragmentActivity activity) {
        
        
        if (!checkFingerprintSupport(activity)) return;

        initAuthenticationCallback(activity);
        createPrompt(activity);
        sdk28(activity);
    }

    public void start(Fragment fragment) {
        
        
        if (!checkFingerprintSupport(fragment.getContext())) return;

        initAuthenticationCallback(fragment.getContext());
        createPrompt(fragment);
        sdk28(fragment.getContext());
    }

    
    public void initAuthenticationCallback(Context context) {
        mAuthenticationCallback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                
                
                

                
                
                if (errorCode == BiometricPrompt.ERROR_NO_BIOMETRICS) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                        context.startActivity(enrollIntent);
                    }
                }
                
                if (null != mBiometricCallback) mBiometricCallback.listener(errorCode);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                

                if (isOpenBiometric) {
                    
                    mKStoreUtils.encrypt(result.getCryptoObject().getCipher(), mPassword);
                    
                    if (null != mBiometricCallback) mBiometricCallback.callback("");
                } else {
                    String password = mKStoreUtils.decrypt(result.getCryptoObject().getCipher());
                    
                    if (null != mBiometricCallback) mBiometricCallback.callback(password);
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                
            }
        };
    }

    
    public static boolean checkHardSupport(Context context) {
        try {
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            
            return fingerprintManager.isHardwareDetected();
        } catch (Exception e) {
            return false;
        }
    }

    
    public static boolean checkHasFinger(Context context) {
        try {
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            
            return fingerprintManager.hasEnrolledFingerprints();
        } catch (Exception e) {
            return false;
        }
    }

    
    public static boolean checkFingerprintSupport(Context context) {
        try {
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            if (!fingerprintManager.isHardwareDetected()) {
                
                return false;
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                
                
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    
    @Deprecated
    private boolean biometricCanUse(FragmentActivity activity) {
        BiometricManager biometricManager = BiometricManager.from(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int authenticate = biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
            switch (authenticate) {
                case BiometricManager.BIOMETRIC_SUCCESS:
                    Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                    return true;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    Log.e("MY_APP_TAG", "No biometric features available on this device.");
                    return false;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                    return false;
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    Log.d("MY_APP_TAG", "BIOMETRIC_ERROR_NONE_ENROLLED");
                    
                    
                    final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                    enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                    activity.startActivity(enrollIntent);
                    break;
                case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                    
                    Log.d("MY_APP_TAG", "BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED");
                    break;
                case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                    
                    Log.d("MY_APP_TAG", "BIOMETRIC_ERROR_UNSUPPORTED");
                    break;
                case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                    
                    Log.d("MY_APP_TAG", "BIOMETRIC_STATUS_UNKNOWN");
                    break;
            }
        } else {
            
            
            KeyguardManager keyguardManager = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
            boolean result = keyguardManager.isDeviceSecure();
            if (!result) {
                String pcgName = "com.android.settings";
                
                
                
                
                
                String clsName = "com.android.settings.fingerprint.FingerprintSettingsActivity";
                Intent intent = new Intent();
                ComponentName componentName = new ComponentName(pcgName, clsName);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setComponent(componentName);
                activity.startActivity(intent);

                return false;
            }
            return true;
        }

        return false;
    }

    
    private void createPrompt(FragmentActivity activity) {
        Executor executor = ContextCompat.getMainExecutor(activity);
        mPrompt = new BiometricPrompt(activity, executor, mAuthenticationCallback);
    }

    
    private void createPrompt(Fragment fragment) {
        Executor executor = ContextCompat.getMainExecutor(fragment.getContext());
        mPrompt = new BiometricPrompt(fragment, executor, mAuthenticationCallback);
    }

    private void sdk28(Context context) {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(context.getString(R.string.biometric_string_1))
                .setDescription(context.getString(R.string.biometric_string_2))
                .setNegativeButtonText(context.getString(R.string.biometric_string_5))
                .setConfirmationRequired(true)
                .build();

        
        try {
            Cipher cipher;
            if (isOpenBiometric) {
                cipher = mKStoreUtils.startEncryptCipher();
            } else {
                cipher = mKStoreUtils.startDecryptCipher();
            }
            mPrompt.authenticate(promptInfo, new BiometricPrompt.CryptoObject(cipher));
        } catch (Throwable e) {
            
            e.printStackTrace();
            if (e instanceof KeyPermanentlyInvalidatedException || e instanceof UnrecoverableKeyException || e.getCause() instanceof UnrecoverableKeyException){
                
                ToastUtil.showToast(context.getString(R.string.close_finger_pay_tips));
                SpUtil.setAppBiometricOpen(keyName, false);
            }
            if (null != mBiometricCallback) mBiometricCallback.error(e);
        }
    }

    
    public static interface BiometricCallback {
        
        default void error(Throwable e) {
        }

        
        void listener(int statusCode);

        void callback(String password);
    }
}
