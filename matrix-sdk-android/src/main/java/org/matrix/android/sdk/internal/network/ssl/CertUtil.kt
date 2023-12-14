

package org.matrix.android.sdk.internal.network.ssl

import okhttp3.ConnectionSpec
import okhttp3.internal.tls.OkHostnameVerifier
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig
import timber.log.Timber
import java.security.KeyStore
import java.security.MessageDigest
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLPeerUnverifiedException
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


internal object CertUtil {

    
    private const val USE_DEFAULT_HOSTNAME_VERIFIER = true

    private val hexArray = "0123456789ABCDEF".toCharArray()

    
    @Throws(CertificateException::class)
    fun generateSha256Fingerprint(cert: X509Certificate): ByteArray {
        return generateFingerprint(cert, "SHA-256")
    }

    
    @Throws(CertificateException::class)
    fun generateSha1Fingerprint(cert: X509Certificate): ByteArray {
        return generateFingerprint(cert, "SHA-1")
    }

    
    @Throws(CertificateException::class)
    private fun generateFingerprint(cert: X509Certificate, type: String): ByteArray {
        val fingerprint: ByteArray
        val md: MessageDigest
        try {
            md = MessageDigest.getInstance(type)
        } catch (e: Exception) {
            
            throw CertificateException(e)
        }

        fingerprint = md.digest(cert.encoded)

        return fingerprint
    }

    
    fun fingerprintToHexString(fingerprint: ByteArray, sep: Char = ' '): String {
        val hexChars = CharArray(fingerprint.size * 3)
        for (j in fingerprint.indices) {
            val v = (fingerprint[j].toInt() and 0xFF)
            hexChars[j * 3] = hexArray[v.ushr(4)]
            hexChars[j * 3 + 1] = hexArray[v and 0x0F]
            hexChars[j * 3 + 2] = sep
        }
        return String(hexChars, 0, hexChars.size - 1)
    }

    
    fun getCertificateException(root: Throwable?): UnrecognizedCertificateException? {
        var e = root
        var i = 0 
        while (e != null && i < 10) {
            if (e is UnrecognizedCertificateException) {
                return e
            }
            e = e.cause
            i++
        }

        return null
    }

    internal data class PinnedSSLSocketFactory(
            val sslSocketFactory: SSLSocketFactory,
            val x509TrustManager: X509TrustManager
    )

    
    fun newPinnedSSLSocketFactory(hsConfig: HomeServerConnectionConfig): PinnedSSLSocketFactory {
        try {
            var defaultTrustManager: X509TrustManager? = null

            
            
            if (!hsConfig.shouldPin) {
                var tf: TrustManagerFactory? = null

                
                try {
                    tf = TrustManagerFactory.getInstance("PKIX")
                } catch (e: Exception) {
                    Timber.e(e, "## newPinnedSSLSocketFactory() : TrustManagerFactory.getInstance failed")
                }

                
                if (null == tf) {
                    try {
                        tf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                    } catch (e: Exception) {
                        Timber.e(e, "## newPinnedSSLSocketFactory() : TrustManagerFactory.getInstance of default failed")
                    }
                }

                tf!!.init(null as KeyStore?)
                val trustManagers = tf.trustManagers

                for (i in trustManagers.indices) {
                    if (trustManagers[i] is X509TrustManager) {
                        defaultTrustManager = trustManagers[i] as X509TrustManager
                        break
                    }
                }
            }

            val trustPinned = arrayOf<TrustManager>(PinnedTrustManagerProvider.provide(hsConfig.allowedFingerprints, defaultTrustManager))

            val sslSocketFactory = if (hsConfig.forceUsageTlsVersions && !hsConfig.tlsVersions.isNullOrEmpty()) {
                
                TLSSocketFactory(trustPinned, hsConfig.tlsVersions)
            } else {
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, trustPinned, java.security.SecureRandom())
                sslContext.socketFactory
            }

            return PinnedSSLSocketFactory(sslSocketFactory, defaultTrustManager!!)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    
    fun newHostnameVerifier(hsConfig: HomeServerConnectionConfig): HostnameVerifier {
        val defaultVerifier: HostnameVerifier = OkHostnameVerifier 
        val trustedFingerprints = hsConfig.allowedFingerprints

        return HostnameVerifier { hostname, session ->
            if (USE_DEFAULT_HOSTNAME_VERIFIER) {
                if (defaultVerifier.verify(hostname, session)) return@HostnameVerifier true
            }
            
            if (trustedFingerprints.isEmpty()) return@HostnameVerifier false

            
            try {
                for (cert in session.peerCertificates) {
                    for (allowedFingerprint in trustedFingerprints) {
                        if (cert is X509Certificate && allowedFingerprint.matchesCert(cert)) {
                            return@HostnameVerifier true
                        }
                    }
                }
            } catch (e: SSLPeerUnverifiedException) {
                return@HostnameVerifier false
            } catch (e: CertificateException) {
                return@HostnameVerifier false
            }

            false
        }
    }

    
    fun newConnectionSpecs(hsConfig: HomeServerConnectionConfig): List<ConnectionSpec> {
        val builder = ConnectionSpec.Builder(ConnectionSpec.RESTRICTED_TLS)
        val tlsVersions = hsConfig.tlsVersions
        if (!tlsVersions.isNullOrEmpty()) {
            builder.tlsVersions(*tlsVersions.toTypedArray())
        }

        val tlsCipherSuites = hsConfig.tlsCipherSuites
        if (!tlsCipherSuites.isNullOrEmpty()) {
            builder.cipherSuites(*tlsCipherSuites.toTypedArray())
        }

        @Suppress("DEPRECATION")
        builder.supportsTlsExtensions(hsConfig.shouldAcceptTlsExtensions)
        val list = ArrayList<ConnectionSpec>()
        list.add(builder.build())
        
        if (hsConfig.allowHttpExtension || hsConfig.homeServerUriBase.toString().startsWith("http://")) {
            list.add(ConnectionSpec.CLEARTEXT)
        }
        return list
    }
}
