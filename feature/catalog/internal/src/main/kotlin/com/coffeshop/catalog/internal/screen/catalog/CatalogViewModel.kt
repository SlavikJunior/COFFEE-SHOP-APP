package com.coffeshop.catalog.internal.screen.catalog

import android.util.Log
import androidx.compose.runtime.Stable
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
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.result.Result
import com.coffeeshop.common.result.asErrorResult
import com.coffeeshop.common.result.isSuccess
import com.coffeeshop.product_detail.api.presentation.navigation.ProductDetailRoute
import com.coffeeshop.profile.api.presentation.navigation.ProfileRoute
import com.coffeeshop.utils.groupBy
import com.coffeshop.catalog.api.domain.usecase.GetFullMenuUseCase
import com.github.slavikjunior.favorites.api.navigation.FavoritesRoute
import com.coffeshop.catalog.api.domain.usecase.GetProductDetailByProductIdUseCase
import com.coffeshop.catalog.api.domain.usecase.IsProductDetailStoredInCacheUseCase
import com.coffeshop.catalog.api.domain.usecase.SaveProductDetailInCacheUseCase
import com.coffeshop.navigation.Route
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

internal sealed interface CatalogUiStateStatus {

    data object Loading : CatalogUiStateStatus

    data object Success : CatalogUiStateStatus

    data class Error(val cause: Throwable) : CatalogUiStateStatus
}

@Stable
internal data class CatalogUiState(
    val productsMap: Map<CategoryType, List<Product>> = emptyMap(),
    val status: CatalogUiStateStatus = CatalogUiStateStatus.Loading,
    val selectedCategoryType: CategoryType = CategoryType.COFFEE,
    val selectedProduct: ProductWithModifiers? = null,
    val favouriteProductIds: Set<ID> = emptySet(),
    val cartPrice: Price = Price(0, 0),
)

internal sealed interface CatalogUiStateEvent {
    data object RetryAfterErrorClicked : CatalogUiStateEvent
    data class ChangeCategoryType(val categoryType: CategoryType) : CatalogUiStateEvent
    data class ToggleProductFavorite(val productId: ID) : CatalogUiStateEvent
    data class GetProductDetail(val productId: ID) : CatalogUiStateEvent
    data object ProfileClicked : CatalogUiStateEvent
    data object NavigateToCart : CatalogUiStateEvent
    data object BottomNavigateToFavorites : CatalogUiStateEvent
    data object BottomNavigateToProfile : CatalogUiStateEvent
    data object BottomNavigateToActiveOrders : CatalogUiStateEvent
}

internal class CatalogViewModel
@Inject constructor(
    private val getFullMenu: GetFullMenuUseCase,
    private val getProductDetailByProductId: GetProductDetailByProductIdUseCase,
    private val isProductDetailStoredInCache: IsProductDetailStoredInCacheUseCase,
    private val saveProductDetailInCache: SaveProductDetailInCacheUseCase,
    private val getTotalPriceFromCart: GetTotalPriceFromCartUseCase,
    private val isUserLoggedIn: IsUserLoggedInUseCase,
    private val router: Router<Route>
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
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

    fun reduce(event: CatalogUiStateEvent) {
        when (event) {
            CatalogUiStateEvent.RetryAfterErrorClicked -> onRetryAfterErrorClicked()
            is CatalogUiStateEvent.ChangeCategoryType -> onChangeCategoryType(event)
            is CatalogUiStateEvent.GetProductDetail -> onGetProductDetail(event)
            is CatalogUiStateEvent.ToggleProductFavorite -> onToggleProductFavorite(event)
            CatalogUiStateEvent.ProfileClicked -> onProfileClicked()
            CatalogUiStateEvent.NavigateToCart -> onNavigateToCart()
            CatalogUiStateEvent.BottomNavigateToFavorites -> onBottomNavigateToFavorites()
            CatalogUiStateEvent.BottomNavigateToProfile -> onBottomNavigateToProfile()
            CatalogUiStateEvent.BottomNavigateToActiveOrders -> onBottomNavigateToActiveOrders()
        }
    }

    private fun onBottomNavigateToActiveOrders() =
        if (isUserLoggedIn()) router.replaceStack(ActiveOrdersRoute)
        else router.push(LoginRoute(message = LOGIN_NAVIGATE_MESSAGE))

    private fun onBottomNavigateToProfile() =
        if (isUserLoggedIn()) router.replaceStack(ProfileRoute())
        else router.push(LoginRoute(message = LOGIN_NAVIGATE_MESSAGE))

    private fun onBottomNavigateToFavorites() {
        // todo()
        //router.replaceStack(FavoritesRoute)
    }

    private fun onNavigateToCart() {
        router.push(CartRoute)
    }

    private fun onProfileClicked() =
        if (isUserLoggedIn()) router.push(ProfileRoute(isLoggedIn = true))
        else router.push(LoginRoute(message = LOGIN_NAVIGATE_MESSAGE))

    private fun onRetryAfterErrorClicked() {
        _uiState.update { it.copy(status = CatalogUiStateStatus.Loading) }
        initData()
    }

    private fun onChangeCategoryType(event: CatalogUiStateEvent.ChangeCategoryType) {
        _uiState.update { it.copy(selectedCategoryType = event.categoryType) }
    }

    private fun onGetProductDetail(event: CatalogUiStateEvent.GetProductDetail) {
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
                is Result.Error -> _uiState.update { it.copy(status = CatalogUiStateStatus.Error(result.exception)) }
                else -> throw IllegalArgumentException("Illegal product detail result: $result")
            }
        }
    }

    private fun onToggleProductFavorite(event: CatalogUiStateEvent.ToggleProductFavorite) {
        _uiState.update { state ->
            val ids = state.favouriteProductIds
            state.copy(
                favouriteProductIds = if (event.productId in ids) ids - event.productId else ids + event.productId
            )
        }
    }

    private fun initData() {
        searchJob = viewModelScope.launch {
            when (val result = getFullMenu()) {
                Result.Loading -> {
                    _uiState.update { it.copy(status = CatalogUiStateStatus.Loading) }
                }

                is Result.Error -> {
                    _uiState.update { it.copy(status = CatalogUiStateStatus.Error(cause = result.exception)) }
                }

                is Result.Success<*> -> {
                    result as Result.Success<List<Product>>
                    products = result.data

                    products?.let { list ->
                        productsIntoMap = list.groupBy<CategoryType, Product>()
                    }
                    products = null

                    _uiState.update { it.copy(
                        productsMap = productsIntoMap ?: emptyMap(),
                        status = CatalogUiStateStatus.Success,
                    ) }
                }
            }
        }
    }

    override fun onCleared() {
        searchJob?.cancel(CancellationException("$TAG onCleared"))
        super.onCleared()
    }

    private companion object {
        const val LOGIN_NAVIGATE_MESSAGE = "Для начала войдите в систему."
        const val TAG = "CatalogViewModel"
    }
}
