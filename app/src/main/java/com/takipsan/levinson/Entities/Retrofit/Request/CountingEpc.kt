package com.takipsan.levinson.Entities.Retrofit.Request

import com.fasterxml.jackson.annotation.JsonProperty

data class CountingEpc(
    @JsonProperty("countingId")
    val countingId: Int
)
