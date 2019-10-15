package io.selja.seljabackend.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND)
data class AdNotFoundException(val id: Long) : RuntimeException()