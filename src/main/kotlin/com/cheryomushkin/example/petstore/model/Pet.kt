package com.cheryomushkin.example.petstore.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigInteger

@Document
class Pet(
    var name: String,
    @Indexed var category: String,
    @Indexed var tags: List<String>,
    var photoUrls: List<String>
    ) {
    @Id
    var id: ObjectId = ObjectId.get()
}