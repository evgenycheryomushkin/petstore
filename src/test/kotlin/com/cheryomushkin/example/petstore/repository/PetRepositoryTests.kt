package com.cheryomushkin.example.petstore.repository

import com.cheryomushkin.example.petstore.model.Pet
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.reactive.asPublisher
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PetRepositoryTests(
    @Autowired val petRepository: PetRepository
) {

    val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun `should create pet`() = runBlocking {
        val name = "lada"
        val category = "dog"
        val tags = listOf("dog")
        val photoUrls = listOf("http://some.url")
        val pet = Pet(name, category, tags, photoUrls)
        val res = petRepository.save(pet).awaitSingle()
        assertNotNull(res)
        assertNotNull(res.id)
        assertEquals(name, res.name)
        assertEquals(category, res.category)
        assertEquals(tags.size, res.tags.size)
        assertEquals(tags[0], res.tags[0])
        assertEquals(photoUrls.size, res.photoUrls.size)
        assertEquals(photoUrls[0], res.photoUrls[0])
        log.info("res id ${res.id}")
    }

    @Test
    fun `should find by tag`() {
        petRepository.deleteAll()
        val categoryDog = "dog"
        val categoryBird = "bird"
        val urls = listOf("htp://some.url")
        val dog = Pet(
            "Lada", categoryDog,
            listOf("animal", "dog"), urls
        )
        val owl = Pet(
            "Букля", categoryBird,
            listOf("bird", "night", "white"), urls
        )
        val owl2 = Pet(
            "Сычик", categoryBird,
            listOf("bird", "night", "brown"), urls
        )
        val bird = Pet(
            "Canary", categoryBird,
            listOf("bird", "day"), urls
        )
        val saved = petRepository.saveAll(flowOf(dog, owl, owl2, bird).asPublisher())
        Assertions.assertEquals(4, saved.count().block())
        val dogRes = petRepository.findByTags(listOf("dog"))
        Assertions.assertEquals(1, dogRes.count().block())
        val birdRes = petRepository.findByTags(listOf("bird"))
        Assertions.assertEquals(3, birdRes.count().block())
        val owlRes = petRepository.findByTags(listOf("bird", "night"))
        Assertions.assertEquals(2, owlRes.count().block())
    }
}