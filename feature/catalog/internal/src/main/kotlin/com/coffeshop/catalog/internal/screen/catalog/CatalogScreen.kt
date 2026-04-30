package com.coffeshop.catalog.internal.screen.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coffeeshop.common.model.products.CategoryType
import com.coffeeshop.common.model.products.ModifierCategory
import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.model.products.display
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.model.support.Size
import com.coffeeshop.common.model.support.display
import com.coffeeshop.designsystem.components.CategoryTabRow
import com.coffeeshop.designsystem.components.HomeTopBar
import com.coffeeshop.designsystem.components.LoadingOverlay
import com.coffeeshop.designsystem.components.ModifierGroup
import com.coffeeshop.designsystem.components.ProductCard
import com.coffeeshop.designsystem.components.ProductDetailBottomSheet
import com.coffeeshop.designsystem.components.ProductDetailState
import com.coffeeshop.designsystem.components.RetryOverlay

@Composable
fun MyCatalogScreen(
    onError: () -> Unit,
    onProfileClick: () -> Unit,
    viewModelFactory: ViewModelProvider.Factory,
) = MyCatalogScreenInternal(
    onError = onError,
    onProfileClick = onProfileClick,
    viewModelFactory = viewModelFactory,
)

@Composable
internal fun MyCatalogScreenInternal(
    onError: () -> Unit,
    onProfileClick: () -> Unit,
    viewModelFactory: ViewModelProvider.Factory,
    viewModel: MyCatalogViewModel = viewModel(
        modelClass = MyCatalogViewModel::class,
        factory = viewModelFactory,
    )
) {
    Scaffold(
        topBar = {
            HomeTopBar(
                onProfileClick = onProfileClick,
                modifier = Modifier.statusBarsPadding(),
            )
        }
    ) { paddingValues ->
        MyCatalogScreenContent(
            viewModel = viewModel,
            onError = onError,
            paddingValues = paddingValues
        )
    }
}

@Composable
private fun MyCatalogScreenContent(
    viewModel: MyCatalogViewModel,
    onError: () -> Unit,
    paddingValues: PaddingValues = PaddingValues()
) {
    val uiState: State<MyCatalogModel> = viewModel.uiState.collectAsState()
    when (uiState.value.state) {
        MyCatalogUiState.Loading -> LoadingOverlay()
        is MyCatalogUiState.Error -> RetryOverlay(
            onRetry = {
                viewModel.reduce(event = MyCatalogEvent.RetryAfterErrorClicked)
            }
        )

        MyCatalogUiState.Success -> MyCatalogScreenSuccessContent(
            viewModel = viewModel,
            paddingValues = paddingValues
        )

        is MyCatalogUiState.ShowingProductDetail -> {
            MyCatalogScreenSuccessContent(
                viewModel = viewModel,
                paddingValues = paddingValues
            )
            ProductDetailBottomSheetWrapper(viewModel = viewModel)
        }
    }
}

@Composable
private fun MyCatalogScreenSuccessContent(
    viewModel: MyCatalogViewModel,
    paddingValues: PaddingValues = PaddingValues()
) {
    val uiState = viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        CategoryTabRow(
            tabs = CategoryType.entries.map { it.russianName },
            selectedIndex = CategoryType.entries.indexOf(uiState.value.selectedCategoryType),
            onTabSelected = { index ->
                viewModel.reduce(
                    event = MyCatalogEvent.ChangeCategoryType(
                        categoryType = CategoryType.entries[index]
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )

        when (uiState.value.selectedCategoryType) {
            CategoryType.SIGNATURE -> MyCatalogScreenSuccessColumnContent(viewModel = viewModel)
            else -> MyCatalogScreenSuccessGridContent(viewModel = viewModel)
        }
    }
}

@Composable
private fun MyCatalogScreenSuccessColumnContent(
    viewModel: MyCatalogViewModel
) {
    val uiState = viewModel.uiState.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(
            start = 12.dp,
            end = 12.dp,
            top = 12.dp,
            bottom = 80.dp,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
    ) {
        items(uiState.value.products, key = { it.productId.value }) { product ->
            ProductCard(
                name = product.productName.value,
                price = try {
                    product.prices.values.max().display()
                } catch (_: Throwable) {
                    "EMPTY PRICE"
                },
                imageUrl = product.imageUrl,
                isFavourite = product.productId in uiState.value.favouriteProductIds,
                onFavouriteToggle = {
                    viewModel.reduce(MyCatalogEvent.ToggleProductFavorite(product.productId))
                },
                onClick = {
                    viewModel.reduce(MyCatalogEvent.GetProductDetail(product.productId))
                },
            )
        }
    }
}

@Composable
private fun MyCatalogScreenSuccessGridContent(
    viewModel: MyCatalogViewModel
) {
    val uiState = viewModel.uiState.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            start = 12.dp,
            end = 12.dp,
            top = 12.dp,
            bottom = 80.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
    ) {
        items(uiState.value.products, key = { it.productId.value }) { product ->
            ProductCard(
                name = product.productName.value,
                price = try {
                    product.prices.values.max().display()
                } catch (_: Throwable) {
                    "EMPTY PRICE"
                },
                imageUrl = product.imageUrl,
                isFavourite = product.productId in uiState.value.favouriteProductIds,
                onFavouriteToggle = {
                    viewModel.reduce(MyCatalogEvent.ToggleProductFavorite(product.productId))
                },
                onClick = {
                    viewModel.reduce(MyCatalogEvent.GetProductDetail(product.productId))
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailBottomSheetWrapper(viewModel: MyCatalogViewModel) {
    val model = viewModel.uiState.collectAsState().value
    val product = model.selectedProduct ?: return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val volumes = product.availableSizes.sortedBy { it.ml }.map { it.display() }
    val selectedVolumeStr = model.selectedVolume?.display()

    val modifierGroups = product.compatibleModifiers
        .groupBy { it.category }
        .map { (category, modifiers) ->
            ModifierGroup(
                title = category.display(),
                options = modifiers.map { it.additiveName.value },
                selectedOption = model.selectedModifiers[category]?.additiveName?.value,
            )
        }

    val basePrice = model.selectedVolume?.let { product.prices[it] }
        ?: product.prices.values.minOrNull()
        ?: Price(0, 0)
    val modifiersTotal = model.selectedModifiers.values
        .fold(Price(0, 0)) { acc, m -> acc + m.price }
    val totalPrice = (basePrice + modifiersTotal) * model.quantity

    ProductDetailBottomSheet(
        state = ProductDetailState(
            name = product.productName.value,
            imageUrl = product.imageUrl,
            volumes = volumes,
            selectedVolume = selectedVolumeStr,
            modifierGroups = modifierGroups,
            quantity = model.quantity,
            comment = model.comment,
            totalPrice = totalPrice.display(),
        ),
        sheetState = sheetState,
        onDismiss = { viewModel.reduce(MyCatalogEvent.DismissProductDetail) },
        onVolumeSelected = { volumeStr ->
            val size = Size.entries.find { it.display() == volumeStr }
            size?.let { viewModel.reduce(MyCatalogEvent.SelectVolume(it)) }
        },
        onModifierSelected = { _, optionName ->
            val modifier = product.compatibleModifiers.find { it.additiveName.value == optionName }
            modifier?.let { viewModel.reduce(MyCatalogEvent.SelectModifier(it)) }
        },
        onQuantityDecrement = { viewModel.reduce(MyCatalogEvent.DecrementQuantity) },
        onQuantityIncrement = { viewModel.reduce(MyCatalogEvent.IncrementQuantity) },
        onCommentChange = { viewModel.reduce(MyCatalogEvent.CommentChanged(it)) },
        onAddToCart = { /* TODO: подключить к feature:cart */ },
    )
}
