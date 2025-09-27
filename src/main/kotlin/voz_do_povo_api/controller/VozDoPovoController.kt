
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import voz_do_povo_api.controller.requests.PublicationData
import voz_do_povo_api.service.VozDoPovoService

@RestController
@RequestMapping(value = ["/voz-do-povo"])
class VozDoPovoController ( val vozDoPovoService: VozDoPovoService) {

    @PostMapping("/publish")
    @ResponseStatus(HttpStatus.CREATED)
    fun createReport(@RequestBody publicationData: PublicationData): Mono<PublicationData> {
        return vozDoPovoService.createReportPublication(publicationData)
    }
}
