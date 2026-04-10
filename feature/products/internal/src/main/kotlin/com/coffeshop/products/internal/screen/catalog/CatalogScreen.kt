package com.coffeshop.products.internal.screen.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coffeeshop.designsystem.DarkBrown
import com.coffeeshop.designsystem.White
import com.coffeeshop.designsystem.components.CategoryTabRow
import com.coffeeshop.designsystem.components.HomeTopBar
import com.coffeeshop.designsystem.components.ProductCard

@Composable
fun CatalogScreen(
    onProfileClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {},
) {
    CatalogScreenInternal(
        onProfileClick = onProfileClick,
        onProductClick = onProductClick,
    )
}

@Composable
internal fun CatalogScreenInternal(
    onProfileClick: () -> Unit,
    onProductClick: (String) -> Unit,
    viewModel: CatalogViewModel = viewModel(factory = CatalogViewModel.factory),
) {
    val uiState = viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        HomeTopBar(
            onProfileClick = onProfileClick,
            modifier = Modifier.statusBarsPadding(),
        )

        when (val state = uiState.value) {
            CatalogUiState.Loading -> LoadingContent()
            is CatalogUiState.Success -> SuccessContent(
                state = state,
                onCategorySelected = viewModel::onCategorySelected,
                onProductClick = onProductClick,
            )
            is CatalogUiState.Error -> ErrorContent(message = state.message)
        }
    }
}

@Composable
private fun SuccessContent(
    state: CatalogUiState.Success,
    onCategorySelected: (Int) -> Unit,
    onProductClick: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        CategoryTabRow(
            tabs = state.categories,
            selectedIndex = state.selectedIndex,
            onTabSelected = onCategorySelected,
            modifier = Modifier.fillMaxWidth(),
        )
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
            items(state.products, key = { it.id }) { product ->
                ProductCard(
                    name = product.name,
                    price = product.price,
                    imageUrl = product.imageUrl,
                    onClick = { onProductClick(product.id) },
                )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(modifier = Modifier.size(64.dp))
    }
}

@Composable
private fun ErrorContent(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = message, color = DarkBrown)
    }
}

@Preview
@Composable
private fun CatalogScreenPreview() = CatalogScreen()
