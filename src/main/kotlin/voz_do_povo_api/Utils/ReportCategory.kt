package voz_do_povo_api.Utils

enum class ReportCategory {
    CLEANING_AND_ENVIRONMENT,
    URBAN_INFRASTRUCTURE,

    TRANSPORT_AND_MOBILITY,

    ESSENTIAL_PUBLIC_SERVICES,

    LAND_USE_AND_OCCUPATION,

    DISTURBANCE_OF_THE_PEACE,

    PUBLIC_SAFETY_AND_ORDER,

    ANIMAL_HEALTH_AND_ZOONOSES
}

fun getMessageByCategoryReport (category: ReportCategory) : String {
    return when (category){
        ReportCategory.CLEANING_AND_ENVIRONMENT -> "LIMPEZA E MEIO AMBIENTE"
        ReportCategory.URBAN_INFRASTRUCTURE -> "INFRAESTRUTURA"
        ReportCategory.TRANSPORT_AND_MOBILITY -> "TRANSPORTE E MOBILIDADE"
        ReportCategory.ESSENTIAL_PUBLIC_SERVICES -> "SERVIÇOS"
        ReportCategory.LAND_USE_AND_OCCUPATION -> " ÁGUA, ENERGIA E SANEAMENTO BÁSICO"
        ReportCategory.DISTURBANCE_OF_THE_PEACE -> "PERTURBAÇÃO DO SOSSEGO"
        ReportCategory.PUBLIC_SAFETY_AND_ORDER -> "SEGURANÇA"
        ReportCategory.ANIMAL_HEALTH_AND_ZOONOSES -> "ANIMAIS E ZOONOSES"
    }
}