package com.github.slavikjunior.favorites.internal.screen.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arttttt.nav3router.Router
import com.coffeeshop.activeorders.api.presentation.navigation.ActiveOrdersRoute
import com.coffeeshop.auth.api.domain.usecase.IsUserLoggedInUseCase
import com.coffeeshop.auth.api.presentation.navigation.LoginRoute
import com.coffeeshop.cart.api.domain.usecase.GetTotalPriceFromCartUseCase
import com.coffeeshop.cart.api.presentation.navigation.CartRoute
import com.coffeeshop.common.model.products.CategoryType
import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.result.Result
import com.coffeeshop.common.result.asErrorResult
import com.coffeeshop.common.result.isSuccess
import com.coffeeshop.product_detail.api.presentation.navigation.ProductDetailRoute
import com.coffeeshop.profile.api.presentation.navigation.ProfileRoute
import com.coffeeshop.utils.groupBy
import com.coffeshop.catalog.api.domain.usecase.GetProductDetailByProductIdUseCase
import com.coffeshop.catalog.api.presentation.navigation.CatalogRoute
import com.coffeshop.catalog.api.domain.usecase.IsProductDetailStoredInCacheUseCase
import com.coffeshop.catalog.api.domain.usecase.SaveProductDetailInCacheUseCase
import com.coffeshop.navigation.Route
import com.github.slavikjunior.favorites.api.domain.usecase.GetAllFavoritesProductsUseCase
import com.github.slavikjunior.favorites.api.domain.usecase.ToggleProductByIdUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class FavoritesViewModel
@Inject constructor(
    private val getProductDetailByProductId: GetProductDetailByProductIdUseCase,
    private val isProductDetailStoredInCache: IsProductDetailStoredInCacheUseCase,
    private val saveProductDetailInCache: SaveProductDetailInCacheUseCase,
    private val getTotalPriceFromCart: GetTotalPriceFromCartUseCase,
    private val isUserLoggedIn: IsUserLoggedInUseCase,
    private val router: Router<Route>,
    private val getAllFavoritesProducts: GetAllFavoritesProductsUseCase,
    private val toggleProductByIdUseCase: ToggleProductByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiSate())
    val uiState = _uiState.asStateFlow()

    private var products: List<Product>? = null
    private var productsIntoMap: Map<CategoryType, List<Product>>? = null

    private var searchJob: Job? = null

    init {
        initData()
        viewModelScope.launch {
            getTotalPriceFromCart().collect { result ->
                if (result is Result.Success) {
                    _uiState.update { state -> state.copy(cartPrice = result.data) }
                }
            }
        }
    }

    fun reduce(event: FavoritesUiStateEvent) {
        when (event) {
            FavoritesUiStateEvent.RetryAfterErrorClicked -> onRetryAfterErrorClicked()
            is FavoritesUiStateEvent.ChangeCategoryType -> onChangeCategoryType(event)
            is FavoritesUiStateEvent.ToggleProductFavorite -> onToggleProductFavorite(event)
            is FavoritesUiStateEvent.GetProductDetail -> onGetProductDetail(event)
            FavoritesUiStateEvent.ProfileClicked -> onProfileClicked()
            FavoritesUiStateEvent.NavigateToCart -> onNavigateToCart()
            FavoritesUiStateEvent.BottomNavigateToCatalog -> onBottomNavigateToCatalog()
            FavoritesUiStateEvent.BottomNavigateToProfile -> onBottomNavigateToProfile()
            FavoritesUiStateEvent.BottomNavigateToActiveOrders -> onBottomNavigateToActiveOrders()
        }
    }

    private fun onBottomNavigateToActiveOrders() =
        if (isUserLoggedIn()) router.replaceStack(ActiveOrdersRoute)
        else router.push(LoginRoute(message = LOGIN_NAVIGATE_MESSAGE))

    private fun onBottomNavigateToProfile() =
        if (isUserLoggedIn()) router.replaceStack(ProfileRoute())
        else router.push(LoginRoute(message = LOGIN_NAVIGATE_MESSAGE))

    private fun onBottomNavigateToCatalog() = router.replaceStack(CatalogRoute())

    private fun onNavigateToCart() {
        router.push(CartRoute)
    }

    private fun onProfileClicked() =
        if (isUserLoggedIn()) router.push(ProfileRoute(isLoggedIn = true))
        else router.push(LoginRoute(message = LOGIN_NAVIGATE_MESSAGE))

    private fun onRetryAfterErrorClicked() {
        _uiState.update { it.copy(status = FavoritesUiStateStatus.Loading) }
        initData()
    }

    private fun onChangeCategoryType(event: FavoritesUiStateEvent.ChangeCategoryType) {
        _uiState.update { it.copy(selectedCategoryType = event.categoryType) }
    }

    private fun onGetProductDetail(event: FavoritesUiStateEvent.GetProductDetail) {
        Log.d(TAG, "onGetProductDetail invoked with productId: ${event.productId.value}")

        searchJob = viewModelScope.launch {
            val isStored = isProductDetailStoredInCache(event.productId)
            if (isStored.isSuccess() && (isStored as Result.Success).data) {
                Log.d(TAG, "productId: ${event.productId.value} found in cache")

                router.push(ProductDetailRoute(productID = event.productId))
                return@launch
            }

            val result: Result<ProductWithModifiers> =
                try {
                    getProductDetailByProductId(productId = event.productId)
                } catch (cause: Throwable) {
                    cause.asErrorResult()
                }

            Log.d(TAG, "Result on get product detail from server: $result")

            when (result) {
                is Result.Success<ProductWithModifiers> -> {
                    val product = result.data
                    val saved: Result<Boolean> = saveProductDetailInCache(product)
                    if (saved.isSuccess() && (saved as Result.Success).data) {
                        router.push(ProductDetailRoute(productID = product.productId))
                    }
                }
                is Result.Error -> _uiState.update { it.copy(status = FavoritesUiStateStatus.Error(result.exception)) }
                else -> throw IllegalArgumentException("Illegal product detail result: $result")
            }
        }
    }

    private fun onToggleProductFavorite(event: FavoritesUiStateEvent.ToggleProductFavorite) {
        try {
            viewModelScope.launch {
                val result: Result<Boolean> = toggleProductByIdUseCase(event.productId)
                if (result.isSuccess()) {
                    _uiState.update { state ->
                        val ids = state.favouriteProductIds
                        state.copy(
                            favouriteProductIds = if (event.productId in ids) ids - event.productId else ids + event.productId
                        )
                    }
                }
            }
        } catch (cause: Throwable) {
            _uiState.update { state ->
                state.copy(
                    status = FavoritesUiStateStatus.Error(cause)
                )
            }
        }
    }

    private fun initData() {
        searchJob = viewModelScope.launch {
            getAllFavoritesProducts().collect { result ->
                when(result) {
                    Result.Loading -> {
                        _uiState.update { it.copy(status = FavoritesUiStateStatus.Loading) }
                    }

                    is Result.Error -> {
                        _uiState.update { it.copy(status = FavoritesUiStateStatus.Error(cause = result.exception)) }
                    }

                    is Result.Success<*> -> {
                        result as Result.Success<List<Product>>
                        products = result.data

                        products?.let { list ->
                            productsIntoMap = list.groupBy<CategoryType, Product>()
                        }
                        products = null

                        val categories = productsIntoMap?.keys ?: emptySet()
                        _uiState.update { state ->
                            state.copy(
                                productsMap = productsIntoMap ?: emptyMap(),
                                status = FavoritesUiStateStatus.Success,
                                favouriteProductIds = productsIntoMap?.values?.flatten()
                                    ?.map { it.productId }?.toSet() ?: emptySet(),
                                selectedCategoryType = if (state.selectedCategoryType in categories)
                                    state.selectedCategoryType
                                else
                                    categories.firstOrNull() ?: CategoryType.COFFEE
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        searchJob?.cancel("$TAG onCleared")
        super.onCleared()
    }

    private companion object {
        const val LOGIN_NAVIGATE_MESSAGE = "Для начала войдите в систему."
        const val TAG = "CatalogViewModel"
    }
}