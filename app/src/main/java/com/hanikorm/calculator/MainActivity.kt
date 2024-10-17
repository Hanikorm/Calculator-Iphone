package com.hanikorm.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.activity.viewModels
import java.math.BigDecimal
import java.math.RoundingMode
/**
 * класс основной активности калькулятора
 * @autor hanikorm
 * @version 0.11
 */
class MainActivity : AppCompatActivity() {
    /**
     * ViewModel для хранения данных калькулятора.
     * ViewModel помогает сохранить данные при изменении конфигурации (например, поворот экрана).
     */
    private val calculatorViewModel: CalculatorViewModel by viewModels()
    /**
     * display отображает данные на экране у пользователя
     * инициализируется позже, когда интерфес будет готов.
    */
    private lateinit var display: TextView
    /**
     * Метод, который вызывается при создании активности.
     * Устанавливает макет и инициализирует основные компоненты.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //это ранее вызванная перменная
        display = findViewById(R.id.display)
        calculatorViewModel.temporaryValue.observe(this, Observer { value ->
            display.text = value
        })
        // Это функция обработчиков кнопок
        setupButtons()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Сохраняем текущее значение временного числа
        outState.putString("temporaryValue", calculatorViewModel.temporaryValue.value)
        // Сохраняем другие нужные данные
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Восстанавливаем сохранённые значения
        val savedTemporaryValue = savedInstanceState.getString("temporaryValue")
        if (savedTemporaryValue != null) {
            calculatorViewModel.temporaryValue.value = savedTemporaryValue
        }
    }

    /**
     * Метод для настройки обработчиков событий для кнопок калькулятора.
     * Связывает кнопки с соответствующими методами.
     */
    private fun setupButtons() {
        findViewById<Button>(R.id.buttonOtv).setOnClickListener { onButtonResultClicked() }
        findViewById<Button>(R.id.buttonPlus).setOnClickListener { onButtonWithSignClicked("+") }
        findViewById<Button>(R.id.buttonMinus).setOnClickListener { onButtonWithSignClicked("-") }
        findViewById<Button>(R.id.buttondivide).setOnClickListener { onButtonWithSignClicked("/") }
        findViewById<Button>(R.id.buttonmultiply).setOnClickListener { onButtonWithSignClicked("*") }
        findViewById<Button>(R.id.buttonplusandminus).setOnClickListener { onPlusOrMinusButtonClicked() }
        findViewById<Button>(R.id.buttonzp).setOnClickListener { onCommaButtonClicked() }
        findViewById<Button>(R.id.buttonAC).setOnClickListener { resetCalculator() }
        findViewById<Button>(R.id.buttonpercent).setOnClickListener { onButtonPercentClicked() }
        //функция кнопок с числами
        setupNumberButtons()
    }

    private fun setupNumberButtons() {
        val numberButtons = listOf(
            R.id.buttonnull to "0",
            R.id.buttonNumber1 to "1",
            R.id.buttonNumber2 to "2",
            R.id.buttonNumber3 to "3",
            R.id.buttonNumber4 to "4",
            R.id.buttonNumber5 to "5",
            R.id.buttonNumber6 to "6",
            R.id.buttonNumber7 to "7",
            R.id.buttonNumber8 to "8",
            R.id.buttonnumber9 to "9"
        )
        // Для каждой кнопки устанавливаю обработчик нажатия
        for ((buttonId, number) in numberButtons) {
            findViewById<Button>(buttonId).setOnClickListener {
                // Метод для обработки нажатия на кнопку с числом
                onNumberButtonClicked(number)
            }
        }
    }

    private fun onNumberButtonClicked(number: String) {
        // Получаем текущее значение из ViewModel. Если его нет, используем "0"
        val currentValue = calculatorViewModel.temporaryValue.value ?: "0"
        // Проверяем, чтобы длина числа не превышала 9 символов
        if (currentValue.length < 9) {
            val updatedValue = if (currentValue == "0" || currentValue == "-0") {
                // Если текущее значение "0" или "-0", заменяем его на новое число
                if (currentValue == "-0") "-$number" else number
            } else {
                // Иначе просто добавляем новое число к текущему значению
                currentValue + number
            }
            // Обновляем значение в ViewModel
            calculatorViewModel.temporaryValue.value = updatedValue
        }
    }
    // Метод для обработки нажатия на кнопку запятой
    private fun onCommaButtonClicked() {
        val currentValue = calculatorViewModel.temporaryValue.value ?: "0"
        // Если в числе ещё нет запятой, добавляем её
        if (!currentValue.contains(".")) {
            calculatorViewModel.temporaryValue.value = "$currentValue."
        }
    }
    // Метод для обработки нажатия на кнопку "+/-" (смена знака числа)
    private fun onPlusOrMinusButtonClicked() {
        val currentValue = calculatorViewModel.temporaryValue.value ?: "0"
        // Меняем знак числа
        val updatedValue = if (currentValue.startsWith("-")) {
            currentValue.drop(1)    // Убираем знак минус
        } else {
            "-$currentValue"    // Добавляем знак минус
        }
        calculatorViewModel.temporaryValue.value = updatedValue
    }

    private fun onButtonWithSignClicked(sign: String) {
        calculatorViewModel.firstValueEntered.value = calculatorViewModel.temporaryValue.value
        calculatorViewModel.signSelectionVariable.value = sign
        calculatorViewModel.temporaryValue.value = "0"
    }

    private fun onButtonPercentClicked() {
        // Если есть первое введённое значение и операция, выполняем расчет в контексте операции
        if (calculatorViewModel.firstValueEntered.value?.isNotEmpty() == true && calculatorViewModel.signSelectionVariable.value?.isNotEmpty() == true) {
            // Пробуем преобразовать временное значение (второе число) в BigDecimal
            val secondValue = calculatorViewModel.temporaryValue.value?.toBigDecimalOrNull() ?: return
            // Преобразуем первое значение в BigDecimal
            val firstValue = calculatorViewModel.firstValueEntered.value?.toBigDecimalOrNull() ?: return
            // Вычисляем процентное значение
            val percentValue = (firstValue * secondValue).divide(BigDecimal(100), 10, RoundingMode.HALF_UP)
            // Обновляем временное значение и текст на экране
            calculatorViewModel.temporaryValue.value = percentValue.stripTrailingZeros().toPlainString()
        } else {
            // Если операция не выбрана, просто рассчитываем процент от текущего числа
            val value = calculatorViewModel.temporaryValue.value?.toBigDecimalOrNull() ?: return
            // Делим текущее число на 100 и округляем
            val percentValue = value.divide(BigDecimal(100), 10, RoundingMode.HALF_UP)
            // Обновляем временное значение и текст на экране
            calculatorViewModel.temporaryValue.value = percentValue.stripTrailingZeros().toPlainString()
        }
    }

    private fun onButtonResultClicked() {
        val firstValue = calculatorViewModel.firstValueEntered.value?.toDoubleOrNull() ?: return
        val secondValue = calculatorViewModel.temporaryValue.value?.toDoubleOrNull() ?: return
        val sign = calculatorViewModel.signSelectionVariable.value ?: return

        val result = when (sign) {
            "+" -> firstValue + secondValue
            "-" -> firstValue - secondValue
            "*" -> firstValue * secondValue
            "/" -> if (secondValue != 0.0) firstValue / secondValue else {
                display.text = "Ошибка"
                return
            }
            else -> return
        }
        calculatorViewModel.temporaryValue.value = result.toString()
    }

    private fun resetCalculator() {
        calculatorViewModel.temporaryValue.value = "0"
        calculatorViewModel.firstValueEntered.value = ""
        calculatorViewModel.signSelectionVariable.value = ""
    }

    private fun formatNumber(value: String): String {
        val number = value.toDouble()
        return if (number == number.toLong().toDouble()) {
            number.toLong().toString()
        } else {
            String.format("%.10f", number).trimEnd('0').trimEnd('.')
        }
    }
}
