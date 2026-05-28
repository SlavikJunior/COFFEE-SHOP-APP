package com.coffeeshop.product_detail.internal.screen.product_detail

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.model.products.Category
import com.coffeeshop.common.model.products.CategoryType
import com.coffeeshop.common.model.products.ModifierCategory
import com.coffeeshop.common.model.products.Modifier as _Modifier
import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.model.support.Size
import com.coffeeshop.common.model.support.display
import com.coffeeshop.designsystem.common.Beige
import com.coffeeshop.designsystem.common.DarkBrown
import com.coffeeshop.designsystem.components.CoffeeButtonFilled
import com.coffeeshop.designsystem.components.CoffeeInputField
import com.coffeeshop.designsystem.components.CoffeeStepper
import com.coffeeshop.designsystem.components.OptionSelectorGroup
import com.coffeeshop.product_detail.internal.R

@Composable
internal fun ProductDetailScreen(
    productId: ID,
    viewModelFactory: ViewModelProvider.Factory,
) = ProductDetailScreenInternal(
    productId = productId,
    viewModelFactory = viewModelFactory,
)

@Composable
internal fun ProductDetailScreenInternal(
    productId: ID,
    viewModelFactory: ViewModelProvider.Factory,
) {
    val viewModel = viewModel<ProductDetailViewModel>(factory = viewModelFactory)

    LaunchedEffect(productId) {
        viewModel.reduce(ProductDetailUiStateEvent.LoadProduct(productId))
    }

    val clickListener: (ProductDetailUiStateEvent) -> Unit = { event -> viewModel.reduce(event) }

    ProductDetailContent(
        state = viewModel.uiState.collectAsState().value,
        onVolumeSelected = { clickListener(ProductDetailUiStateEvent.SelectVolume(it)) },
        onModifierSelected = { groupTitle, option -> clickListener(ProductDetailUiStateEvent.SelectModifier(groupTitle, option)) },
        onQuantityDecrement = { clickListener(ProductDetailUiStateEvent.DecrementQuantity(it)) },
        onQuantityIncrement = { clickListener(ProductDetailUiStateEvent.IncrementQuantity(it)) },
        onCommentChange = { clickListener(ProductDetailUiStateEvent.CommentChanged(it)) },
        onAddToCart = { clickListener(ProductDetailUiStateEvent.AddToCart) },
    )
}

@Composable
private fun ProductDetailContent(
    state: ProductDetailUiState,
    onVolumeSelected: (String) -> Unit,
    onModifierSelected: (groupTitle: String, option: String) -> Unit,
    onQuantityDecrement: (Int) -> Unit,
    onQuantityIncrement: (Int) -> Unit,
    onCommentChange: (String) -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            AsyncImage(
                model = state.selectedProduct?.imageUrl,
                contentDescription = state.selectedProduct?.productName?.value,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Beige),
            )
            Text(
                text = state.selectedProduct?.productName?.value.orEmpty(),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkBrown,
            )
        }

        if (state.volumes.isNotEmpty()) {
            OptionSelectorGroup(
                title = stringResource(R.string.volume_text),
                options = state.volumes,
                selectedOption = state.selectedVolume!!.display(),
                onOptionSelected = onVolumeSelected,
            )
        }

        state.modifierGroups.forEach { group ->
            OptionSelectorGroup(
                title = group.title,
                options = group.options,
                selectedOption = group.selectedOption,
                onOptionSelected = { onModifierSelected(group.title, it) },
            )
        }

        CoffeeInputField(
            label = stringResource(R.string.comment_text),
            value = state.comment,
            onValueChange = onCommentChange,
            placeholder = stringResource(R.string.comment_text_placeholder),
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CoffeeStepper(
                value = state.quantity,
                onDecrement = { onQuantityDecrement(state.quantity) },
                onIncrement = { onQuantityIncrement(state.quantity) },
            )

            Spacer(modifier = Modifier.width(12.dp))

            CoffeeButtonFilled(
                text = stringResource(R.string.add_to_cart_button_text, state.totalPrice),
                onClick = onAddToCart,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
private fun ProductDetailContentPreview() = ProductDetailContent(
    state = ProductDetailUiState(
        status = ProductDetailUiStateStatus.Success,
        selectedProduct = ProductWithModifiers(
            productId = ID.random(),
            productName = NameModel("КАПУЧИНО"),
            description = null,
            category = Category(
                categoryId = ID.random(),
                categoryName = NameModel("КОФЕ"),
                categoryType = CategoryType.COFFEE,
                sortOrder = 1
            ),
            prices = mapOf(Size.MEDIUM to Price(300, 0)),
            availableSizes = setOf(Size.MEDIUM),
            imageUrl = null,
            isAvailable = true,
            compatibleModifiers = listOf(
                _Modifier(
                    additiveId = ID.random(),
                    additiveName = NameModel("Сахар"),
                    price = Price(25, 0),
                    category = ModifierCategory.SYRUP,
                    isAvailable = true,
                )
            )
        ),
        selectedCategoryType = CategoryType.COFFEE,
        quantity = 1,
        totalPrice = "325 ₽",
    ),
    onVolumeSelected = {},
    onModifierSelected = { _, _ -> },
    onQuantityDecrement = {},
    onQuantityIncrement = {},
    onCommentChange = {},
    onAddToCart = {},
)
