package voz_do_povo_api.controller

import org.bson.Document
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import voz_do_povo_api.controller.requests.Images
import voz_do_povo_api.service.ReportImageService
import java.time.Instant

@RestController
@RequestMapping(value = ["/voz-do-povo"])
class ReportImageController(
    private val gridFs: ReactiveGridFsTemplate,
    private val service: ReportImageService
) {

    @PostMapping("/images/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
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
                id = fileId.toHexString(),
                url = "$baseUrl/images/${fileId.toHexString()}",
                contentType = file.headers().contentType?.toString(),
                filename = file.filename(),
                uploadedAt = Instant.now()
            )

            service.uploadImageReport(id, image)
                .flatMap {
                    Mono.just(mapOf(
                        "publicationId" to id,
                        "image" to image
                    ))
                }
        }
    }






//    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
//    fun uploadImage(
//        @RequestPart("image") file: FilePart,
//        response: ServerHttpResponse
//    ): Mono<Void> {
//        val ct = MediaType.APPLICATION_OCTET_STREAM
//        val meta = Document()
//            .append("uploadedAt", Instant.now().toString())
//            .append("source", "api")
//            .append("contentType", ct.toString())
//
//        return gridFs.store(
//            file.content(),
//            file.filename(),
//            ct.toString(),
//            meta
//        )
//            .flatMap { id -> gridFs.findOne(Query(Criteria.where("_id").`is`(id))) }
//            .switchIfEmpty(Mono.error(NoSuchElementException("File not found")))
//            .flatMap { f -> gridFs.getResource(f) }
//            .flatMap { res ->
//                DataBufferUtils.join(res.content)
//                    .flatMap { dataBuffer ->
//                        val bytes = ByteArray(dataBuffer.readableByteCount())
//                        dataBuffer.read(bytes)
//                        DataBufferUtils.release(dataBuffer)
//                        val base64 = Base64.getEncoder().encodeToString(bytes)
//                        val result = decodeToBase64(body = mapOf("base64" to base64), response)
//                        return@flatMap result
//                    }
//            }
//    }
//
//    fun decodeToBase64(body: Map<String, String>, response: ServerHttpResponse): Mono<Void> {
//        val base64 = body["base64"] ?: return Mono.error(IllegalArgumentException("Missing base64"))
//        val bytes = Base64.getDecoder().decode(base64)
//
//        response.headers.contentType = MediaType.IMAGE_JPEG
//        response.headers.set("Content-Disposition", "inline; filename=\"decoded.jpg\"")
//
//        val buffer = response.bufferFactory().wrap(bytes)
//        return response.writeWith(Mono.just(buffer))
//    }

//    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
//    fun uploadImageAndReturnBase64(@RequestPart("image") file: FilePart): Mono<Void> {
//
//        val meta = Document()
//            .append("uploadedAt", Instant.now().toString())
//            .append("source", "api")
//            .append("contentType", file.headers().contentType?.toString() ?: "application/octet-stream")
//
//        return gridFs.store(
//            file.content(),                     // Flux<DataBuffer> (stream, sem carregar tudo na RAM)
//            file.filename(),
//            (file.headers().contentType ?: MediaType.APPLICATION_OCTET_STREAM).toString(),
//            meta
//
//        ).flatMap { id ->
//            gridFs.findOne(Query(Criteria.where("_id").`is`(id)))
//                .switchIfEmpty(Mono.error(NoSuchElementException("File not found")))
//                .flatMap { file -> gridFs.getResource(file) }
//                .flatMap { res ->
//                    val ct = MediaType.APPLICATION_OCTET_STREAM
//                    val base64Mono = DataBufferUtils.join(res.content) // junta todos os buffers em memória
//                        .map { dataBuffer ->
//                            val bytes = ByteArray(dataBuffer.readableByteCount())
//                            dataBuffer.read(bytes)
//                            DataBufferUtils.release(dataBuffer) // libera memória
//                            Base64.getEncoder().encodeToString(bytes)
//                        }
//                    base64Mono.map { base64 ->
//                        mapOf(
//                            "id" to id.toHexString(),
//                            "filename" to res.filename,
//                            "contentType" to ("application/octet-stream"),
//                            "base64" to base64,
//                            "url" to "/images/$id"
//                        )
//                    }
//                }
//        }
//    }


//@GetMapping
//fun list(
//    @RequestParam(required = false) filename: String?
//): Flux<Map<String, Any?>> {
//    val query = if (filename.isNullOrBlank()) Query() else Query(Criteria.where("filename").`is`(filename))
//    return gridFs.find(query).map { f ->
//        mapOf(
//            "id" to f.objectId?.toHexString(),
//            "filename" to f.filename,
//            "length" to f.length,
//            "uploadDate" to f.uploadDate
//        )
//    }
//}
////
//    @DeleteMapping("/{id}")
//    fun delete(@PathVariable id: String): Mono<Map<String, String>> {
//        val oid = ObjectId(id)
//        return gridFs.delete(Query(Criteria.where("_id").`is`(oid)))
//            .thenReturn(mapOf("deleted" to id))
//    }


//
//    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
//    fun uploadImageX(
//        @RequestPart("image") file: FilePart,
//        response: ServerHttpResponse
//    ): Mono<Void> {
//        val ct = MediaType.APPLICATION_OCTET_STREAM
//        val meta = Document()
//            .append("uploadedAt", Instant.now().toString())
//            .append("source", "api")
//            .append("contentType", ct.toString())
//
//        return gridFs.store(
//            file.content(),
//            file.filename(),
//            ct.toString(),
//            meta
//        )
//            .flatMap { id -> gridFs.findOne(Query(Criteria.where("_id").`is`(id))) }
//            .switchIfEmpty(Mono.error(NoSuchElementException("File not found")))
//            .flatMap { f -> gridFs.getResource(f) }
//            .flatMap { res ->
//                DataBufferUtils.join(res.content)
//                    .flatMap { dataBuffer ->
//                        val bytes = ByteArray(dataBuffer.readableByteCount())
//                        dataBuffer.read(bytes)
//                        DataBufferUtils.release(dataBuffer)
//                        val base64 = Base64.getEncoder().encodeToString(bytes)
//                        var idF = gridFs.findOne(Query(Criteria.where("_id"))).toString()
//
//                        val r: RImage = RImage(
//                            image =  decodeToBase64(body = mapOf("base64" to base64), response) ,
//                            data = Retorn( contentType = ct.toString(), filename = res.filename, id = idF))
//                        return@flatMap r
//                        }
//                    }
//            }
//
//
//    data class  Retorn( val contentType: String, val filename: String, val id : String)
//    data class  RImage(val image: Mono<Void>, val data: Retorn)
}