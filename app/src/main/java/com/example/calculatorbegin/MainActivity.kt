package com.example.calculatorbegin

import android.annotation.SuppressLint
import android.os.Bundle
import android.service.autofill.FillEventHistory
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var textViewHistory: TextView
    private lateinit var textViewResult: TextView
    private lateinit var editTextParams: EditText
    private var paramsTyping: String? = ""
    private var operator: String? = null
    private var firstValue: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewHistory = findViewById(R.id.textViewHistory)
        textViewResult = findViewById(R.id.textViewResult)
        editTextParams = findViewById(R.id.editTextParams)

        val buttonNumbers = arrayOf(
            R.id.button0, R.id.button1, R.id.button2,
            R.id.button3, R.id.button4, R.id.button5,
            R.id.button6, R.id.button7, R.id.button8, R.id.button9
        )

        for (i in buttonNumbers.indices) {
            findViewById<Button>(buttonNumbers[i]).setOnClickListener {
                val s = i.toString()
                editTextParams.text.append(s)
                paramsTyping += s
            }
        }
        findViewById<Button>(R.id.buttonDot).setOnClickListener {
            editTextParams.text.append(".")
            paramsTyping += "."
        }
        findViewById<Button>(R.id.buttonAdd).setOnClickListener { setOperator("+") }
        findViewById<Button>(R.id.buttonSub).setOnClickListener { setOperator("-") }
        findViewById<Button>(R.id.buttonMul).setOnClickListener { setOperator("*") }
        findViewById<Button>(R.id.buttonDiv).setOnClickListener { setOperator("/") }

        findViewById<Button>(R.id.buttonEq).setOnClickListener { getResult() }
        findViewById<Button>(R.id.buttonClr).setOnClickListener { clearParams() }
    }

    private fun setOperator(op: String) {
        if (!paramsTyping.isNullOrEmpty()) {
            operator = op
            editTextParams.text.append(op)
            firstValue = paramsTyping.toString().toDouble()
            paramsTyping = ""
        }
    }

    private fun clearParams() {
        editTextParams.text.clear()
        operator = null
        firstValue = 0.0
        paramsTyping = ""
    }

    @SuppressLint("SetTextI18n")
    private fun getResult() {
        if (!paramsTyping.isNullOrEmpty()) {
            val secondValue = paramsTyping?.toDoubleOrNull()!!
            val result = when (operator) {
                "+" -> firstValue + secondValue
                "-" -> firstValue - secondValue
                "*" -> firstValue * secondValue
                "/" -> {
                    if (secondValue != 0.0) {
                        firstValue / secondValue
                    } else {
                        "Error: Division by zero"
                    }
                }

                else -> "Error: Unknown operator"
            }
            textViewResult.setText("=" + result.toString())
            textViewHistory.setText(textViewHistory.text.toString()+ "\n" + editTextParams.text.toString() + "=" + result.toString())
            editTextParams.text.clear()
        }
        operator = null
        firstValue = 0.0
        paramsTyping = ""
    }
}