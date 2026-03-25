package com.coffeeshop.design_system.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeeshop.design_system.Beige
import com.coffeeshop.design_system.DarkBrown
import com.coffeeshop.design_system.R
import com.coffeeshop.design_system.White

/**
 * Плавающая кнопка корзины — отображается поверх экрана каталога.
 * При [itemCount] > 0 показывает бейдж с количеством товаров.
 * Используется в [Scaffold] каталога как floatingActionButton.
 */
@Composable
fun CartFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    itemCount: Int = 0,
) {
    Box(modifier = modifier) {
        FloatingActionButton(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            containerColor = DarkBrown,
            contentColor = White,
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = stringResource(R.string.cd_cart),
                modifier = Modifier.size(24.dp),
            )
        }

        if (itemCount > 0) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Beige)
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = itemCount.coerceAtMost(99).toString(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown,
                )
            }
        }
    }
}

@Preview
@Composable
private fun CartFabPreview() = CartFab(
    onClick = {}
)