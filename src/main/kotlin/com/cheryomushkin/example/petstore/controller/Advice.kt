package com.cheryomushkin.example.petstore.controller

import com.cheryomushkin.example.petstore.exception.PetClinicException
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.*
import kotlin.NoSuchElementException

@ControllerAdvice
class Advice {
    @ExceptionHandler
    fun handlePetClinicException(ex: PetClinicException) =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.TEXT_PLAIN)
            .body(ex.message)

}