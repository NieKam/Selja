package io.selja.seljabackend.model

import org.springframework.http.HttpStatus

data class ErrorResponse(val status: HttpStatus, val message: String)