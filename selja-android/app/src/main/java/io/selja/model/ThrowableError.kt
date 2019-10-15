package io.selja.model


data class ThrowableError(val error: Error?) : Throwable()