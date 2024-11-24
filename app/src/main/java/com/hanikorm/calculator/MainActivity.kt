package com.hanikorm.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels


class MainActivity : AppCompatActivity() {
    private val calculatorViewModel: CalculatorViewModel by viewModels()
    private lateinit var display: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.display)
        calculatorViewModel.firstValueEntered.observe(this) { value -> display.text = value }


        setupButtons()
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.buttonOtv).setOnClickListener { calculatorViewModel.onButtonResultClicked() }
        findViewById<Button>(R.id.buttonPlus).setOnClickListener { calculatorViewModel.onButtonWithSignClicked("+") }
        findViewById<Button>(R.id.buttonMinus).setOnClickListener { calculatorViewModel.onButtonWithSignClicked("-") }
        findViewById<Button>(R.id.buttondivide).setOnClickListener { calculatorViewModel.onButtonWithSignClicked("/") }
        findViewById<Button>(R.id.buttonmultiply).setOnClickListener { calculatorViewModel.onButtonWithSignClicked("*") }
        findViewById<Button>(R.id.buttonplusandminus).setOnClickListener { calculatorViewModel.onPlusOrMinusButtonClicked() }
        findViewById<Button>(R.id.buttonzp).setOnClickListener { calculatorViewModel.onCommaButtonClicked() }
        findViewById<Button>(R.id.buttonAC).setOnClickListener { calculatorViewModel.resetCalculator() }
        findViewById<Button>(R.id.buttonpercent).setOnClickListener { calculatorViewModel.onButtonPercentClicked() }
        findViewById<TextView>(R.id.display).setOnClickListener { calculatorViewModel.deleteLastDigit() }

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
                calculatorViewModel.onNumberButtonClicked(number)
            }
        }
    }
}
