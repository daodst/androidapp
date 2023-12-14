

package im.vector.app.features.call.conference.jwt

import im.vector.app.core.utils.ensureProtocol
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.matrix.android.sdk.api.session.openid.OpenIdToken
import javax.inject.Inject

class JitsiJWTFactory @Inject constructor() {

    
    fun create(openIdToken: OpenIdToken,
               jitsiServerDomain: String,
               roomId: String,
               userAvatarUrl: String,
               userDisplayName: String): String {
        
        val key = Keys.secretKeyFor(SignatureAlgorithm.HS256)
        val context = mapOf(
                "matrix" to mapOf(
                        "token" to openIdToken.accessToken,
                        "room_id" to roomId,
                        "server_name" to openIdToken.matrixServerName
                ),
                "user" to mapOf(
                        "name" to userDisplayName,
                        "avatar" to userAvatarUrl
                )
        )
        
        
        
        return Jwts.builder()
                .setIssuer(jitsiServerDomain)
                .setSubject(jitsiServerDomain)
                .setAudience(jitsiServerDomain.ensureProtocol())
                
                .claim("room", "*")
                .claim("context", context)
                .signWith(key)
                .compact()
    }
}
