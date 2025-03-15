package com.example.calculatorbegin

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsController
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
//import org.mariuszgromada.math.mxparser.Expression
//import net.objecthunter.exp4j.ExpressionBuilder

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    private lateinit var textViewHistory: TextView
    private lateinit var textViewResult: TextView
    private lateinit var editTextParams: EditText
    private var lastResult: Boolean = true

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
        assignTextView()
        assignButton()
    }
    private fun assignTextView() {
        textViewHistory = findViewById(R.id.textViewHistory)
        textViewResult = findViewById(R.id.textViewResult)
        editTextParams = findViewById(R.id.editTextParams)
    }

    private fun assignButton() {
        findViewById<Button>(R.id.button0).setOnClickListener { pressParams("0") }
        findViewById<Button>(R.id.button1).setOnClickListener { pressParams("1") }
        findViewById<Button>(R.id.button2).setOnClickListener { pressParams("2") }
        findViewById<Button>(R.id.button3).setOnClickListener { pressParams("3") }
        findViewById<Button>(R.id.button4).setOnClickListener { pressParams("4") }
        findViewById<Button>(R.id.button5).setOnClickListener { pressParams("5") }
        findViewById<Button>(R.id.button6).setOnClickListener { pressParams("6") }
        findViewById<Button>(R.id.button7).setOnClickListener { pressParams("7") }
        findViewById<Button>(R.id.button8).setOnClickListener { pressParams("8") }
        findViewById<Button>(R.id.button9).setOnClickListener { pressParams("9") }

        findViewById<Button>(R.id.buttonDot).setOnClickListener { pressOperator(".") }
        findViewById<Button>(R.id.buttonAdd).setOnClickListener { pressOperator("+") }
        findViewById<Button>(R.id.buttonSub).setOnClickListener { pressOperator("-") }
        findViewById<Button>(R.id.buttonMul).setOnClickListener { pressOperator("x") }
        findViewById<Button>(R.id.buttonDiv).setOnClickListener { pressOperator("/") }
        findViewById<Button>(R.id.buttonPct).setOnClickListener { pressOperator("%") }

        findViewById<Button>(R.id.buttonEq).setOnClickListener { getEqResult() }
        findViewById<Button>(R.id.buttonClr).setOnClickListener { clearParams() }
        findViewById<Button>(R.id.buttonDel).setOnClickListener { deleteParams() }
    }

    private fun pressOperator(key: String) {
        Log.i("MainActivity", "press operator: $key")
        editTextParams.text.append(key)
        getTmpResult()
    }

    private fun pressParams(key: String) {
        Log.i("MainActivity", "press key: $key")
        if (lastResult == true) {
            editTextParams.text.clear()
        }
        editTextParams.text.append(key)
        getTmpResult()
    }

    private fun deleteParams() {
        var params = editTextParams.text.toString()
        Log.i("MainActivity", "delete back from : $params")
        if (params.isEmpty()) {
            Log.w("MainActivity",  "empty params")
            return
        }
        editTextParams.text.delete(editTextParams.text.length-1, editTextParams.text.length)
        getTmpResult()
    }

    private fun clearParams() {
        editTextParams.text.clear()
    }

    private fun udpateHistory() {
        val params = editTextParams.text
        val res = textViewResult.text
        val his = textViewHistory.text
        textViewHistory.text = "$his\n$params $res"
    }

    private fun setResult(params: String, res0: String) {
        var res = res0
        if (!params.contains('.') && res.endsWith(".0")) {
            res = res.removeSuffix(".0")
        }
        textViewResult.text = "=$res"
    }

    private fun setError(err: String) {
        Log.w("MainActivity",  err)
        textViewResult.text = err
    }

    private fun getTmpResult() {
        val params = editTextParams.text.toString()
        Log.i("MainActivity", "get tmp result of : $params")

        lastResult = false
        try {
            val result = getResult(params)
            setResult(params, result)
        } catch (e: Exception) {
            return
        }
    }

    private fun getEqResult() {
        var params = editTextParams.text.toString()
        if (params.isEmpty()) {
            editTextParams.text.append("0")
            params = "0"
        }
        Log.i("MainActivity", "get result of : $params")

        lastResult = true
        val result = try {
            getResult(params)
        } catch (e: Exception) {
            setError("params ${params} err: ${e.message}")
            return
        }
        setResult(params, result)
        udpateHistory()
        editTextParams.text.clear()
        editTextParams.append(textViewResult.text.toString().removePrefix("="))
    }

    private fun getResult(params: String): String {
         try {
             var tmpParams = replaceParams(params)
             return evaluateExpression(tmpParams).toString()
         } catch (e: Exception) {
             throw e
         }
    }

    private fun replaceParams(params: String): String {
        var rs = params
        rs = rs.replace("x", "*")
        rs = rs.replace("%", "*(0.01)")
        return rs
    }

    private fun evaluateExpression(params: String): Double {
        return try {
            val expObj = ExpressionBuilder(params)
            expObj.build().evaluate()
        } catch (e: Exception) {
            throw e
        }
    }
}