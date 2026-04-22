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
import com.coffeeshop.common.model.products.Product
import com.coffeeshop.designsystem.components.CategoryTabRow
import com.coffeeshop.designsystem.components.HomeTopBar
import com.coffeeshop.designsystem.components.LoadingOverlay
import com.coffeeshop.designsystem.components.ProductCard
import com.coffeeshop.designsystem.components.RetryOverlay
import com.coffeeshop.utils.groupBy as group

private const val COFFEE_INDEX = 0
private const val MATCHA_INDEX = 1
private const val NON_COFFEE_INDEX = 2
private const val SIGNATURE_INDEX = 3

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
            tabs = uiState.value.products.group<CategoryType, Product>().keys.map { it.russianName },
            selectedIndex = when(uiState.value.selectedCategoryType) {
                CategoryType.COFFEE -> COFFEE_INDEX
                CategoryType.MATCHA -> MATCHA_INDEX
                CategoryType.NON_COFFEE -> NON_COFFEE_INDEX
                CategoryType.SIGNATURE -> SIGNATURE_INDEX
            },
            onTabSelected = { index: Int ->
                viewModel.reduce(event = MyCatalogEvent.ChangeCategoryType(
                    categoryType = when(index) {
                        COFFEE_INDEX -> CategoryType.COFFEE
                        MATCHA_INDEX -> CategoryType.MATCHA
                        NON_COFFEE_INDEX -> CategoryType.NON_COFFEE
                        SIGNATURE_INDEX -> CategoryType.SIGNATURE
                        else -> throw IllegalArgumentException()
                    }
                ))
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
        items(uiState.value.products, key = { it.productId }) { product ->
            ProductCard(
                name = product.productName.value,
                price = product.prices.values.max().toString(),
                imageUrl = product.imageUrl,
                onClick = {
                    viewModel.reduce(event = MyCatalogEvent.GetProductDetail(
                        productId = product.productId
                    ))
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
        items(uiState.value.products, key = { it.productId }) { product ->
            ProductCard(
                name = product.productName.value,
                price = product.prices.values.max().toString(),
                imageUrl = product.imageUrl,
                onClick = {
                    viewModel.reduce(event = MyCatalogEvent.GetProductDetail(
                        productId = product.productId
                    ))
                },
            )
        }
    }
}