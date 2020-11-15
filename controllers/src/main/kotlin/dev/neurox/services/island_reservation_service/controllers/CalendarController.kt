package dev.neurox.services.island_reservation_service.controllers

import dev.neurox.services.island_reservation_service.model.Range
import dev.neurox.services.island_reservation_service.repository.dao.IslandCalendarRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import java.time.LocalDate

@Controller
class CalendarController(val islandCalendarRepository: IslandCalendarRepository) : CalendarApi {

    override fun getCampsiteAvailability(available: Boolean, range: Range?): ResponseEntity<List<LocalDate>> {

        val startDate = LocalDate.now().plusDays(1)
        val endDate = if (Range.WEEK == range) {
            startDate.plusDays(7)
        } else {
            startDate.plusMonths(1)
        }

        val dates: List<LocalDate> = if (available) {
            islandCalendarRepository.findFreeDays(startDate, endDate)
        } else {
            islandCalendarRepository.findReservedDays(startDate, endDate)
        }

        return ResponseEntity(dates, HttpStatus.OK)

    }
}