package dev.neurox.services.island_reservation_service.repository.dao

import dev.neurox.services.island_reservation_service.repository.model.IslandCalendar
import dev.neurox.services.island_reservation_service.repository.model.Reservation
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface IReservationRepository : MongoRepository<Reservation,String> {

}

@Repository
interface IIslandCalendarRepository : MongoRepository<IslandCalendar,String> {

}
