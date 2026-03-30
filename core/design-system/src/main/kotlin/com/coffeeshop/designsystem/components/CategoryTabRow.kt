package com.coffeeshop.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.coffeeshop.designsystem.DarkBrown
import com.coffeeshop.designsystem.Secondary
import com.coffeeshop.designsystem.White

/**
 * Горизонтальный скролл-ряд категорий с подчёркиванием активного таба.
 * Используется в верхней части экрана каталога
 * (ФИРМЕННЫЕ / КОФЕ / НЕ КОФЕ / МАТЧА и т.д.).
 */
@Composable
fun CategoryTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier
            .background(White)
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        itemsIndexed(tabs) { index, tab ->
            CategoryTab(
                text = tab,
                selected = index == selectedIndex,
                onClick = { onTabSelected(index) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryTabRowPreview() = CategoryTabRow(
    tabs = listOf("Фирменные", "Кофе", "Не кофе", "Матча", "Выпечка"),
    selectedIndex = 1,
    onTabSelected = {},
)

@Composable
private fun CategoryTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val textColor = if (selected) DarkBrown else Secondary
    val indicatorColor = DarkBrown

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(40.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp)
            .drawBehind {
                if (selected) {
                    drawLine(
                        color = indicatorColor,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 2.dp.toPx(),
                    )
                }
            },
    ) {
        Text(
            text = text.uppercase(),
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            letterSpacing = 1.sp,
            color = textColor,
        )
    }
}
