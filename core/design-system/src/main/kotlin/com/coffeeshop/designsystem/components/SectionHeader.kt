package com.coffeeshop.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.coffeeshop.designsystem.DarkBrown
import com.coffeeshop.designsystem.Secondary

/**
 * Заголовок секции: текст all-caps серым цветом + горизонтальный разделитель.
 * Используется для визуального разбиения длинных экранов на блоки,
 * например в истории заказов или настройках профиля.
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp,
            color = Secondary,
            modifier = Modifier.padding(vertical = 12.dp),
        )
        HorizontalDivider(color = Secondary.copy(alpha = 0.2f))
    }
}

@Preview(showBackground = true)
@Composable
private fun SectionHeaderPreview() = SectionHeader(title = "История заказов")

/**
 * Кликабельная строка-ссылка с разделителем снизу.
 * Используется на экране профиля для навигационных пунктов:
 * «История заказов» → экран заказов, «Обратная связь» → чат с бариста.
 */
@Composable
fun ProfileLinkRow(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp,
            color = DarkBrown,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 14.dp),
        )
        HorizontalDivider(color = Secondary.copy(alpha = 0.2f))
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileLinkRowPreview() = ProfileLinkRow(
    label = "История заказов",
    onClick = {},
)
