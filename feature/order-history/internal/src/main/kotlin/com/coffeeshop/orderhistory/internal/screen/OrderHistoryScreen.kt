package com.coffeeshop.orderhistory.internal.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.designsystem.common.DarkBrown
import com.coffeeshop.designsystem.common.Secondary
import com.coffeeshop.designsystem.components.CommonBottomBar
import com.coffeeshop.designsystem.components.CommonBottomBarDestinations
import com.coffeeshop.designsystem.components.LoadingOverlay
import com.coffeeshop.designsystem.components.RetryOverlay
import com.coffeeshop.designsystem.components.SimpleTopBar
import com.coffeeshop.orderhistory.api.domain.model.OrderSummary
import com.coffeeshop.orderhistory.internal.R

@Composable
internal fun OrderHistoryScreen(viewModelFactory: ViewModelProvider.Factory) {
    val viewModel = viewModel<OrderHistoryViewModel>(factory = viewModelFactory)
    val uiState by viewModel.uiState.collectAsState()

    OrderHistoryContent(
        uiState = uiState,
        onEvent = { event -> viewModel.reduce(event) }
    )
}

@Composable
private fun OrderHistoryContent(
    uiState: OrderHistoryUiState,
    onEvent: (OrderHistoryUiStateEvent) -> Unit
) {


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        when (uiState) {
            OrderHistoryUiState.Loading -> OrderHistoryLoading()
            is OrderHistoryUiState.Error -> OrderHistoryError(message = uiState.message) {
                onEvent(OrderHistoryUiStateEvent.Retry)
            }

            is OrderHistoryUiState.Success -> {
                if (uiState.orders.isEmpty()) OrderHistoryEmpty()
                else OrderHistoryList(orders = uiState.orders)
            }
        }
    }
}

@Composable
@Preview
private fun OrderHistoryContentPreview() = OrderHistoryContent(
    uiState = OrderHistoryUiState.Success(
        orders = listOf(
            OrderSummary(
                id = ID.random().value,
                status = "Sample Status",
                createdAt = "28.02.2006",
                totalAmount = Price(123, 32).display()
            ),
            OrderSummary(
                id = ID.random().value,
                status = "Sample Status",
                createdAt = "28.02.2006",
                totalAmount = Price(4321, 32).display()
            ),
            OrderSummary(
                id = ID.random().value,
                status = "Sample Status",
                createdAt = "28.02.2006",
                totalAmount = Price(342, 32).display()
            ),
            OrderSummary(
                id = ID.random().value,
                status = "Sample Status",
                createdAt = "28.02.2006",
                totalAmount = Price(228228, 32).display()
            )
        )
    ),
    onEvent = {}
)

@Composable
private fun OrderHistoryLoading() = LoadingOverlay(progressIndicatorSizeDp = 32.dp)

@Composable
private fun OrderHistoryError(
    message: String,
    onRetry: () -> Unit
) =
    RetryOverlay(text = message.ifBlank { stringResource(R.string.order_history_error_label) }) {
        onRetry()
    }

@Composable
private fun OrderHistoryEmpty() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.order_history_empty_label),
            fontSize = 14.sp,
            color = Secondary,
        )
    }
}

@Composable
private fun OrderHistoryList(orders: List<OrderSummary>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        items(orders, key = { it.id }) { order ->
            OrderHistoryItem(order = order)
        }
    }
}

@Composable
private fun OrderHistoryItem(order: OrderSummary) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.order_history_order_item_title, order.id),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkBrown,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = order.createdAt,
                    fontSize = 11.sp,
                    color = Secondary,
                    letterSpacing = 0.5.sp,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = order.status,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Secondary,
                    letterSpacing = 1.sp,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = order.totalAmount,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkBrown,
                )
            }
        }
        HorizontalDivider(color = Secondary.copy(alpha = 0.2f))
    }
}