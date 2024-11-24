package com.hanikorm.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.math.BigDecimal
import java.math.RoundingMode

class CalculatorViewModel : ViewModel() {
    // MutableLiveData для временного значения, введенного пользователем
    private var _firstValueEntered = MutableLiveData("0")
    // MutableLiveData для первого значения, введенного пользователем
    private var _secondValueEntered = MutableLiveData("")
    // MutableLiveData для знака (+, -, *, /), выбранного пользователем
    private var _signSelectionVariable = MutableLiveData("")
    // MutableLiveData для текста, отображаемого на экране калькулятора
    private var _displayText = MutableLiveData("0")

    // LiveData для доступа к временному значению
    val firstValueEntered: LiveData<String> = _firstValueEntered

    // Вызывается при нажатии кнопки с числом
    // Добавляет нажатое число к текущему значению
    fun onNumberButtonClicked(number: String) {
        val currentValue = _firstValueEntered.value ?: "0"
        if (currentValue.length < 9) {
            val updatedValue = if (currentValue == "0" || currentValue == "-0") {
                if (currentValue == "-0") "-$number" else number
            } else {
                currentValue + number
            }
            _firstValueEntered.value = updatedValue
        }
    }

    // Вызывается при нажатии кнопки с запятой
    // Добавляет десятичную точку к текущему значению, если она отсутствует
    fun onCommaButtonClicked() {
        val currentValue = _firstValueEntered.value ?: "0"
        if (!currentValue.contains(".") && currentValue.length <= 9) {
            _firstValueEntered.value = "$currentValue."
        }
    }

    // Вызывается при нажатии кнопки плюс/минус
    // Переключает знак текущего значения
    fun onPlusOrMinusButtonClicked() {
        val currentValue = _firstValueEntered.value ?: "0"
        val updatedValue = if (currentValue.startsWith("-")) currentValue.drop(1) else "-$currentValue"
        _firstValueEntered.value = updatedValue
    }

    // Вызывается при нажатии кнопки с знаком (+, -, *, /)
    // Обновляет выбранный знак и сбрасывает отображаемый текст
    fun onButtonWithSignClicked(sign: String) {
        if (!_firstValueEntered.value.isNullOrEmpty()) {
            if (!_secondValueEntered.value.isNullOrEmpty() && !_signSelectionVariable.value.isNullOrEmpty()) {
                updateSign(sign)
            } else {
                updateFirstValue(_firstValueEntered.value ?: "0")
                updateSign(sign)
            }
        }
        _firstValueEntered.value = "0"
    }

    // Вызывается при нажатии кнопки процента
    // Вычисляет процент от текущего значения
    fun onButtonPercentClicked() {
        val firstValue = _firstValueEntered.value?.toBigDecimalOrNull() ?: return
        val secondValue = _secondValueEntered.value?.toBigDecimalOrNull() ?: return
        val percentValue = (firstValue * secondValue).divide(BigDecimal(100), 10, RoundingMode.HALF_UP)
        _firstValueEntered.value = percentValue.stripTrailingZeros().toPlainString()
    }

    // Вызывается при нажатии кнопки результата
    // Вычисляет результат текущей операции
    fun onButtonResultClicked() {
        val firstValue = _secondValueEntered.value?.toDoubleOrNull() ?: return
        val secondValue = _firstValueEntered.value?.toDoubleOrNull() ?: return
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
        _firstValueEntered.value = formatNumber(result.toString())
    }

    // Удаляет последнюю цифру с экрана
    fun deleteLastDigit() {
        val currentValue = _firstValueEntered.value ?: "0"
        val newValue = if (currentValue.length > 1) {
            currentValue.dropLast(1)
        } else {
            "0"
        }
        _firstValueEntered.value = newValue
    }

    // Сбрасывает калькулятор в начальное состояние
    fun resetCalculator() {
        _firstValueEntered.value = "0"
        _secondValueEntered.value = ""
        _signSelectionVariable.value = ""
        _displayText.value = "0"
    }

    // Обновляет первое введенное значение
    private fun updateFirstValue(value: String) {
        _secondValueEntered.value = value
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