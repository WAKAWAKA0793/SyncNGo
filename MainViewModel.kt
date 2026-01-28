package com.example.tripshare

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class DeepLink(val type: String, val id: Long)

class MainViewModel : ViewModel() {
    private val _pending = MutableStateFlow<DeepLink?>(null)
    val pendingDeepLink: StateFlow<DeepLink?> = _pending

    fun setDeepLink(dl: DeepLink) { _pending.value = dl }
    fun consumeDeepLink() { _pending.value = null }
}
