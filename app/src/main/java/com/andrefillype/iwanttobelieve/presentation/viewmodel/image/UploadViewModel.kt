package com.andrefillype.iwanttobelieve.presentation.viewmodel.image

import androidx.lifecycle.ViewModel
import com.andrefillype.iwanttobelieve.domain.usecase.image.UploadImageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    data class Success(val imageUrl: String) : UploadState()
    data class Error(val throwable: Throwable) : UploadState()
}

class UploadViewModel(
    private val uploadImageUseCase: UploadImageUseCase
) : ViewModel() {

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState

    fun uploadImage(file: File) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading
            val result = uploadImageUseCase(file)
            _uploadState.value = result.fold(
                onSuccess = { url -> UploadState.Success(url) },
                onFailure = { error -> UploadState.Error(error) }
            )
        }
    }
}