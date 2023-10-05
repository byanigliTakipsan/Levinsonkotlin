package com.takipsan.levinson.Entities.Retrofit.Request

import com.fasterxml.jackson.annotation.JsonProperty

data class ConsignmentEpc(
    @JsonProperty("consignmentId")
    val consignmentId: Int
)
