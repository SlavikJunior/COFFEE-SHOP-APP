package com.coffeeshop.cart.internal.screen.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coffeeshop.cart.internal.R
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.designsystem.common.Beige
import com.coffeeshop.designsystem.common.DarkBrown
import com.coffeeshop.designsystem.common.Secondary
import com.coffeeshop.designsystem.components.CartItemCard
import com.coffeeshop.designsystem.components.CoffeeButton
import com.coffeeshop.designsystem.components.DoubleSidedRow
import com.coffeeshop.designsystem.components.LoadingOverlay
import com.coffeeshop.designsystem.components.RetryOverlay
import com.coffeeshop.designsystem.components.ScreenTopBar

@Composable
fun CartScreen(
    viewModelFactory: ViewModelProvider.Factory
) {
    val viewModel = viewModel(factory = viewModelFactory, modelClass = CartViewModel::class)
    val uiState = viewModel.uiState.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.reduce(CartUiStateEvent.LoadData)
    }

    CartScreenContent(
        state = uiState,
        onNavigateBack = {
            viewModel.reduce(CartUiStateEvent.NavigateBack)
        },
        onRetryAfterError = {
            viewModel.reduce(CartUiStateEvent.LoadData)
        },
        onRemoveFromCart = { uniqueCartItemID ->
            viewModel.reduce(CartUiStateEvent.RemoveFromCart(uniqueCartItemID))
        },
        onGoToPayment = {
            viewModel.reduce(CartUiStateEvent.GoToPayment)
        }
    )
}

@Composable
private fun CartScreenContent(
    state: CartUiState,
    onNavigateBack: () -> Unit,
    onRetryAfterError: () -> Unit,
    onRemoveFromCart: (ID) -> Unit,
    onGoToPayment: () -> Unit
) {
    Scaffold(
        modifier = Modifier.background(Beige),
        topBar = {
            ScreenTopBar(
                title = stringResource(R.string.cart_title),
                onCloseClick = onNavigateBack,
                modifier = Modifier.statusBarsPadding(),
            )
        }
    ) { paddingValues ->
        when (state) {
            CartUiState.Loading -> CartScreenLoadingContent()
            is CartUiState.Error -> CartScreenErrorContent(onRetry = onRetryAfterError, message = state.message)
            is CartUiState.Success -> CartScreenSuccessContent(
                state = state,
                paddingValues = paddingValues,
                onRemoveFromCart = onRemoveFromCart,
                onGoToPayment = onGoToPayment
            )
        }
    }
}

@Composable
private fun CartScreenLoadingContent() = LoadingOverlay()

@Composable
private fun CartScreenErrorContent(onRetry: () -> Unit, message: String) = RetryOverlay(onRetry = onRetry, text = message)

@Composable
private fun CartScreenSuccessContent(
    state: CartUiState.Success,
    paddingValues: PaddingValues = PaddingValues(),
    onRemoveFromCart: (ID) -> Unit,
    onGoToPayment: () -> Unit
) {
    Box(modifier = Modifier
//        .statusBarsPadding()
        .padding(paddingValues)
        .fillMaxSize()
    ) {
        if (state.items.isEmpty()) {
            EmptyCart()
        } else {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(items = state.items, key = { item -> item.id }) { item ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val uniqueCartItemID = ID(item.id)

                        CartItemCard(
                            name = item.name,
                            volume = item.volume,
                            price = item.price,
                            imageUrl = item.photoUrl,
                            count = item.quantity,
                            onRemoveFromCart = { onRemoveFromCart(uniqueCartItemID) },
                        )

                        DoubleSidedRow(firstString = stringResource(R.string.syrup_text), secondString = item.modifiers.syrup)
                        DoubleSidedRow(firstString = stringResource(R.string.milk_text), secondString = item.modifiers.milk)
                        DoubleSidedRow(firstString = stringResource(R.string.vitamins_text), secondString = item.modifiers.vitamins)
                        DoubleSidedRow(firstString = stringResource(R.string.marshmallows_text), secondString = item.modifiers.marshmallows)
                        DoubleSidedRow(firstString = stringResource(R.string.comments_text), secondString = item.modifiers.comment)
                    }
                }
                item {
                    CoffeeButton(
                        text = stringResource(R.string.go_to_payment_text, state.totalPrice),
                        onClick = onGoToPayment,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                            .size(width = 256.dp, height = 64.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCart() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Outlined.ShoppingCart,
            contentDescription = null,
            tint = Secondary,
            modifier = Modifier.size(72.dp),
        )
        Text(
            text = stringResource(R.string.empty_cart_text),
            style = MaterialTheme.typography.headlineMedium,
            color = DarkBrown,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = stringResource(R.string.add_to_empty_cart_text),
            style = MaterialTheme.typography.bodyMedium,
            color = Secondary,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
@Preview
private fun CartScreenContentPreview() = CartScreenContent(
    state = CartUiState.Success(
        items = listOf(
            CartUiStateCartItem(
                name = "Зилант",
                volume = "450 мл",
                price = "250 ₽",
                quantity = 2,
                modifiers = CartUiStateCartItemModifierBlock(
                    syrup = "Малина",
                    milk = "Протеиновое",
                ),
            )
        )
    ),
    onNavigateBack = {},
    onRetryAfterError = {},
    onRemoveFromCart = {},
    onGoToPayment = {}
)

@Composable
@Preview
private fun EmptyCartPreview() = EmptyCart()
