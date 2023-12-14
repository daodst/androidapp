

package im.vector.app.features.onboarding

import im.vector.app.core.platform.ScreenOrientationLocker
import im.vector.app.databinding.ActivityLoginBinding
import im.vector.app.features.VectorFeatures
import im.vector.app.features.login2.LoginViewModel2
import im.vector.app.features.onboarding.ftueauth.FtueAuthVariant
import javax.inject.Inject

class OnboardingVariantFactory @Inject constructor(
        private val vectorFeatures: VectorFeatures,
        private val orientationLocker: ScreenOrientationLocker,
) {

    fun create(activity: OnboardingActivity,
               views: ActivityLoginBinding,
               onboardingViewModel: Lazy<OnboardingViewModel>,
               loginViewModel2: Lazy<LoginViewModel2>
    ) = when (vectorFeatures.onboardingVariant()) {
        VectorFeatures.OnboardingVariant.LEGACY    -> error("Legacy is not supported by the FTUE")
        VectorFeatures.OnboardingVariant.FTUE_AUTH -> FtueAuthVariant(
                views = views,
                onboardingViewModel = onboardingViewModel.value,
                activity = activity,
                supportFragmentManager = activity.supportFragmentManager,
                vectorFeatures = vectorFeatures,
                orientationLocker = orientationLocker
        )
        VectorFeatures.OnboardingVariant.LOGIN_2   -> Login2Variant(
                views = views,
                loginViewModel = loginViewModel2.value,
                activity = activity,
                supportFragmentManager = activity.supportFragmentManager
        )
    }
}
