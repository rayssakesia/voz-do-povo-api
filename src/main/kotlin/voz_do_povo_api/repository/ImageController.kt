package voz_do_povo_api.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.web.bind.annotation.*
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.Base64

@RestController
@RequestMapping("/images")
class ImageController(
    private val gridFs: ReactiveGridFsTemplate
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(@RequestPart("image") file: FilePart): Mono<Map<String, Any>> {
        val meta = org.bson.Document()
            .append("uploadedAt", Instant.now().toString())
            .append("source", "api")
            .append("contentType", file.headers().contentType?.toString() ?: "application/octet-stream")

        return gridFs.store(
            file.content(),                     // Flux<DataBuffer> (stream, sem carregar tudo na RAM)
            file.filename(),
            (file.headers().contentType ?: MediaType.APPLICATION_OCTET_STREAM).toString(),
            meta
        ).map { id ->
            val downloadUrl = "http://localhost:8080/images/$id"
            mapOf(
                "id" to id.toHexString(),
                "filename" to file.filename(),
                "url" to downloadUrl
            )
        }
    }

    @GetMapping("/{id}")
    fun download(@PathVariable id: String, response: ServerHttpResponse): Mono<Void> {
        val oid = ObjectId(id)
        return gridFs.findOne(Query(Criteria.where("_id").`is`(oid)))
            .switchIfEmpty(Mono.error(NoSuchElementException("File not found")))
            .flatMap { file -> gridFs.getResource(file) }
            .flatMap { res ->
                val ct = MediaType.APPLICATION_OCTET_STREAM
                response.headers.contentType = ct
                response.headers.set("Content-Disposition", "inline; filename=\"${res.filename}\"")
                response.writeWith(res.content) // stream direto
            }
    }

    @PostMapping("/decode")
    fun decodeBase64(@RequestBody body: Map<String, String>, response: ServerHttpResponse): Mono<Void> {
        val base64 = body["base64"] ?: return Mono.error(IllegalArgumentException("Missing base64"))
        val bytes = Base64.getDecoder().decode(base64)

        response.headers.contentType = MediaType.IMAGE_JPEG
        response.headers.set("Content-Disposition", "inline; filename=\"decoded.jpg\"")

        val buffer = response.bufferFactory().wrap(bytes)
        return response.writeWith(Mono.just(buffer))
    }

//    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
//    fun uploadImage(@RequestPart("image") file: FilePart): Mono<Void> {
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
//                }.flatMap { body ->
//                    var r = decodeBase64ToImageFile(
//                        body = body,
//                        response = null
//                    )
//                    r
//                }
//        }
//    }
//
//    fun decodeBase64ToImageFile(
//        body: Map<String, String>,
//        response: ServerHttpResponse?
//    ): Mono<Void> {
//        val base64 = body["base64"] ?: return Mono.error(IllegalArgumentException("Missing base64"))
//        val bytes = Base64.getDecoder().decode(base64)
//        return Mono.fromCallable {
//            val path = Paths.get("images/${body["id"]}.png")
//            Files.createDirectories(path.parent)
//            Files.write(path, bytes)
//            path.toFile()
//        }.flatMap { file ->
//            val ct = MediaType.IMAGE_PNG
//            response?.headers?.contentType = ct
//            response?.headers?.set("Content-Disposition", "inline; filename=\"${file.name}\"")
//            response?.writeWith(
//                DataBufferUtils.readInputStream({ Files.newInputStream(file.toPath()) }, response.bufferFactory(), 8192)
//            )
//        }
//    }


//    @GetMapping("/{id}")
//    fun download(@PathVariable id: String, response: ServerHttpResponse): Mono<Void> {
//        val oid = ObjectId(id)
//        return gridFs.findOne(Query(Criteria.where("_id").`is`(oid)))
//            .switchIfEmpty(Mono.error(NoSuchElementException("File not found")))
//            .flatMap { file -> gridFs.getResource(file) }
//            .flatMap { res ->
//                val ct = "application/octet-stream"
//                response.headers.contentType = MediaType.parseMediaType(ct)
//                response.headers.set("Content-Disposition", "inline; filename=\"${res.filename}\"")
//
//                res.inputStream
//                    .flatMapMany { `is` ->
//                        DataBufferUtils.readInputStream({ `is` }, response.bufferFactory(), 8192)
//                    }
//                    .`as`(response::writeWith)
//            }
//    }

    @GetMapping("/{id}/base64")
    fun downloadAsBase64(@PathVariable id: String): Mono<Map<String, Any>> {
        val oid = ObjectId(id)
        return gridFs.findOne(Query(Criteria.where("_id").`is`(oid)))
            .switchIfEmpty(Mono.error(NoSuchElementException("File not found")))
            .flatMap { file -> gridFs.getResource(file) }
            .flatMap { res ->
                DataBufferUtils.join(res.content) // junta todos os buffers em memória
                    .map { dataBuffer ->
                        val bytes = ByteArray(dataBuffer.readableByteCount())
                        dataBuffer.read(bytes)
                        DataBufferUtils.release(dataBuffer) // libera memória
                        val base64 = java.util.Base64.getEncoder().encodeToString(bytes)
                        mapOf(
                            "id" to oid.toHexString(),
                            "filename" to res.filename,
                            "contentType" to ("application/octet-stream"),
                            "base64" to base64
                        )
                    }
            }
    }


//
//    @GetMapping("/{id}/meta")
//    fun metadata(@PathVariable id: String): Mono<Map<String, Any?>> {
//        val oid = ObjectId(id)
//        return gridFs.findOne(Query(Criteria.where("_id").`is`(oid)))
//            .switchIfEmpty(Mono.error(NoSuchElementException("File not found")))
//            .map { f ->
//                mapOf(
//                    "id" to f.objectId?.toHexString(),
//                    "filename" to f.filename,
//                    "length" to f.length,
//                    "chunkSize" to f.chunkSize,
//                    "uploadDate" to f.uploadDate,
//                    "contentType" to (f.metadata?.getString("contentType")
//                        ?: f.metadata?.getString("_contentType")),
//                    "metadata" to f.metadata
//                )
//            }
//    }
//
//    @GetMapping
//    fun list(
//        @RequestParam(required = false) filename: String?
//    ): Flux<Map<String, Any?>> {
//        val query = if (filename.isNullOrBlank()) Query() else Query(Criteria.where("filename").`is`(filename))
//        return gridFs.find(query).map { f ->
//            mapOf(
//                "id" to f.objectId?.toHexString(),
//                "filename" to f.filename,
//                "length" to f.length,
//                "uploadDate" to f.uploadDate
//            )
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    fun delete(@PathVariable id: String): Mono<Map<String, String>> {
//        val oid = ObjectId(id)
//        return gridFs.delete(Query(Criteria.where("_id").`is`(oid)))
//            .thenReturn(mapOf("deleted" to id))
//    }

}