package com.coffeeshop.designsystem.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.coffeeshop.designsystem.R

enum class CommonBottomBarDestinations {
    CATALOG,
    FAVORITES,
    PROFILE,
    ACTIVE_ORDERS
}

@Composable
fun CommonBottomBar(
    selectedDestination: CommonBottomBarDestinations,
    destinationsEvents: Map<CommonBottomBarDestinations, () -> Unit>
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedDestination == CommonBottomBarDestinations.CATALOG,
            onClick = destinationsEvents[CommonBottomBarDestinations.CATALOG] ?: {},
            icon = {
                Icon(
                    painterResource(R.drawable.list_box),
                    contentDescription = CommonBottomBarDestinations.CATALOG.name
                )
            }
        )
        NavigationBarItem(
            selected = selectedDestination == CommonBottomBarDestinations.FAVORITES,
            onClick = destinationsEvents[CommonBottomBarDestinations.FAVORITES] ?: {},
            icon = {
                Icon(
                    rememberVectorPainter(Icons.Default.Favorite),
                    contentDescription = CommonBottomBarDestinations.FAVORITES.name
                )
            }
        )
        NavigationBarItem(
            selected = selectedDestination == CommonBottomBarDestinations.PROFILE,
            onClick = destinationsEvents[CommonBottomBarDestinations.PROFILE] ?: {},
            icon = {
                Icon(
                    rememberVectorPainter(Icons.Default.AccountBox),
                    contentDescription = CommonBottomBarDestinations.PROFILE.name
                )
            }
        )
        NavigationBarItem(
            selected = selectedDestination == CommonBottomBarDestinations.ACTIVE_ORDERS,
            onClick = destinationsEvents[CommonBottomBarDestinations.ACTIVE_ORDERS] ?: {},
            icon = {
                Icon(
                    painterResource(R.drawable.stove),
                    contentDescription = CommonBottomBarDestinations.ACTIVE_ORDERS.name
                )
            }
        )
    }
}

@Composable
@Preview
private fun CommonBottomBarPreviewSelectedCatalog() = CommonBottomBar(
    selectedDestination = CommonBottomBarDestinations.CATALOG, mapOf()
)

@Composable
@Preview
private fun CommonBottomBarPreviewSelectedFavorites() = CommonBottomBar(
    selectedDestination = CommonBottomBarDestinations.FAVORITES, mapOf()
)

@Composable
@Preview
private fun CommonBottomBarPreviewSelectedProfile() = CommonBottomBar(
    selectedDestination = CommonBottomBarDestinations.PROFILE, mapOf()
)

@Composable
@Preview
private fun CommonBottomBarPreviewSelectedActiveOrders() = CommonBottomBar(
    selectedDestination = CommonBottomBarDestinations.ACTIVE_ORDERS, mapOf()
)