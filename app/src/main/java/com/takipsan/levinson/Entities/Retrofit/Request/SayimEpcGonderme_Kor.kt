package com.takipsan.levinson.Entities.Retrofit.Request

import com.fasterxml.jackson.annotation.JsonProperty

data class SayimEpcGonderme_Kor(
    @JsonProperty("userId")
    val userId: Long,

    @JsonProperty("countingId")
    val countingId: Long,

    val epc : Map<String,String>

)
