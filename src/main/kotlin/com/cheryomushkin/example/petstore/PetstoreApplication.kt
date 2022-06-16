package com.cheryomushkin.example.petstore

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PetstoreApplication

fun main(args: Array<String>) {
    runApplication<PetstoreApplication>(*args)
}
