package io.selja.seljabackend.web.rest.errors

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND)
data class AdNotFoundException(val id: Long) : RuntimeException()