package com.coffeeshop.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.coffeeshop.designsystem.Beige
import com.coffeeshop.designsystem.DarkBrown
import com.coffeeshop.designsystem.White

// TODO: перенести в ProductDetailViewModel (feature:catalog) когда будет создан фича-модуль.
//  Здесь только временный контракт для composable — composable не должен знать о бизнес-слое.
data class ProductDetailState(
    val name: String,
    val imageUrl: String?,
    val volumes: List<String>,
    val selectedVolume: String?,
    val modifierGroups: List<ModifierGroup>,
    val quantity: Int,
    val comment: String,
    val totalPrice: String,
)

data class ModifierGroup(
    val title: String,
    val options: List<String>,
    val selectedOption: String?,
)

/**
 * Боттом-шит с деталями товара — открывается при тапе на карточку в каталоге.
 * Содержит: фото + название, группы [OptionSelectorGroup] для выбора объёма
 * и модификаторов, поле комментария, счётчик [CoffeeStepper] и кнопку
 * «Добавить N ₽» ([CoffeeButtonFilled]).
 * Все состояния хранятся снаружи во ViewModel, шит только отображает.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailBottomSheet(
    state: ProductDetailState,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onVolumeSelected: (String) -> Unit,
    onModifierSelected: (groupTitle: String, option: String) -> Unit,
    onQuantityDecrement: () -> Unit,
    onQuantityIncrement: () -> Unit,
    onCommentChange: (String) -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Шапка: фото + название
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                AsyncImage(
                    model = state.imageUrl,
                    contentDescription = state.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Beige),
                )
                Text(
                    text = state.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkBrown,
                )
            }

            // Объём
            if (state.volumes.isNotEmpty()) {
                OptionSelectorGroup(
                    title = "Объём",
                    options = state.volumes,
                    selectedOption = state.selectedVolume,
                    onOptionSelected = onVolumeSelected,
                )
            }

            // Модификаторы
            state.modifierGroups.forEach { group ->
                OptionSelectorGroup(
                    title = group.title,
                    options = group.options,
                    selectedOption = group.selectedOption,
                    onOptionSelected = { onModifierSelected(group.title, it) },
                )
            }

            // Комментарий
            CoffeeInputField(
                label = "Комментарий",
                value = state.comment,
                onValueChange = onCommentChange,
                placeholder = "Пожелания к заказу",
                modifier = Modifier.fillMaxWidth(),
            )

            // Количество + итог + кнопка
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CoffeeStepper(
                    value = state.quantity,
                    onDecrement = onQuantityDecrement,
                    onIncrement = onQuantityIncrement,
                )

                Spacer(modifier = Modifier.width(12.dp))

                CoffeeButtonFilled(
                    text = "Добавить  ${state.totalPrice}",
                    onClick = onAddToCart,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun ProductDetailBottomSheetPreview() {
    val previewState = ProductDetailState(
        name = "Капучино",
        imageUrl = null,
        volumes = listOf("250 мл", "350 мл", "450 мл"),
        selectedVolume = "350 мл",
        modifierGroups = listOf(
            ModifierGroup(
                title = "Молоко",
                options = listOf("Обычное", "Овсяное", "Кокосовое"),
                selectedOption = "Обычное",
            ),
        ),
        quantity = 1,
        comment = "",
        totalPrice = "280 ₽",
    )
    ProductDetailBottomSheet(
        state = previewState,
        sheetState = rememberModalBottomSheetState(),
        onDismiss = {},
        onVolumeSelected = {},
        onModifierSelected = { _, _ -> },
        onQuantityDecrement = {},
        onQuantityIncrement = {},
        onCommentChange = {},
        onAddToCart = {},
    )
}
