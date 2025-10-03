package voz_do_povo_api

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate

@Configuration
class MongoGridFsConfig(
    private val factory: ReactiveMongoDatabaseFactory,
    private val converter: MappingMongoConverter
) {
    @Bean
    fun reactiveGridFsTemplate(): ReactiveGridFsTemplate =
        ReactiveGridFsTemplate(factory, converter)
}