package br.com.devbean.convertershub.services

import ch.qos.logback.core.encoder.ByteArrayUtil
import org.springframework.stereotype.Service
import java.nio.ByteBuffer
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Service
// Service for converting CPF or CNPJ to UUID and vice versa
class DocumentConversionService {

    final val cipherKey = "4c7b85674f6b3b9bad2b152c8c05a503"
    val secretKey : SecretKey= SecretKeySpec(ByteArrayUtil.hexStringToByteArray(cipherKey), "AES")

    // Converts CPF or CNPJ to UUID
    fun convertToUuid(document: String): String {
        // Check if the document ID is either CPF (11 digits) or CNPJ (14 digits)
       val cipher = Cipher.getInstance("AES")

        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptionBytes = cipher.doFinal(document.toByteArray())

        return uuidFromBytes(encryptionBytes).toString()
    }

    // Attempts to revert the UUID back to the original document ID
    fun revertFromUuid(uuid: String): String {
        val parseUUID = UUID.fromString(uuid)

        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)

        val bytesUuid = uuidToBytes(parseUUID)
        return String(cipher.doFinal(bytesUuid))
    }

    fun uuidToBytes(uuid: UUID): ByteArray {
        val bb = ByteBuffer.wrap(ByteArray(16))
        bb.putLong(uuid.mostSignificantBits)
        bb.putLong(uuid.leastSignificantBits)
        return bb.array()
    }

    fun uuidFromBytes(decoded: ByteArray): UUID {
        val bb = ByteBuffer.wrap(decoded)
        val mostSigBits = bb.getLong()
        val leastSigBits = bb.getLong()
        return UUID(mostSigBits, leastSigBits)
    }
}
