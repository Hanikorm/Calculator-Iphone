package com.hanikorm.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {
    private val calculatorViewModel: CalculatorViewModel by viewModels()
    private lateinit var display: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.display)
        calculatorViewModel.temporaryValue.observe(this) { value -> display.text = value }

        setupButtons()
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.buttonOtv).setOnClickListener { onButtonResultClicked() }
        findViewById<Button>(R.id.buttonPlus).setOnClickListener { onButtonWithSignClicked("+") }
        findViewById<Button>(R.id.buttonMinus).setOnClickListener { onButtonWithSignClicked("-") }
        findViewById<Button>(R.id.buttondivide).setOnClickListener { onButtonWithSignClicked("/") }
        findViewById<Button>(R.id.buttonmultiply).setOnClickListener { onButtonWithSignClicked("*") }
        findViewById<Button>(R.id.buttonplusandminus).setOnClickListener { onPlusOrMinusButtonClicked() }
        findViewById<Button>(R.id.buttonzp).setOnClickListener { onCommaButtonClicked() }
        findViewById<Button>(R.id.buttonAC).setOnClickListener { calculatorViewModel.resetCalculator() }
        findViewById<Button>(R.id.buttonpercent).setOnClickListener { onButtonPercentClicked() }
        findViewById<TextView>(R.id.display).setOnClickListener { deleteLastDigit() }

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

        for ((buttonId, number) in numberButtons) {
            findViewById<Button>(buttonId).setOnClickListener {
                onNumberButtonClicked(number)
            }
        }
    }
    private fun deleteLastDigit() {
        val currentValue = calculatorViewModel.temporaryValue.value ?: "0"
        val newValue = if (currentValue.length > 1) {
            currentValue.dropLast(1)
        } else {
            "0"
        }
        calculatorViewModel.updateTemporaryValue(newValue)
    }
    private fun onNumberButtonClicked(number: String) {
        val currentValue = calculatorViewModel.temporaryValue.value ?: "0"
        if (currentValue.length < 9) {
            val updatedValue = if (currentValue == "0" || currentValue == "-0") {
                if (currentValue == "-0") "-$number" else number
            } else {
                currentValue + number
            }
            calculatorViewModel.updateTemporaryValue(updatedValue)
        }
    }

    private fun onCommaButtonClicked() {
        val currentValue = calculatorViewModel.temporaryValue.value ?: "0"
        if (!currentValue.contains(".") && currentValue.length <= 9) {
            calculatorViewModel.updateTemporaryValue("$currentValue.")
        }
    }

    private fun onPlusOrMinusButtonClicked() {
        val currentValue = calculatorViewModel.temporaryValue.value ?: "0"
        val updatedValue = if (currentValue.startsWith("-")) currentValue.drop(1) else "-$currentValue"
        calculatorViewModel.updateTemporaryValue(updatedValue)
    }

    private fun onButtonWithSignClicked(sign: String) {
        if (!calculatorViewModel.temporaryValue.value.isNullOrEmpty()) {
            if (!calculatorViewModel.firstValueEntered.value.isNullOrEmpty() && !calculatorViewModel.signSelectionVariable.value.isNullOrEmpty()) {
                calculatorViewModel.updateSign(sign)
            } else {
                calculatorViewModel.updateFirstValue(calculatorViewModel.temporaryValue.value ?: "0")
                calculatorViewModel.updateSign(sign)
            }
        }
        calculatorViewModel.updateTemporaryValue("0")
    }

    private fun onButtonPercentClicked() {
        val firstValue = calculatorViewModel.firstValueEntered.value?.toBigDecimalOrNull() ?: return
        val secondValue = calculatorViewModel.temporaryValue.value?.toBigDecimalOrNull() ?: return
        val percentValue = (firstValue * secondValue).divide(BigDecimal(100), 10, RoundingMode.HALF_UP)
        calculatorViewModel.updateTemporaryValue(percentValue.stripTrailingZeros().toPlainString())
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
        calculatorViewModel.updateTemporaryValue(formatNumber(result.toString()))
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
