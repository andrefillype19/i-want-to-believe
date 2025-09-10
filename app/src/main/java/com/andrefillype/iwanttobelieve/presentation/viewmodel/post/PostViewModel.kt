package com.andrefillype.iwanttobelieve.presentation.viewmodel.post

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrefillype.iwanttobelieve.domain.usecase.image.UploadImageUseCase
import com.andrefillype.iwanttobelieve.domain.usecase.post.CreatePostUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

sealed class UploadState {
    object Idle : UploadState()
    object Uploading : UploadState()
    data class Success(val url: String) : UploadState()
    data class Error(val message: String) : UploadState()
}

// Converte Uri (imagem da galeria) em File temporário
fun uriToFile(context: Context, uri: Uri, fileName: String = "temp_image"): File {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val tempFile = File(context.cacheDir, fileName)
    inputStream.use { input ->
        FileOutputStream(tempFile).use { output ->
            input?.copyTo(output)
        }
    }
    return tempFile
}

class PostViewModel(
    private val createPostUseCase: CreatePostUseCase,
    private val uploadImageUseCase: UploadImageUseCase
) : ViewModel() {

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> get() = _uploadState

    fun uploadImageAndCreatePost(context: Context, description: String, imageUri: Uri) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Uploading
            try {
                val file = uriToFile(context, imageUri) // converte Uri → File

                val uploadResult = uploadImageUseCase(file) // Usa ImgBB agora 🚀
                uploadResult.fold(
                    onSuccess = { url ->
                        _uploadState.value = UploadState.Success(url)

                        val postResult = createPostUseCase(description, url)
                        postResult.fold(
                            onSuccess = { /* Post criado com sucesso */ },
                            onFailure = { e ->
                                _uploadState.value =
                                    UploadState.Error(e.message ?: "Erro ao criar post")
                            }
                        )
                    },
                    onFailure = { e ->
                        _uploadState.value =
                            UploadState.Error(e.message ?: "Erro ao fazer upload")
                    }
                )
            } catch (e: Exception) {
                _uploadState.value = UploadState.Error(e.message ?: "Erro inesperado")
            }
        }
    }
}
