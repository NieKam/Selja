package io.selja.seljabackend.web.rest.errors

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class BadLocationException : RuntimeException("Incorrect location parameter")