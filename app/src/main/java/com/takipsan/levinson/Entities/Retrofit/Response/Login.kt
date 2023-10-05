package com.takipsan.levinson.Entities.Retrofit.Response

import com.fasterxml.jackson.annotation.JsonProperty

data class Login(
    @JsonProperty("id") val id: Long,
    @JsonProperty("name") val name: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("access_token") val access_token: String?
)
