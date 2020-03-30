package io.selja.seljabackend.advice

import io.selja.seljabackend.web.rest.errors.AdNotFoundException
import io.selja.seljabackend.web.rest.errors.BadLocationException
import io.selja.seljabackend.domain.ErrorResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class AdAdvice : ResponseEntityExceptionHandler() {

    override fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
        val errors = ex.bindingResult.fieldErrors.map { it.defaultMessage }.toList()
        val body = linkedMapOf(
                "status" to status.value(),
                "errors" to errors
        )
        return ResponseEntity(body, headers, status)
    }

    override fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException,
                                              headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
        val error = "Malformed JSON request"
        return buildResponseEntity(ErrorResponse(HttpStatus.BAD_REQUEST, error))
    }

    @ResponseBody
    @ExceptionHandler(AdNotFoundException::class)
    fun adNotFoundHandler(ex: AdNotFoundException): ResponseEntity<*> {
        val error = ErrorResponse(HttpStatus.NOT_FOUND, "Item with id: ${ex.id} doesn't exists")
        return ResponseEntity<Any>(error, HttpStatus.NOT_FOUND)
    }

    @ResponseBody
    @ExceptionHandler(BadLocationException::class)
    fun badLocationHandler(ex: BadLocationException): ResponseEntity<*> {
        val error = ErrorResponse(HttpStatus.BAD_REQUEST, ex.message!!)
        return ResponseEntity<Any>(error, HttpStatus.BAD_REQUEST)
    }

    private fun buildResponseEntity(errorResponse: ErrorResponse): ResponseEntity<Any> {
        return ResponseEntity(errorResponse, errorResponse.status)
    }
}