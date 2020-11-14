package dev.neurox.services.island_reservation_service.controllers

import dev.neurox.services.island_reservation_service.model.Reservation
import dev.neurox.services.island_reservation_service.repository.dao.ReservationRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import java.nio.charset.Charset
import kotlin.random.Random

@Controller
class ReservationsController(val reservationRepository: ReservationRepository) : ReservationsApi{

    override fun createReservation(reservation: Reservation): ResponseEntity<String> {
        return super.createReservation(reservation)
    }

    override fun getReservation(reservationId: String): ResponseEntity<Reservation> {
        return super.getReservation(reservationId)
    }

    override fun updateReservation(reservationId: String, transaction: String, reservation: Reservation?): ResponseEntity<Unit> {
        return super.updateReservation(reservationId, transaction, reservation)
    }

    fun getNewReservationNumber(): String {
        var reservationNumber: String = generatorReservationNumber(8)
        while(reservationRepository.getReservation(reservationNumber) != null){
            reservationNumber = generatorReservationNumber(8)
        }
        return reservationNumber
    }

    fun generatorReservationNumber(length: Int): String {

        val array = ByteArray(256)
        Random.nextBytes(array)

        val randomString = String(array, Charset.forName("UTF-8"))
        val r = StringBuffer()

        var size = 0
        for (element in randomString) {
            if (element in 'A'..'Z' || element in '0'..'9') {
                r.append(element)
                size++
                if(size == length){
                    break
                }
            }
        }

        // return the resultant string
        return r.toString()

    }



}