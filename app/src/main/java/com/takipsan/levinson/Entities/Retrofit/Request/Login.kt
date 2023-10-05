package com.takipsan.levinson.Entities.Retrofit.Request

import com.fasterxml.jackson.annotation.JsonProperty

data class Login (
    @JsonProperty("username")
    val username: String,
    @JsonProperty("password")
    val password: String,
    @JsonProperty("secretKey")
    val secretKey: String
)