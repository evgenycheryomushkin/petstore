package com.cheryomushkin.example.petstore.controller

import com.cheryomushkin.example.petstore.converter.PetConverter
import com.cheryomushkin.example.petstore.exception.ExceptionService
import com.cheryomushkin.example.petstore.repository.PetRepository
import com.cheryomushkin.example.petstore.transport.PetDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class PetApiImpl(
    private val petConverter: PetConverter,
    private val petRepository: PetRepository,
    private val exceptionService: ExceptionService
): PetApi {
    override suspend fun addPet(petDto: PetDto): ResponseEntity<PetDto> {
        val pet = petConverter.petDtoToPet(petDto)
        val res = petRepository
            .save(pet)
            .map { petConverter.petToPetDto(it) }
            .awaitSingle()
        return ResponseEntity.status(HttpStatus.CREATED).body(res)
    }

    override suspend fun getPetById(petId: String): ResponseEntity<PetDto> {
        val pet = petRepository.findById(petId).awaitSingleOrNull()
        if (pet == null) exceptionService.throwException("record_missing", "Pet", petId)
        return  ResponseEntity.ok(petConverter.petToPetDto(pet!!))
    }

    override fun findPetsByTags(tags: List<String>): ResponseEntity<Flow<PetDto>> {
        return ResponseEntity.ok(
            petRepository
                .findByTags(tags)
                .map { petConverter.petToPetDto(it) }
                .asFlow()
        )
    }

    override suspend fun updatePet(petDto: PetDto): ResponseEntity<PetDto> {
        if (petDto.id == null) exceptionService.throwException("json_item_missing", "id")
        if (petRepository.findById(petDto.id!!).awaitSingleOrNull() == null)
            exceptionService.throwException("record_missing", "Pet", petDto.id)
        val res =  petRepository.save(
            petConverter.petDtoToPet(petDto)
        ).awaitSingle()
        return ResponseEntity.ok().body(
            petConverter.petToPetDto(res)
        )
    }

    override suspend fun deletePet(petId: String, apiKey: String?): ResponseEntity<Unit> {
        petRepository.deleteById(petId).awaitSingleOrNull()
        return ResponseEntity.ok(null)
    }
}