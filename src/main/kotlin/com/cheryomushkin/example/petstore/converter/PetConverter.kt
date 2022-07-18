package com.cheryomushkin.example.petstore.converter

import com.cheryomushkin.example.petstore.model.Pet
import com.cheryomushkin.example.petstore.exception.ExceptionService
import com.cheryomushkin.example.petstore.transport.PetDto
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

@Component
class PetConverter(
    private val exceptionService: ExceptionService
) {
    suspend fun petDtoToPet(petDto: PetDto): Pet {
        val pet = Pet(petDto.name, petDto.category, petDto.tags ?: emptyList(), petDto.photoUrls)
        if (petDto.id != null) pet.id = ObjectId(petDto.id)
        return pet
    }

    fun petToPetDto(pet: Pet) = PetDto(
        pet.category, pet.name, pet.photoUrls,
        pet.id.toHexString(), pet.tags,null
    )
}
