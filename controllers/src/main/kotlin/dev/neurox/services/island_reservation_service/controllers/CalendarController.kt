package dev.neurox.services.island_reservation_service.controllers

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import java.time.LocalDate

@Controller
class CalendarController : CalendarApi {
    override fun getCampsiteAvailability(available: Boolean, range: String?): ResponseEntity<List<LocalDate>> {
        return super.getCampsiteAvailability(available, range)
    }
}