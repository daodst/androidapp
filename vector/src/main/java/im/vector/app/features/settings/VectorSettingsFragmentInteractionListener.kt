
package im.vector.app.features.settings

interface VectorSettingsFragmentInteractionListener {

    fun requestHighlightPreferenceKeyOnResume(key: String?)

    fun requestedKeyToHighlight(): String?

    fun navigateToEmailAndPhoneNumbers()
}
