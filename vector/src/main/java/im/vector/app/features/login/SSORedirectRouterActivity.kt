

package im.vector.app.features.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.features.navigation.Navigator
import javax.inject.Inject

@AndroidEntryPoint
class SSORedirectRouterActivity : AppCompatActivity() {

    @Inject lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigator.loginSSORedirect(this, intent.data)
        finish()
    }

    companion object {
        
        const val VECTOR_REDIRECT_URL = "element://connect"
    }
}
