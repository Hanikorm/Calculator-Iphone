package com.hanikorm.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData

class CalculatorViewModel : ViewModel() {
    private val _temporaryValue: MutableLiveData<String> = MutableLiveData("0")
    private val _firstValueEntered: MutableLiveData<String> = MutableLiveData("")
    private val _signSelectionVariable: MutableLiveData<String> = MutableLiveData("")

    val temporaryValue: LiveData<String> = _temporaryValue
    val firstValueEntered: LiveData<String> = _firstValueEntered
    val signSelectionVariable: LiveData<String> = _signSelectionVariable

    fun resetCalculator() {
        _temporaryValue.value = "0"
        _firstValueEntered.value = ""
        _signSelectionVariable.value = ""
    }

    fun updateTemporaryValue(value: String) {
        _temporaryValue.value = value
    }

    fun updateFirstValue(value: String) {
        _firstValueEntered.value = value
    }

    fun updateSign(value: String) {
        _signSelectionVariable.value = value
    }

    fun deleteLastDigit() {
        val currentValue = _temporaryValue.value ?: "0"
        if (currentValue.length > 1) {
            _temporaryValue.value = currentValue.dropLast(1)
        } else {
            _temporaryValue.value = "0"
        }
    }
}