package dev.neurox.services.island_reservation_service.controllers

import dev.neurox.services.island_reservation_service.controllers.exceptions.BadRequestException
import dev.neurox.services.island_reservation_service.controllers.exceptions.ForbiddenException
import dev.neurox.services.island_reservation_service.controllers.exceptions.InternalErrorException
import dev.neurox.services.island_reservation_service.controllers.mappers.ReservationMapper
import dev.neurox.services.island_reservation_service.controllers.utils.Utilities
import dev.neurox.services.island_reservation_service.model.NewReservationResponse
import dev.neurox.services.island_reservation_service.model.Reservation
import dev.neurox.services.island_reservation_service.repository.dao.ReservationException
import dev.neurox.services.island_reservation_service.repository.dao.ReservationRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller

@Controller
class ReservationsController(val reservationRepository: ReservationRepository, val reservationMapper: ReservationMapper,
                             val utilities: Utilities) : ReservationsApi {

    override fun createReservation(reservation: Reservation): ResponseEntity<NewReservationResponse> {

        val messages = utilities.isReservationDatesValid(reservation)
        if (messages.size > 0) {
            throw BadRequestException(messages)
        }

        return try {
            ResponseEntity.ok(
                    NewReservationResponse(reservationRepository.createReservation(reservationMapper.map2dao
                    (reservation)).reservationNumber)
            )
        } catch (e: ReservationException) {
            messages.add(e.message!!)
            throw ForbiddenException(messages)
        } catch (e: Exception) {
            messages.add(e.message ?: "Something bad happened. See server logs for more details")
            throw InternalErrorException(messages)
        }
    }

    override fun getReservation(reservationId: String): ResponseEntity<Reservation> {
        return super.getReservation(reservationId)
    }

    override fun updateReservation(reservationId: String, transaction: String, reservation: Reservation?)
            : ResponseEntity<Unit> {
        return super.updateReservation(reservationId, transaction, reservation)
    }

}