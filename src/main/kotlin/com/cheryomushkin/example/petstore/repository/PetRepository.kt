package com.cheryomushkin.example.petstore.repository

import com.cheryomushkin.example.petstore.model.Pet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface PetRepository: ReactiveMongoRepository<Pet, String>, PetCustomRepository {
    fun save(pet:Pet): Mono<Pet>
}