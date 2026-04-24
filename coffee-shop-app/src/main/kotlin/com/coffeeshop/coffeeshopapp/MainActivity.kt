package com.coffeeshop.coffeeshopapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.arttttt.nav3router.Nav3Host
import com.arttttt.nav3router.Router
import com.coffeeshop.auth.internal.navigation.loginScreenEntry
import com.coffeeshop.auth.internal.navigation.registerScreenEntry
import com.coffeeshop.profile.internal.navigation.profileScreenEntry
import com.coffeshop.catalog.api.presentation.navigation.CatalogRoute
import com.coffeshop.catalog.internal.navigation.catalogScreenEntry
import com.coffeshop.navigation.Route
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
            Nav3Host(
                backStack = backStack,
                router = router,
            ) { backStack: NavBackStack<NavKey>, onBack: () -> Unit, router: Router<Route> ->
                NavDisplay(
                    backStack = backStack,
                    onBack = onBack,
                    entryProvider = entryProvider {
                        loginScreenEntry(router)
                        registerScreenEntry(router)
                        catalogScreenEntry(router, viewModelFactory)
                        profileScreenEntry(router)
                    }
                )
            }
        }
    }
}
