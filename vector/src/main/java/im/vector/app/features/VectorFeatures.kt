

package im.vector.app.features

import im.vector.app.BuildConfig

interface VectorFeatures {

    fun onboardingVariant(): OnboardingVariant
    fun isOnboardingAlreadyHaveAccountSplashEnabled(): Boolean
    fun isOnboardingSplashCarouselEnabled(): Boolean
    fun isOnboardingUseCaseEnabled(): Boolean
    fun isOnboardingPersonalizeEnabled(): Boolean
    fun isOnboardingCombinedRegisterEnabled(): Boolean
    fun isLiveLocationEnabled(): Boolean
    fun isScreenSharingEnabled(): Boolean

    enum class OnboardingVariant {
        LEGACY,
        LOGIN_2,
        FTUE_AUTH
    }
}

class DefaultVectorFeatures : VectorFeatures {
    override fun onboardingVariant(): VectorFeatures.OnboardingVariant = BuildConfig.ONBOARDING_VARIANT
    override fun isOnboardingAlreadyHaveAccountSplashEnabled() = true
    override fun isOnboardingSplashCarouselEnabled() = true
    override fun isOnboardingUseCaseEnabled() = true
    override fun isOnboardingPersonalizeEnabled() = false
    override fun isOnboardingCombinedRegisterEnabled() = false
    override fun isLiveLocationEnabled(): Boolean = false
    override fun isScreenSharingEnabled(): Boolean = false
}
