package dev.neurox.services.island_reservation_service.controllers.utils

import dev.neurox.services.island_reservation_service.model.Reservation
import dev.neurox.services.island_reservation_service.repository.dao.ReservationRepository
import org.springframework.stereotype.Service
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.random.Random

@Service
class Utilities(val reservationRepository: ReservationRepository) {

    fun isReservationDatesValid(reservation: Reservation): ArrayList<String> {
        var errors: ArrayList<String> = ArrayList()

        if (reservation.endDate.isBefore(reservation.startDate)) {
            errors.add("Start date of reservation can not be after end date.")
        } else {
            if (ChronoUnit.DAYS.between(reservation.startDate, reservation.endDate) > 2) {
                errors.add("Reservation can not be for more than 3 days.")
            }
            if (reservation.startDate.isBefore(LocalDate.now().plusDays(1))) {
                errors.add("Reservation start date can not be before: ${LocalDate.now().plusDays(1)}")
            }
            if (reservation.endDate.isAfter(LocalDate.now().plusMonths(1))) {
                errors.add("Reservation end date can not be after: ${LocalDate.now().plusMonths(1)}")
            }
        }
        return errors
    }


    fun getNewReservationNumber(): String {
        var reservationNumber: String = generateReservationNumber()
        while (reservationRepository.getReservation(reservationNumber) != null) {
            reservationNumber = generateReservationNumber()
        }
        return reservationNumber
    }

    private fun generateReservationNumber(length: Int = 8): String {

        val array = ByteArray(256)
        Random.nextBytes(array)

        val randomString = String(array, Charset.forName("UTF-8"))
        val r = StringBuffer()

        var size = 0
        for (element in randomString) {
            if (element in 'A'..'Z' || element in '0'..'9') {
                r.append(element)
                size++
                if (size == length) {
                    break
                }
            }
        }

        // return the resultant string
        return r.toString()

    }

}