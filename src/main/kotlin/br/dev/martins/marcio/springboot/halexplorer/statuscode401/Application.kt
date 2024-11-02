package br.dev.martins.marcio.springboot.halexplorer.statuscode401

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.mediatype.Affordances
import org.springframework.hateoas.server.mvc.andAffordances
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@RestController
class Controller {
    @GetMapping("/")
    fun index200() = ResponseEntity.ok(createEntityModel())

    @GetMapping("/unauthorized")
    fun unauthorized401() = ResponseEntity.status(401).body(createEntityModel())

    private fun createEntityModel(): EntityModel<Any> {
        return EntityModel.of(
            object {},
            linkTo<Controller> { index200() }.withSelfRel(),
            linkTo<Controller> { unauthorized401() }.withRel("unauthorized"),
            Link.of("http://localhost:8090/realms/your-realm/protocol/openid-connect/token")
                .withRel("authenticate")
                .andAffordances {
                    affordances.add(
                        Affordances.of(Link.of("http://localhost:8090/realms/your-realm/protocol/openid-connect/token"))
                            .afford(HttpMethod.POST)
                            .withInput(AuthenticationPayload::class.java)
                            .withInputMediaType(MediaType.APPLICATION_FORM_URLENCODED)
                            .withName("authenticate")
                            .build()
                            .stream().findFirst().orElseThrow()
                    )
                },
        )
    }

    @Suppress("PropertyName")
    data class AuthenticationPayload(
        var client_id: String = "api",
        var client_secret: String,
        var username: String,
        var password: String,
        var grant_type: String = "password",
    )
}
