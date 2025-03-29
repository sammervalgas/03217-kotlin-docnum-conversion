package br.com.devbean.convertershub.concepts

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/conversao")
class ConversaoController(private val conversaoService: ConversaoService) {

    @PostMapping("/codificar")
    fun codificarJson(@RequestBody jsonDinamico: JsonNode): ResponseEntity<Map<String, String>> {
        val identificador = conversaoService.converterParaIdentificador(jsonDinamico)

        return ResponseEntity.ok(mapOf(
            "identificador" to identificador
        ))
    }

    @GetMapping("/decodificar")
    fun decodificarJson(@RequestParam identificador: String): ResponseEntity<JsonNode> {
        val jsonRecuperado = conversaoService.recuperarJson(identificador)

        return if (jsonRecuperado != null) {
            ResponseEntity.ok(jsonRecuperado)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}