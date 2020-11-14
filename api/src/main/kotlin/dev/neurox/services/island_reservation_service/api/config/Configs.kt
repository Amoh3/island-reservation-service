package dev.neurox.services.island_reservation_service.api.config


import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.util.UriComponentsBuilder
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.paths.Paths
import springfox.documentation.spring.web.paths.RelativePathProvider
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.sql.Date
import java.time.LocalDate
import java.time.OffsetDateTime
import javax.servlet.ServletContext

@Configuration
@EnableSwagger2
class SwaggerConfig {
    fun apiInfo(): ApiInfo? {
        return ApiInfoBuilder()
                .title("Island Reservation API")
                .description("Backend to manage Island Reservations")
                .license("")
                .licenseUrl("http://unlicense.org")
                .termsOfServiceUrl("")
                .version("1.0.0")
                .contact(Contact("Abdelrahman Mohamed", "", "Abdelrahman@neurox.dev"))
                .build()
    }

    @Bean
    fun customImplementation(servletContext: ServletContext?, @Value("\${openapi.islandReservation.base-path:}") basePath: String): Docket? {
        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("dev.neurox.services.island_reservation_service.controllers"))
                .build()
                .pathProvider(BasePathAwareRelativePathProvider(servletContext, basePath))
                .directModelSubstitute(LocalDate::class.java, Date::class.java)
                .directModelSubstitute(OffsetDateTime::class.java, java.util.Date::class.java)
                .apiInfo(apiInfo())
    }

    internal class BasePathAwareRelativePathProvider(servletContext: ServletContext?, private val basePath: String) : RelativePathProvider(servletContext) {
        override fun applicationPath(): String {
            return Paths.removeAdjacentForwardSlashes(UriComponentsBuilder.fromPath(super.applicationPath()).path(basePath).build().toString())
        }

        override fun getOperationPath(operationPath: String): String {
            val uriComponentsBuilder = UriComponentsBuilder.fromPath("/")
            return Paths.removeAdjacentForwardSlashes(
                    uriComponentsBuilder.path(operationPath.replaceFirst("^" + basePath.toRegex(), "")).build().toString())
        }
    }
}

@Configuration
@ComponentScan("dev.neurox.services.island_reservation_service")
class SpringConfig {

}
