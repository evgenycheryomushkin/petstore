package com.cheryomushkin.example.petstore.exception

import com.cheryomushkin.example.petstore.exception.PetClinicException
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import java.util.*

@Service
class ExceptionService(
    private val messageSource: MessageSource
) {
    suspend fun throwException(code: String, vararg parameters: String) {
        throw PetClinicException(messageSource.getMessage(code, parameters, Locale.US))
    }
}