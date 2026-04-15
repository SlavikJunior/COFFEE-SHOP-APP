package com.coffeeshop.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeeshop.designsystem.CoffeeTheme
import com.coffeeshop.designsystem.DarkBrown
import com.coffeeshop.designsystem.Secondary
import com.coffeeshop.designsystem.White

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DesignSystemShowcase() {
    CoffeeTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            ShowcaseSection("Top Bars") {
                HomeTopBar(onProfileClick = {})
                ProfileTopBar(title = "Профиль", onCloseClick = {})
                ScreenTopBar(title = "Корзина", onCloseClick = {})
            }

            ShowcaseSection("Category Tabs") {
                CategoryTabRow(
                    tabs = listOf("Фирменные", "Кофе", "Не кофе", "Матча", "Выпечка"),
                    selectedIndex = 1,
                    onTabSelected = {},
                )
            }

            ShowcaseSection("Buttons") {
                CoffeeButton(
                    text = "Прислать SMS-код",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                )
                CoffeeButtonFilled(
                    text = "Добавить  280 ₽",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                )
                CoffeeButton(
                    text = "Disabled",
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            ShowcaseSection("Text Fields") {
                CoffeeInputField(
                    label = "Номер телефона",
                    value = "",
                    onValueChange = {},
                    placeholder = "+7 (999) 000-00-00",
                )
                CoffeeInputField(
                    label = "SMS-код",
                    value = "1234",
                    onValueChange = {},
                )
                CoffeeProfileField(
                    label = "Имя",
                    value = "Вячеслав",
                    onValueChange = {},
                )
                CoffeeProfileField(
                    label = "Почта",
                    value = "user@example.com",
                    onValueChange = {},
                )
            }

            ShowcaseSection("Option Selector") {
                OptionSelectorGroup(
                    title = "Объём",
                    options = listOf("250 мл", "350 мл", "450 мл"),
                    selectedOption = "350 мл",
                    onOptionSelected = {},
                )
                OptionSelectorGroup(
                    title = "Молоко",
                    options = listOf("Обычное", "Овсяное", "Кокосовое"),
                    selectedOption = "Обычное",
                    onOptionSelected = {},
                )
            }

            ShowcaseSection("Stepper & FAB") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    CoffeeStepper(value = 1, onDecrement = {}, onIncrement = {})
                    CoffeeStepper(value = 3, onDecrement = {}, onIncrement = {})
                    CartFab(onClick = {}, itemCount = 0)
                    CartFab(onClick = {}, itemCount = 5)
                }
            }

            ShowcaseSection("Favourite Button") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FavouriteButton(isFavourite = false, onToggle = {})
                    FavouriteButton(isFavourite = true, onToggle = {})
                }
            }

            ShowcaseSection("Product Card") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProductCard(
                        name = "Капучино",
                        price = "280 ₽",
                        imageUrl = null,
                        onClick = {},
                        modifier = Modifier.weight(1f),
                    )
                    ProductCard(
                        name = "Флэт уайт с овсяным молоком",
                        price = "320 ₽",
                        imageUrl = null,
                        onClick = {},
                        isFavourite = true,
                        onFavouriteToggle = {},
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            ShowcaseSection("Toggle") {
                CoffeeToggleRow(
                    label = "Уведомления",
                    checked = true,
                    onCheckedChange = {},
                )
                CoffeeToggleRow(
                    label = "Push-уведомления",
                    checked = false,
                    onCheckedChange = {},
                )
            }

            ShowcaseSection("Section Header & Links") {
                SectionHeader(title = "Настройки")
                ProfileLinkRow(label = "История заказов", onClick = {})
                ProfileLinkRow(label = "Обратная связь", onClick = {})
            }
        }
    }
}

@Composable
private fun ShowcaseSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = Secondary,
        )
        HorizontalDivider(color = DarkBrown.copy(alpha = 0.15f))
        content()
    }
}
