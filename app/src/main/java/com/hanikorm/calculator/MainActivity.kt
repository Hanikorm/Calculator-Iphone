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

        val buttonResult = findViewById<Button>(R.id.buttonOtv)
        buttonResult.setOnClickListener {
            onButtonResultClicked()
        }

        val buttonPlus = findViewById<Button>(R.id.buttonPlus)
        buttonPlus.setOnClickListener {
            onButtonWithSignClicked("+")
        }

        val buttonMinus = findViewById<Button>(R.id.buttonMinus)
        buttonMinus.setOnClickListener {
            onButtonWithSignClicked("-")
        }

        val buttonDivide = findViewById<Button>(R.id.buttondivide)
        buttonDivide.setOnClickListener {
            onButtonWithSignClicked("/")
        }

        val buttonMultiply = findViewById<Button>(R.id.buttonmultiply)
        buttonMultiply.setOnClickListener {
            onButtonWithSignClicked("*")
        }

        val buttonPlusOrMinus = findViewById<Button>(R.id.buttonplusandminus)
        buttonPlusOrMinus.setOnClickListener {
            onPlusOrMinusButtonClicked()
        }

        val buttonComma = findViewById<Button>(R.id.buttonzp)
        buttonComma.setOnClickListener {
            onCommaButtonClicked()
        }

        val buttonAC = findViewById<Button>(R.id.buttonAC)
        buttonAC.setOnClickListener {
            resetCalculator(display)
        }

        val buttonPercent = findViewById<Button>(R.id.buttonpercent)
        buttonPercent.setOnClickListener {
            onButtonPercentClicked()
        }

        val buttonNumber0 = findViewById<Button>(R.id.buttonnull)
        buttonNumber0.setOnClickListener {
            onNumberButtonClicked("0")
        }

        val buttonNumber1 = findViewById<Button>(R.id.buttonNumber1)
        buttonNumber1.setOnClickListener {
            onNumberButtonClicked("1")
        }

        val buttonNumber2 = findViewById<Button>(R.id.buttonNumber2)
        buttonNumber2.setOnClickListener {
            onNumberButtonClicked("2")
        }

        val buttonNumber3 = findViewById<Button>(R.id.buttonNumber3)
        buttonNumber3.setOnClickListener {
            onNumberButtonClicked("3")
        }

        val buttonNumber4 = findViewById<Button>(R.id.buttonNumber4)
        buttonNumber4.setOnClickListener {
            onNumberButtonClicked("4")
        }

        val buttonNumber5 = findViewById<Button>(R.id.buttonNumber5)
        buttonNumber5.setOnClickListener {
            onNumberButtonClicked("5")
        }

        val buttonNumber6 = findViewById<Button>(R.id.buttonNumber6)
        buttonNumber6.setOnClickListener {
            onNumberButtonClicked("6")
        }

        val buttonNumber7 = findViewById<Button>(R.id.buttonNumber7)
        buttonNumber7.setOnClickListener {
            onNumberButtonClicked("7")
        }

        val buttonNumber8 = findViewById<Button>(R.id.buttonNumber8)
        buttonNumber8.setOnClickListener {
            onNumberButtonClicked("8")
        }

        val buttonNumber9 = findViewById<Button>(R.id.buttonnumber9)
        buttonNumber9.setOnClickListener {
            onNumberButtonClicked("9")
        }
    }

    private fun onNumberButtonClicked(number: String) {
        val display = findViewById<TextView>(R.id.display)
        if (temporaryValue.length < 9) {
            if (temporaryValue == "0" || temporaryValue == "-0") {
                temporaryValue = if (temporaryValue == "-0") "-$number" else number
            } else {
                temporaryValue += number
            }
        }
        display.text = temporaryValue
    }

    private fun onCommaButtonClicked() {
        if (!temporaryValue.contains(".")) {
            temporaryValue += "."
        }
        val display = findViewById<TextView>(R.id.display)
        display.text = temporaryValue
    }

    private fun onPlusOrMinusButtonClicked() {
        val display = findViewById<TextView>(R.id.display)
        temporaryValue = if (temporaryValue.startsWith("-")) {
            temporaryValue.drop(1)
        } else {
            "-$temporaryValue"
        }
        display.text = temporaryValue
    }

    private fun onButtonWithSignClicked(sign: String) {
        if (temporaryValue.isNotEmpty()) {
            firstValueEntered = temporaryValue
            signSelectionVariable = sign
            temporaryValue = "0"
        }
    }

    private fun onButtonPercentClicked() {
        val display = findViewById<TextView>(R.id.display)
        if (firstValueEntered.isNotEmpty() && signSelectionVariable.isNotEmpty()) {
            val secondValue = temporaryValue.toDoubleOrNull() ?: return
            val percentValue = (firstValueEntered.toDouble() * secondValue) / 100
            temporaryValue = percentValue.toString()
            display.text = temporaryValue
        } else {
            val value = temporaryValue.toDoubleOrNull() ?: return
            temporaryValue = (value / 100).toString()
            display.text = formatNumber(temporaryValue)
        }
    }

    private fun onButtonResultClicked() {
        val display = findViewById<TextView>(R.id.display)
        if (firstValueEntered.isNotEmpty() && signSelectionVariable.isNotEmpty()) {
            val result = when (signSelectionVariable) {
                "+" -> firstValueEntered.toDouble() + temporaryValue.toDouble()
                "-" -> firstValueEntered.toDouble() - temporaryValue.toDouble()
                "*" -> firstValueEntered.toDouble() * temporaryValue.toDouble()
                "/" -> {
                    if (temporaryValue.toDouble() == 0.0) {
                        display.text = "Ошибка"
                        return
                    } else {
                        firstValueEntered.toDouble() / temporaryValue.toDouble()
                    }
                }
                else -> 0.0
            }
            temporaryValue = result.toString()
            display.text = formatNumber(temporaryValue)
            signSelectionVariable = ""
        }
    }

    private fun resetCalculator(display: TextView) {
        display.text = "0"
        temporaryValue = "0"
        firstValueEntered = ""
        signSelectionVariable = ""
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
