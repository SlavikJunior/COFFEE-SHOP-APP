package com.coffeeshop.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.coffeeshop.designsystem.common.Beige
import com.coffeeshop.designsystem.common.DarkBrown
import com.coffeeshop.designsystem.common.Secondary
import com.coffeeshop.designsystem.common.White

@Composable
fun CartItemCard(
    name: String,
    volume: String,
    price: String,
    imageUrl: String?,
    onRemoveFromCart: () -> Unit,
    count: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(White)
            .padding(vertical = 22.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(82.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Beige),
            contentAlignment = Alignment.Center,
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(6.dp)),
                )
            }
        }

        Column(
            modifier = Modifier.weight(6f / 10f),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = name.uppercase(),
                fontSize = 17.sp,
                fontWeight = FontWeight.W700,
                color = DarkBrown,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = volume,
                    fontSize = 13.sp,
                    color = Secondary,
                    fontWeight = FontWeight.W500,
                )
                Text(
                    text = "×$count",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W600,
                    color = DarkBrown,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Beige)
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                )
            }
        }

        Row(
            modifier = Modifier.weight(3f / 10f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = price,
                fontSize = 16.sp,
                fontWeight = FontWeight.W800,
                color = DarkBrown,
            )
            IconButton(
                onClick = onRemoveFromCart,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = Secondary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Preview
@Composable
private fun CartItemCardPreview() = CartItemCard(
    name = "Зилант",
    volume = "350 мл",
    price = "200 ₽",
    imageUrl = null,
    onRemoveFromCart = {},
    count = 1
)
