package voz_do_povo_api.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import voz_do_povo_api.Utils.ReportCategory
import voz_do_povo_api.Utils.getMessageByCategoryReport
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
        return PublicationRequest(UserRequest(name = "joao", email = "joao@gmail.com"),
            ReportAddressRequest(
                number = "1020",
                zipCode = "60175-055",
                street = "Avenida Santos Dumont",
                complement = "Apartamento 1203, Bloco B",
                city = "Fortaleza",
                state = "CE",
                country = "Brasil"
            ),
            ReportRequest(
                reportCategory = getMessageByCategoryReport(ReportCategory.URBAN_INFRASTRUCTURE),
                report = "falta de iluminação na rua",
                imagesUrl = listOf("https://share.google/images/DFEuJbIM6aPaPJV6w", "https://share.google/images/DFEuJbIM6aPaPJV6w")
            )
        )
    }

    @GetMapping("/email") //(/id user)
    @ResponseStatus (HttpStatus.OK)
    fun findPublications(): PublicationRequest {
        return PublicationRequest(UserRequest(email = "joao@gmail.com"),
            ReportAddressRequest(
                number = "1020",
                zipCode = "60175-055",
                street = "Avenida Santos Dumont",
                complement = "Apartamento 1203, Bloco B",
                city = "Fortaleza",
                state = "CE",
                country = "Brasil"
            ),
            ReportRequest(
                reportCategory = getMessageByCategoryReport(ReportCategory.URBAN_INFRASTRUCTURE),
                report = "falta de iluminação na rua",
                imagesUrl = listOf("https://share.google/images/DFEuJbIM6aPaPJV6w", "https://share.google/images/DFEuJbIM6aPaPJV6w")
            )
        )
    }

    @GetMapping("/id") //(/id user)
    @ResponseStatus (HttpStatus.OK)
    fun findPublication(): PublicationRequest {
        return PublicationRequest(UserRequest(email = "joao@gmail.com"),
            ReportAddressRequest(
                number = "1020",
                zipCode = "60175-055",
                street = "Avenida Santos Dumont",
                complement = "Apartamento 1203, Bloco B",
                city = "Fortaleza",
                state = "CE",
                country = "Brasil"
            ),
            ReportRequest(
                reportCategory = getMessageByCategoryReport(ReportCategory.URBAN_INFRASTRUCTURE),
                report = "falta de iluminação na rua",
                imagesUrl = listOf("https://share.google/images/DFEuJbIM6aPaPJV6w", "https://share.google/images/DFEuJbIM6aPaPJV6w")
            )
        )
    }

    @PutMapping("/publication/id") //(/id user)
    @ResponseStatus (HttpStatus.CREATED)
    fun changePublication(): PublicationRequest {
        return PublicationRequest(UserRequest(email = "joao@gmail.com"),
            ReportAddressRequest(
                number = "1020",
                zipCode = "60175-055",
                street = "Avenida Santos Dumont",
                complement = "Apartamento 1203, Bloco B",
                city = "Fortaleza",
                state = "CE",
                country = "Brasil"
            ),
            ReportRequest(
                reportCategory = getMessageByCategoryReport(ReportCategory.URBAN_INFRASTRUCTURE),
                report = "falta de iluminação na rua",
                imagesUrl = listOf("https://share.google/images/DFEuJbIM6aPaPJV6w", "https://share.google/images/DFEuJbIM6aPaPJV6w")
            )
        )
    }

}