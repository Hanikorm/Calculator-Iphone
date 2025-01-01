package com.hanikorm.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.math.BigDecimal
import java.math.RoundingMode

class CalculatorViewModel : ViewModel() {
    //  для временного значения, введенного пользователем
    private var firstValueEntered = "0"
    //  для первого значения, введенного пользователем
    private var secondValueEntered = ""
    //  для знака (+, -, *, /), выбранного пользователем
    private var signSelectionVariable = ""
    // флаг для определения, нужно ли начать ввод нового числа
    private var needNewNumber = false
    // Сохраняем последнее введенное число для повторных операций
    private var lastNumber = ""
    // Сохраняем последнюю операцию
    private var lastOperation = ""

    private var _displayText = MutableLiveData("0")
    val displayText: LiveData<String> = _displayText

    // Вызывается при нажатии кнопки с числом
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

    // Вызывается при нажатии кнопки с запятой
    fun onCommaButtonClicked() {
        if (needNewNumber) {
            firstValueEntered = "0."
            needNewNumber = false
        } else if (!firstValueEntered.contains(".") && firstValueEntered.length <= 9) {
            firstValueEntered = "$firstValueEntered."
        }
        _displayText.value = firstValueEntered
    }

    // Вызывается при нажатии кнопки плюс/минус
    fun onPlusOrMinusButtonClicked() {
        firstValueEntered = if (firstValueEntered.startsWith("-")) {
            firstValueEntered.substring(1)
        } else {
            "-$firstValueEntered"
        }
        _displayText.value = firstValueEntered
    }

    // Вызывается при нажатии кнопки с знаком (+, -, *, /)
    fun onButtonWithSignClicked(sign: String) {
        if (firstValueEntered.isNotEmpty()) {
            if (secondValueEntered.isNotEmpty() && signSelectionVariable.isNotEmpty()) {
                secondValueEntered = firstValueEntered
            } else {
                updateFirstValue(_displayText.value ?: "0")
            }
            lastNumber = ""
            updateSign(sign)
            lastOperation = sign
            needNewNumber = true
        }
    }

    // Вызывается при нажатии кнопки процента
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

    // Вызывается при нажатии кнопки результата
    fun onButtonResultClicked() {
        if (signSelectionVariable.isEmpty() && lastNumber.isNotEmpty()) {
            val currentValue = firstValueEntered.toDoubleOrNull() ?: return
            val lastValue = lastNumber.toDoubleOrNull() ?: return
            val result = when (lastOperation) {
                "+" -> currentValue + lastValue
                "-" -> currentValue - lastValue
                "*" -> currentValue * lastValue
                "/" -> if (lastValue != 0.0) currentValue / lastValue else {
                    _displayText.value = "Ошибка"
                    resetCalculator()
                    return
                }
                else -> return
            }
            val formattedResult = formatNumber(result.toString())
            _displayText.value = formattedResult
            firstValueEntered = formattedResult
            needNewNumber = true
            return
        }
        if (signSelectionVariable.isEmpty() || secondValueEntered.isEmpty()) return

        val firstValue = secondValueEntered.toDoubleOrNull() ?: return
        val secondValue = firstValueEntered.toDoubleOrNull() ?: return
        lastNumber = firstValueEntered
        lastOperation = signSelectionVariable

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

    private fun updateSign(value: String) {
        signSelectionVariable = value
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