package br.com.devbean.convertershub.controllers

import br.com.devbean.convertershub.services.DocumentConversionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/document")
class DocumentConverterController(
    val documentService: DocumentConversionService
) {

    @PostMapping("num-to-uuid")
    fun numToUUID(@RequestParam("document_number") documentNumber: String): ResponseEntity<String> {
        return ResponseEntity.ok(documentService.convertToUuid(documentNumber))
    }

    @PostMapping("from-uuid")
    fun fromUUID(@RequestParam uuid: String): ResponseEntity<String> {
        return try {
            val doc = documentService.revertFromUuid(uuid)
            ResponseEntity.ok(doc)
        } catch (e: Exception) {
            ResponseEntity.status(400).body("Error: ${e.message}")
        }
    }
}