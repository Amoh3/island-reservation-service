package dev.neurox.services.island_reservation_service.controllers

import dev.neurox.services.island_reservation_service.model.Reservation
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller

@Controller
class ReservationsController : ReservationsApi{
    override fun createReservation(reservation: Reservation): ResponseEntity<String> {
        return super.createReservation(reservation)
    }

    override fun getReservation(reservationId: String): ResponseEntity<Reservation> {
        return super.getReservation(reservationId)
    }

    override fun updateReservation(reservationId: String, transaction: String, reservation: Reservation?): ResponseEntity<Unit> {
        return super.updateReservation(reservationId, transaction, reservation)
    }
}