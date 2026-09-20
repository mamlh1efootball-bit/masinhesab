package com.example.calculator.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.CalculationHistoryItem
import com.example.calculator.CalculatorEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryBottomSheet(
  isOpen: Boolean,
  onDismiss: () -> Unit,
  historyList: List<CalculationHistoryItem>,
  usePersianDigits: Boolean,
  onClearHistory: () -> Unit,
  onSelectHistoryItem: (CalculationHistoryItem, Boolean) -> Unit
) {
  if (!isOpen) return

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val context = LocalContext.current

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 8.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .testTag("history_sheet")
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
          Text(
            text = "تاریخچه محاسبات",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          )
        }

        if (historyList.isNotEmpty()) {
          TextButton(
            onClick = onClearHistory,
            modifier = Modifier.testTag("clear_history_button")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "پاک کردن",
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
              text = "پاک کردن",
              color = MaterialTheme.colorScheme.error,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      HorizontalDivider(
        modifier = Modifier.padding(vertical = 12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
      )

      if (historyList.isEmpty()) {
        // Empty State
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
          )
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "هنوز محاسبه‌ای انجام نشده است",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "محاسبات شما پس از زدن دکمه مساوی (=) در اینجا ذخیره می‌شوند",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
          )
        }
      } else {
        // History Items List
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .height(360.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(historyList, key = { it.id }) { item ->
            val exprText = if (usePersianDigits) CalculatorEngine.toPersianDigits(item.expression) else item.expression
            val resText = if (usePersianDigits) CalculatorEngine.toPersianDigits(item.result) else item.result
            val timeText = if (usePersianDigits) CalculatorEngine.toPersianDigits(item.timestamp) else item.timestamp

            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                  onSelectHistoryItem(item, true)
                }
                .testTag("history_item_${item.id}"),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
              ),
              shape = RoundedCornerShape(16.dp)
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp)
              ) {
                // Time & Copy
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = timeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                  )
                  IconButton(
                    onClick = {
                      val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                      val clip = ClipData.newPlainText("نتیجه", item.result)
                      clipboard.setPrimaryClip(clip)
                      Toast.makeText(context, "در حافظه کپی شد", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(28.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.ContentCopy,
                      contentDescription = "کپی",
                      tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }

                // Expression
                Text(
                  text = exprText,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                      onSelectHistoryItem(item, false)
                    },
                  textAlign = TextAlign.End
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Result
                Text(
                  text = "= $resText",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  ),
                  modifier = Modifier.fillMaxWidth(),
                  textAlign = TextAlign.End
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
