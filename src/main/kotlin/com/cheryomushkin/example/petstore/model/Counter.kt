package com.cheryomushkin.example.petstore.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Counter(
    @Id var id: String
) {
    var value: Long = 0
}