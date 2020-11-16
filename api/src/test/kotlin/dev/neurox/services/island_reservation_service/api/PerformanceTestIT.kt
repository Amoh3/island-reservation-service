package dev.neurox.services.island_reservation_service.api

import io.restassured.RestAssured
import io.restassured.response.Response
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PerformanceTestIT {

    private lateinit var url: String

    @LocalServerPort
    private val port = 0

    @Before
    fun setup() {
        url = "http://localhost:$port"
    }

    private val numberOfConnections: Int = 100
    private val numberOfRequests: Int = 5000

    @Test
    fun testPerformance() {


        val executorService: ExecutorService = Executors.newFixedThreadPool(numberOfConnections)
        val calendarRequests: ArrayList<GetCalendarCallable> = ArrayList()

        for (x in 0 until numberOfRequests) {
            calendarRequests.add(GetCalendarCallable(url))
        }

        val startTime: Long = System.nanoTime()
        executorService.invokeAll(calendarRequests)
        val estimatedTimeSeconds: Long = (System.nanoTime() - startTime) / 1000000000

        println("**${numberOfRequests} requests took $estimatedTimeSeconds seconds")
        assert((estimatedTimeSeconds ) < 10)
    }
}

data class GetCalendarCallable(val url: String) : Callable<Response?> {

    override fun call(): Response {
        val resp: Response = RestAssured.given()
                .baseUri(url)
                .log().all()
                .header("Content-Type", "application/json")
                .queryParam("available", "TRUE")
                .queryParam("RANGE", "MONTHLY")
                .get("/campsite/calendar/dates")

        println("*******")
        resp.body().prettyPrint()
        return resp
    }

}