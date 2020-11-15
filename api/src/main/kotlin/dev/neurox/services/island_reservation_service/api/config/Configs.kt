package dev.neurox.services.island_reservation_service.api.config


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import springfox.documentation.swagger.web.SwaggerResource
import springfox.documentation.swagger.web.SwaggerResourcesProvider
import springfox.documentation.swagger2.annotations.EnableSwagger2


@Configuration
@EnableSwagger2
class SwaggerConfig {
    @Primary
    @Bean
    fun swaggerResourcesProvider(): SwaggerResourcesProvider? {
        return SwaggerResourcesProvider {
            val wsResource = SwaggerResource()
            wsResource.name = "Documentation"
            wsResource.swaggerVersion = "2.0"
            wsResource.location = "/swagger.yaml"
            listOf(wsResource)
        }
    }
}

@Configuration
@ComponentScan("dev.neurox.services.island_reservation_service")
class SpringConfig
