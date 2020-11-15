package dev.neurox.services.island_reservation_service.controllers.mappers

import dev.neurox.services.island_reservation_service.controllers.utils.Utilities
import dev.neurox.services.island_reservation_service.model.Client
import dev.neurox.services.island_reservation_service.model.Reservation
import dev.neurox.services.island_reservation_service.repository.model.ReservationStatus
import org.springframework.stereotype.Service
import dev.neurox.services.island_reservation_service.repository.model.Reservation as DaoReservation

@Service
class ReservationMapper(val clientMapper: ClientMapper, val utilities: Utilities) {

    fun map2dom(dao: DaoReservation): Reservation {
        return Reservation(
                clientMapper.map2dom(dao.client),
                dao.startDate,
                dao.endDate
        )
    }

    fun map2dao(dom: Reservation, reservationNumber: String? = null,  reservationStatus: ReservationStatus =
            ReservationStatus.BOOKED): DaoReservation {

        return DaoReservation(
                null,
                reservationNumber ?: utilities.getNewReservationNumber(),
                clientMapper.map2dao(dom.client),
                dom.startDate,
                dom.endDate,
                reservationStatus
        )
    }

}

@Service
class ClientMapper {

    fun map2dom(dao: dev.neurox.services.island_reservation_service.repository.model.Client): Client {
        return Client(dao.email, dao.firstName, dao.lastName)
    }

    fun map2dao(dom: Client): dev.neurox.services.island_reservation_service.repository.model.Client {
        return dev.neurox.services.island_reservation_service.repository.model.Client(
                dom.email, dom.firstName, dom.lastName)
    }

}