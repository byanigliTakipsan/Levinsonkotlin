package com.takipsan.levinson.Entities.Retrofit.Response

import com.fasterxml.jackson.annotation.JsonProperty

data class ConsigmentEpc(
    @JsonProperty("epc")
    val epc: String,

    @JsonProperty("found")
    var found: Int
)
