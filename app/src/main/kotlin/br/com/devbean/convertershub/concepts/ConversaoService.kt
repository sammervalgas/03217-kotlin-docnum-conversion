package br.com.devbean.convertershub.concepts

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.util.*
import java.util.zip.*

@Service
class ConversaoService {
    private val objectMapper = ObjectMapper()
    private val logger = LoggerFactory.getLogger(ConversaoService::class.java)

    fun converterParaIdentificador(jsonDinamico: JsonNode): String {
        try {
            // Converte JSON para string canônica
            val jsonCanonical = objectMapper.writeValueAsString(jsonDinamico)
            logger.debug("JSON Canonical: $jsonCanonical")

            // Comprime o JSON
            val jsonComprimido = comprimirDados(jsonCanonical.toByteArray(Charsets.UTF_8))
            logger.debug("JSON Comprimido (Base64): ${Base64.getEncoder().encodeToString(jsonComprimido)}")

            // Gera hash SHA-256 dos dados comprimidos
            val hash = gerarHash(jsonComprimido)

            // Codifica hash em Base62 para gerar identificador curto e único
            return converterHashParaBase62(hash)
        } catch (e: Exception) {
            logger.error("Erro ao converter para identificador", e)
            throw e
        }
    }

    fun recuperarJson(identificador: String): JsonNode? {
        try {
            // Converte Base62 de volta para hash
            val hash = converterBase62ParaHash(identificador)

            // Processo reverso: hash para dados comprimidos e depois para JSON
            val jsonComprimido = descomprimirDados(hash)
            logger.debug("JSON Descomprimido (Bytes): ${jsonComprimido.size} bytes")

            val jsonString = String(jsonComprimido, Charsets.UTF_8)
            logger.debug("JSON Recuperado: $jsonString")

            return objectMapper.readTree(jsonString)
        } catch (e: Exception) {
            logger.error("Erro ao recuperar JSON", e)
            return null
        }
    }

    // Compressão de dados usando Deflater
    private fun comprimirDados(dados: ByteArray): ByteArray {
        val deflater = Deflater()
        deflater.setInput(dados)
        deflater.finish()

        val outputStream = ByteArrayOutputStream(dados.size)
        val buffer = ByteArray(1024)
        while (!deflater.finished()) {
            val compressedDataLength = deflater.deflate(buffer)
            outputStream.write(buffer, 0, compressedDataLength)
        }
        outputStream.close()
        deflater.end()

        return outputStream.toByteArray()
    }

    // Descompressão de dados usando Inflater
    private fun descomprimirDados(dadosComprimidos: ByteArray): ByteArray {
        val inflater = Inflater()
        inflater.setInput(dadosComprimidos)

        val outputStream = ByteArrayOutputStream(dadosComprimidos.size * 2)
        val buffer = ByteArray(1024)

        try {
            while (!inflater.finished()) {
                val count = inflater.inflate(buffer)
                outputStream.write(buffer, 0, count)
            }
        } catch (e: DataFormatException) {
            logger.error("Erro de formato de dados durante descompressão", e)
            throw RuntimeException("Erro ao descomprimir dados", e)
        } finally {
            inflater.end()
            outputStream.close()
        }

        return outputStream.toByteArray()
    }

    // Geração de hash SHA-256
    private fun gerarHash(dados: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(dados)
    }

    // Conversão de hash para Base62
    private fun converterHashParaBase62(hash: ByteArray): String {
        val base62Chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        val resultado = StringBuilder()

        var valor = hash.fold(0L) { acc, byte ->
            (acc shl 8) or (byte.toLong() and 0xFF)
        }

        while (valor > 0) {
            resultado.insert(0, base62Chars[(valor % 62).toInt()])
            valor /= 62
        }

        return resultado.toString()
    }

    // Conversão de Base62 para hash
    private fun converterBase62ParaHash(base62: String): ByteArray {
        val base62Chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        var valor = 0L

        base62.forEach { char ->
            valor = valor * 62 + base62Chars.indexOf(char)
        }

        // Converter valor de volta para ByteArray
        return ByteArray(32) { index ->
            ((valor shr (8 * (31 - index))) and 0xFF).toByte()
        }
    }
}