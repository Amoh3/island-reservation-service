package dev.neurox.services.island_reservation_service.repository.dao

import dev.neurox.services.island_reservation_service.repository.model.Reservation
import dev.neurox.services.island_reservation_service.repository.model.ReservationStatus
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ReservationRepository(
        private val repo: IReservationRepository,
        private val mongoTemplate: MongoTemplate,
        private val islandCalendarRepository: IslandCalendarRepository) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createReservation(reservation: Reservation, dates: ArrayList<LocalDate> = getList(reservation.startDate,
            reservation.endDate)): Reservation {
        if (!islandCalendarRepository.reserve(dates)) {
            logger.info("All or some of days of ${dates.forEach(::print)} is already reserved by " +
                    "someone else")
            throw ReservationException("Unable to reserve Island for desired time.")
        }
        try {
            return repo.save(reservation)
        } catch (e: Exception) {
            islandCalendarRepository.deleteReservation(dates)
            logger.error("Deleted reserved days:${dates.forEach(::print)} due to\n${e.message}")
            throw e
        }

    }

    fun updateReservation(oldReservation: Reservation, reservation: Reservation): Reservation {
        val isClientChanged: Boolean = oldReservation.client != reservation.client
        val isDateChanged: Boolean = oldReservation.startDate != reservation.startDate
                || oldReservation.endDate != reservation.endDate

        return if (isClientChanged && !isDateChanged) {
            repo.save(reservation)
        } else {

            val datesToAdd = getList(reservation.startDate, reservation.endDate)
            datesToAdd.removeAll(getList(oldReservation.startDate, oldReservation.endDate))

            val datesToDelete = getList(oldReservation.startDate, oldReservation.endDate)
            datesToDelete.removeAll(getList(reservation.startDate, reservation.endDate))

            val updatedRes = createReservation(reservation, datesToAdd)
            islandCalendarRepository.deleteReservation(datesToDelete)

            updatedRes
        }

    }

    fun cancelReservation(reservation: Reservation): Reservation {
        reservation.status = ReservationStatus.CANCELLED
        return mongoTemplate.save(reservation)
    }

    fun getReservation(reservationNumber: String, reservationStatus: ReservationStatus? = null): Reservation? {
        val query = Query().addCriteria(Reservation::reservationNumber isEqualTo reservationNumber)
        if (reservationStatus != null) {
            query.addCriteria(Reservation::status isEqualTo reservationStatus)
        }
        return mongoTemplate.findOne(query)
    }


    private fun getList(startDate: LocalDate, endDate: LocalDate): ArrayList<LocalDate> {
        val dates: ArrayList<LocalDate> = ArrayList()
        var date: LocalDate = startDate
        while (date.isBefore(endDate) || date.isEqual(endDate)) {
            dates.add(date)
            date = date.plusDays(1)
        }
        return dates
    }
}

class ReservationException(msg: String) : Exception(msg)