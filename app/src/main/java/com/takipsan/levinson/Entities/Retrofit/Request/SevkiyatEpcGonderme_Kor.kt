package com.takipsan.levinson.Entities.Retrofit.Request

data class SevkiyatEpcGonderme_Kor(
val userId: Long,
val consignmentId: Long,
val epc: Map<String, String>
)
