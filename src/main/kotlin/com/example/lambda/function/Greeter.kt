package com.example.lambda.function

import java.util.function.Function

class Greeter : Function<String, String> {

    override fun apply(s: String): String {
        return "Hello $s, and welcome to Spring Cloud Function!!!"
    }
}