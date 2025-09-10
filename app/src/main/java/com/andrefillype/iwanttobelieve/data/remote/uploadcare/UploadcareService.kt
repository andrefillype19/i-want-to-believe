package com.andrefillype.iwanttobelieve.data.remote.uploadcare

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import android.util.Log

class UploadcareService(
    private val publicKey: String
) {

    private val client = OkHttpClient()
    private val baseUrl = "https://upload.uploadcare.com/base/"

    /**
     * Faz o upload de uma imagem e retorna a URL final
     * 6161a9d174716f31bf36
     */
    suspend fun uploadImage(file: File): Result<String> {
        return try {
            Log.d("UploadDebug", "Iniciando upload para Uploadcare: ${file.absolutePath}")
            // Cria corpo da requisição multipart
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("1d79b77cacd59387657d", publicKey)
                .addFormDataPart("file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
                .build()

            val request = Request.Builder()
                .url(baseUrl)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                return Result.failure(Exception("Uploadcare upload failed: ${response.code}"))
            }

            val responseBody = response.body?.string()
            val json = JSONObject(responseBody ?: "{}")
            val fileUuid = json.getString("file")

            // URL final da imagem
            val fileUrl = "https://ucarecdn.com/$fileUuid/"

            Result.success(fileUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}