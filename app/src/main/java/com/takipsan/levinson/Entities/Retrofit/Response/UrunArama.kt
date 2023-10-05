package com.takipsan.levinson.Entities.Retrofit.Response

import com.fasterxml.jackson.annotation.JsonProperty

data class UrunArama (
    @JsonProperty("code")
    var code: String? = null,

    @JsonProperty("epc")
    var epc: String? = null,

    @JsonProperty("name")
    var name: String? = null,
)