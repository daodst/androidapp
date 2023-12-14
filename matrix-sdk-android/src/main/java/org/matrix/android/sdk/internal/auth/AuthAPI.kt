

package org.matrix.android.sdk.internal.auth

import org.matrix.android.sdk.api.auth.data.Credentials
import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.internal.auth.data.Availability
import org.matrix.android.sdk.internal.auth.data.JwtLoginParams
import org.matrix.android.sdk.internal.auth.data.LoginFlowResponse
import org.matrix.android.sdk.internal.auth.data.PasswordLoginParams
import org.matrix.android.sdk.internal.auth.data.TokenLoginParams
import org.matrix.android.sdk.internal.auth.data.WalletLoginParams
import org.matrix.android.sdk.internal.auth.data.WebClientConfig
import org.matrix.android.sdk.internal.auth.login.ResetPasswordMailConfirmed
import org.matrix.android.sdk.internal.auth.registration.AddThreePidRegistrationParams
import org.matrix.android.sdk.internal.auth.registration.AddThreePidRegistrationResponse
import org.matrix.android.sdk.internal.auth.registration.RegistrationCustomParams
import org.matrix.android.sdk.internal.auth.registration.RegistrationParams
import org.matrix.android.sdk.internal.auth.registration.SuccessResult
import org.matrix.android.sdk.internal.auth.registration.ValidationCodeBody
import org.matrix.android.sdk.internal.auth.version.Versions
import org.matrix.android.sdk.internal.network.NetworkConstants
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url


internal interface AuthAPI {
    
    @GET("config.{domain}.json")
    suspend fun getWebClientConfigDomain(@Path("domain") domain: String): WebClientConfig

    
    @GET("config.json")
    suspend fun getWebClientConfig(): WebClientConfig

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_ + "versions")
    suspend fun versions(): Versions

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "register")
    suspend fun register(@Body registrationParams: RegistrationParams): Credentials

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "register")
    suspend fun registerCustom(@Body registrationCustomParams: RegistrationCustomParams): Credentials

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "register/available")
    suspend fun registerAvailable(@Query("username") username: String): Availability

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "profile/{userId}")
    suspend fun getProfile(@Path("userId") userId: String): JsonDict

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "register/{threePid}/requestToken")
    suspend fun add3Pid(@Path("threePid") threePid: String,
                        @Body params: AddThreePidRegistrationParams): AddThreePidRegistrationResponse

    
    @POST
    suspend fun validate3Pid(@Url url: String,
                             @Body params: ValidationCodeBody): SuccessResult

    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "login")
    suspend fun getLoginFlows(): LoginFlowResponse

    
    @Headers("CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000")
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "login")
    suspend fun login(@Body loginParams: PasswordLoginParams): Credentials

    @Headers("CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000")
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "login")
    suspend fun loginWithBlockChain(@Body loginParams: WalletLoginParams): Credentials

    @Headers("CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000")
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "login")
    suspend fun loginWithJwt(@Body loginParams: JwtLoginParams): Credentials

    
    @Headers("CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000")
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "login")
    suspend fun login(@Body loginParams: TokenLoginParams): Credentials

    @Headers("CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000")
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "login")
    suspend fun login(@Body loginParams: JsonDict): Credentials

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "account/password/email/requestToken")
    suspend fun resetPassword(@Body params: AddThreePidRegistrationParams): AddThreePidRegistrationResponse

    
    @POST(NetworkConstants.URI_API_PREFIX_PATH_R0 + "account/password")
    suspend fun resetPasswordMailConfirmed(@Body params: ResetPasswordMailConfirmed)
}
