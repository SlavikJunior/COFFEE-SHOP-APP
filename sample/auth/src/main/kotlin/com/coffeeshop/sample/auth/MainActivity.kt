package com.coffeeshop.sample.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.arttttt.nav3router.Nav3Host
import com.arttttt.nav3router.Router
import com.coffeeshop.auth.api.presentation.navigation.LoginRoute
import com.coffeeshop.auth.internal.navigation.loginScreenEntry
import com.coffeeshop.auth.internal.navigation.registerScreenEntry
import com.coffeeshop.designsystem.common.CoffeeTheme
import com.coffeshop.catalog.api.presentation.navigation.CatalogRoute
import com.coffeshop.catalog.internal.navigation.catalogScreenEntry
import com.coffeshop.catalog.internal.screen.catalog.CatalogViewModel_Factory
import com.coffeshop.navigation.Route

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val router = Router<Route>()

        enableEdgeToEdge()
        setContent {
            CoffeeTheme {
                val backStack = rememberNavBackStack(LoginRoute())
                Nav3Host(
                    backStack = backStack,
                    router = router,
                ) { backStack: NavBackStack<NavKey>, onBack: () -> Unit, router: Router<Route> ->
                    NavDisplay(
                        backStack = backStack,
                        onBack = onBack,
                        entryProvider = entryProvider {
//                            loginScreenEntry(router)
//                            registerScreenEntry(router)
                        }
                    )
                }
            }
        }
    }
}