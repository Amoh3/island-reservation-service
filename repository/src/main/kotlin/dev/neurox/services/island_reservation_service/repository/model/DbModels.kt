package dev.neurox.services.island_reservation_service.repository.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.IndexDirection
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate


@Document(collection = "Calendar")
data class IslandCalendar(
        @Id var id: String?,
        @Indexed(unique = true, direction = IndexDirection.DESCENDING) var reservedDay: LocalDate
)

@Document(collection = "Reservations")
data class Reservation(
        @Id var id: String?,
        @Indexed(unique = true) var reservationNumber: String,
        var client: Client,
        var startDate: LocalDate,
        @Indexed(direction = IndexDirection.DESCENDING) var endDate: LocalDate,
        var status: ReservationStatus
)

enum class ReservationStatus {
    BOOKED,
    CANCELLED,
}

data class Client(
        var firstName: String,
        var lastName: String,
        var email: String
)
