package com.coffeeshop.activeorders.internal.screen.activeorders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coffeeshop.activeorders.api.domain.model.ActiveOrder
import com.coffeeshop.activeorders.api.domain.model.ActiveOrderItem
import com.coffeeshop.activeorders.internal.R
import com.coffeeshop.common.model.order.OrderStatus
import com.coffeeshop.designsystem.common.Beige
import com.coffeeshop.designsystem.common.DarkBrown
import com.coffeeshop.designsystem.common.Secondary
import com.coffeeshop.designsystem.components.CommonBottomBar
import com.coffeeshop.designsystem.components.CommonBottomBarDestinations
import com.coffeeshop.designsystem.components.ProfileTopBar
import com.coffeeshop.designsystem.components.RetryOverlay
import com.coffeeshop.designsystem.components.SimpleTopBar

@Composable
internal fun ActiveOrdersScreen(viewModelFactory: ViewModelProvider.Factory) {
    val viewModel = viewModel<ActiveOrdersViewModel>(factory = viewModelFactory)
    val uiState by viewModel.uiState.collectAsState()

    ActiveOrdersScreenContent(
        uiState = uiState,
        onEvent = viewModel::reduce,
    )
}

@Composable
private fun ActiveOrdersScreenContent(
    uiState: ActiveOrdersUiState,
    onEvent: (ActiveOrdersUiStateEvent) -> Unit,
) {
    Scaffold(
        containerColor = Beige,
        bottomBar = {
            CommonBottomBar(
                selectedDestination = CommonBottomBarDestinations.ACTIVE_ORDERS,
                destinationsEvents = mapOf(
                    CommonBottomBarDestinations.CATALOG to { onEvent(ActiveOrdersUiStateEvent.BottomNavigateToCatalog) },
                    CommonBottomBarDestinations.FAVORITES to { onEvent(ActiveOrdersUiStateEvent.BottomNavigateToFavorites) },
                    CommonBottomBarDestinations.PROFILE to { onEvent(ActiveOrdersUiStateEvent.BottomNavigateToProfile) },
                    CommonBottomBarDestinations.ACTIVE_ORDERS to {}
                )
            )
        }
    ) { innerPadding ->
        when (uiState) {
            is ActiveOrdersUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = DarkBrown)
                }
            }

            is ActiveOrdersUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    RetryOverlay(
                        text = uiState.message,
                        onRetry = { onEvent(ActiveOrdersUiStateEvent.Retry) },
                    )
                }
            }

            is ActiveOrdersUiState.Success -> {
                Column {
                    SimpleTopBar(stringResource(R.string.active_orders_top_bar_title), modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding())

                    Spacer(modifier = Modifier.height(24.dp))

                    if (uiState.orders.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(R.string.active_orders_empty),
                                color = Secondary,
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(uiState.orders, key = { it.id }) { order ->
                                ActiveOrderCard(order = order)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ActiveOrdersScreenPreview() = ActiveOrdersScreenContent(
    uiState = ActiveOrdersUiState.Success(
        orders = listOf(
            ActiveOrder(
                id = 1L,
                items = listOf(ActiveOrderItem("Латте", 1), ActiveOrderItem("Маффин", 1)),
                totalAmount = "520 ₽",
                comment = "Без сахара",
                createdAt = "27 мая 2026, 14:30",
                status = OrderStatus.PREPARING,
            ),
        )
    ),
    onEvent = {},
)
