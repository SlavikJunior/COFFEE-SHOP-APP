package com.coffeeshop.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.coffeeshop.designsystem.common.Beige
import com.coffeeshop.designsystem.common.DarkBrown
import com.coffeeshop.designsystem.common.Secondary

/**
 * Карточка товара в сетке каталога и на экране избранного.
 * Квадратное фото (1:1) с опциональной кнопкой сердца в углу,
 * название и цена под фото.
 * При передаче [onFavouriteToggle] показывается [FavouriteButton].
 */
@Composable
fun ProductCard(
    name: String,
    price: String,
    imageUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavourite: Boolean = false,
    onFavouriteToggle: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Beige),
            contentAlignment = Alignment.Center,
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp)),
                )
            }
            if (onFavouriteToggle != null) {
                FavouriteButton(
                    isFavourite = isFavourite,
                    onToggle = onFavouriteToggle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp),
                )
            }
        }

        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = DarkBrown,
            maxLines = 2,
        )
        Text(
            text = price,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = Secondary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductCardPreview() = ProductCard(
    name = "Капучино",
    price = "280 ₽",
    imageUrl = null,
    onClick = {},
    isFavourite = false,
    onFavouriteToggle = {},
)
