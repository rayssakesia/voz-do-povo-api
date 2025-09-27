package voz_do_povo_api.controller.requests

import kotlinx.serialization.Serializable
import org.jetbrains.annotations.NotNull
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Serializable
@Document(collection = "report")
data class PublicationData(

    @Id
    var id: String?,

    val userRequest: UserRequest,

    val reportAddressRequest: ReportAddressRequest,

    val report: ReportRequest

)

@Serializable
data class UserRequest(

    val name: String? = "Usuário Anônimo",
    @NotNull
    val email: String
)

@Serializable
data class ReportAddressRequest(

    @NotNull
    val number: String,
    @NotNull
    val zipCode: String,
    @NotNull
    val street: String,
    val complement: String?,
    @NotNull
    val city: String,
    @NotNull
    val state: String,
    @NotNull
    val country: String
)

@Serializable
data class ReportRequest(
    @NotNull
    val report: String,
    @NotNull
    val imagesUrl: List<String>,
    @NotNull
    val reportCategory: String
)
