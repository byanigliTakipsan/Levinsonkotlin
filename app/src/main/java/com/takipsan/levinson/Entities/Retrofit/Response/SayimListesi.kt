package com.takipsan.levinson.Entities.Retrofit.Response

import com.fasterxml.jackson.annotation.JsonProperty

data class SayimListesi(
    @JsonProperty("id")
    val id: Int,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("mode")
    val mode: Int,

    @JsonProperty("status")
    val status: Int,

    @JsonProperty("counted")
    val counted: Int
) ;