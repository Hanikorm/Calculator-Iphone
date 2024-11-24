package com.hanikorm.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.math.BigDecimal
import java.math.RoundingMode

// ViewModel для калькулятора
// Обрабатывает логику операций калькулятора и обновляет UI через LiveData
class CalculatorViewModel : ViewModel() {
    // MutableLiveData для временного значения, введенного пользователем
    private var _temporaryValue = MutableLiveData("0")
    // MutableLiveData для первого значения, введенного пользователем
    private var _firstValueEntered = MutableLiveData("")
    // MutableLiveData для знака (+, -, *, /), выбранного пользователем
    private var _signSelectionVariable = MutableLiveData("")
    // MutableLiveData для текста, отображаемого на экране калькулятора
    private var _displayText = MutableLiveData("0")

    // LiveData для доступа к временному значению
    val temporaryValue: LiveData<String> = _temporaryValue
    // LiveData для доступа к первому введенному значению
    val firstValueEntered: LiveData<String> = _firstValueEntered
    // LiveData для доступа к выбранному знаку
    val signSelectionVariable: LiveData<String> = _signSelectionVariable
    // LiveData для доступа к отображаемому тексту
    val displayText: LiveData<String> = _displayText

    // Вызывается при нажатии кнопки с числом
    // Добавляет нажатое число к текущему значению
    fun onNumberButtonClicked(number: String) {
        val currentValue = _temporaryValue.value ?: "0"
        if (currentValue.length < 9) {
            val updatedValue = if (currentValue == "0" || currentValue == "-0") {
                if (currentValue == "-0") "-$number" else number
            } else {
                currentValue + number
            }
            _temporaryValue.value = updatedValue
        }
    }

    // Вызывается при нажатии кнопки с запятой
    // Добавляет десятичную точку к текущему значению, если она отсутствует
    fun onCommaButtonClicked() {
        val currentValue = _temporaryValue.value ?: "0"
        if (!currentValue.contains(".") && currentValue.length <= 9) {
            _temporaryValue.value = "$currentValue."
        }
    }

    // Вызывается при нажатии кнопки плюс/минус
    // Переключает знак текущего значения
    fun onPlusOrMinusButtonClicked() {
        val currentValue = _temporaryValue.value ?: "0"
        val updatedValue = if (currentValue.startsWith("-")) currentValue.drop(1) else "-$currentValue"
        _temporaryValue.value = updatedValue
    }

    // Вызывается при нажатии кнопки с знаком (+, -, *, /)
    // Обновляет выбранный знак и сбрасывает отображаемый текст
    fun onButtonWithSignClicked(sign: String) {
        if (!_temporaryValue.value.isNullOrEmpty()) {
            if (!_firstValueEntered.value.isNullOrEmpty() && !_signSelectionVariable.value.isNullOrEmpty()) {
                updateSign(sign)
            } else {
                updateFirstValue(_temporaryValue.value ?: "0")
                updateSign(sign)
            }
        }
        _temporaryValue.value = "0"
    }

    // Вызывается при нажатии кнопки процента
    // Вычисляет процент от текущего значения
    fun onButtonPercentClicked() {
        val firstValue = _firstValueEntered.value?.toBigDecimalOrNull() ?: return
        val secondValue = _temporaryValue.value?.toBigDecimalOrNull() ?: return
        val percentValue = (firstValue * secondValue).divide(BigDecimal(100), 10, RoundingMode.HALF_UP)
        _temporaryValue.value = percentValue.stripTrailingZeros().toPlainString()
    }

    // Вызывается при нажатии кнопки результата
    // Вычисляет результат текущей операции
    fun onButtonResultClicked() {
        val firstValue = _firstValueEntered.value?.toDoubleOrNull() ?: return
        val secondValue = _temporaryValue.value?.toDoubleOrNull() ?: return
        val sign = _signSelectionVariable.value ?: return
        val result = when (sign) {
            "+" -> firstValue + secondValue
            "-" -> firstValue - secondValue
            "*" -> firstValue * secondValue
            "/" -> if (secondValue != 0.0) firstValue / secondValue else {
                _displayText.value = "Ошибка"
                return
            }
            else -> return
        }
        _temporaryValue.value = formatNumber(result.toString())
    }

    // Удаляет последнюю цифру с экрана
    fun deleteLastDigit() {
        val currentValue = _temporaryValue.value ?: "0"
        val newValue = if (currentValue.length > 1) {
            currentValue.dropLast(1)
        } else {
            "0"
        }
        _temporaryValue.value = newValue
    }

    // Сбрасывает калькулятор в начальное состояние
    fun resetCalculator() {
        _temporaryValue.value = "0"
        _firstValueEntered.value = ""
        _signSelectionVariable.value = ""
        _displayText.value = "0"
    }

    // Обновляет временное значение
    fun updateTemporaryValue(value: String) {
        _temporaryValue.value = value
    }

    // Обновляет первое введенное значение
    private fun updateFirstValue(value: String) {
        _firstValueEntered.value = value
    }

    // Обновляет выбранный знак
    private fun updateSign(value: String) {
        _signSelectionVariable.value = value
    }

    // Форматирует число, удаляя ненужные десятичные знаки
    private fun formatNumber(value: String): String {
        val number = value.toDouble()
        return if (number % 1 == 0.0) {
            number.toInt().toString()
        } else {
            String.format("%.10f", number).trimEnd('0').trimEnd('.')
        }
    }
}