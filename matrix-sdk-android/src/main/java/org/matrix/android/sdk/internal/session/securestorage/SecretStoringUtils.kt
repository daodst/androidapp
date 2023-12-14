

@file:Suppress("DEPRECATION")

package org.matrix.android.sdk.internal.session.securestorage

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import org.matrix.android.sdk.internal.util.system.BuildVersionSdkIntProvider
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.SecureRandom
import java.util.Calendar
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.security.auth.x500.X500Principal


internal class SecretStoringUtils @Inject constructor(
        private val context: Context,
        private val buildVersionSdkIntProvider: BuildVersionSdkIntProvider
) {

    companion object {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val AES_MODE = "AES/GCM/NoPadding"
        private const val RSA_MODE = "RSA/ECB/PKCS1Padding"

        private const val FORMAT_API_M: Byte = 0
        private const val FORMAT_1: Byte = 1
    }

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEY_STORE).apply {
            load(null)
        }
    }

    private val secureRandom = SecureRandom()

    fun safeDeleteKey(keyAlias: String) {
        try {
            keyStore.deleteEntry(keyAlias)
        } catch (e: KeyStoreException) {
            Timber.e(e)
        }
    }

    
    @SuppressLint("NewApi")
    @Throws(Exception::class)
    fun securelyStoreString(secret: String, keyAlias: String): ByteArray {
        return when {
            buildVersionSdkIntProvider.get() >= Build.VERSION_CODES.M -> encryptStringM(secret, keyAlias)
            else                                                      -> encryptString(secret, keyAlias)
        }
    }

    
    @SuppressLint("NewApi")
    @Throws(Exception::class)
    fun loadSecureSecret(encrypted: ByteArray, keyAlias: String): String {
        encrypted.inputStream().use { inputStream ->
            
            return when (val format = inputStream.read().toByte()) {
                FORMAT_API_M -> decryptStringM(inputStream, keyAlias)
                FORMAT_1     -> decryptString(inputStream, keyAlias)
                else         -> throw IllegalArgumentException("Unknown format $format")
            }
        }
    }

    @SuppressLint("NewApi")
    fun securelyStoreObject(any: Any, keyAlias: String, output: OutputStream) {
        when {
            buildVersionSdkIntProvider.get() >= Build.VERSION_CODES.M -> saveSecureObjectM(keyAlias, output, any)
            else                                                      -> saveSecureObject(keyAlias, output, any)
        }
    }

    @SuppressLint("NewApi")
    fun <T> loadSecureSecret(inputStream: InputStream, keyAlias: String): T? {
        
        return when (val format = inputStream.read().toByte()) {
            FORMAT_API_M -> loadSecureObjectM(keyAlias, inputStream)
            FORMAT_1     -> loadSecureObject(keyAlias, inputStream)
            else         -> throw IllegalArgumentException("Unknown format $format")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getOrGenerateSymmetricKeyForAliasM(alias: String): SecretKey {
        val secretKeyEntry = (keyStore.getEntry(alias, null) as? KeyStore.SecretKeyEntry)
                ?.secretKey
        if (secretKeyEntry == null) {
            
            val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val keyGenSpec = KeyGenParameterSpec.Builder(alias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(128)
                    .build()
            generator.init(keyGenSpec)
            return generator.generateKey()
        }
        return secretKeyEntry
    }

    
    private fun getOrGenerateKeyPairForAlias(alias: String): KeyStore.PrivateKeyEntry {
        val privateKeyEntry = (keyStore.getEntry(alias, null) as? KeyStore.PrivateKeyEntry)

        if (privateKeyEntry != null) return privateKeyEntry

        val start = Calendar.getInstance()
        val end = Calendar.getInstance()
        end.add(Calendar.YEAR, 30)

        val spec = KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias)
                .setSubject(X500Principal("CN=$alias"))
                .setSerialNumber(BigInteger.TEN)
                
                .setStartDate(start.time)
                .setEndDate(end.time)
                .build()
        KeyPairGenerator.getInstance("RSA" , ANDROID_KEY_STORE).run {
            initialize(spec)
            generateKeyPair()
        }
        return (keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun encryptStringM(text: String, keyAlias: String): ByteArray {
        val secretKey = getOrGenerateSymmetricKeyForAliasM(keyAlias)

        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        
        val encryptedBytes: ByteArray = cipher.doFinal(text.toByteArray(Charsets.UTF_8))
        return formatMMake(iv, encryptedBytes)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun decryptStringM(inputStream: InputStream, keyAlias: String): String {
        val (iv, encryptedText) = formatMExtract(inputStream)

        val secretKey = getOrGenerateSymmetricKeyForAliasM(keyAlias)

        val cipher = Cipher.getInstance(AES_MODE)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        return String(cipher.doFinal(encryptedText), Charsets.UTF_8)
    }

    private fun encryptString(text: String, keyAlias: String): ByteArray {
        
        val key = ByteArray(16)
        secureRandom.nextBytes(key)
        val sKey = SecretKeySpec(key, "AES")

        
        val encryptedKey = rsaEncrypt(keyAlias, key)

        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, sKey)
        val iv = cipher.iv
        val encryptedBytes: ByteArray = cipher.doFinal(text.toByteArray(Charsets.UTF_8))

        return format1Make(encryptedKey, iv, encryptedBytes)
    }

    private fun decryptString(inputStream: InputStream, keyAlias: String): String {
        val (encryptedKey, iv, encrypted) = format1Extract(inputStream)

        
        val sKeyBytes = rsaDecrypt(keyAlias, ByteArrayInputStream(encryptedKey))
        val cipher = Cipher.getInstance(AES_MODE)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(sKeyBytes, "AES"), spec)

        return String(cipher.doFinal(encrypted), Charsets.UTF_8)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Throws(IOException::class)
    private fun saveSecureObjectM(keyAlias: String, output: OutputStream, writeObject: Any) {
        val secretKey = getOrGenerateSymmetricKeyForAliasM(keyAlias)

        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv

        val bos1 = ByteArrayOutputStream()
        ObjectOutputStream(bos1).use {
            it.writeObject(writeObject)
        }
        
        val doFinal = cipher.doFinal(bos1.toByteArray())
        output.write(FORMAT_API_M.toInt())
        output.write(iv.size)
        output.write(iv)
        output.write(doFinal)
    }

    private fun saveSecureObject(keyAlias: String, output: OutputStream, writeObject: Any) {
        
        val key = ByteArray(16)
        secureRandom.nextBytes(key)
        val sKey = SecretKeySpec(key, "AES")

        
        val encryptedKey = rsaEncrypt(keyAlias, key)

        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, sKey)
        val iv = cipher.iv

        val bos1 = ByteArrayOutputStream()
        val cos = CipherOutputStream(bos1, cipher)
        ObjectOutputStream(cos).use {
            it.writeObject(writeObject)
        }

        output.write(FORMAT_1.toInt())
        output.write((encryptedKey.size and 0xFF00).shr(8))
        output.write(encryptedKey.size and 0x00FF)
        output.write(encryptedKey)
        output.write(iv.size)
        output.write(iv)
        output.write(bos1.toByteArray())
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Throws(IOException::class)
    private fun <T> loadSecureObjectM(keyAlias: String, inputStream: InputStream): T? {
        val secretKey = getOrGenerateSymmetricKeyForAliasM(keyAlias)

        val ivSize = inputStream.read()
        val iv = ByteArray(ivSize)
        inputStream.read(iv, 0, ivSize)
        val cipher = Cipher.getInstance(AES_MODE)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        CipherInputStream(inputStream, cipher).use { cipherInputStream ->
            ObjectInputStream(cipherInputStream).use {
                val readObject = it.readObject()
                @Suppress("UNCHECKED_CAST")
                return readObject as? T
            }
        }
    }

    @Throws(IOException::class)
    private fun <T> loadSecureObject(keyAlias: String, inputStream: InputStream): T? {
        val (encryptedKey, iv, encrypted) = format1Extract(inputStream)

        
        val sKeyBytes = rsaDecrypt(keyAlias, ByteArrayInputStream(encryptedKey))
        val cipher = Cipher.getInstance(AES_MODE)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(sKeyBytes, "AES"), spec)

        val encIS = ByteArrayInputStream(encrypted)

        CipherInputStream(encIS, cipher).use { cipherInputStream ->
            ObjectInputStream(cipherInputStream).use {
                val readObject = it.readObject()
                @Suppress("UNCHECKED_CAST")
                return readObject as? T
            }
        }
    }

    @Throws(Exception::class)
    private fun rsaEncrypt(alias: String, secret: ByteArray): ByteArray {
        val privateKeyEntry = getOrGenerateKeyPairForAlias(alias)
        
        val inputCipher = Cipher.getInstance(RSA_MODE)
        inputCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.certificate.publicKey)

        val outputStream = ByteArrayOutputStream()
        CipherOutputStream(outputStream, inputCipher).use {
            it.write(secret)
        }

        return outputStream.toByteArray()
    }

    @Throws(Exception::class)
    private fun rsaDecrypt(alias: String, encrypted: InputStream): ByteArray {
        val privateKeyEntry = getOrGenerateKeyPairForAlias(alias)
        val output = Cipher.getInstance(RSA_MODE)
        output.init(Cipher.DECRYPT_MODE, privateKeyEntry.privateKey)

        return CipherInputStream(encrypted, output).use { it.readBytes() }
    }

    private fun formatMExtract(bis: InputStream): Pair<ByteArray, ByteArray> {
        val ivSize = bis.read()
        val iv = ByteArray(ivSize)
        bis.read(iv, 0, ivSize)

        val encrypted = bis.readBytes()
        return Pair(iv, encrypted)
    }

    private fun formatMMake(iv: ByteArray, data: ByteArray): ByteArray {
        val bos = ByteArrayOutputStream(2 + iv.size + data.size)
        bos.write(FORMAT_API_M.toInt())
        bos.write(iv.size)
        bos.write(iv)
        bos.write(data)
        return bos.toByteArray()
    }

    private fun format1Extract(bis: InputStream): Triple<ByteArray, ByteArray, ByteArray> {
        val keySizeBig = bis.read()
        val keySizeLow = bis.read()
        val encryptedKeySize = keySizeBig.shl(8) + keySizeLow
        val encryptedKey = ByteArray(encryptedKeySize)
        bis.read(encryptedKey)

        val ivSize = bis.read()
        val iv = ByteArray(ivSize)
        bis.read(iv)

        val encrypted = bis.readBytes()
        return Triple(encryptedKey, iv, encrypted)
    }

    private fun format1Make(encryptedKey: ByteArray, iv: ByteArray, encryptedBytes: ByteArray): ByteArray {
        val bos = ByteArrayOutputStream(4 + encryptedKey.size + iv.size + encryptedBytes.size)
        bos.write(FORMAT_1.toInt())
        bos.write((encryptedKey.size and 0xFF00).shr(8))
        bos.write(encryptedKey.size and 0x00FF)
        bos.write(encryptedKey)
        bos.write(iv.size)
        bos.write(iv)
        bos.write(encryptedBytes)

        return bos.toByteArray()
    }
}
