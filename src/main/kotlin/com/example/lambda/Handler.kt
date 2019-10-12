package com.example.lambda

import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler


class Handler : SpringBootRequestHandler<String, String>(AwsLambdaDemoApplication::class.java)