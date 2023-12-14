

package im.vector.app.features.location

import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.junit.Test
import org.matrix.android.sdk.api.session.room.model.message.LocationAsset
import org.matrix.android.sdk.api.session.room.model.message.LocationAssetType
import org.matrix.android.sdk.api.session.room.model.message.LocationInfo
import org.matrix.android.sdk.api.session.room.model.message.MessageLocationContent

class LocationDataTest {
    @Test
    fun validCases() {
        parseGeo("geo:12.34,56.78;13.56") shouldBeEqualTo
                LocationData(latitude = 12.34, longitude = 56.78, uncertainty = 13.56)
        parseGeo("geo:12.34,56.78") shouldBeEqualTo
                LocationData(latitude = 12.34, longitude = 56.78, uncertainty = null)
        
        parseGeo("geo:12.34,56.78;13.5z6") shouldBeEqualTo
                LocationData(latitude = 12.34, longitude = 56.78, uncertainty = null)
        parseGeo("geo:12.34,56.78;13. 56") shouldBeEqualTo
                LocationData(latitude = 12.34, longitude = 56.78, uncertainty = null)
        
        parseGeo("geo: 12.34,56.78;13.56") shouldBeEqualTo
                LocationData(latitude = 12.34, longitude = 56.78, uncertainty = 13.56)
        parseGeo("geo:12.34,56.78; 13.56") shouldBeEqualTo
                LocationData(latitude = 12.34, longitude = 56.78, uncertainty = 13.56)
    }

    @Test
    fun invalidCases() {
        parseGeo("").shouldBeNull()
        parseGeo("geo").shouldBeNull()
        parseGeo("geo:").shouldBeNull()
        parseGeo("geo:12.34").shouldBeNull()
        parseGeo("geo:12.34;13.56").shouldBeNull()
        parseGeo("gea:12.34,56.78;13.56").shouldBeNull()
        parseGeo("geo:12.x34,56.78;13.56").shouldBeNull()
        parseGeo("geo:12.34,56.7y8;13.56").shouldBeNull()
        
        parseGeo("geo:12.3 4,56.78;13.56").shouldBeNull()
        parseGeo("geo:12.34,56.7 8;13.56").shouldBeNull()
        
        parseGeo(" geo:12.34,56.78;13.56").shouldBeNull()
        parseGeo("ge o:12.34,56.78;13.56").shouldBeNull()
        parseGeo("geo :12.34,56.78;13.56").shouldBeNull()
    }

    @Test
    fun selfLocationTest() {
        val contentWithNullAsset = MessageLocationContent(body = "", geoUri = "")
        contentWithNullAsset.isSelfLocation().shouldBeTrue()

        val contentWithNullAssetType = MessageLocationContent(body = "", geoUri = "", unstableLocationAsset = LocationAsset(type = null))
        contentWithNullAssetType.isSelfLocation().shouldBeTrue()

        val contentWithSelfAssetType = MessageLocationContent(body = "", geoUri = "", unstableLocationAsset = LocationAsset(type = LocationAssetType.SELF))
        contentWithSelfAssetType.isSelfLocation().shouldBeTrue()
    }

    @Test
    fun unstablePrefixTest() {
        val geoUri = "geo :12.34,56.78;13.56"

        val contentWithUnstablePrefixes = MessageLocationContent(body = "", geoUri = "", unstableLocationInfo = LocationInfo(geoUri = geoUri))
        contentWithUnstablePrefixes.getBestLocationInfo()?.geoUri.shouldBeEqualTo(geoUri)

        val contentWithStablePrefixes = MessageLocationContent(body = "", geoUri = "", locationInfo = LocationInfo(geoUri = geoUri))
        contentWithStablePrefixes.getBestLocationInfo()?.geoUri.shouldBeEqualTo(geoUri)
    }
}
