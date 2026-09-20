package com.example.calculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalculatorViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(CalculatorUiState())
  val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

  fun onDigit(digit: String) {
    _uiState.update { current ->
      val newExpr = if (current.isEvaluated) {
        digit
      } else {
        current.expression + digit
      }
      val preview = calculatePreview(newExpr, current.angleMode)
      current.copy(
        expression = newExpr,
        previewResult = preview,
        isEvaluated = false,
        isError = false,
        errorMessage = ""
      )
    }
  }

  fun onOperator(op: String) {
    _uiState.update { current ->
      var expr = current.expression

      if (current.isEvaluated && current.previewResult.isNotEmpty() && !current.isError) {
        // Continue from last evaluated result
        val cleanResult = current.previewResult.replace(",", "")
        expr = cleanResult
      }

      if (expr.isEmpty()) {
        if (op == "−" || op == "-") {
          expr = "−"
        } else {
          expr = "0 $op "
        }
      } else {
        val trimmed = expr.trimEnd()
        // If last token was an operator, replace it
        if (trimmed.endsWith("+") || trimmed.endsWith("−") || trimmed.endsWith("×") || trimmed.endsWith("÷")) {
          val lastOpIdx = trimmed.lastIndexOfAny(charArrayOf('+', '−', '×', '÷'))
          expr = if (lastOpIdx > 0) {
            trimmed.substring(0, lastOpIdx).trimEnd() + " $op "
          } else {
            "$op "
          }
        } else {
          expr = "$trimmed $op "
        }
      }

      val preview = calculatePreview(expr, current.angleMode)
      current.copy(
        expression = expr,
        previewResult = preview,
        isEvaluated = false,
        isError = false
      )
    }
  }

  fun onDecimal() {
    _uiState.update { current ->
      var expr = current.expression
      if (current.isEvaluated) {
        expr = "0."
      } else if (expr.isEmpty() || expr.endsWith(" ") || expr.endsWith("(") || expr.endsWith("√")) {
        expr += "0."
      } else {
        // Find last number token
        val lastSpace = expr.lastIndexOf(' ')
        val lastToken = if (lastSpace >= 0) expr.substring(lastSpace + 1) else expr
        if (!lastToken.contains(".")) {
          expr += "."
        }
      }

      val preview = calculatePreview(expr, current.angleMode)
      current.copy(
        expression = expr,
        previewResult = preview,
        isEvaluated = false
      )
    }
  }

  fun onPercentage() {
    _uiState.update { current ->
      if (current.expression.isEmpty()) return@update current
      val trimmed = current.expression.trimEnd()
      val lastChar = trimmed.last()
      if (lastChar.isDigit() || lastChar == ')') {
        val newExpr = "$trimmed%"
        val preview = calculatePreview(newExpr, current.angleMode)
        current.copy(expression = newExpr, previewResult = preview, isEvaluated = false)
      } else {
        current
      }
    }
  }

  fun onToggleSign() {
    _uiState.update { current ->
      if (current.expression.isEmpty()) {
        return@update current.copy(expression = "−")
      }
      var expr = current.expression.trim()
      
      // If ends with a number, wrap in negation or remove it
      val regex = Regex("([0-9.]+)$")
      val match = regex.find(expr)
      if (match != null) {
        val num = match.value
        val prefix = expr.substring(0, match.range.first)
        if (prefix.endsWith("(−")) {
          // Remove (-
          val newPrefix = prefix.substring(0, prefix.length - 2)
          expr = "$newPrefix$num"
        } else {
          expr = "$prefix(−$num)"
        }
      } else if (expr.endsWith(")")) {
        // If ends with parenthesis negation, toggle back
        val negParenRegex = Regex("\\(−([0-9.]+)\\)$")
        val negMatch = negParenRegex.find(expr)
        if (negMatch != null) {
          val num = negMatch.groupValues[1]
          expr = expr.substring(0, negMatch.range.first) + num
        }
      }

      val preview = calculatePreview(expr, current.angleMode)
      current.copy(expression = expr, previewResult = preview, isEvaluated = false)
    }
  }

  fun onParenthesis() {
    _uiState.update { current ->
      val expr = current.expression
      val openCount = expr.count { it == '(' }
      val closeCount = expr.count { it == ')' }

      val newExpr = if (expr.isEmpty() || expr.endsWith(" ") || expr.endsWith("(")) {
        "$expr("
      } else {
        val trimmed = expr.trimEnd()
        val last = trimmed.last()
        if (openCount > closeCount && (last.isDigit() || last == ')')) {
          "$trimmed)"
        } else {
          "$trimmed × ("
        }
      }

      val preview = calculatePreview(newExpr, current.angleMode)
      current.copy(expression = newExpr, previewResult = preview, isEvaluated = false)
    }
  }

  fun onFunction(funcName: String) {
    _uiState.update { current ->
      val expr = if (current.isEvaluated) "" else current.expression
      val newExpr = when (funcName) {
        "√" -> if (expr.isEmpty() || expr.endsWith(" ") || expr.endsWith("(")) "${expr}√(" else "$expr × √("
        "x²" -> if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) "$expr ^ 2" else expr
        "^" -> if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) "$expr ^ " else expr
        "sin", "cos", "tan", "ln", "log" -> if (expr.isEmpty() || expr.endsWith(" ") || expr.endsWith("(")) "$expr$funcName(" else "$expr × $funcName("
        "π" -> if (expr.isEmpty() || expr.endsWith(" ") || expr.endsWith("(")) "${expr}π" else "$expr × π"
        "e" -> if (expr.isEmpty() || expr.endsWith(" ") || expr.endsWith("(")) "${expr}e" else "$expr × e"
        else -> expr
      }

      val preview = calculatePreview(newExpr, current.angleMode)
      current.copy(expression = newExpr, previewResult = preview, isEvaluated = false)
    }
  }

  fun onBackspace() {
    _uiState.update { current ->
      if (current.isEvaluated) {
        return@update current.copy(expression = "", previewResult = "", isEvaluated = false)
      }
      var expr = current.expression
      if (expr.isEmpty()) return@update current

      // If ends with multi-char function name like "sin(", "cos(", "tan(", "ln(", "log(", "√("
      for (fn in listOf("sin(", "cos(", "tan(", "log(", "ln(", "√(")) {
        if (expr.endsWith(fn)) {
          expr = expr.substring(0, expr.length - fn.length)
          val preview = calculatePreview(expr, current.angleMode)
          return@update current.copy(expression = expr, previewResult = preview)
        }
      }

      // If ends with spaced operator like " + "
      if (expr.endsWith(" ")) {
        expr = expr.trimEnd()
        if (expr.endsWith("+") || expr.endsWith("−") || expr.endsWith("×") || expr.endsWith("÷") || expr.endsWith("^")) {
          expr = expr.substring(0, expr.length - 1).trimEnd()
        }
      } else {
        expr = expr.substring(0, expr.length - 1)
      }

      val preview = calculatePreview(expr, current.angleMode)
      current.copy(expression = expr, previewResult = preview, isError = false)
    }
  }

  fun onClear() {
    _uiState.update { current ->
      current.copy(
        expression = "",
        previewResult = "",
        isEvaluated = false,
        isError = false,
        errorMessage = ""
      )
    }
  }

  fun onEquals() {
    _uiState.update { current ->
      val expr = current.expression.trim()
      if (expr.isEmpty()) return@update current

      when (val result = CalculatorEngine.evaluate(expr, current.angleMode)) {
        is CalculatorEngine.EvalResult.Success -> {
          val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
          val newHistory = CalculationHistoryItem(
            expression = expr,
            result = result.formatted,
            timestamp = timeFormat.format(Date())
          )
          current.copy(
            expression = expr,
            previewResult = result.formatted,
            isEvaluated = true,
            isError = false,
            errorMessage = "",
            historyList = listOf(newHistory) + current.historyList
          )
        }
        is CalculatorEngine.EvalResult.Error -> {
          current.copy(
            isError = true,
            errorMessage = result.message.ifEmpty { "خطای محاسبه" }
          )
        }
      }
    }
  }

  fun toggleScientific() {
    _uiState.update { it.copy(isScientificExpanded = !it.isScientificExpanded) }
  }

  fun toggleAngleMode() {
    _uiState.update { current ->
      val newMode = if (current.angleMode == CalculatorEngine.AngleMode.DEGREE) {
        CalculatorEngine.AngleMode.RADIAN
      } else {
        CalculatorEngine.AngleMode.DEGREE
      }
      val preview = calculatePreview(current.expression, newMode)
      current.copy(angleMode = newMode, previewResult = preview)
    }
  }

  fun toggleHistorySheet(open: Boolean) {
    _uiState.update { it.copy(isHistorySheetOpen = open) }
  }

  fun clearHistory() {
    _uiState.update { it.copy(historyList = emptyList()) }
  }

  fun restoreFromHistory(item: CalculationHistoryItem, useResult: Boolean = true) {
    _uiState.update { current ->
      val text = if (useResult) item.result.replace(",", "") else item.expression
      val preview = calculatePreview(text, current.angleMode)
      current.copy(
        expression = text,
        previewResult = preview,
        isEvaluated = useResult,
        isHistorySheetOpen = false
      )
    }
  }

  fun togglePersianDigits() {
    _uiState.update { it.copy(usePersianDigits = !it.usePersianDigits) }
  }

  private fun calculatePreview(expression: String, angleMode: CalculatorEngine.AngleMode): String {
    val clean = expression.trim()
    if (clean.isEmpty()) return ""

    // Don't calculate if ends with bare operator or open parenthesis
    val last = clean.last()
    if (last == '+' || last == '−' || last == '×' || last == '÷' || last == '^' || last == '(') {
      return ""
    }

    return when (val res = CalculatorEngine.evaluate(clean, angleMode)) {
      is CalculatorEngine.EvalResult.Success -> res.formatted
      is CalculatorEngine.EvalResult.Error -> ""
    }
  }
}
