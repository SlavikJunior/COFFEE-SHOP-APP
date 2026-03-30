package com.coffeeshop.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeeshop.designsystem.Beige
import com.coffeeshop.designsystem.DarkBrown
import com.coffeeshop.designsystem.Secondary
import com.coffeeshop.designsystem.White

/**
 * Группа чипов-опций с заголовком: выбор объёма (250 мл / 350 мл),
 * добавок (сироп, молоко) или других модификаторов.
 * Используется внутри [ProductDetailBottomSheet] — по одному блоку
 * на каждую группу модификаторов, пришедших с бэкенда.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OptionSelectorGroup(
    title: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = title.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp,
            color = Secondary,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            options.forEach { option ->
                OptionChip(
                    text = option,
                    selected = option == selectedOption,
                    onClick = { onOptionSelected(option) },
                )
            }
        }
    }
}

@Preview
@Composable
private fun OptionSelectorGroupPreview() = OptionSelectorGroup(
    title = "TITLE PREVIEW",
    options = listOf("OPTIONS 1", "OPTIONS 2", "OPTIONS 3"),
    selectedOption = null,
    onOptionSelected = { }
)

@Composable
private fun OptionChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(6.dp)
    Box(
        modifier = Modifier
            .widthIn(min = 72.dp)
            .clip(shape)
            .background(if (selected) DarkBrown else Beige)
            .border(
                width = 1.dp,
                color = if (selected) DarkBrown else Secondary.copy(alpha = 0.3f),
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = if (selected) White else DarkBrown,
            textAlign = TextAlign.Center,
        )
    }
}
