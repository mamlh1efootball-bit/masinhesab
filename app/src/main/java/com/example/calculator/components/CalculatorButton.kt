package com.example.calculator.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ButtonType {
  NUMBER,
  FUNCTION,
  OPERATOR,
  EQUALS,
  SCIENTIFIC
}

@Composable
fun CalculatorButton(
  text: String,
  type: ButtonType,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  onLongClick: (() -> Unit)? = null,
  icon: ImageVector? = null,
  fontSize: TextUnit = 24.sp,
  testTag: String = "btn_$text",
  contentDescriptionText: String = text
) {
  val haptic = LocalHapticFeedback.current
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.92f else 1f,
    label = "button_scale"
  )

  val isDark = MaterialTheme.colorScheme.background.red < 0.5f

  val (bgColor, textColor) = when (type) {
    ButtonType.NUMBER -> {
      if (isDark) {
        Color(0xFF1E293B) to Color(0xFFF8FAFC)
      } else {
        Color(0xFFFFFFFF) to Color(0xFF0F172A)
      }
    }
    ButtonType.FUNCTION -> {
      if (isDark) {
        Color(0xFF334155) to Color(0xFF38BDF8)
      } else {
        Color(0xFFE2E8F0) to Color(0xFF0369A1)
      }
    }
    ButtonType.OPERATOR -> {
      Color(0xFFF97316) to Color(0xFFFFFFFF)
    }
    ButtonType.EQUALS -> {
      Color(0xFFEA580C) to Color(0xFFFFFFFF)
    }
    ButtonType.SCIENTIFIC -> {
      if (isDark) {
        Color(0xFF1E2433) to Color(0xFFA5B4FC)
      } else {
        Color(0xFFEEF2F6) to Color(0xFF4338CA)
      }
    }
  }

  Box(
    modifier = modifier
      .padding(4.dp)
      .scale(scale)
      .minimumInteractiveComponentSize()
      .clip(RoundedCornerShape(20.dp))
      .background(bgColor)
      .testTag(testTag)
      .semantics {
        contentDescription = contentDescriptionText
      }
      .clickable(
        interactionSource = interactionSource,
        indication = androidx.compose.material3.ripple(bounded = true, color = textColor.copy(alpha = 0.3f)),
        role = Role.Button
      ) {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        onClick()
      },
    contentAlignment = Alignment.Center
  ) {
    if (icon != null) {
      androidx.compose.material3.Icon(
        imageVector = icon,
        contentDescription = contentDescriptionText,
        tint = textColor
      )
    } else {
      Text(
        text = text,
        color = textColor,
        fontSize = fontSize,
        fontWeight = if (type == ButtonType.NUMBER) FontWeight.SemiBold else FontWeight.Bold
      )
    }
  }
}
