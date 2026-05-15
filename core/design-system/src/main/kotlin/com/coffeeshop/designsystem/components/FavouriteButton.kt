package com.coffeeshop.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.coffeeshop.designsystem.common.Beige
import com.coffeeshop.designsystem.common.DarkBrown
import com.coffeeshop.designsystem.R
import com.coffeeshop.designsystem.common.Secondary

/**
 * Кнопка «в избранное» — маленькое сердце в бежевом круге.
 * Overlay поверх фото в [ProductCard] на экранах каталога и избранного.
 * Закрашенное сердце — товар уже в избранном, контурное — нет.
 */
@Composable
fun FavouriteButton(
    isFavourite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Beige.copy(alpha = 0.9f))
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = stringResource(
                if (isFavourite) R.string.cd_favourite_remove else R.string.cd_favourite_add,
            ),
            tint = if (isFavourite) DarkBrown else Secondary,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Preview
@Composable
private fun FavouriteButtonPreview() = FavouriteButton(
    isFavourite = false,
    onToggle = {},
)

@Preview
@Composable
private fun FavouriteButtonActivePreview() = FavouriteButton(
    isFavourite = true,
    onToggle = {},
)
