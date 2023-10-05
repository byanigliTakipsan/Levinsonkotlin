package com.takipsan.levinson.Entities.Retrofit.Request

import com.fasterxml.jackson.annotation.JsonProperty

data class UrunArama (
    @JsonProperty("keyword")
    var keyword: String? = null
)