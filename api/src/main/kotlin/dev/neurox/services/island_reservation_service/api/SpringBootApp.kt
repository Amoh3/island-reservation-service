package dev.neurox.services.island_reservation_service.api


import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@SpringBootApplication
@EnableMongoRepositories(basePackages = ["dev.neurox.services.island_reservation_service.repository"])
class SpringBootApp

fun main(args: Array<String>) {
    SpringApplication.run(SpringBootApp::class.java, *args)
}


@Controller
class SwaggerController {
    @RequestMapping("/")
    fun index(): String {
        return "redirect:swagger-ui.html"
    }
}