package com.takipsan.levinson.Entities.Retrofit.Response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class ApiResponseBase<T>(
    @JsonProperty("status")
    var status: Boolean = false,

    @JsonProperty("code")
    var code: String? = null,

    @JsonProperty("message")
    var message: String? = null,

    @JsonProperty("data")
    var data: T,


)