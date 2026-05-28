package com.coffeeshop.coffeeshopapp

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.arttttt.nav3router.Nav3Host
import com.arttttt.nav3router.Router
import com.coffeeshop.activeorders.internal.navigation.activeOrdersEntry
import com.coffeeshop.coffeeshopapp.navigation.BottomSheetSceneStrategy
import com.coffeeshop.coffeeshopapp.permissions.NotificationsPermissionGate
import com.github.slavikjunior.favorites.internal.navigation.favoritesEntry
import com.coffeeshop.coffeeshopapp.permissions.notificationsPermissionState
import com.coffeeshop.auth.internal.navigation.loginScreenEntry
import com.coffeeshop.auth.internal.navigation.registerScreenEntry
import com.coffeeshop.cart.internal.navigation.cartEntry
import com.coffeeshop.designsystem.common.CoffeeTheme
import com.coffeeshop.orderhistory.internal.navigation.orderHistoryEntry
import com.coffeeshop.product_detail.internal.navigation.productDetailScreenEntry
import com.coffeeshop.profile.internal.navigation.profileScreenEntry
import com.coffeshop.catalog.api.presentation.navigation.CatalogRoute
import com.coffeshop.catalog.internal.navigation.catalogScreenEntry
import com.coffeshop.navigation.Route
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    internal lateinit var router: Router<Route>

    override fun onCreate(savedInstanceState: Bundle?) {
        coffeeShopAppComponent().inject(this)

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val registerViewModelFactory = featureAuthComponent().registerViewModelFactory()
        val loginViewModelFactory = featureAuthComponent().loginViewModelFactory()
        val catalogViewModelFactory = featureCatalogComponent().viewModelFactory
        val profileViewModelFactory = featureProfileComponent().viewModelFactory
        val productDetailViewModelFactory = featureProductDetail().viewModelFactory
        val cartViewModelFactory = featureCart().viewModelFactory
        val activeOrdersViewModelFactory = featureActiveOrders().activeOrdersViewModelFactory()
        val orderHistoryViewModelFactory = featureOrderHistory().viewModelFactory
        val favoritesViewModelFactory = featureFavorites().viewModelFactory

        setContent {
            CoffeeTheme {
                val scope = rememberCoroutineScope()
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { granted ->
                    scope.launch {
                        notificationsPermissionState().refresh(this@MainActivity)
                    }
                }

                NotificationsPermissionGate(permissionLauncher = permissionLauncher) {
                    val backStack = rememberNavBackStack(CatalogRoute())
                    Nav3Host(
                        backStack = backStack,
                        router = router,
                    ) { backStack: NavBackStack<NavKey>, onBack: () -> Unit, router: Router<Route> ->
                        val sceneStrategies = remember { listOf<SceneStrategy<NavKey>>(BottomSheetSceneStrategy()) }
                        NavDisplay(
                            backStack = backStack,
                            onBack = onBack,
                            sceneStrategies = sceneStrategies,
                            entryProvider = entryProvider {
                                loginScreenEntry(router, loginViewModelFactory)
                                registerScreenEntry(router, registerViewModelFactory)
                                catalogScreenEntry(catalogViewModelFactory)
                                profileScreenEntry(router, profileViewModelFactory)
                                productDetailScreenEntry(productDetailViewModelFactory)
                                cartEntry(cartViewModelFactory)
                                activeOrdersEntry(activeOrdersViewModelFactory)
                                orderHistoryEntry(orderHistoryViewModelFactory)
                                favoritesEntry(favoritesViewModelFactory)
                            }
                        )
                    }
                }
            }
        }
    }
}
