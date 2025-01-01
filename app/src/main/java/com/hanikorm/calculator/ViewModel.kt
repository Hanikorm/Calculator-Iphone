package com.hanikorm.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.math.BigDecimal
import java.math.RoundingMode

class CalculatorViewModel : ViewModel() {
    private var firstValueEntered = "0"
    private var secondValueEntered = ""
    private var signSelectionVariable = ""
    private var needNewNumber = false
    private var lastNumber = ""
    private var lastOperation = ""

    private var _displayText = MutableLiveData("0")
    val displayText: LiveData<String> = _displayText

    fun onNumberButtonClicked(number: String) {
        if (needNewNumber) {
            firstValueEntered = number
            needNewNumber = false
        } else {
            val currentValue = _displayText.value ?: "0"
            if (currentValue.length < 9) {
                firstValueEntered = if (currentValue == "0") number else currentValue + number
            }
        }
        _displayText.value = firstValueEntered
    }

    fun onCommaButtonClicked() {
        if (needNewNumber) {
            firstValueEntered = "0."
            needNewNumber = false
        } else if (!firstValueEntered.contains(".") && firstValueEntered.length <= 9) {
            firstValueEntered = "$firstValueEntered."
        }
        _displayText.value = firstValueEntered
    }

    fun onPlusOrMinusButtonClicked() {
        firstValueEntered = if (firstValueEntered.startsWith("-")) {
            firstValueEntered.substring(1)
        } else {
            "-$firstValueEntered"
        }
        _displayText.value = firstValueEntered
    }

    fun onButtonWithSignClicked(sign: String) {
        if (signSelectionVariable.isNotEmpty() && secondValueEntered.isNotEmpty()) {
            onButtonResultClicked()
        }

        secondValueEntered = firstValueEntered
        signSelectionVariable = sign
        needNewNumber = true
    }

    fun onButtonPercentClicked() {
        val currentValue = firstValueEntered.toDoubleOrNull() ?: return

        val result = if (secondValueEntered.isEmpty()) {
            currentValue / 100.0
        } else {
            val baseValue = secondValueEntered.toDoubleOrNull() ?: return
            (baseValue * currentValue) / 100.0
        }

        val formattedResult = formatNumber(result.toString())
        _displayText.value = formattedResult
        firstValueEntered = formattedResult
        needNewNumber = true
    }

    fun onButtonResultClicked() {
        if (signSelectionVariable.isEmpty()) return

        val firstValue = secondValueEntered.toDoubleOrNull() ?: return
        val secondValue = firstValueEntered.toDoubleOrNull() ?: return

        val result = when (signSelectionVariable) {
            "+" -> firstValue + secondValue
            "-" -> firstValue - secondValue
            "*" -> firstValue * secondValue
            "/" -> if (secondValue != 0.0) firstValue / secondValue else {
                _displayText.value = "Ошибка"
                resetCalculator()
                return
            }
            else -> return
        }

        val formattedResult = formatNumber(result.toString())
        _displayText.value = formattedResult
        firstValueEntered = formattedResult
        secondValueEntered = ""
        signSelectionVariable = ""
        needNewNumber = true
    }

    fun resetCalculator() {
        firstValueEntered = "0"
        secondValueEntered = ""
        signSelectionVariable = ""
        lastNumber = ""
        lastOperation = ""
        needNewNumber = false
        _displayText.value = "0"
    }

    fun deleteLastDigit() {
        if (needNewNumber) return

        firstValueEntered = if (firstValueEntered.length > 1) {
            firstValueEntered.dropLast(1)
        } else {
            "0"
        }
        _displayText.value = firstValueEntered
    }

    private fun updateFirstValue(value: String) {
        secondValueEntered = value
    }

    private fun formatNumber(value: String): String {
        val number = value.toDouble()
        return if (number % 1 == 0.0) {
            number.toInt().toString()
        } else {
            String.format("%.10f", number).trimEnd('0').trimEnd('.')
        }
    }
}
