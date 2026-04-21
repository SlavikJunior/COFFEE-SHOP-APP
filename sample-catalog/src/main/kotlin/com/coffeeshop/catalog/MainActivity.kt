package com.coffeeshop.catalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.arttttt.nav3router.Nav3Host
import com.arttttt.nav3router.Router
import com.coffeeshop.retry_dialog_overlay.internal.navigation.retryDialogOverlayEntry
import com.coffeshop.navigation.Route
import com.coffeshop.products.api.presentation.navigation.CatalogRoute
import com.coffeshop.products.internal.navigation.catalogScreenEntry
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    internal lateinit var router: Router<Route>

    override fun onCreate(savedInstanceState: Bundle?) {
        coffeeShopAppComponent().inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModelFactory = featureCatalogComponent().viewModelFactory
        setContent {
            val backStack = rememberNavBackStack(CatalogRoute())
            val dialogStrategy = remember { DialogSceneStrategy<NavKey>() }
            Nav3Host(
                backStack = backStack,
                router = router,
            ) { backStack: NavBackStack<NavKey>, onBack: () -> Unit, router: Router<Route> ->
                NavDisplay(
                    backStack = backStack,
                    onBack = onBack,
                    sceneStrategies = listOf(dialogStrategy),
                    entryProvider = entryProvider {
                        catalogScreenEntry(router, viewModelFactory)
                        retryDialogOverlayEntry(router)
                    }
                )
            }
        }
    }
}
