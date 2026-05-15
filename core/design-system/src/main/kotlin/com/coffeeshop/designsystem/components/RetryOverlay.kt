package com.coffeeshop.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.coffeeshop.designsystem.R
import com.coffeeshop.designsystem.common.CoffeeShopButtonTextStyle
import com.coffeeshop.designsystem.common.Secondary

@Composable
fun RetryOverlay(
    onRetry: () -> Unit,
    alfa: Float = 0.35f,
    text: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = alfa)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            text?.let { text ->
                Text(
                    text = text,
                    style = CoffeeShopButtonTextStyle
                )
            }

            IconButton(
                onClick = {},
                modifier = Modifier.size(100.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = stringResource(R.string.cd_retry),
                    modifier = Modifier
                        .clickable(onClick = onRetry)
                        .size(81.dp)
                        .background(Secondary)
                        .padding(4.dp),
                )
            }
        }
    }
}

@Composable
@Preview
private fun RetryOverlayPreview() = RetryOverlay(
    onRetry = {},
    text = "Что-то пошло не так :("
)