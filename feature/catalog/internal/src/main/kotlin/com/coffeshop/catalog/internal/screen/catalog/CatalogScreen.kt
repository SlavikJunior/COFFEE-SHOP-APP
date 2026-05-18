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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coffeeshop.common.model.products.CategoryType
import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.designsystem.components.CategoryTabRow
import com.coffeeshop.designsystem.components.CoffeeShopFloatingActionButton
import com.coffeeshop.designsystem.components.HomeTopBar
import com.coffeeshop.designsystem.components.LoadingOverlay
import com.coffeeshop.designsystem.components.ProductCard
import com.coffeeshop.designsystem.components.RetryOverlay
import kotlinx.coroutines.launch

@Composable
 fun CatalogScreen(
    viewModelFactory: ViewModelProvider.Factory,
) {
    val viewModel: CatalogViewModel = viewModel(modelClass = CatalogViewModel::class,factory = viewModelFactory,)
    val uiState = viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            HomeTopBar(
                onProfileClick = { viewModel.reduce(CatalogUiStateEvent.ProfileClicked) },
                modifier = Modifier.statusBarsPadding(),
            )
        },
        floatingActionButton = { CoffeeShopFloatingActionButton(
            imageVector = Icons.Default.ShoppingCart,
            onClick = { viewModel.reduce(event = CatalogUiStateEvent.NavigateToCart) },
            text = uiState.value.cartPrice.display()
        ) }
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
    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = CategoryType.entries.indexOf(uiState.value.selectedCategoryType)
    ) { CategoryType.entries.size }

    // Свайп пейджера → обновить категорию в ViewModel
    LaunchedEffect(pagerState.currentPage) {
        viewModel.reduce(
            CatalogUiStateEvent.ChangeCategoryType(CategoryType.entries[pagerState.currentPage])
        )
    }

    // Смена категории через таб → анимировать пейджер
    LaunchedEffect(uiState.value.selectedCategoryType) {
        val targetPage = CategoryType.entries.indexOf(uiState.value.selectedCategoryType)
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        CategoryTabRow(
            tabs = CategoryType.entries.map { it.russianName },
            selectedIndex = CategoryType.entries.indexOf(uiState.value.selectedCategoryType),
            onTabSelected = { index ->
                coroutineScope.launch { pagerState.animateScrollToPage(index) }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            val category = CategoryType.entries[page]
            val products = uiState.value.productsMap[category] ?: emptyList()
            when (category) {
                CategoryType.SIGNATURE -> CatalogScreenSuccessColumnContent(
                    products = products,
                    favouriteProductIds = uiState.value.favouriteProductIds,
                    onFavouriteToggle = { viewModel.reduce(CatalogUiStateEvent.ToggleProductFavorite(it)) },
                    onProductClick = { viewModel.reduce(CatalogUiStateEvent.GetProductDetail(it)) },
                )
                else -> CatalogScreenSuccessGridContent(
                    products = products,
                    favouriteProductIds = uiState.value.favouriteProductIds,
                    onFavouriteToggle = { viewModel.reduce(CatalogUiStateEvent.ToggleProductFavorite(it)) },
                    onProductClick = { viewModel.reduce(CatalogUiStateEvent.GetProductDetail(it)) },
                )
            }
        }
    }
}

@Composable
private fun CatalogScreenSuccessColumnContent(
    products: List<Product>,
    favouriteProductIds: Set<ID>,
    onFavouriteToggle: (ID) -> Unit,
    onProductClick: (ID) -> Unit,
) {
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
        items(products, key = { it.productId.value }) { product ->
            ProductCard(
                name = product.productName.value,
                price = product.prices.values.min().display(),
                imageUrl = product.imageUrl,
                isFavourite = product.productId in favouriteProductIds,
                onFavouriteToggle = { onFavouriteToggle(product.productId) },
                onClick = { onProductClick(product.productId) },
            )
        }
    }
}

@Composable
private fun CatalogScreenSuccessGridContent(
    products: List<Product>,
    favouriteProductIds: Set<ID>,
    onFavouriteToggle: (ID) -> Unit,
    onProductClick: (ID) -> Unit,
) {
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
        items(products, key = { it.productId.value }) { product ->
            ProductCard(
                name = product.productName.value,
                price = product.prices.values.min().display(),
                imageUrl = product.imageUrl,
                isFavourite = product.productId in favouriteProductIds,
                onFavouriteToggle = { onFavouriteToggle(product.productId) },
                onClick = { onProductClick(product.productId) },
            )
        }
    }
}
