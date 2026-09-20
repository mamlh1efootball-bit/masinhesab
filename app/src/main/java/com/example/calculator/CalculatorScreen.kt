package com.example.calculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculator.components.ButtonType
import com.example.calculator.components.CalculatorButton
import com.example.calculator.components.CalculatorDisplay
import com.example.calculator.components.HistoryBottomSheet

@Composable
fun CalculatorScreen(
  viewModel: CalculatorViewModel = viewModel(),
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()

  Scaffold(
    modifier = modifier.fillMaxSize(),
    contentWindowInsets = WindowInsets.safeDrawing,
    containerColor = MaterialTheme.colorScheme.background
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .navigationBarsPadding(),
      contentAlignment = Alignment.Center
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .widthIn(max = 600.dp) // Adaptive design for tablets/foldables
          .padding(horizontal = 12.dp, vertical = 8.dp)
      ) {
        // Display Area (Top)
        CalculatorDisplay(
          uiState = uiState,
          onToggleScientific = { viewModel.toggleScientific() },
          onToggleAngleMode = { viewModel.toggleAngleMode() },
          onToggleHistory = { viewModel.toggleHistorySheet(true) },
          onTogglePersianDigits = { viewModel.togglePersianDigits() },
          modifier = Modifier.weight(1.1f)
        )

        // Scientific Keypad (Animated expansion)
        AnimatedVisibility(
          visible = uiState.isScientificExpanded,
          enter = expandVertically() + fadeIn(),
          exit = shrinkVertically() + fadeOut()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            // Scientific Row 1: sin, cos, tan, ln, log
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              CalculatorButton(
                text = "sin",
                type = ButtonType.SCIENTIFIC,
                fontSize = 15.sp,
                onClick = { viewModel.onFunction("sin") },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                testTag = "btn_sin"
              )
              CalculatorButton(
                text = "cos",
                type = ButtonType.SCIENTIFIC,
                fontSize = 15.sp,
                onClick = { viewModel.onFunction("cos") },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                testTag = "btn_cos"
              )
              CalculatorButton(
                text = "tan",
                type = ButtonType.SCIENTIFIC,
                fontSize = 15.sp,
                onClick = { viewModel.onFunction("tan") },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                testTag = "btn_tan"
              )
              CalculatorButton(
                text = "ln",
                type = ButtonType.SCIENTIFIC,
                fontSize = 15.sp,
                onClick = { viewModel.onFunction("ln") },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                testTag = "btn_ln"
              )
              CalculatorButton(
                text = "log",
                type = ButtonType.SCIENTIFIC,
                fontSize = 15.sp,
                onClick = { viewModel.onFunction("log") },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                testTag = "btn_log"
              )
            }

            // Scientific Row 2: √, x², ^, π, e
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              CalculatorButton(
                text = "√",
                type = ButtonType.SCIENTIFIC,
                fontSize = 18.sp,
                onClick = { viewModel.onFunction("√") },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                testTag = "btn_sqrt"
              )
              CalculatorButton(
                text = "x²",
                type = ButtonType.SCIENTIFIC,
                fontSize = 16.sp,
                onClick = { viewModel.onFunction("x²") },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                testTag = "btn_square"
              )
              CalculatorButton(
                text = "^",
                type = ButtonType.SCIENTIFIC,
                fontSize = 18.sp,
                onClick = { viewModel.onFunction("^") },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                testTag = "btn_pow"
              )
              CalculatorButton(
                text = "π",
                type = ButtonType.SCIENTIFIC,
                fontSize = 18.sp,
                onClick = { viewModel.onFunction("π") },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                testTag = "btn_pi"
              )
              CalculatorButton(
                text = "e",
                type = ButtonType.SCIENTIFIC,
                fontSize = 16.sp,
                onClick = { viewModel.onFunction("e") },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                testTag = "btn_e"
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Main Keypad Area (5 Rows)
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .weight(2.2f),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // Row 1: AC, ⌫, %, ÷
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            CalculatorButton(
              text = if (uiState.expression.isEmpty()) "AC" else "C",
              type = ButtonType.FUNCTION,
              fontSize = 22.sp,
              onClick = { viewModel.onClear() },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              testTag = "btn_clear"
            )
            CalculatorButton(
              text = "⌫",
              type = ButtonType.FUNCTION,
              icon = Icons.AutoMirrored.Filled.Backspace,
              contentDescriptionText = "پاک کردن نویسه",
              onClick = { viewModel.onBackspace() },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              testTag = "btn_backspace"
            )
            CalculatorButton(
              text = "%",
              type = ButtonType.FUNCTION,
              fontSize = 22.sp,
              onClick = { viewModel.onPercentage() },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              testTag = "btn_percent"
            )
            CalculatorButton(
              text = "÷",
              type = ButtonType.OPERATOR,
              fontSize = 26.sp,
              onClick = { viewModel.onOperator("÷") },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              testTag = "btn_divide"
            )
          }

          // Row 2: 7, 8, 9, ×
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            CalculatorButton(
              text = if (uiState.usePersianDigits) "۷" else "7",
              type = ButtonType.NUMBER,
              fontSize = 24.sp,
              onClick = { viewModel.onDigit("7") },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              testTag = "btn_7"
            )
            CalculatorButton(
              text = if (uiState.usePersianDigits) "۸" else "8",
              type = ButtonType.NUMBER,
              fontSize = 24.sp,
              onClick = { viewModel.onDigit("8") },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              testTag = "btn_8"
            )
            CalculatorButton(
              text = if (uiState.usePersianDigits) "۹" else "9",
              type = ButtonType.NUMBER,
              fontSize = 24.sp,
              onClick = { viewModel.onDigit("9") },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              testTag = "btn_9"
            )
            CalculatorButton(
              text = "×",
              type = ButtonType.OPERATOR,
              fontSize = 26.sp,
              onClick = { viewModel.onOperator("×") },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              testTag = "btn_multiply"
            )
          }

          // Row 3: 4, 5, 6, −
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            CalculatorButton(
              text = if (uiState.usePersianDigits) "۴" else "4",
              type = ButtonType.NUMBER,
              fontSize = 24.sp,
              onClick = { viewModel.onDigit("4") },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              testTag = "btn_4"
            )
            CalculatorButton(
              text = if (uiState.usePersianDigits) "۵" else "5",
              type = ButtonType.NUMBER,
              fontSize = 24.sp,
              onClick = { viewModel.onDigit("5") },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              testTag = "btn_5"
            )
            CalculatorButton(
              text = if (uiState.usePersianDigits) "۶" else "6",
              type = ButtonType.NUMBER,
              fontSize = 24.sp,
              onClick = { viewModel.onDigit("6") },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              testTag = "btn_6"
            )
            CalculatorButton(
              text = "−",
              type = ButtonType.OPERATOR,
              fontSize = 26.sp,
              onClick = { viewModel.onOperator("−") },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              testTag = "btn_subtract"
            )
          }

          // Row 4: 1, 2, 3, +
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            CalculatorButton(
              text = if (uiState.usePersianDigits) "۱" else "1",
              type = ButtonType.NUMBER,
              fontSize = 24.sp,
              onClick = { viewModel.onDigit("1") },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              testTag = "btn_1"
            )
            CalculatorButton(
              text = if (uiState.usePersianDigits) "۲" else "2",
              type = ButtonType.NUMBER,
              fontSize = 24.sp,
              onClick = { viewModel.onDigit("2") },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              testTag = "btn_2"
            )
            CalculatorButton(
              text = if (uiState.usePersianDigits) "۳" else "3",
              type = ButtonType.NUMBER,
              fontSize = 24.sp,
              onClick = { viewModel.onDigit("3") },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              testTag = "btn_3"
            )
            CalculatorButton(
              text = "+",
              type = ButtonType.OPERATOR,
              fontSize = 26.sp,
              onClick = { viewModel.onOperator("+") },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              testTag = "btn_add"
            )
          }

          // Row 5: ( ), 0, ., =
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            CalculatorButton(
              text = "( )",
              type = ButtonType.FUNCTION,
              fontSize = 20.sp,
              onClick = { viewModel.onParenthesis() },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              testTag = "btn_parenthesis"
            )
            CalculatorButton(
              text = if (uiState.usePersianDigits) "۰" else "0",
              type = ButtonType.NUMBER,
              fontSize = 24.sp,
              onClick = { viewModel.onDigit("0") },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              testTag = "btn_0"
            )
            CalculatorButton(
              text = if (uiState.usePersianDigits) "٫" else ".",
              type = ButtonType.NUMBER,
              fontSize = 26.sp,
              onClick = { viewModel.onDecimal() },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              testTag = "btn_decimal"
            )
            CalculatorButton(
              text = "=",
              type = ButtonType.EQUALS,
              fontSize = 28.sp,
              onClick = { viewModel.onEquals() },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              testTag = "btn_equals"
            )
          }
        }
      }
    }

    // Calculation History Bottom Sheet
    HistoryBottomSheet(
      isOpen = uiState.isHistorySheetOpen,
      onDismiss = { viewModel.toggleHistorySheet(false) },
      historyList = uiState.historyList,
      usePersianDigits = uiState.usePersianDigits,
      onClearHistory = { viewModel.clearHistory() },
      onSelectHistoryItem = { item, useResult ->
        viewModel.restoreFromHistory(item, useResult)
      }
    )
  }
}
