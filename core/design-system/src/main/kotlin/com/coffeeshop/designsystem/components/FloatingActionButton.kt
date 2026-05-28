package com.coffeeshop.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.coffeeshop.designsystem.R

@Composable
fun CoffeeShopFloatingActionButton(
    imageVector: ImageVector,
    onClick: () -> Unit,
    text: String? = null
) {
    FloatingActionButton(
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = rememberVectorPainter(imageVector),
                    contentDescription = stringResource(R.string.cd_cart)
                )

                text?.let { text ->
                    Text(
                        text = text
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun CoffeeShopFloatingActionButtonPreview() = CoffeeShopFloatingActionButton(
    onClick = {}, imageVector = Icons.Default.ShoppingCart, text = "250P"
)