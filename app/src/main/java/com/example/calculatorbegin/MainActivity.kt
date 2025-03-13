package com.example.calculatorbegin

import android.annotation.SuppressLint
//import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsController
//import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    private lateinit var textViewHistory: TextView
    private lateinit var textViewResult: TextView
    private lateinit var editTextParams: EditText
    private var paramsTyping: String = ""
    private var operator: String = ""
    private var firstParam: String = "0"
    private var lastResult: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.ic_launcher_background)
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            window.statusBarColor = ContextCompat.getColor(this, R.color.ic_launcher_background)
        }

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
            if (paramsTyping.contains('.')) {
                Log.w("MainActivity", "Duplicate dot")
                return@setOnClickListener
            }
            if (paramsTyping.isEmpty()) {
                editTextParams.text.append("0.")
                paramsTyping += "0."
                return@setOnClickListener
            }
            editTextParams.text.append(".")
            paramsTyping += "."
        }
        findViewById<Button>(R.id.buttonAdd).setOnClickListener { setOperator("+") }
        findViewById<Button>(R.id.buttonSub).setOnClickListener { setOperator("-") }
        findViewById<Button>(R.id.buttonMul).setOnClickListener { setOperator("x") }
        findViewById<Button>(R.id.buttonDiv).setOnClickListener { setOperator("/") }
        findViewById<Button>(R.id.buttonPct).setOnClickListener { setOperator("%") }

        findViewById<Button>(R.id.buttonEq).setOnClickListener { getResult() }
        findViewById<Button>(R.id.buttonClr).setOnClickListener { clearParams() }
        findViewById<Button>(R.id.buttonDel).setOnClickListener { deleteParams() }
    }

    private fun setOperator(op: String) {
        if (paramsTyping.isEmpty() && firstParam.isEmpty()) {
            editTextParams.text.clear()
            paramsTyping = textViewResult.text.toString().replace("=", "")
            if (paramsTyping.isEmpty()) {
                paramsTyping = "0"
            }
            editTextParams.text.append(paramsTyping)
        } else if (paramsTyping.isEmpty() && operator.isNotEmpty()) {
            paramsTyping = firstParam
            deleteParams()
        } else if (operator == "%") {
            paramsTyping = if (paramsTyping.isEmpty()) {
                "0.01"
            } else {
                (paramsTyping.toDouble() / 100).toString()
            }
            return
        }
        operator = op
        editTextParams.text.append(op)
        firstParam = paramsTyping
        paramsTyping = ""
    }

    private fun deleteParams() {
        Log.i("MainActivity", "start deleteParams firstParam=$firstParam operator=$operator paramsTyping=$paramsTyping")
        if (paramsTyping.isEmpty() && operator.isEmpty()) {
            Log.w("MainActivity",  "empty params")
            return
        }
        editTextParams.text.delete(editTextParams.text.length - 1, editTextParams.text.length)
        if(paramsTyping.isNotEmpty()) {
            paramsTyping = paramsTyping.substring(0, paramsTyping.length - 1)
        } else {
            operator = ""
            paramsTyping = firstParam
            firstParam = "0"
        }

        Log.i("MainActivity", "end deleteParams firstParam=$firstParam operator=$operator paramsTyping=$paramsTyping")
    }

    private fun clearParams() {
//        editTextParams.text.clear()
        operator = ""
        firstParam = ""
        paramsTyping = ""
    }

    private fun setResult(params: String, res0: String) {
        var res = res0
        if (!params.contains('.') && res.endsWith(".0")) {
            res = res.replace(".0", "")
        }
        textViewResult.text = "=$res"
        val his = textViewHistory.text
        textViewHistory.text = "$his\n$params =$res"
    }

    private fun setError(err: String) {
        textViewResult.text = err
        Log.w("MainActivity",  err)
    }

    private fun getResult() {
        if (operator.isEmpty() || paramsTyping.isEmpty()) {
            if (operator.isNotEmpty()) {
                deleteParams()
            }
            if (paramsTyping.isNotEmpty()) {
                firstParam = paramsTyping
            }
            if (firstParam.isEmpty()) {
                editTextParams.setText("0")
            }
            setResult(editTextParams.text.toString(), firstParam)
            clearParams()
            Log.w("MainActivity", "operator or second value is null")
            return
        }
        val secondValue = paramsTyping.toDouble()
        val firstValue = firstParam.toDouble()
        val result = when (operator) {
            "+" -> firstValue + secondValue
            "-" -> firstValue - secondValue
            "x" -> firstValue * secondValue
            "/" -> {
                if (secondValue != 0.0) {
                    firstValue / secondValue
                } else {
                    setError("Error: Division by zero")
                    clearParams()
                    return
                }
            }
            else -> {
                setError("Error: Unknown operator")
                clearParams()
                return
            }
        }
        setResult(editTextParams.text.toString(), result.toString())
        clearParams()
        lastResult = true
    }
}