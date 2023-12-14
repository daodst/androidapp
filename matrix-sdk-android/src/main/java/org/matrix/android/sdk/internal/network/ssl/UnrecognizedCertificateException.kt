

package org.matrix.android.sdk.internal.network.ssl

import org.matrix.android.sdk.api.network.ssl.Fingerprint
import java.security.cert.CertificateException
import java.security.cert.X509Certificate


internal data class UnrecognizedCertificateException(
        val certificate: X509Certificate,
        val fingerprint: Fingerprint,
        override val cause: Throwable?
) : CertificateException("Unrecognized certificate with unknown fingerprint: " + certificate.subjectDN, cause)
