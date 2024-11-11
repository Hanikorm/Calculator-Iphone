package com.hanikorm.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {
    private val _temporaryValue = MutableLiveData("0")
    private val _firstValueEntered = MutableLiveData("")
    private val _signSelectionVariable = MutableLiveData("")

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
}