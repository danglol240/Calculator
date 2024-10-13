package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal
import java.util.Stack

class MainActivity : AppCompatActivity() {

    private lateinit var workingsTV: TextView
    private lateinit var resultTV: TextView
    private var workings: String = ""
    private var currentResult: String = ""
    private var isOperatorPressed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        workingsTV = findViewById(R.id.workingsTV)
        resultTV = findViewById(R.id.resultTV)
    }

    private fun updateWorkings(value: String) {
        workings += value
        workingsTV.text = workings
    }

    fun numAct(view: View) {
        val button = view as Button
        if (isOperatorPressed) {
            currentResult = ""
            isOperatorPressed = false
        }
        updateWorkings(button.text.toString())
    }

    fun opAct(view: View) {
        val button = view as Button
        val operator = button.text.toString()
        if (workings.isEmpty() && operator == "-") {
            updateWorkings(operator)
        } else if (!isOperatorPressed) {
            updateWorkings(operator)
            isOperatorPressed = true
        }
    }

    fun AllClearAct(view: View) {
        workings = ""
        currentResult = ""
        workingsTV.text = ""
        resultTV.text = ""
    }

    fun backspaceAct(view: View) {
        if (workings.isNotEmpty()) {
            workings = workings.dropLast(1)
            workingsTV.text = workings
        }
    }

    fun EqualFunc(view: View) {
        try {
            currentResult = eval(workings)
            resultTV.text = currentResult
        } catch (e: Exception) {
            resultTV.text = "Error"
        }
    }

    // Evaluate simple expressions
    private fun eval(expression: String): String {
        return try {
            val sanitizedExpression = expression.replace("x", "*")
            val result = calculate(sanitizedExpression)
            result.toString()
        } catch (e: Exception) {
            "Error"
        }
    }

    // Basic expression evaluation for +, -, *, /
    private fun calculate(expression: String): BigDecimal {
        // Convert the string into a list of operands and operators
        val tokens = expression.split("(?<=[-+*/])|(?=[-+*/])".toRegex()).filter { it.isNotEmpty() }
        val operandStack = Stack<BigDecimal>()
        val operatorStack = Stack<Char>()

        // Helper function to apply the operator
        fun applyOperator(op: Char, b: BigDecimal, a: BigDecimal): BigDecimal {
            return when (op) {
                '+' -> a.add(b)
                '-' -> a.subtract(b)
                '*' -> a.multiply(b)
                '/' -> a.divide(b)
                else -> throw IllegalArgumentException("Unknown operator: $op")
            }
        }

        tokens.forEach { token ->
            when {
                token.matches(Regex("-?\\d+(\\.\\d+)?")) -> operandStack.push(BigDecimal(token)) // It's a number
                token.matches(Regex("[+\\-*/]")) -> {
                    while (operatorStack.isNotEmpty()) {
                        operandStack.push(applyOperator(operatorStack.pop(), operandStack.pop(), operandStack.pop()))
                    }
                    operatorStack.push(token[0])
                }
            }
        }

        while (operatorStack.isNotEmpty()) {
            operandStack.push(applyOperator(operatorStack.pop(), operandStack.pop(), operandStack.pop()))
        }

        return operandStack.pop()
    }
}
