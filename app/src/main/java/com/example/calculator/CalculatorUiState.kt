package com.example.calculator

data class CalculationHistoryItem(
  val id: Long = System.currentTimeMillis(),
  val expression: String,
  val result: String,
  val timestamp: String
)

data class CalculatorUiState(
  val expression: String = "",
  val previewResult: String = "",
  val isEvaluated: Boolean = false,
  val isError: Boolean = false,
  val errorMessage: String = "",
  val isScientificExpanded: Boolean = false,
  val angleMode: CalculatorEngine.AngleMode = CalculatorEngine.AngleMode.DEGREE,
  val isHistorySheetOpen: Boolean = false,
  val historyList: List<CalculationHistoryItem> = emptyList(),
  val usePersianDigits: Boolean = false
)
