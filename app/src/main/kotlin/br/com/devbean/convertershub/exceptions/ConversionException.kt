package br.com.devbean.convertershub.exceptions

class ConversionException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
