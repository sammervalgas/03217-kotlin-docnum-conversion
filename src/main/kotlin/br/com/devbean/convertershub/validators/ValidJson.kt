package br.com.devbean.convertershub.validators

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidJsonValidator::class])
annotation class ValidJson(
    val message: String = "Invalid JSON",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
