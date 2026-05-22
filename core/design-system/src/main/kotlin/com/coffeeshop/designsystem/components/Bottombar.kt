package com.coffeeshop.designsystem.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.coffeeshop.designsystem.R

enum class CommonBottomBarDestinations { CATALOG, FAVORITES, PROFILE }

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
                    Icons.Default.Favorite,
                    contentDescription = CommonBottomBarDestinations.FAVORITES.name
                )
            }
        )
        NavigationBarItem(
            selected = selectedDestination == CommonBottomBarDestinations.PROFILE,
            onClick = destinationsEvents[CommonBottomBarDestinations.PROFILE] ?: {},
            icon = {
                Icon(
                    Icons.Default.AccountBox,
                    contentDescription = CommonBottomBarDestinations.PROFILE.name
                )
            }
        )
    }
}

@Composable
@Preview
fun CommonBottomBarPreviewSelectedCatalog() = CommonBottomBar(
    selectedDestination = CommonBottomBarDestinations.CATALOG, mapOf()
)

@Composable
@Preview
fun CommonBottomBarPreviewSelectedFavorites() = CommonBottomBar(
    selectedDestination = CommonBottomBarDestinations.FAVORITES, mapOf()
)

@Composable
@Preview
fun CommonBottomBarPreviewSelectedProfile() = CommonBottomBar(
    selectedDestination = CommonBottomBarDestinations.PROFILE, mapOf()
)