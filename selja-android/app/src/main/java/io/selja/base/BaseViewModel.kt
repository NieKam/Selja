package io.selja.base

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.selja.model.ThrowableError
import io.selja.utils.ErrorMapper
import kotlinx.coroutines.*


abstract class BaseViewModel : ViewModel() {
    val errors = MutableLiveData<ThrowableError>()
    val loading = ObservableBoolean(false)
    val hasLocationPermission = ObservableBoolean(false)

    open fun onAttached() {}
    open fun onDetached() {}

    open fun initPermissionState(hasLocationPermission : Boolean) {
        this.hasLocationPermission.set(hasLocationPermission)
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                Log.e("Coroutine", "Error", throwable)
                errors.value = ThrowableError(ErrorMapper.map(throwable))
                loading.set(false)
            }
        }

    }

    var coroutineScope = viewModelScope + coroutineExceptionHandler

    var backgroundDispatcher = Dispatchers.IO
}