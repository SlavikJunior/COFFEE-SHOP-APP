package com.coffeeshop.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.coffeeshop.designsystem.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeeshop.designsystem.DarkBrown
import com.coffeeshop.designsystem.Secondary
import com.coffeeshop.designsystem.White

/**
 * Счётчик количества «− N +» с ограничением минимального значения.
 * Используется в боттом-шите товара рядом с кнопкой «Добавить».
 * [minValue] по умолчанию = 1, кнопка «−» блокируется при достижении минимума.
 */
@Composable
fun CoffeeStepper(
    value: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
    minValue: Int = 1,
) {
    val shape = RoundedCornerShape(4.dp)

    Row(
        modifier = modifier
            .border(width = 1.dp, color = DarkBrown.copy(alpha = 0.3f), shape = shape)
            .clip(shape)
            .background(White),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        StepperButton(
            onClick = onDecrement,
            enabled = value > minValue,
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = stringResource(R.string.cd_stepper_decrement),
                tint = if (value > minValue) DarkBrown else Secondary,
                modifier = Modifier.size(16.dp),
            )
        }

        Text(
            text = value.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DarkBrown,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 36.dp),
        )

        StepperButton(onClick = onIncrement, enabled = true) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.cd_stepper_increment),
                tint = DarkBrown,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Preview
@Composable
private fun CoffeeStepperPreview() = CoffeeStepper(
    value = 2,
    onDecrement = {},
    onIncrement = {}
)

@Composable
private fun StepperButton(
    onClick: () -> Unit,
    enabled: Boolean,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
