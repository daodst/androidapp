

package im.vector.app.features.raw.wellknown

import com.squareup.moshi.JsonAdapter
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.util.MatrixJsonParser

object ElementWellKnownMapper {

    val adapter: JsonAdapter<ElementWellKnown> = MatrixJsonParser.getMoshi().adapter(ElementWellKnown::class.java)

    fun from(value: String): ElementWellKnown? {
        return tryOrNull("Unable to parse well-known data") { adapter.fromJson(value) }
    }
}
