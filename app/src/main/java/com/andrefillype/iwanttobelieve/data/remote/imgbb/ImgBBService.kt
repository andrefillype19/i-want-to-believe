package com.andrefillype.iwanttobelieve.data.remote.imgbb

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class ImgBBService(private val apiKey: String) {

    private val api: ImgBBApi

    init {
        val client = OkHttpClient.Builder().build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.imgbb.com/") // base da API
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ImgBBApi::class.java)
    }

    suspend fun uploadImage(file: File): Result<String> {
        return try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val response = api.uploadImage(apiKey, body)

            if (response.isSuccessful && response.body() != null) {
                val url = response.body()!!.data.url
                Result.success(url)
            } else {
                Result.failure(Exception("Erro ao enviar imagem: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
