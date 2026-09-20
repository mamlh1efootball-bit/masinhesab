package com.example.calculator.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.CalculatorEngine
import com.example.calculator.CalculatorUiState

@Composable
fun CalculatorDisplay(
  uiState: CalculatorUiState,
  onToggleScientific: () -> Unit,
  onToggleAngleMode: () -> Unit,
  onToggleHistory: () -> Unit,
  onTogglePersianDigits: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  // Scroll to the end whenever expression changes
  LaunchedEffect(uiState.expression) {
    scrollState.animateScrollTo(scrollState.maxValue)
  }

  val displayExpression = if (uiState.usePersianDigits) {
    CalculatorEngine.toPersianDigits(uiState.expression)
  } else {
    uiState.expression
  }

  val displayPreview = if (uiState.usePersianDigits && uiState.previewResult.isNotEmpty()) {
    CalculatorEngine.toPersianDigits(uiState.previewResult)
  } else {
    uiState.previewResult
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.Bottom
  ) {
    // Top Controls Bar
    Row(
      modifier = modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // App Title
        Text(
          text = "ماشین حساب",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          ),
          modifier = Modifier.testTag("app_title")
        )

        // Angle Mode Badge (DEG / RAD)
        FilterChip(
          selected = uiState.angleMode == CalculatorEngine.AngleMode.RADIAN,
          onClick = onToggleAngleMode,
          label = {
            Text(
              text = if (uiState.angleMode == CalculatorEngine.AngleMode.DEGREE) "DEG" else "RAD",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
          ),
          modifier = Modifier.height(30.dp).testTag("angle_mode_chip")
        )
      }

      // Actions Row (Persian toggle, Scientific toggle, History)
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        // Persian digits toggle
        IconButton(
          onClick = onTogglePersianDigits,
          modifier = Modifier
            .size(38.dp)
            .testTag("persian_digits_toggle")
        ) {
          Box(
            modifier = Modifier
              .size(30.dp)
              .clip(CircleShape)
              .background(
                if (uiState.usePersianDigits) MaterialTheme.colorScheme.primaryContainer
                else Color.Transparent
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (uiState.usePersianDigits) "۱۲۳" else "123",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (uiState.usePersianDigits) MaterialTheme.colorScheme.onPrimaryContainer
              else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Scientific keypad toggle
        IconButton(
          onClick = onToggleScientific,
          modifier = Modifier
            .size(38.dp)
            .testTag("scientific_toggle")
        ) {
          Icon(
            imageVector = Icons.Default.Functions,
            contentDescription = "توابع پیشرفته",
            tint = if (uiState.isScientificExpanded) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
          )
        }

        // History button with count badge
        IconButton(
          onClick = onToggleHistory,
          modifier = Modifier
            .size(38.dp)
            .testTag("history_button")
        ) {
          BadgedBox(
            badge = {
              if (uiState.historyList.isNotEmpty()) {
                Badge(
                  containerColor = MaterialTheme.colorScheme.primary,
                  contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                  val countStr = if (uiState.historyList.size > 99) "+99" else uiState.historyList.size.toString()
                  Text(text = if (uiState.usePersianDigits) CalculatorEngine.toPersianDigits(countStr) else countStr)
                }
              }
            }
          ) {
            Icon(
              imageVector = Icons.Default.History,
              contentDescription = "تاریخچه محاسبات",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }

    // Display Surface Box
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp)),
      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
      tonalElevation = 2.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.End
      ) {
        // Expression Line (Scrollable)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (displayExpression.isEmpty()) "0" else displayExpression,
            color = if (displayExpression.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.onSurface,
            fontSize = if (displayExpression.length > 14) 28.sp else 38.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.End,
            maxLines = 1,
            modifier = Modifier.testTag("expression_text")
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Result / Live Preview Line
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Copy button for result
          if (uiState.previewResult.isNotEmpty() && !uiState.isError) {
            IconButton(
              onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("نتیجه محاسبه", uiState.previewResult)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "نتیجه در حافظه کپی شد", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier
                .size(32.dp)
                .testTag("copy_result_button")
            ) {
              Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "کپی نتیجه",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
              )
            }
          } else {
            Spacer(modifier = Modifier.width(32.dp))
          }

          // Error or Result Text
          if (uiState.isError) {
            Text(
              text = uiState.errorMessage,
              color = MaterialTheme.colorScheme.error,
              fontSize = 20.sp,
              fontWeight = FontWeight.Medium,
              textAlign = TextAlign.End,
              modifier = Modifier.testTag("error_text")
            )
          } else {
            Text(
              text = if (displayPreview.isNotEmpty()) "= $displayPreview" else "",
              color = if (uiState.isEvaluated) MaterialTheme.colorScheme.primary
              else MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = if (uiState.isEvaluated) 32.sp else 24.sp,
              fontWeight = if (uiState.isEvaluated) FontWeight.Bold else FontWeight.Normal,
              textAlign = TextAlign.End,
              maxLines = 1,
              modifier = Modifier.testTag("preview_text")
            )
          }
        }
      }
    }
  }
}
