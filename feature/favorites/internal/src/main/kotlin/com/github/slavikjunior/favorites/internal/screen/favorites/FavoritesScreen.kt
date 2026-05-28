package com.github.slavikjunior.favorites.internal.screen.favorites

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
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coffeeshop.common.model.products.CategoryType
import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.designsystem.components.CategoryTabRow
import com.coffeeshop.designsystem.components.CoffeeShopFloatingActionButton
import com.coffeeshop.designsystem.components.CommonBottomBar
import com.coffeeshop.designsystem.components.CommonBottomBarDestinations
import com.coffeeshop.designsystem.components.HomeTopBar
import com.coffeeshop.designsystem.components.LoadingOverlay
import com.coffeeshop.designsystem.components.ProductCard
import com.coffeeshop.designsystem.components.RetryOverlay
import kotlinx.coroutines.launch

@Composable
internal fun FavoritesScreen(
    viewModelFactory: ViewModelProvider.Factory,
) {
    val viewModel: FavoritesViewModel = viewModel(modelClass = FavoritesViewModel::class, factory = viewModelFactory)
    val uiState = viewModel.uiState.collectAsState()

    FavoritesScreenContent(uiState.value, { event -> viewModel.reduce(event) })
}

@Composable
private fun FavoritesScreenContent(
    uiState: FavoritesUiSate,
    onEvent: (FavoritesUiStateEvent) -> Unit
) {
    Scaffold(
        topBar = {
            HomeTopBar(
                onProfileClick = { onEvent(FavoritesUiStateEvent.ProfileClicked) },
                modifier = Modifier.statusBarsPadding(),
            )
        },
        floatingActionButton = {
            CoffeeShopFloatingActionButton(
                imageVector = Icons.Default.ShoppingCart,
                onClick = { onEvent(FavoritesUiStateEvent.NavigateToCart) },
                text = uiState.cartPrice.display()
            )
        },
        bottomBar = {
            CommonBottomBar(
                selectedDestination = CommonBottomBarDestinations.FAVORITES,
                destinationsEvents = mapOf(
                    CommonBottomBarDestinations.CATALOG to { onEvent(FavoritesUiStateEvent.BottomNavigateToCatalog) },
                    CommonBottomBarDestinations.FAVORITES to {},
                    CommonBottomBarDestinations.PROFILE to { onEvent(FavoritesUiStateEvent.BottomNavigateToProfile) },
                    CommonBottomBarDestinations.ACTIVE_ORDERS to { onEvent(FavoritesUiStateEvent.BottomNavigateToActiveOrders) }
                )
            )
        }
    ) { paddingValues ->
        when (uiState.status) {
            FavoritesUiStateStatus.Loading -> LoadingOverlay()
            is FavoritesUiStateStatus.Error -> RetryOverlay(
                onRetry = {
                    onEvent(FavoritesUiStateEvent.RetryAfterErrorClicked)
                }
            )
            FavoritesUiStateStatus.Success -> {
                if (uiState.productsMap.isEmpty()) {
                    FavoritesEmptyContent(paddingValues = paddingValues)
                } else {
                    FavoritesSuccessContent(
                        onEvent = onEvent,
                        uiState = uiState,
                        paddingValues = paddingValues
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritesEmptyContent(paddingValues: PaddingValues = PaddingValues()) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.FavoriteBorder,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 16.dp)
                .fillMaxWidth(fraction = 0.4f)
                .fillMaxSize(fraction = 0.4f)
        )
        Text(
            text = "Избранное пусто",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Добавьте что-нибудь в избранное!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FavoritesSuccessContent(
    uiState: FavoritesUiSate,
    onEvent: (FavoritesUiStateEvent) -> Unit,
    paddingValues: PaddingValues = PaddingValues()
) {
    val coroutineScope = rememberCoroutineScope()

    val categories = uiState.productsMap.keys.toList()
    val pagerState = rememberPagerState(
        initialPage = categories.indexOf(uiState.selectedCategoryType).coerceAtLeast(0)
    ) { categories.size }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage < categories.size) {
            onEvent(FavoritesUiStateEvent.ChangeCategoryType(categories[pagerState.currentPage]))
        }
    }

    LaunchedEffect(uiState.selectedCategoryType) {
        val targetPage = categories.indexOf(uiState.selectedCategoryType).coerceAtLeast(0)
        if (pagerState.currentPage != targetPage && targetPage < categories.size) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        CategoryTabRow(
            tabs = categories.map { it.russianName },
            selectedIndex = categories.indexOf(uiState.selectedCategoryType).coerceAtLeast(0),
            onTabSelected = { index ->
                coroutineScope.launch { pagerState.animateScrollToPage(index) }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            val category = categories[page]
            val products = uiState.productsMap[category] ?: emptyList()
            when (category) {
                CategoryType.SIGNATURE -> FavoritesSuccessColumnContent(
                    products = products,
                    favouriteProductIds = uiState.favouriteProductIds,
                    onFavouriteToggle = { onEvent(FavoritesUiStateEvent.ToggleProductFavorite(it)) },
                    onProductClick = { onEvent(FavoritesUiStateEvent.GetProductDetail(it)) },
                )
                else -> FavoritesSuccessGridContent(
                    products = products,
                    favouriteProductIds = uiState.favouriteProductIds,
                    onFavouriteToggle = { onEvent(FavoritesUiStateEvent.ToggleProductFavorite(it)) },
                    onProductClick = { onEvent(FavoritesUiStateEvent.GetProductDetail(it)) },
                )
            }
        }
    }
}

@Composable
private fun FavoritesSuccessColumnContent(
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
private fun FavoritesSuccessGridContent(
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
