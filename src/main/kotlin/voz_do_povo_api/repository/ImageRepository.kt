package voz_do_povo_api.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import voz_do_povo_api.controller.requests.PublicationData

@Repository
interface ImageRepository : ReactiveCrudRepository <PublicationData, String>