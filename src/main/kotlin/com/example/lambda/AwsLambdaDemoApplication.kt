package com.example.lambda

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.util.function.Function


@SpringBootApplication
class AwsLambdaDemoApplication {

    @Bean
    fun function(): Function<String, String> = Function {
        "Hello, $it"
    }
}

fun main(args: Array<String>) {
    runApplication<AwsLambdaDemoApplication>(*args)
}