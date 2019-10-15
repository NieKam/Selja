package io.selja.seljabackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class SeljaBackendApplication

fun main(args: Array<String>) {
    runApplication<SeljaBackendApplication>(*args)
}
