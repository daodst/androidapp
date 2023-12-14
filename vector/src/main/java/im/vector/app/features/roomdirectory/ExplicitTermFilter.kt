

package im.vector.app.features.roomdirectory

import im.vector.app.core.utils.AssetReader
import javax.inject.Inject

class ExplicitTermFilter @Inject constructor(
        assetReader: AssetReader
) {
    
    private val explicitTerms = assetReader.readAssetFile("forbidden_terms.txt")
            .orEmpty()
            .split("\n")
            .map { it.trim() }
            .distinct()
            .filter { it.isNotEmpty() }

    private val explicitContentRegex = explicitTerms
            .joinToString(prefix = ".*\\b(", separator = "|", postfix = ")\\b.*")
            .toRegex(RegexOption.IGNORE_CASE)

    fun canSearchFor(term: String): Boolean {
        return term !in explicitTerms && term != "18+"
    }

    fun isValid(str: String): Boolean {
        return explicitContentRegex.matches(str.replace("\n", " ")).not() &&
                
                str.contains("18+").not()
    }
}
