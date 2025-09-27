package voz_do_povo_api.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import voz_do_povo_api.controller.requests.PublicationData
import voz_do_povo_api.repository.VozDoPovoRepository
import java.util.UUID

@Service
class VozDoPovoService @Autowired constructor(val vozDoPovoRepository: VozDoPovoRepository){

    fun createReportPublication (publicationData: PublicationData) : Mono<PublicationData> {
        publicationData.id = UUID.randomUUID().toString()
        return vozDoPovoRepository.save(publicationData)
        }
    }
