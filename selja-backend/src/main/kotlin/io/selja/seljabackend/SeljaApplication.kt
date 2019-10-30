package io.selja.seljabackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SeljaApplication

fun main(args: Array<String>) {
    runApplication<SeljaApplication>(*args)
}
