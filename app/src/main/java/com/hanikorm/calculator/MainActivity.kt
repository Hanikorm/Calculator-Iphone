package com.hanikorm.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var temporaryValue = "0"
    private var firstValueEntered = ""
    private var signSelectionVariable = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val display = findViewById<TextView>(R.id.display)

        // Инициализация кнопок
        val buttonResult = findViewById<Button>(R.id.buttonOtv)
        buttonResult.setOnClickListener {
            onButtonResulClicked(display)
        }

        val buttonPlus = findViewById<Button>(R.id.buttonPlus)
        buttonPlus.setOnClickListener {
            onButtonWithSignClicked("+", display)
        }

        val buttonMinus = findViewById<Button>(R.id.buttonMinus)
        buttonMinus.setOnClickListener {
            onButtonWithSignClicked("-", display)
        }

        val buttonDelete = findViewById<Button>(R.id.buttondivide)
        buttonDelete.setOnClickListener {
            onButtonWithSignClicked("/", display)
        }

        val buttonMultiply = findViewById<Button>(R.id.buttonmultiply)
        buttonMultiply.setOnClickListener {
            onButtonWithSignClicked("*", display)
        }

        val buttonPlusOrMinus = findViewById<Button>(R.id.buttonplusandminus)
        buttonPlusOrMinus.setOnClickListener {
            onPlusOrMinusButtonClicked(display)
        }

        val buttonComma = findViewById<Button>(R.id.buttonzp)
        buttonComma.setOnClickListener {
            onCommaButtonClicked(display)
        }

        val buttonAC = findViewById<Button>(R.id.buttonAC)
        buttonAC.setOnClickListener {
            display.text = "0"
            temporaryValue = "0"
            firstValueEntered = ""
            signSelectionVariable = ""
        }

        val buttonPercent = findViewById<Button>(R.id.buttonpercent)
        buttonPercent.setOnClickListener {
            onButtonPercentClicked(display)
        }

        val numberButtons = mapOf(
            R.id.buttonnumber9 to "9",
            R.id.buttonNumber8 to "8",
            R.id.buttonNumber7 to "7",
            R.id.buttonNumber6 to "6",
            R.id.buttonNumber5 to "5",
            R.id.buttonNumber4 to "4",
            R.id.buttonNumber3 to "3",
            R.id.buttonNumber2 to "2",
            R.id.buttonNumber1 to "1",
            R.id.buttonnull to "0"
        )

        numberButtons.forEach { (buttonId, number) ->
            findViewById<Button>(buttonId).setOnClickListener {
                onNumberButtonClicked(number, display)
            }
        }
    }

    private fun onNumberButtonClicked(number: String, display: TextView) {
        if (temporaryValue.length < 9) {
            temporaryValue = if (temporaryValue == "0" && number == "0") {
                "0"
            } else if ((temporaryValue == "0" || temporaryValue == "-0") && number != "0") {
                if (temporaryValue.startsWith("-")) {
                    "-$number"
                } else {
                    number
                }
            } else {
                temporaryValue + number
            }
        }
        display.text = temporaryValue
    }

    private fun onCommaButtonClicked(display: TextView) {
        if (!temporaryValue.contains(".")) {
            temporaryValue += "."
            display.text = temporaryValue
        }
    }

    private fun onButtonPercentClicked(display: TextView) {
        val firstValue = firstValueEntered.toDoubleOrNull()
        val secondValue = temporaryValue.toDoubleOrNull()

        if (firstValue != null && secondValue != null) {
            temporaryValue = when (signSelectionVariable) {
                "+" -> (firstValue + (firstValue * secondValue / 100)).toString()
                "-" -> (firstValue - (firstValue * secondValue / 100)).toString()
                "*" -> (firstValue * (secondValue / 100)).toString()
                "/" -> if (secondValue != 0.0) {
                    (firstValue / (secondValue / 100)).toString()
                } else {
                    "Ошибка"
                }
                else -> "Ошибка"
            }
            display.text = formatNumber(temporaryValue)
        } else if (secondValue != null) {
            temporaryValue = (secondValue / 100).toString()
            display.text = formatNumber(temporaryValue)
        } else {
            display.text = "Ошибка"
        }
    }

    private fun onPlusOrMinusButtonClicked(display: TextView) {
        temporaryValue = if (temporaryValue.startsWith("-")) {
            temporaryValue.removePrefix("-")
        } else {
            "-$temporaryValue"
        }
        display.text = temporaryValue
    }

    private fun onButtonWithSignClicked(sign: String, display: TextView) {
        firstValueEntered = temporaryValue
        temporaryValue = "0"
        signSelectionVariable = sign
    }

    private fun onButtonResulClicked(display: TextView) {
        val secondValue = temporaryValue.toDoubleOrNull()
        val firstValue = firstValueEntered.toDoubleOrNull()

        if (firstValue == null || secondValue == null) {
            display.text = "Ошибка"
            return
        }

        temporaryValue = when (signSelectionVariable) {
            "+" -> (firstValue + secondValue).toString()
            "-" -> (firstValue - secondValue).toString()
            "*" -> (firstValue * secondValue).toString()
            "/" -> if (secondValue != 0.0) {
                (firstValue / secondValue).toString()
            } else {
                "Ошибка"
            }
            else -> "Ошибка"
        }
        display.text = formatNumber(temporaryValue)
    }

    private fun formatNumber(value: String): String {
        val number = value.toDoubleOrNull() ?: return "Ошибка"
        return if (number == number.toLong().toDouble()) {
            number.toLong().toString()
        } else {
            String.format("%.10f", number).trimEnd('0').trimEnd('.')
        }
    }
}
