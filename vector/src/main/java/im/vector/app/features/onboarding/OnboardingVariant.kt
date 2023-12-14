

package im.vector.app.features.onboarding

import android.content.Intent

interface OnboardingVariant {
    fun onNewIntent(intent: Intent?)
    fun initUiAndData(isFirstCreation: Boolean)
    fun setIsLoading(isLoading: Boolean)
}
