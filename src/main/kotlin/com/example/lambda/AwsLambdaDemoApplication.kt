package com.example.lambda

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class AwsLambdaDemoApplication

fun main(args: Array<String>) {
    runApplication<AwsLambdaDemoApplication>(*args)
}