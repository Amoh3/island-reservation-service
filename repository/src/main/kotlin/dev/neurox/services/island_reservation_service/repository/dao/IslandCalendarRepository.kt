package dev.neurox.services.island_reservation_service.repository.dao

import dev.neurox.services.island_reservation_service.repository.model.IslandCalendar
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.*
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.stream.Collectors


@Component
class IslandCalendarRepository(private val repo: IIslandCalendarRepository, private val mongoTemplate: MongoTemplate) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun reserve(dates: ArrayList<LocalDate>): Boolean {
        val datesToRevert: ArrayList<IslandCalendar> = ArrayList()
        try {
            for (date in dates) {
                datesToRevert.add(repo.save(IslandCalendar(null, date)))
                logger.info(date.toString() + "reserved!")
            }
        } catch (e: Exception) {
            for (dateToRevert in datesToRevert) {
                repo.delete(dateToRevert)
                logger.info(dateToRevert.reservedDay.toString() + "rolled back!")
            }
            return false
        }
        return true
    }

    fun findReservedDays(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        val query = Query(Criteria().andOperator(IslandCalendar::reservedDay gte (startDate),
                IslandCalendar::reservedDay lte (endDate)))
        val reservedDays: List<IslandCalendar> = mongoTemplate.find(query)
        return reservedDays.map { it.reservedDay }
    }

    fun findFreeDays(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        val freeDays: ArrayList<LocalDate> =
                ArrayList(startDate.datesUntil(endDate).collect(Collectors.toList()))
        freeDays.removeAll(findReservedDays(startDate, endDate))
        return freeDays
    }

    fun deleteReservation(dates: ArrayList<LocalDate>) {
        val query = Query().addCriteria(IslandCalendar::reservedDay inValues dates)
        mongoTemplate.remove(query)
        logger.info("deleted reserved days: ${dates.forEach(::print)}")
    }


}