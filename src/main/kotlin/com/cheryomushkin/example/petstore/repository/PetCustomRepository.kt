package com.cheryomushkin.example.petstore.repository

import com.cheryomushkin.example.petstore.model.Pet
import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Flux

interface PetCustomRepository {
    fun findByTags(tags: List<String>): Flux<Pet>
}
