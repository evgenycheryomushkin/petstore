package com.cheryomushkin.example.petstore.controller

import com.cheryomushkin.example.petstore.converter.PetConverter
import com.cheryomushkin.example.petstore.model.Pet
import com.cheryomushkin.example.petstore.repository.PetRepository
import com.cheryomushkin.example.petstore.exception.ExceptionService
import com.cheryomushkin.example.petstore.exception.PetClinicException
import com.cheryomushkin.example.petstore.transport.PetDto
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.TestInstance
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

private const val CATEGORY = "dog"
private const val NAME = "Lada"

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class PetApiImplTest() {
    private val exceptionService = mockk<ExceptionService>()
    private val petRepository = mockk<PetRepository>()
    private val petApi = PetApiImpl(PetConverter(exceptionService), petRepository, exceptionService)
    private val advice = Advice()
    private val client = WebTestClient.bindToController(petApi, advice).build()

    init {
        coEvery { exceptionService.throwException(any(), any()) } throws PetClinicException("text")
        coEvery { exceptionService.throwException(any(), any(), any()) } throws PetClinicException("text")
    }

    @Test
    fun `should store pet to database`() {
        val petSlot = slot<Pet>()
        val tags = listOf("dog", "laika")
        val urls = listOf("http://some.url")
        every { petRepository.save(capture(petSlot)) } returns Mono.just(Pet(NAME, CATEGORY, tags, urls))
        val createPetBody = PetDto(CATEGORY, NAME, urls, null, tags)

        client.post().uri("/v2/pet")
            .accept(MediaType.APPLICATION_JSON).bodyValue(createPetBody)
            .exchange()
            .expectStatus().isCreated

        assertTrue(petSlot.isCaptured)
        assertEquals(CATEGORY, petSlot.captured.category)
        assertEquals(NAME, petSlot.captured.name)
        assertEquals(2, petSlot.captured.tags.size)
        assertEquals(tags[0], petSlot.captured.tags[0])
        assertEquals(tags[1], petSlot.captured.tags[1])
        assertEquals(1, petSlot.captured.photoUrls.size)
        assertEquals(urls[0], petSlot.captured.photoUrls[0])
    }

    @Test
    fun `should find pet by tags`() {
        val tagsListSlot = slot<List<String>>()
        val category = "owl"
        val urls = listOf("http://some.url")
        val tags = listOf("owl")
        val pet1Name = "Букля"
        val pet2Name = "Сычик"
        every { petRepository.findByTags(capture(tagsListSlot)) } returns Flux.just(
            Pet(pet1Name, category, tags, urls),
            Pet(pet2Name, category, tags, urls)
        )

        val result = client.get().uri("/v2/pet/findByTags?tags=owl")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult(Pet::class.java)

        assertTrue(tagsListSlot.isCaptured)
        assertEquals(1, tagsListSlot.captured.size)
        assertEquals(tags[0], tagsListSlot.captured[0])

        val pets = result.responseBody.collectList().block()
        assertNotNull(pets)

        assertEquals(2, pets!!.size)
        assertEquals(pet1Name, pets[0].name)
        assertEquals(category, pets[0].category)
        assertEquals(1, pets[0].tags.size)
        assertEquals(tags[0], pets[0].tags[0])

        assertEquals(pet2Name, pets[1].name)
        assertEquals(category, pets[1].category)
        assertEquals(1, pets[1].tags.size)
        assertEquals(tags[0], pets[1].tags[0])
    }

    @Test
    fun `should not find pet by missing tags`() {
        every { petRepository.findByTags(any()) } returns Flux.empty()

        val result = client.get().uri("/v2/pet/findByTags?tags=owl")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult(Pet::class.java)

        val pets = result.responseBody.collectList().block()
        assertNotNull(pets)

        assertEquals(0, pets!!.size)
    }

    @Test
    fun `should update pet by PUT method`() {
        val petSlot = slot<Pet>()
        val tags = listOf("dog", "laika")
        val urls = listOf("http://some.url")
        val objectId = ObjectId()
        val pet = Pet(NAME, CATEGORY, tags, urls)
        every { petRepository.save(capture(petSlot)) } returns Mono.just(
            pet
        )
        every { petRepository.findById(objectId.toHexString()) } returns Mono.just(pet)

        val res = client.put().uri("/v2/pet")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(PetDto(CATEGORY, NAME, urls, objectId.toHexString(), tags, null))
            .exchange()
            .expectStatus().isOk
            .returnResult(Pet::class.java)

        val list = res.responseBody.collectList().block()
        assertNotNull(list)
        assertEquals(1, list!!.size)

        assertTrue(petSlot.isCaptured)
        assertEquals(NAME, petSlot.captured.name)
        assertEquals(CATEGORY, petSlot.captured.category)
        assertEquals(objectId, petSlot.captured.id)
        assertEquals(tags.size, petSlot.captured.tags.size)
        assertEquals(tags[0], petSlot.captured.tags[0])
        assertEquals(tags[1], petSlot.captured.tags[1])
        assertEquals(urls.size, petSlot.captured.photoUrls.size)
        assertEquals(urls[0], petSlot.captured.photoUrls[0])
    }

    @Test
    fun `should error update pet by PUT method when no object id is supplied`() {
        val tags = listOf("dog", "laika")
        val urls = listOf("http://some.url")

        val res = client.put().uri("/v2/pet")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(PetDto(CATEGORY, NAME, urls, null, tags, null))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `should error update pet by PUT method when object id is missing in database`() {
        val tags = listOf("dog", "laika")
        val urls = listOf("http://some.url")
        val objectId = ObjectId()
        val idSlot = slot<String>()
        every { petRepository.findById(capture(idSlot)) } returns Mono.empty()

        val res = client.put().uri("/v2/pet")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(PetDto(CATEGORY, NAME, urls, objectId.toHexString(), tags, null))
            .exchange()
            .expectStatus().isBadRequest

        assertTrue(idSlot.isCaptured)
        assertEquals(objectId.toHexString(), idSlot.captured)
    }

    @Test
    fun `should delete pet`() {
        val id = ObjectId()
        every { petRepository.deleteById(id.toHexString()) } returns Mono.empty()

        client.delete().uri("/v2/pet/${id.toHexString()}")
            .exchange()
            .expectStatus().isOk
            .returnResult(Void::class.java)
            .responseBody
            .blockFirst()
    }
}