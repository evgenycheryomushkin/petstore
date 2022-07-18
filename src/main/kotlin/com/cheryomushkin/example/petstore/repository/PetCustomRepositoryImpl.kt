package com.cheryomushkin.example.petstore.repository

import com.cheryomushkin.example.petstore.model.Pet
import kotlinx.coroutines.flow.Flow
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import reactor.core.publisher.Flux

class PetCustomRepositoryImpl(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) : PetCustomRepository {
    override fun findByTags(tags: List<String>): Flux<Pet> {
        val q = Query(Criteria.where("tags").all(tags))
        return reactiveMongoTemplate.find(q, Pet::class.java)
    }
}