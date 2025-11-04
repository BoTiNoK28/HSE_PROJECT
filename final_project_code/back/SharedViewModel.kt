package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _clearListEvent = MutableLiveData<Boolean>()
    val clearListEvent: LiveData<Boolean> get() = _clearListEvent
    private val _booleanValue = MutableLiveData<Boolean>(false)

    val booleanValue: LiveData<Boolean> get() = _booleanValue
    private val _ApiKey = MutableLiveData<String>()
    val ApiKey: MutableLiveData<String> get() = _ApiKey

    fun triggerClearList() {
        _clearListEvent.value = true
    }

    fun updateBooleanValue(newValue: Boolean) {
        _booleanValue.value = newValue
    }

    fun updateAPI(key: String) {
        _ApiKey.value = key
    }

    fun clearEventHandled() {
        _clearListEvent.value = false
    }
}