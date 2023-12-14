

package org.matrix.android.sdk.internal.auth.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes


@JsonClass(generateAdapter = true)
internal data class WalletLoginParams(
        @Json(name = "identifier") val identifier: Map<String, String>,
        @Json(name = "password") val password: String,
        @Json(name = "sign") val sign: String,
        @Json(name = "timestamp") val timestamp: String,
        @Json(name = "pub_key") val pubkey: String,
        @Json(name = "type") override val type: String,
        @Json(name = "initial_device_display_name") val deviceDisplayName: String?,
        @Json(name = "device_id") val deviceId: String?,
        @Json(name = "chat_pub_key") val chat_pub_key: String,
        @Json(name = "chat_sign") val chat_sign: String,

        ) : LoginParams {

    companion object {
        private const val IDENTIFIER_KEY_TYPE = "type"

        private const val IDENTIFIER_KEY_TYPE_USER = "m.id.user"
        private const val IDENTIFIER_KEY_USER = "user"

        private const val IDENTIFIER_KEY_TYPE_THIRD_PARTY = "m.id.thirdparty"
        private const val IDENTIFIER_KEY_MEDIUM = "medium"
        private const val IDENTIFIER_KEY_ADDRESS = "address"

        private const val IDENTIFIER_KEY_TYPE_PHONE = "m.id.phone"
        private const val IDENTIFIER_KEY_COUNTRY = "country"
        private const val IDENTIFIER_KEY_PHONE = "phone"

        private const val IDENTIFIER_KEY_SIGN = "sign"
        private const val IDENTIFIER_KEY_TIMESTAMP = "timestamp"
        private const val IDENTIFIER_KEY_PUBKEY = "pub_key"

        fun userIdentifier(user: String,
                           password: String,
                           sign: String,
                           timestamp: String,
                           pubkey: String,
                           deviceDisplayName: String?,
                           deviceId: String?, chat_pub_key: String, chat_sign: String): WalletLoginParams {
            return WalletLoginParams(
                    identifier = mapOf(
                            IDENTIFIER_KEY_TYPE to IDENTIFIER_KEY_TYPE_USER,
                            IDENTIFIER_KEY_USER to user
                    ),
                    password = password,
                    sign = sign,
                    timestamp = timestamp,
                    pubkey = pubkey,
                    type = LoginFlowTypes.BLOCKCHAIN,
                    deviceDisplayName = deviceDisplayName,
                    deviceId = deviceId,
                    chat_pub_key = chat_pub_key,
                    chat_sign = chat_sign
            )
        }
    }
}
