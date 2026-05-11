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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coffeeshop.common.model.products.CategoryType
import com.coffeeshop.designsystem.components.CategoryTabRow
import com.coffeeshop.designsystem.components.HomeTopBar
import com.coffeeshop.designsystem.components.LoadingOverlay
import com.coffeeshop.designsystem.components.ProductCard
import com.coffeeshop.designsystem.components.RetryOverlay

@Composable
fun CatalogScreen(
    viewModelFactory: ViewModelProvider.Factory,
) = CatalogScreenInternal(
    viewModelFactory = viewModelFactory,
)

@Composable
internal fun CatalogScreenInternal(
    viewModelFactory: ViewModelProvider.Factory,
    viewModel: CatalogViewModel = viewModel(
        modelClass = CatalogViewModel::class,
        factory = viewModelFactory,
    )
) {
    Scaffold(
        topBar = {
            HomeTopBar(
                onProfileClick = { viewModel.reduce(CatalogUiStateEvent.ProfileClicked) },
                modifier = Modifier.statusBarsPadding(),
            )
        }
    ) { paddingValues ->
        CatalogScreenContent(
            viewModel = viewModel,
            paddingValues = paddingValues
        )
    }
}

@Composable
private fun CatalogScreenContent(
    viewModel: CatalogViewModel,
    paddingValues: PaddingValues = PaddingValues()
) {
    val uiState: State<CatalogUiState> = viewModel.uiState.collectAsState()
    when (uiState.value.state) {
        CatalogUiStateStatus.Loading -> LoadingOverlay()
        is CatalogUiStateStatus.Error -> RetryOverlay(
            onRetry = {
                viewModel.reduce(event = CatalogUiStateEvent.RetryAfterErrorClicked)
            }
        )

        CatalogUiStateStatus.Success -> CatalogScreenSuccessContent(
            viewModel = viewModel,
            paddingValues = paddingValues
        )
    }
}

@Composable
private fun CatalogScreenSuccessContent(
    viewModel: CatalogViewModel,
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
                    event = CatalogUiStateEvent.ChangeCategoryType(
                        categoryType = CategoryType.entries[index]
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )

        when (uiState.value.selectedCategoryType) {
            CategoryType.SIGNATURE -> CatalogScreenSuccessColumnContent(viewModel = viewModel)
            else -> CatalogScreenSuccessGridContent(viewModel = viewModel)
        }
    }
}

@Composable
private fun CatalogScreenSuccessColumnContent(
    viewModel: CatalogViewModel
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
                    viewModel.reduce(CatalogUiStateEvent.ToggleProductFavorite(product.productId))
                },
                onClick = {
                    viewModel.reduce(CatalogUiStateEvent.GetProductDetail(product.productId))
                },
            )
        }
    }
}

@Composable
private fun CatalogScreenSuccessGridContent(
    viewModel: CatalogViewModel
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
                    viewModel.reduce(CatalogUiStateEvent.ToggleProductFavorite(product.productId))
                },
                onClick = {
                    viewModel.reduce(CatalogUiStateEvent.GetProductDetail(product.productId))
                },
            )
        }
    }
}