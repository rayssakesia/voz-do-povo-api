package voz_do_povo_api.controller

import org.bson.Document
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import voz_do_povo_api.controller.requests.Images
import voz_do_povo_api.controller.requests.PublicationData
import voz_do_povo_api.repository.VozDoPovoRepository
import voz_do_povo_api.service.ReportImageService
import java.time.Instant
import java.util.Base64

@RestController
@RequestMapping(value = ["/voz-do-povo"])
class ReportImageController(
    private val gridFs: ReactiveGridFsTemplate,
    private val service: ReportImageService,
    val vozDoPovoRepository: VozDoPovoRepository
) {

    @PostMapping("/reportImage/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun uploadImageById(
        @PathVariable id: String,
        @RequestPart("image") file: FilePart,
        request: ServerHttpRequest
    ): Mono<Map<String, Any>> {
        val baseUrl = "${request.uri.scheme}://${request.uri.host}:${request.uri.port}"

        val meta = Document("uploadedAt", Instant.now().toString())
            .append("contentType", file.headers().contentType?.toString())

        return gridFs.store(
            file.content(),
            file.filename(),
            MediaType.APPLICATION_OCTET_STREAM.toString(),
            meta
        ).flatMap { fileId ->
            val image = Images(
                id = fileId.toString(),
                url = "$baseUrl/images/${fileId}",
                contentType = file.headers().contentType?.toString(),
                filename = file.filename(),
                uploadedAt = Instant.now()
            )

            service.uploadImageReport(id, image)
                .flatMap {
                    Mono.just(
                        mapOf(
                            "publicationId" to id,
                            "image" to image
                        )
                    )
                }
        }
    }

    // TODO: Return the list of images associated with the report
    @GetMapping("/reportImage/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun findImageByReportId(@PathVariable id: String, response: ServerHttpResponse): Mono<Void> {
        val ct = MediaType.APPLICATION_OCTET_STREAM

        val imageId = getImageId(id).flatMap { publication ->
            Mono.just(publication.report.images?.id ?: throw NoSuchElementException("Image ID not found"))
        }.block() ?: throw NoSuchElementException("Image ID not found")

        return gridFs.findOne(Query(Criteria.where("_id").`is`(imageId)))
            .switchIfEmpty(Mono.error(NoSuchElementException("File not found")))
            .flatMap { f -> gridFs.getResource(f) }
            .flatMap { res ->
                DataBufferUtils.join(res.content)
                    .flatMap { dataBuffer ->
                        val bytes = ByteArray(dataBuffer.readableByteCount())
                        dataBuffer.read(bytes)
                        DataBufferUtils.release(dataBuffer)
                        val base64 = Base64.getEncoder().encodeToString(bytes)
                        val result = decodeToBase64(
                            body = mapOf("base64" to base64), response
                        )
                        return@flatMap result
                    }
            }
    }

    fun getImageId(id: String): Mono<PublicationData> {
        return vozDoPovoRepository.findById(id).flatMap { publication ->
            Mono.just(publication ?: throw NoSuchElementException("Image ID not found"))
        }
    }

    fun decodeToBase64(body: Map<String, String>, response: ServerHttpResponse): Mono<Void> {
        val base64 = body["base64"] ?: return Mono.error(IllegalArgumentException("Missing base64"))
        val bytes = Base64.getDecoder().decode(base64)

        response.headers.contentType = MediaType.IMAGE_JPEG
        response.headers.set("Content-Disposition", "inline; filename=\"decoded.jpg\"")

        val buffer = response.bufferFactory().wrap(bytes)
        return response.writeWith(Mono.just(buffer))
    }
}