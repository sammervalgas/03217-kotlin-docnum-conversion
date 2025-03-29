package br.com.devbean.convertershub.validators

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class ValidJsonValidator(): ConstraintValidator<ValidJson, Map<String, Any>> {

    override fun isValid(value: Map<String, Any>?, context: ConstraintValidatorContext?): Boolean {
        return try {
            ObjectMapper().writeValueAsString(value)
            true
        } catch (e: Exception) {
            false
        }
    }
}