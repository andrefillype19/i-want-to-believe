package com.andrefillype.iwanttobelieve.data.remote.imgbb

data class ImgBBResponse(
    val data: ImgBBData,
    val success: Boolean,
    val status: Int
)

data class ImgBBData(
    val url: String,
    val display_url: String
)