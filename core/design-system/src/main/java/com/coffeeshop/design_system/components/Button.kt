package com.coffeeshop.design_system.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeeshop.design_system.DarkBrown
import com.coffeeshop.design_system.White

/**
 * Кнопка с обводкой (белый фон, тёмная рамка).
 * Используется для вторичных/нейтральных действий:
 * «Прислать SMS-код» (логин), «Войти» (OTP), «Выйти» и «Сохранить» (профиль).
 */
@Composable
fun CoffeeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = White,
            contentColor = DarkBrown,
            disabledContainerColor = White,
            disabledContentColor = DarkBrown.copy(alpha = 0.4f),
        ),
        modifier = modifier
            .height(44.dp)
            .border(
                width = 1.dp,
                color = if (enabled) DarkBrown else DarkBrown.copy(alpha = 0.4f),
                shape = RoundedCornerShape(4.dp),
            ),
    ) {
        Text(
            text = text.uppercase(),
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun CoffeeButtonPreview() = CoffeeButton(
    text = "CoffeeButtonPreview",
    onClick = {},
    modifier = Modifier,
    enabled = true
)

/**
 * Акцентная кнопка с тёмной заливкой.
 * Используется для главного CTA на экране:
 * «Добавить 200 ₽» (боттом-шит товара), «Оформить заказ» (корзина).
 */
@Composable
fun CoffeeButtonFilled(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DarkBrown,
            contentColor = White,
            disabledContainerColor = DarkBrown.copy(alpha = 0.4f),
            disabledContentColor = White,
        ),
        modifier = modifier.height(44.dp),
    ) {
        Text(
            text = text.uppercase(),
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun CoffeeButtonFilledPreview() = CoffeeButtonFilled(
    text = "CoffeeButtonFilledPreview",
    onClick = {},
    modifier = Modifier,
    enabled = true
)