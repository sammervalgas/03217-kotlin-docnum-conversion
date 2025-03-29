package br.com.devbean.convertershub.concepts

import br.com.devbean.convertershub.validators.ValidJson
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.Valid
import org.springframework.core.convert.ConversionException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/json")
class JSONConversionController(
    val jsonConversionService: JSONConversionService,
    val objectMapper: ObjectMapper
) {

    @PostMapping("to-uuid")
    fun convertToUuid(@Valid @RequestBody @ValidJson value: Map<String, Any>): ResponseEntity<String> {

        val valueString = objectMapper.writeValueAsString(value)
        println(valueString)

        return ok(jsonConversionService.convertToUuid(valueString))
    }

    @PostMapping("from-uuid")
    fun convertToUuid(@RequestParam uuid: String): ResponseEntity<String> {

        return ok(jsonConversionService.revertFromUuid(uuid))
    }

    @ExceptionHandler(ConversionException::class)
    fun handleConversionException(
        ex: ConversionException
    ): ResponseEntity<Map<String, String>> {
        val errorResponse = mapOf(
            "error" to "CONVERSION_ERROR",
            "message" to (ex.message ?: "Erro desconhecido"),
            "details" to (ex.cause?.message ?: "Sem detalhes")
        )
        return status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }


}