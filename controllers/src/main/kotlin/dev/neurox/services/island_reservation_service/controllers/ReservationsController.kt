package dev.neurox.services.island_reservation_service.controllers

import dev.neurox.services.island_reservation_service.controllers.exceptions.BadRequestException
import dev.neurox.services.island_reservation_service.controllers.exceptions.ForbiddenException
import dev.neurox.services.island_reservation_service.controllers.exceptions.InternalErrorException
import dev.neurox.services.island_reservation_service.controllers.exceptions.NotFoundException
import dev.neurox.services.island_reservation_service.controllers.mappers.ReservationMapper
import dev.neurox.services.island_reservation_service.controllers.utils.Utilities
import dev.neurox.services.island_reservation_service.model.NewReservationResponse
import dev.neurox.services.island_reservation_service.model.Reservation
import dev.neurox.services.island_reservation_service.model.UpdateTransaction
import dev.neurox.services.island_reservation_service.repository.dao.ReservationException
import dev.neurox.services.island_reservation_service.repository.dao.ReservationRepository
import dev.neurox.services.island_reservation_service.repository.model.ReservationStatus
import org.springframework.http.HttpStatus
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

    override fun getReservation(reservationId: String):
            ResponseEntity<Reservation> {
        val reservationDao = reservationRepository.getReservation(reservationId) ?: throw NotFoundException(
                object : ArrayList<String>() {
                    init {
                        add("Can not find reservation with reservation number: $reservationId")
                    }
                }
        )
        return ResponseEntity.ok(reservationMapper.map2dom(reservationDao))
    }

    override fun updateReservation(reservationId: String, transaction: UpdateTransaction, reservation: Reservation?)
            : ResponseEntity<Unit> {
        var messages: ArrayList<String> = ArrayList()
        if (UpdateTransaction.CANCEL == transaction) {
            if (reservation != null) {
                messages.add("Request body has to be empty to cancel the reservation")
                throw BadRequestException(messages)
            }
            val reservationDao = reservationRepository.getReservation(reservationId, ReservationStatus.BOOKED)
            if (reservationDao == null) {
                messages.add("Cannot find an active reservation with that ID to cancel")
                throw NotFoundException(messages)
            }
            reservationRepository.cancelReservation(reservationDao)
            return ResponseEntity(HttpStatus.OK)
        } else {
            if (reservation == null) {
                messages.add("Request body can not be empty to modify the reservation")
                throw BadRequestException(messages)
            }
            val oldReservationDao = reservationRepository.getReservation(reservationId, ReservationStatus.BOOKED)
            if (oldReservationDao == null) {
                messages.add("Cannot find an active reservation with that ID to modify")
                throw NotFoundException(messages)
            }
            messages = utilities.isReservationDatesValid(reservation)
            if (messages.size > 0) {
                throw BadRequestException(messages)
            }


            val newReservationDao = reservationMapper.map2dao(reservation, reservationId)
            newReservationDao.id = oldReservationDao.id
            if (oldReservationDao == newReservationDao) {
                messages.add("Nothing to modify")
                throw BadRequestException(messages)
            }

            reservationRepository.updateReservation(oldReservationDao, newReservationDao)
            return ResponseEntity(HttpStatus.OK)

        }
    }

}