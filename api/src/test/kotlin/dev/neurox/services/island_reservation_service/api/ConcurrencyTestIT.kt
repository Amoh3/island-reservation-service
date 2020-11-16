package dev.neurox.services.island_reservation_service.api

import dev.neurox.services.island_reservation_service.model.Client
import dev.neurox.services.island_reservation_service.model.Reservation
import io.restassured.RestAssured
import io.restassured.response.Response
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConcurrencyTestIT {

    private lateinit var url: String

    @LocalServerPort
    private val port = 0

    private val numberOfConnections: Int = 20
    private val numberOfRequests: Int = 100

    @Before
    fun setup() {
        url = "http://localhost:$port"
    }

    @Test
    fun testConcurrency() {

        val executorService: ExecutorService = Executors.newFixedThreadPool(numberOfConnections)
        val createReservationRequests: ArrayList<CreateReservationCallable> = ArrayList()

        for (x in 0 until numberOfRequests) {
            val startDate: LocalDate = LocalDate.now().plusDays(((1 + (x * 2)) % 25).toLong())
            val endDate: LocalDate = startDate.plusDays(1)
            val reservation = Reservation(
                    Client("concurrency@test.ca", "Client$x", "LastName"),
                    startDate, endDate
            )
            createReservationRequests.add(CreateReservationCallable(url, reservation))
        }

        val responses: List<Future<Response?>> = executorService.invokeAll(createReservationRequests)
        var success = 0
        for (response in responses) {
            if (response.get()!!.statusCode == 200) {
                success++
            }
        }
        assert(success <= 12)
        println("success calls: $success")


    }


}

data class CreateReservationCallable(val url: String, val reservation: Reservation) : Callable<Response?> {

    override fun call(): Response {
        val resp: Response = RestAssured.given()
                .baseUri(url)
                .log().all()
                .header("Content-Type", "application/json")
                .body(reservation)
                .post("/reservations")

        println("\n*** Reservation of ${reservation.startDate} : ${reservation.endDate} with ${
            reservation.client
                    .firstName
        } responded with: ")
        resp.body().prettyPrint()
        return resp
    }

}