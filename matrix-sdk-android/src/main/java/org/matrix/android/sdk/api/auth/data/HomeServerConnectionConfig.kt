

package org.matrix.android.sdk.api.auth.data

import android.net.Uri
import com.squareup.moshi.JsonClass
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.TlsVersion
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig.Builder
import org.matrix.android.sdk.api.network.ssl.Fingerprint
import org.matrix.android.sdk.internal.util.ensureTrailingSlash


@JsonClass(generateAdapter = true)
data class HomeServerConnectionConfig(
        
        val homeServerUri: Uri,
        
        
        
        val homeServerUriBase: Uri = homeServerUri,
        val identityServerUri: Uri? = null,
        val antiVirusServerUri: Uri? = null,
        val allowedFingerprints: List<Fingerprint> = emptyList(),
        val shouldPin: Boolean = false,
        val tlsVersions: List<TlsVersion>? = null,
        val tlsCipherSuites: List<CipherSuite>? = null,
        val shouldAcceptTlsExtensions: Boolean = true,
        val allowHttpExtension: Boolean = false,
        val forceUsageTlsVersions: Boolean = false
) {

    
    class Builder {
        private lateinit var homeServerUri: Uri
        private var identityServerUri: Uri? = null
        private var antiVirusServerUri: Uri? = null
        private val allowedFingerprints: MutableList<Fingerprint> = ArrayList()
        private var shouldPin: Boolean = false
        private val tlsVersions: MutableList<TlsVersion> = ArrayList()
        private val tlsCipherSuites: MutableList<CipherSuite> = ArrayList()
        private var shouldAcceptTlsExtensions: Boolean = true
        private var allowHttpExtension: Boolean = false
        private var forceUsageTlsVersions: Boolean = false

        fun withHomeServerUri(hsUriString: String): Builder {
            return withHomeServerUri(Uri.parse(hsUriString))
        }

        
        fun withHomeServerUri(hsUri: Uri): Builder {
            if (hsUri.scheme != "http" && hsUri.scheme != "https") {
                throw RuntimeException("Invalid homeserver URI: $hsUri")
            }
            
            val hsString = hsUri.toString().ensureTrailingSlash()
            homeServerUri = try {
                Uri.parse(hsString)
            } catch (e: Exception) {
                throw RuntimeException("Invalid homeserver URI: $hsUri")
            }
            return this
        }

        fun withIdentityServerUri(identityServerUriString: String): Builder {
            return withIdentityServerUri(Uri.parse(identityServerUriString))
        }

        
        fun withIdentityServerUri(identityServerUri: Uri): Builder {
            if (identityServerUri.scheme != "http" && identityServerUri.scheme != "https") {
                throw RuntimeException("Invalid identity server URI: $identityServerUri")
            }
            
            val isString = identityServerUri.toString().ensureTrailingSlash()
            this.identityServerUri = try {
                Uri.parse(isString)
            } catch (e: Exception) {
                throw RuntimeException("Invalid identity server URI: $identityServerUri")
            }
            return this
        }

        
        fun withAllowedFingerPrints(allowedFingerprints: List<Fingerprint>?): Builder {
            if (allowedFingerprints != null) {
                this.allowedFingerprints.addAll(allowedFingerprints)
            }
            return this
        }

        
        fun withPin(pin: Boolean): Builder {
            this.shouldPin = pin
            return this
        }

        
        fun withShouldAcceptTlsExtensions(shouldAcceptTlsExtension: Boolean): Builder {
            this.shouldAcceptTlsExtensions = shouldAcceptTlsExtension
            return this
        }

        
        fun addAcceptedTlsVersion(tlsVersion: TlsVersion): Builder {
            this.tlsVersions.add(tlsVersion)
            return this
        }

        
        fun forceUsageOfTlsVersions(forceUsageOfTlsVersions: Boolean): Builder {
            this.forceUsageTlsVersions = forceUsageOfTlsVersions
            return this
        }

        
        fun addAcceptedTlsCipherSuite(tlsCipherSuite: CipherSuite): Builder {
            this.tlsCipherSuites.add(tlsCipherSuite)
            return this
        }

        fun withAntiVirusServerUri(antivirusServerUriString: String?): Builder {
            return withAntiVirusServerUri(antivirusServerUriString?.let { Uri.parse(it) })
        }

        
        fun withAntiVirusServerUri(antivirusServerUri: Uri?): Builder {
            if (null != antivirusServerUri && "http" != antivirusServerUri.scheme && "https" != antivirusServerUri.scheme) {
                throw RuntimeException("Invalid antivirus server URI: $antivirusServerUri")
            }
            this.antiVirusServerUri = antivirusServerUri
            return this
        }

        
        @Deprecated("TLS versions and cipher suites are limited by default")
        fun withTlsLimitations(tlsLimitations: Boolean, enableCompatibilityMode: Boolean): Builder {
            if (tlsLimitations) {
                withShouldAcceptTlsExtensions(false)

                
                ConnectionSpec.RESTRICTED_TLS.tlsVersions?.let { this.tlsVersions.addAll(it) }

                forceUsageOfTlsVersions(enableCompatibilityMode)

                
                ConnectionSpec.RESTRICTED_TLS.cipherSuites?.let { this.tlsCipherSuites.addAll(it) }

                if (enableCompatibilityMode) {
                    
                    
                    addAcceptedTlsCipherSuite(CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA)
                    addAcceptedTlsCipherSuite(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA)
                }
            }
            return this
        }

        fun withAllowHttpConnection(allowHttpExtension: Boolean): Builder {
            this.allowHttpExtension = allowHttpExtension
            return this
        }

        
        fun build(): HomeServerConnectionConfig {
            return HomeServerConnectionConfig(
                    homeServerUri = homeServerUri,
                    identityServerUri = identityServerUri,
                    antiVirusServerUri = antiVirusServerUri,
                    allowedFingerprints = allowedFingerprints,
                    shouldPin = shouldPin,
                    tlsVersions = tlsVersions,
                    tlsCipherSuites = tlsCipherSuites,
                    shouldAcceptTlsExtensions = shouldAcceptTlsExtensions,
                    allowHttpExtension = allowHttpExtension,
                    forceUsageTlsVersions = forceUsageTlsVersions
            )
        }
    }
}
