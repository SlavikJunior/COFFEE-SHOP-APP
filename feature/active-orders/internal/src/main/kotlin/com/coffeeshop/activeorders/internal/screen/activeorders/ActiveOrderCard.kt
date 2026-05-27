package com.coffeeshop.activeorders.internal.screen.activeorders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.coffeeshop.activeorders.api.domain.model.ActiveOrder
import com.coffeeshop.activeorders.api.domain.model.ActiveOrderItem
import com.coffeeshop.activeorders.internal.R
import com.coffeeshop.common.model.order.OrderStatus
import com.coffeeshop.designsystem.common.Beige
import com.coffeeshop.designsystem.common.DarkBrown
import com.coffeeshop.designsystem.common.Secondary
import com.coffeeshop.designsystem.common.White

@Composable
internal fun ActiveOrderCard(
    order: ActiveOrder,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = order.createdAt,
                style = MaterialTheme.typography.labelMedium,
                color = Secondary,
            )

            HorizontalDivider(color = Beige)

            order.items.forEach { item ->
                Text(
                    text = "${item.name} x${item.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkBrown,
                )
            }

            HorizontalDivider(color = Beige)

            Text(
                text = stringResource(R.string.active_orders_total, order.totalAmount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = DarkBrown,
            )

            order.comment?.let { comment ->
                Text(
                    text = stringResource(R.string.active_orders_comment, comment),
                    style = MaterialTheme.typography.bodySmall,
                    color = Secondary,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            OrderStatusBar(
                status = order.status,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ActiveOrderCardPreview() = ActiveOrderCard(
    order = ActiveOrder(
        id = 1L,
        items = listOf(
            ActiveOrderItem("Латте", 1),
            ActiveOrderItem("Капучино", 2),
        ),
        totalAmount = "750 ₽",
        comment = "Без сахара",
        createdAt = "27 мая 2026, 14:32",
        status = OrderStatus.PREPARING,
    )
)
