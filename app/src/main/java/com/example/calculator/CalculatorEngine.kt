package com.example.calculator

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

object CalculatorEngine {

  sealed class EvalResult {
    data class Success(val value: Double, val formatted: String) : EvalResult()
    data class Error(val message: String) : EvalResult()
  }

  enum class AngleMode {
    DEGREE,
    RADIAN
  }

  fun evaluate(expression: String, angleMode: AngleMode = AngleMode.DEGREE): EvalResult {
    val clean = expression.trim()
    if (clean.isEmpty()) {
      return EvalResult.Error("")
    }

    return try {
      val parser = ExpressionParser(clean, angleMode)
      val result = parser.parse()
      if (result.isNaN()) {
        EvalResult.Error("خطای محاسبه")
      } else if (result.isInfinite()) {
        EvalResult.Error("تقسیم بر صفر")
      } else {
        EvalResult.Success(result, formatResult(result))
      }
    } catch (e: ArithmeticException) {
      EvalResult.Error(e.message ?: "خطا")
    } catch (e: Exception) {
      EvalResult.Error("عبارت نامعتبر")
    }
  }

  fun formatResult(value: Double): String {
    if (value.isNaN()) return "خطا"
    if (value.isInfinite()) return "بی‌نهایت"

    // If practically zero
    if (abs(value) < 1e-14) return "0"

    // For very large or very small numbers, use scientific notation
    if (abs(value) >= 1e12 || (abs(value) > 0 && abs(value) < 1e-7)) {
      val symbols = DecimalFormatSymbols(Locale.US)
      val df = DecimalFormat("0.######E0", symbols)
      return df.format(value).replace("E", " × 10^")
    }

    // High precision rounding to eliminate floating point issues (like 0.1 + 0.2 = 0.30000000000000004)
    val bd = try {
      BigDecimal(value.toString(), MathContext(12, RoundingMode.HALF_UP))
        .stripTrailingZeros()
    } catch (e: Exception) {
      BigDecimal(value)
    }

    val plain = bd.toPlainString()
    val parts = plain.split(".")
    val integerPart = parts[0]
    val formattedInteger = try {
      val longVal = integerPart.toLong()
      val symbols = DecimalFormatSymbols(Locale.US).apply { groupingSeparator = ',' }
      DecimalFormat("#,###", symbols).format(longVal)
    } catch (e: Exception) {
      integerPart
    }

    return if (parts.size > 1) {
      "$formattedInteger.${parts[1]}"
    } else {
      formattedInteger
    }
  }

  fun toPersianDigits(text: String): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val sb = StringBuilder()
    for (ch in text) {
      if (ch in '0'..'9') {
        sb.append(persianDigits[ch - '0'])
      } else {
        sb.append(ch)
      }
    }
    return sb.toString()
  }

  private class ExpressionParser(
    val rawExpression: String,
    val angleMode: AngleMode
  ) {
    private var pos = -1
    private var ch = ' '
    private val expression: String

    init {
      expression = preprocess(rawExpression)
    }

    private fun preprocess(expr: String): String {
      var s = expr
        .replace("×", "*")
        .replace("÷", "/")
        .replace("−", "-")
        .replace("—", "-")
        .replace("√", "sqrt")
        .replace("π", "PI")
        .replace("e", "E")
        .replace(" ", "")

      // Add implicit multiplication:
      // number followed by (, sqrt, sin, cos, tan, ln, log, PI, E
      val sb = StringBuilder()
      for (i in s.indices) {
        val current = s[i]
        sb.append(current)
        if (i < s.length - 1) {
          val next = s[i + 1]
          val isCurrentNumberOrClosing = (current.isDigit() || current == ')' || current == '%')
          val isNextFunctionOrParen = (next == '(' || next.isLetter())
          if (isCurrentNumberOrClosing && isNextFunctionOrParen) {
            sb.append('*')
          } else if (current == ')' && (next.isDigit() || next == '(')) {
            sb.append('*')
          }
        }
      }
      return sb.toString()
    }

    private fun nextChar() {
      pos++
      ch = if (pos < expression.length) expression[pos] else '\u0000'
    }

    private fun eat(charToEat: Char): Boolean {
      while (ch == ' ') nextChar()
      if (ch == charToEat) {
        nextChar()
        return true
      }
      return false
    }

    fun parse(): Double {
      nextChar()
      val x = parseExpression()
      if (pos < expression.length) {
        throw IllegalArgumentException("کاراکتر غیرمنتظره: $ch")
      }
      return x
    }

    // Addition and Subtraction
    private fun parseExpression(): Double {
      var x = parseTerm()
      while (true) {
        when {
          eat('+') -> x += parseTerm()
          eat('-') -> x -= parseTerm()
          else -> return x
        }
      }
    }

    // Multiplication, Division, Modulo
    private fun parseTerm(): Double {
      var x = parseFactor()
      while (true) {
        when {
          eat('*') -> x *= parseFactor()
          eat('/') -> {
            val divisor = parseFactor()
            if (abs(divisor) < 1e-15) throw ArithmeticException("تقسیم بر صفر")
            x /= divisor
          }
          else -> return x
        }
      }
    }

    // Exponentiation
    private fun parseFactor(): Double {
      var x = parsePrimary()
      if (eat('^')) {
        val exponent = parseFactor() // right-associative
        x = x.pow(exponent)
      }
      return x
    }

    // Numbers, parenthesized expr, unary ops, functions
    private fun parsePrimary(): Double {
      while (ch == ' ') nextChar()

      // Unary operators
      if (eat('+')) return parsePrimary()
      if (eat('-')) return -parsePrimary()

      var x: Double
      val startPos = pos

      if (eat('(')) {
        x = parseExpression()
        if (!eat(')')) {
          // If closing parenthesis is omitted at the end of typing, tolerate it
          if (ch != '\u0000') {
            throw IllegalArgumentException("پرانتز بسته وجود ندارد")
          }
        }
      } else if ((ch in '0'..'9') || ch == '.') {
        while ((ch in '0'..'9') || ch == '.') nextChar()
        val numStr = expression.substring(startPos, pos)
        x = numStr.toDoubleOrNull() ?: throw IllegalArgumentException("عدد نامعتبر: $numStr")
      } else if (ch.isLetter()) {
        while (ch.isLetter()) nextChar()
        val func = expression.substring(startPos, pos)
        
        when (func) {
          "PI" -> x = Math.PI
          "E" -> x = Math.E
          "sqrt" -> {
            val arg = parsePrimary()
            if (arg < 0) throw ArithmeticException("رادیکال عدد منفی")
            x = sqrt(arg)
          }
          "sin" -> {
            val arg = parsePrimary()
            val rad = if (angleMode == AngleMode.DEGREE) Math.toRadians(arg) else arg
            x = sin(rad)
            if (angleMode == AngleMode.DEGREE && abs(arg % 180.0) < 1e-10) x = 0.0
          }
          "cos" -> {
            val arg = parsePrimary()
            val rad = if (angleMode == AngleMode.DEGREE) Math.toRadians(arg) else arg
            x = cos(rad)
            if (angleMode == AngleMode.DEGREE && abs((arg - 90.0) % 180.0) < 1e-10) x = 0.0
          }
          "tan" -> {
            val arg = parsePrimary()
            if (angleMode == AngleMode.DEGREE && abs((arg - 90.0) % 180.0) < 1e-10) {
              throw ArithmeticException("تعریف نشده")
            }
            val rad = if (angleMode == AngleMode.DEGREE) Math.toRadians(arg) else arg
            x = tan(rad)
          }
          "ln" -> {
            val arg = parsePrimary()
            if (arg <= 0) throw ArithmeticException("دامنه نامعتبر")
            x = ln(arg)
          }
          "log" -> {
            val arg = parsePrimary()
            if (arg <= 0) throw ArithmeticException("دامنه نامعتبر")
            x = log10(arg)
          }
          else -> throw IllegalArgumentException("تابع ناشناخته: $func")
        }
      } else {
        throw IllegalArgumentException("کاراکتر غیرمنتظره: $ch")
      }

      // Check for trailing postfix percent '%'
      while (eat('%')) {
        x /= 100.0
      }

      return x
    }
  }
}
