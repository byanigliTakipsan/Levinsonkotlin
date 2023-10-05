package com.takipsan.levinson.Entities.Model

data class EpcScanModel(
    var epcId: String = "",
    var tid: String = "",
    var rssi: Double = 0.0,
    var pc: String
)