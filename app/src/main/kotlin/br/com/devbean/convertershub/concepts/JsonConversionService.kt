package br.com.devbean.convertershub.concepts

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Service
class JSONConversionService {

    @Value("\${conversion.cipher.key}")
    private lateinit var cipherKey: String

    private fun generateSecureKey(): Pair<SecretKeySpec, IvParameterSpec> {
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(cipherKey.toByteArray(Charsets.UTF_8))

        // Primeiros 16 bytes para a chave
        val key = SecretKeySpec(keyBytes.copyOfRange(0, 16), "AES")

        // Próximos 16 bytes para o IV
        val iv = IvParameterSpec(keyBytes.copyOfRange(16, 32))

        return key to iv
    }

    fun convertToUuid(value: String): String {
        val (secretKey, iv) = generateSecureKey()
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)

        // Criptografa e converte para Base64
        val encryptedBytes = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        val base64Encrypted = Base64.getEncoder().encodeToString(encryptedBytes)

        // Converte Base64 para UUID de forma consistente
        return UUID.nameUUIDFromBytes(base64Encrypted.toByteArray(StandardCharsets.UTF_8)).toString()
    }

    fun revertFromUuid(uuid: String): String {
        val (secretKey, iv) = generateSecureKey()
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        // Recupera o UUID original
        val parseUUID = UUID.fromString(uuid)

        // Converte UUID de volta para Base64 string
        val uuidBytes = uuidToBytes(parseUUID)
        val base64Encrypted = String(uuidBytes, StandardCharsets.UTF_8)

        // Decodifica Base64
        val encryptedBytes = Base64.getDecoder().decode(base64Encrypted)

        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return String(decryptedBytes, Charsets.UTF_8).trim()
    }

    fun uuidToBytes(uuid: UUID): ByteArray {
        val bb = ByteBuffer.wrap(ByteArray(16))
        bb.putLong(uuid.mostSignificantBits)
        bb.putLong(uuid.leastSignificantBits)
        return bb.array()
    }
}