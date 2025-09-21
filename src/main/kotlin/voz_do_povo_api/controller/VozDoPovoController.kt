package voz_do_povo_api.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import voz_do_povo_api.controller.requests.PublicationRequest
import voz_do_povo_api.controller.requests.ReportAddressRequest
import voz_do_povo_api.controller.requests.ReportRequest
import voz_do_povo_api.controller.requests.UserRequest

@RestController
@RequestMapping(value = ["/voz-do-povo"])
class VozDoPovoController {

    @PostMapping("/publish")
    @ResponseStatus (HttpStatus.CREATED)
    fun publish(@RequestBody publicationRequest: PublicationRequest): PublicationRequest {
        return PublicationRequest(UserRequest(email = "joao@gmail.com"),
            ReportAddressRequest(
                number = "123",
                zipCode = "123",
                street ="123",
                complement = "123",
                city = "123",
                state = "123",
                country = "123"
            ),
            ReportRequest(report = "falta de iluminação na rua")
        )
    }

    @GetMapping("/email") //(/id user)
    @ResponseStatus (HttpStatus.OK)
    fun findPublications(): PublicationRequest {
        return PublicationRequest(UserRequest(email = "joao@gmail.com"),
            ReportAddressRequest(
                number = "123",
                zipCode = "123",
                street ="123",
                complement = "123",
                city = "123",
                state = "123",
                country = "123"
            ),
            ReportRequest(report = "falta de iluminação na rua")
        )
    }
}

//@Serializable
//data class Message (val name: String)
//
//@RestController
//class ControllerTeste {
//    val m: Message = Message(name = "ready")
//
//    @GetMapping("/users")
//    fun getAnswer(): Message {
//        return m
//    }
//}
