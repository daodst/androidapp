

package org.matrix.android.sdk.internal.session.securestorage

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.amshove.kluent.shouldBeEqualTo
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.matrix.android.sdk.InstrumentedTest
import org.matrix.android.sdk.api.util.fromBase64
import org.matrix.android.sdk.api.util.toBase64NoPadding
import java.io.ByteArrayOutputStream
import java.util.UUID

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.JVM)
class SecretStoringUtilsTest : InstrumentedTest {

    private val buildVersionSdkIntProvider = TestBuildVersionSdkIntProvider()
    private val secretStoringUtils = SecretStoringUtils(context(), buildVersionSdkIntProvider)

    companion object {
        const val TEST_STR = "This is something I want to store safely!"
    }

    @Test
    fun testStringNominalCaseApi21() {
        val alias = generateAlias()
        buildVersionSdkIntProvider.value = Build.VERSION_CODES.LOLLIPOP
        
        val encrypted = secretStoringUtils.securelyStoreString(TEST_STR, alias)
        
        val decrypted = secretStoringUtils.loadSecureSecret(encrypted, alias)
        decrypted shouldBeEqualTo TEST_STR
        secretStoringUtils.safeDeleteKey(alias)
    }

    @Test
    fun testStringNominalCaseApi23() {
        val alias = generateAlias()
        buildVersionSdkIntProvider.value = Build.VERSION_CODES.M
        
        val encrypted = secretStoringUtils.securelyStoreString(TEST_STR, alias)
        
        val decrypted = secretStoringUtils.loadSecureSecret(encrypted, alias)
        decrypted shouldBeEqualTo TEST_STR
        secretStoringUtils.safeDeleteKey(alias)
    }

    @Test
    fun testStringNominalCaseApi30() {
        val alias = generateAlias()
        buildVersionSdkIntProvider.value = Build.VERSION_CODES.R
        
        val encrypted = secretStoringUtils.securelyStoreString(TEST_STR, alias)
        
        val decrypted = secretStoringUtils.loadSecureSecret(encrypted, alias)
        decrypted shouldBeEqualTo TEST_STR
        secretStoringUtils.safeDeleteKey(alias)
    }

    @Test
    fun testStringMigration21_23() {
        val alias = generateAlias()
        buildVersionSdkIntProvider.value = Build.VERSION_CODES.LOLLIPOP
        
        val encrypted = secretStoringUtils.securelyStoreString(TEST_STR, alias)

        
        buildVersionSdkIntProvider.value = Build.VERSION_CODES.M

        
        val decrypted = secretStoringUtils.loadSecureSecret(encrypted, alias)
        decrypted shouldBeEqualTo TEST_STR
        secretStoringUtils.safeDeleteKey(alias)
    }

    @Test
    fun testObjectNominalCaseApi21() {
        val alias = generateAlias()
        buildVersionSdkIntProvider.value = Build.VERSION_CODES.LOLLIPOP

        
        val encrypted = ByteArrayOutputStream().also { outputStream ->
            outputStream.use {
                secretStoringUtils.securelyStoreObject(TEST_STR, alias, it)
            }
        }
                .toByteArray()
                .toBase64NoPadding()
        
        val decrypted = encrypted.fromBase64().inputStream().use {
            secretStoringUtils.loadSecureSecret<String>(it, alias)
        }
        decrypted shouldBeEqualTo TEST_STR
        secretStoringUtils.safeDeleteKey(alias)
    }

    @Test
    fun testObjectNominalCaseApi23() {
        val alias = generateAlias()
        buildVersionSdkIntProvider.value = Build.VERSION_CODES.M

        
        val encrypted = ByteArrayOutputStream().also { outputStream ->
            outputStream.use {
                secretStoringUtils.securelyStoreObject(TEST_STR, alias, it)
            }
        }
                .toByteArray()
                .toBase64NoPadding()
        
        val decrypted = encrypted.fromBase64().inputStream().use {
            secretStoringUtils.loadSecureSecret<String>(it, alias)
        }
        decrypted shouldBeEqualTo TEST_STR
        secretStoringUtils.safeDeleteKey(alias)
    }

    @Test
    fun testObjectNominalCaseApi30() {
        val alias = generateAlias()
        buildVersionSdkIntProvider.value = Build.VERSION_CODES.R

        
        val encrypted = ByteArrayOutputStream().also { outputStream ->
            outputStream.use {
                secretStoringUtils.securelyStoreObject(TEST_STR, alias, it)
            }
        }
                .toByteArray()
                .toBase64NoPadding()
        
        val decrypted = encrypted.fromBase64().inputStream().use {
            secretStoringUtils.loadSecureSecret<String>(it, alias)
        }
        decrypted shouldBeEqualTo TEST_STR
        secretStoringUtils.safeDeleteKey(alias)
    }

    @Test
    fun testObjectMigration21_23() {
        val alias = generateAlias()
        buildVersionSdkIntProvider.value = Build.VERSION_CODES.LOLLIPOP

        
        val encrypted = ByteArrayOutputStream().also { outputStream ->
            outputStream.use {
                secretStoringUtils.securelyStoreObject(TEST_STR, alias, it)
            }
        }
                .toByteArray()
                .toBase64NoPadding()

        
        buildVersionSdkIntProvider.value = Build.VERSION_CODES.M

        
        val decrypted = encrypted.fromBase64().inputStream().use {
            secretStoringUtils.loadSecureSecret<String>(it, alias)
        }
        decrypted shouldBeEqualTo TEST_STR
        secretStoringUtils.safeDeleteKey(alias)
    }

    private fun generateAlias() = UUID.randomUUID().toString()
}
