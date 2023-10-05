package com.takipsan.levinson.Entities.Retrofit.Response

data class SevkiyatListesi(
    val id: Int,
    val type: Int,
    val name: String,
    val mode: Int,
    val quantity: Int,
    val delivery_date: String,
    val status: Int,
    val counted: Int
)
