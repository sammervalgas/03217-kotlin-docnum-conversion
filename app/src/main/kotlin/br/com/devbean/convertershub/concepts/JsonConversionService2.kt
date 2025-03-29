package br.com.devbean.convertershub.concepts

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Service
class JSONConversionService2 {

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

        // Garante que os dados tenham um tamanho múltiplo de 16
        val paddedValue = padToMultipleOf16(value)

        val encryptedBytes = cipher.doFinal(paddedValue.toByteArray(Charsets.UTF_8))

        // Converte para Base64 para garantir transmissão segura
        val base64Encrypted = Base64.getEncoder().encodeToString(encryptedBytes)

        return UUID.nameUUIDFromBytes(base64Encrypted.toByteArray()).toString()
    }

    fun revertFromUuid(uuid: String): String {
        val (secretKey, iv) = generateSecureKey()
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        // Recupera o valor base64 original
        val originalBase64 = Base64.getEncoder().encodeToString(
            uuid.toByteArray(Charsets.UTF_8)
        )

        // Decodifica a Base64
        val encryptedBytes = Base64.getDecoder().decode(originalBase64)

        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return String(decryptedBytes, Charsets.UTF_8).trim()
    }

    // Adiciona padding para garantir múltiplo de 16 bytes
    private fun padToMultipleOf16(input: String): String {
        val paddingChar = ' '
        val blockSize = 16
        val paddingLength = blockSize - (input.length % blockSize)
        return input + paddingChar.toString().repeat(paddingLength)
    }
}