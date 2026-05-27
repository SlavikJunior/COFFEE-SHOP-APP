package com.coffeeshop.activeorders.internal.screen.activeorders

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import com.coffeeshop.activeorders.internal.R
import com.coffeeshop.common.model.order.OrderStatus
import com.coffeeshop.designsystem.common.DarkBrown
import com.coffeeshop.designsystem.common.ErrorRed
import com.coffeeshop.designsystem.common.Secondary

private val StepColor = DarkBrown
private val InactiveColor = Color(0xFFCCCCCC)
private val CancelledColor = Secondary

@Composable
internal fun OrderStatusBar(
    status: OrderStatus,
    modifier: Modifier = Modifier,
) {
    val isCancelled = status == OrderStatus.CANCELLED
    val activeIndex: Int = when (status) {
        OrderStatus.PENDING   -> 0
        OrderStatus.PAID      -> 1
        OrderStatus.PREPARING -> 2
        OrderStatus.READY, OrderStatus.COMPLETED -> 3
        OrderStatus.CANCELLED -> -1
    }

    val targetProgress = if (activeIndex < 0) 0f else activeIndex.toFloat() / 3f
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 600),
        label = "status_bar_progress",
    )

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        ) {
            val dotRadiusPx = 8.dp.toPx()
            val lineThicknessPx = 2.dp.toPx()
            val centerY = size.height / 2f

            val positions = (0..3).map { i ->
                dotRadiusPx + (size.width - 2f * dotRadiusPx) * i / 3f
            }

            // Background line
            drawLine(
                color = InactiveColor,
                start = Offset(positions[0], centerY),
                end = Offset(positions[3], centerY),
                strokeWidth = lineThicknessPx,
            )

            // Progress line
            val progressX = positions[0] + (positions[3] - positions[0]) * animatedProgress
            if (progressX > positions[0]) {
                drawLine(
                    color = if (isCancelled) CancelledColor else StepColor,
                    start = Offset(positions[0], centerY),
                    end = Offset(progressX, centerY),
                    strokeWidth = lineThicknessPx,
                )
            }

            // Dots
            positions.forEachIndexed { index, x ->
                val isCompleted = !isCancelled && activeIndex >= index
                if (isCompleted) {
                    drawCircle(color = StepColor, radius = dotRadiusPx, center = Offset(x, centerY))
                } else {
                    drawCircle(color = InactiveColor, radius = dotRadiusPx, center = Offset(x, centerY))
                    drawCircle(
                        color = Color.White,
                        radius = dotRadiusPx - lineThicknessPx,
                        center = Offset(x, centerY),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (isCancelled) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.status_cancelled),
                    color = ErrorRed,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth()) {
                val labels = listOf(
                    stringResource(R.string.status_pending),
                    stringResource(R.string.status_paid),
                    stringResource(R.string.status_preparing),
                    stringResource(R.string.status_ready),
                )
                labels.forEachIndexed { index, label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (!isCancelled && activeIndex >= index) StepColor else InactiveColor,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderStatusBarPreview() {
    Column {
        OrderStatusBar(status = OrderStatus.PENDING, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OrderStatusBar(status = OrderStatus.PREPARING, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OrderStatusBar(status = OrderStatus.READY, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OrderStatusBar(status = OrderStatus.CANCELLED, modifier = Modifier.fillMaxWidth())
    }
}
