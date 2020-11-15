package dev.neurox.services.island_reservation_service.controllers.exceptions

import com.fasterxml.jackson.databind.ObjectMapper
import dev.neurox.services.island_reservation_service.model.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.OffsetDateTime
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.ConstraintViolationException


sealed class ApiException(val msg: List<String>, val httpStatus: HttpStatus) : Exception(httpStatus.reasonPhrase)

class BadRequestException(msg: List<String>, httpStatus: HttpStatus = HttpStatus.BAD_REQUEST) : ApiException(msg, httpStatus)
class NotFoundException(msg: List<String>, httpStatus: HttpStatus = HttpStatus.NOT_FOUND) : ApiException(msg, httpStatus)
class ForbiddenException(msg: List<String>, httpStatus: HttpStatus = HttpStatus.FORBIDDEN) : ApiException(msg, httpStatus)
class UnauthorizedException(msg: List<String>, httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED) : ApiException(msg, httpStatus)
class InternalErrorException(msg: List<String>, httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR) : ApiException(msg,
        httpStatus)

@ControllerAdvice
class DefaultExceptionHandler {


    @ExceptionHandler(value = [ApiException::class])
    fun onApiException(ex: ApiException, request: HttpServletRequest, response: HttpServletResponse) {
        val error = ErrorResponse(
                OffsetDateTime.now().toString(),
                ex.httpStatus.value(),
                ex.httpStatus.reasonPhrase,
                ex.msg,
                request.servletPath
        )
        response.resetBuffer()
        response.status = ex.httpStatus.value()
        response.setHeader("Content-Type", "application/json")
        response.outputStream.print(ObjectMapper().writeValueAsString(error))

    }

    @ExceptionHandler(value = [ConstraintViolationException::class])
    fun onConstraintViolation(ex: ConstraintViolationException, request: HttpServletRequest, response: HttpServletResponse) {
        val exception = BadRequestException(object : ArrayList<String>() {
            init {
                addAll(ex.constraintViolations.map { it.message })
            }
        })
        onApiException(exception, request, response)
    }


    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    fun onHttpMessageNotReadableViolation(ex: HttpMessageNotReadableException, request: HttpServletRequest, response: HttpServletResponse) {
        val exception = BadRequestException(object : ArrayList<String>() {
            init {
                add(ex.message!!)
            }
        })
        onApiException(exception, request, response)
    }

}
