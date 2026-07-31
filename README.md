# ☕ Coffee Shop Application

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Android Gradle Plugin](https://img.shields.io/badge/AGP-9.0-green.svg?logo=android)](https://developer.android.com/build)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4.svg?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Navigation 3](https://img.shields.io/badge/Navigation-3.x-orange.svg)](https://developer.android.com/guide/navigation)
[![Dagger 2](https://img.shields.io/badge/DI-Dagger%202%20%2B%20KSP-red.svg)](https://dagger.dev)

Современное клиентское Android-приложение для заказа кофе и выпечки. Проект разработан как демонстрационный пет-проект (Portfolio / Production-ready Reference), воплощающий современные архитектурные паттерны, строгую многомодульность (**Feature API / Internal Split**), чистое внедрение зависимостей (**Dagger 2**), декларативный UI (**Jetpack Compose + Material 3**), навигацию новой генерации (**Jetpack Navigation 3**) и реактивное управление состоянием (**MVI + Coroutines/Flow**).

---

## 🏛 Архитектура и Многомодульность

Приложение построено по принципам **Clean Architecture** и **Feature-Sliced Design**. Проект масштабируем: каждый функциональный модуль изолирован и разделен на две составные части: **API** и **Internal (Impl)**.

```
COFFEE-SHOP-APP/
├── core/                        # Базовые инфраструктурные модули
│   ├── common/                  # Общие доменные модели (Price, Result, OrderEventBus)
│   ├── navigation/              # Контракты Route, BottomSheetMetadata, Router
│   ├── network/                 # Retrofit, OkHttp, AuthInterceptor, TokenAuthenticator
│   ├── database/                # Room DB (Entities, DAOs, TypeConverters)
│   ├── cache/                   # API и Internal реализация in-memory кэширования
│   ├── di/                      # Общие DI аннотации, диспатчеры Coroutines
│   ├── deps/                    # Агрегатор системных и корневых зависимостей AppDeps
│   ├── design-system/           # Дизайн-система (Material 3 темы, компоненты UI)
│   ├── build-config/            # Провайдинг конфигурации сборки (API/Internal)
│   ├── logger/                  # Логирование приложения (API/Internal)
│   └── json/                    # Конфигурация Kotlinx Serialization / JSON
│
├── feature/                     # Фичи приложения (Разделены на API и Internal)
│   ├── auth/                    # Авторизация и регистрация (api / internal)
│   ├── catalog/                 # Каталог напитков и еды (api / internal)
│   ├── product-detail/          # Карточка товара с кастомизацией (api / internal)
│   ├── cart/                    # Корзина и расчет стоимости (api / internal)
│   ├── favorites/               # Избранные товары (api / internal)
│   ├── active-orders/           # Активные заказы и отслеживание (api / internal)
│   ├── order-history/           # История заказов (api / internal)
│   └── profile/                 # Профиль пользователя и настройки (api / internal)
│
└── coffee-shop-app/             # Корневой модуль (Application Composition Root)
```

### 💡 Преимущества паттерна Feature API / Internal Split
1. **Изоляция компиляции (Compile-time Safety):** Модуль `:feature:A:internal` **не зависит** от `:feature:B:internal`. Он может зависеть только от публичного контракта `:feature:B:api`.
2. **Скорость сборки (Incremental Build Speed):** Изменения в UI или внутренней логике одной фичи не вызывают перекомпиляцию других фич.
3. **Отсутствие циклических зависимостей:** Исключены случайные утечки деталей реализации между экранами.

---

## 🛠 Ключевые архитектурные решения и паттерны

### 1. Dagger 2: Чистый и безопасный провайдинг UseCase между фичами
Для внедрения зависимостей используется **Dagger 2 + KSP** без магических сервис-локаторов. 

Вместо прямых связей между фичами используется **Composition Root (модуль `coffee-shop-app`)**. Компоненты фич принимают UseCase контракты из `:api` модулей соседних фич через `@BindsInstance` в Dagger Builder:

```kotlin
// В CoffeeShopApp.kt (Composition Root)
featureCatalogComponent = DaggerFeatureCatalogComponent.builder()
    .isUserLoggedIn(featureAuthComponent.isUserLoggedInUseCase)
    .getTotalPriceFromCart(featureCartComponent.getTotalPriceFromCartUseCase)
    .jsonComponent(jsonComponent)
    .networkComponent(networkComponent)
    .databaseComponent(databaseComponent)
    .coreDiComponent(coreDiComponent)
    .router(coreNavigationComponent.router())
    .buildConfigProvider(appDeps.buildConfigProvider)
    .logger(appDeps.logger)
    .productDetailInMemoryCache(coreCacheComponent.productDetailCache())
    .build()
```

> **Почему это красиво?**
> Фича Каталога (`:feature:catalog:internal`) умеет показывать итоговую стоимость корзины на плавающей кнопке, но при этом **ничего не знает о реализации корзины**. Она зависит только от интерфейса `GetTotalPriceFromCartUseCase` из `:feature:cart:api`.

---

### 2. Реактивный поток стоимости корзины (Reactive Cart Price Flow)
В приложении реализован реактивный подход к рассечту стоимости корзины:

- В `CartRepositoryImpl` корзина хранится в едином источнике правды `MutableStateFlow<List<CartItem>>`, синхронизируемом с `CartDao` (Room) и валидируемом через бэкенд.
- `GetTotalPriceFromCartUseCase` предоставляет поток `Flow<Result<Price>>`.
- Экран каталога (`CatalogViewModel`), экран избранного (`FavoritesViewModel`) и сама корзина (`CartViewModel`) подписываются на этот UseCase. При добавлении товара в корзину или изменении количества на любом экране, итоговая сумма мгновенно пересчитывается и обновляется во всех UI-компонентах без лишних перезапросов.

```kotlin
// GetTotalPriceFromCartUseCaseImpl.kt
internal class GetTotalPriceFromCartUseCaseImpl @Inject constructor(
    private val repository: CartRepository
) : GetTotalPriceFromCartUseCase {
    override suspend fun invoke(): Flow<Result<Price>> = repository.getTotalPrice()
}

// CartRepositoryImpl.kt
override fun getTotalPrice(): Flow<Result<Price>> = _items.map { items ->
    Result.Success(items.fold(Price.emptyRublesPrice()) { acc, item -> acc + item.price })
}
```

---

### 3. OrderEventBus Pattern (Push Notifications & Event Bus)
Для доставки асинхронных событий изменения статуса заказа в реальном времени (например, из FCM Push-уведомлений) используется шина событий на базе `MutableSharedFlow`:

```kotlin
// OrderEventBus.kt
data class OrderStatusUpdate(val orderId: Long, val status: OrderStatus)

object OrderEventBus {
    private val _orderStatusChanged = MutableSharedFlow<OrderStatusUpdate>(extraBufferCapacity = 10)
    val orderStatusChanged: SharedFlow<OrderStatusUpdate> = _orderStatusChanged.asSharedFlow()

    fun notifyOrderStatusChanged(update: OrderStatusUpdate) {
        _orderStatusChanged.tryEmit(update)
    }
}
```

- При получении push-уведомления через Firebase Messaging (`MessagingService`), сервис отправляет обновление в `OrderEventBus.notifyOrderStatusChanged(...)`.
- `ActiveOrdersViewModel` слушаёт данный `SharedFlow` и обновляет статус заказа в реальном времени, избавляя от необходимости опрашивать сервер (polling).

---

### 4. Навигация новой генерации: Navigation 3 + Router + BottomSheet Strategy
Проект использует **Jetpack Navigation 3** (`androidx.navigation3.runtime.NavKey`) с высокой степенью декуплирования UI и логики переходов.

- Все маршруты объявлены в `:api` модулях фич как типы `@Serializable`:
  ```kotlin
  @Serializable
  interface Route : NavKey
  
  @Serializable
  data class ProductDetailRoute(val productId: String) : Route
  ```
- Для управления переходами применяется паттерн `Router<Route>` (`com.arttttt.nav3router.Router`), что позволяет вызывать навигацию из ViewModel без жесткой привязки к `NavController` или `Context`.
- **BottomSheetSceneStrategy:** Экран кастомизации товара и диалоги открываются как нативные `Material 3 BottomSheet` внутри Nav3 backstack за счет метаданных маршрута (`bottomSheetMetadata()`).

---

### 5. Domain Value Object: `Price` (Защита от Floating Point ошибок)
Для работы с деньгами реализован immutable data class `Price`:

```kotlin
@Serializable
data class Price(
    val firstPart: Int,   // Рубли
    val secondPart: Int,  // Копейки
    val currency: Currency = Currency.RUBLES
) : Comparable<Price> {

    init {
        require(firstPart >= 0 && secondPart >= 0 && secondPart <= 99) { "Amount must be positive" }
    }

    operator fun plus(other: Price): Price { ... }
    operator fun times(multiplier: Int): Price { ... }
    fun display(): String = "$firstPart,${secondPart.toString().padStart(2, '0')} ₽"
}
```
**Преимущества:**
- Полное исключение ошибок округления, присущих `Float`/`Double` (`0.1 + 0.2 != 0.3`).
- Удобная математика через перегрузку операторов (`+`, `*`).
- Гарантия валидности данных на этапе создания объекта.

---

### 6. Обработка инвалидации сессии (Global Session Expiration)
Сетевой слой (`core:network`) отслеживает статусы `401 Unauthorized` в `AuthInterceptor` и `TokenAuthenticator`. В случае недействительного Refresh token в `NetworkComponent` эмитится событие в `sessionExpiredFlow`. `CoffeeShopApp` подписывается на этот поток и глобально сбрасывает стек навигации на экран входа:

```kotlin
private fun navigateToLoginWhenRequired() {
    applicationScope.launch {
        networkComponent.sessionExpiredFlow.collect {
            coreNavigationComponent.router().replaceCurrent(LoginRoute())
        }
    }
}
```

---

### 7. Конкурентная валидация цен корзины (Coroutines Concurrency)
При старте корзины цены и доступность позиций перепроверяются на бэкенде. Чтобы не задерживать UI и не делать последовательные запросы, применяется чанкинг и параллельное выполнение через `async`/`awaitAll`:

```kotlin
coroutineScope {
    for (batch in currentItems.chunked(MAX_CONCURRENT_VALIDATIONS)) {
        val batchResults = batch.map { item ->
            async {
                try {
                    val detail = cartService.getProductDetail(item.productId.value)
                    recalculatePrice(item, detail)
                } catch (_: Exception) {
                    item
                }
            }
        }.awaitAll()
        updatedItems.addAll(batchResults)
    }
}
```

---

## 📱 Функционал приложения

1. **Авторизация & Безопасность:**
   - Вход и регистрация по номеру телефона / OTP.
   - Безопасное хранение токенов (`TokenStorage`) с автоматическим Refresh Token циклом.
2. **Каталог & Поиск:**
   - Категории товаров (Кофе, Десерты, Выпечка).
   - Быстрый поиск и фильтрация по доступности.
   - Отражение избранных товаров и текущей стоимости корзины.
3. **Конструктор напитка (Product Detail):**
   - Выбор размера (S, M, L / мл).
   - Выбор альтернативного молока, сиропов и топпингов.
   - Динамический пересчет стоимости в реальном времени (`CalculateProductTotalPriceUseCase`).
4. **Корзина & Оформление:**
   - Добавление, удаление, изменение количества позиций.
   - Сохранение корзины в локальную БД Room (`CartEntity`).
   - Синхронизация цен с сервером.
5. **Активные заказы & FCM:**
   - Статусы заказов (*Готовится*, *Готов к выдаче*, *Завершен*).
   - Поддержка Push-уведомлений с обработкой разрешений Android 13+ (`NotificationsPermissionGate`).
6. **Избранное & История заказов:**
   - Сохранение любимых позиций в офлайн-режиме.
   - Повтор заказа из истории в один клик.

---

## 💻 Технологический стек

- **Language:** Kotlin 2.1 (K2 Compiler)
- **UI Framework:** Jetpack Compose, Material 3, Edge-to-Edge
- **Architecture:** Clean Architecture + MVI / MVVM, Feature API / Internal Split
- **Async & Reactive:** Kotlin Coroutines, StateFlow, SharedFlow, Channel
- **Dependency Injection:** Dagger 2, KSP
- **Navigation:** Jetpack Navigation 3 (`NavKey`), Nav3 Router
- **Network:** Retrofit 2, OkHttp 5, Kotlinx Serialization, Custom Interceptors
- **Database & Cache:** Room Database, Encrypted DataStore, In-Memory Cache
- **Notifications:** Firebase Cloud Messaging (FCM)
- **Build System:** Gradle Kotlin DSL (`build.gradle.kts`), Version Catalogs (`libs.versions.toml`), AGP 9.0

---

## 🛠 Сборка и запуск

1. **Требования:**
   - Android Studio Ladybug / Meerkat (2024.2.1+)
   - JDK 17 или 21
   - Android SDK 35+
2. **Клонирование репозитория:**
   ```bash
   git clone https://github.com/your-username/COFFEE-SHOP-APP.git
   cd COFFEE-SHOP-APP
   ```
3. **Конфигурация Firebase:**
   > *Примечание:* Файл `google-services.json` исключен из соображений безопасности (публичный репозиторий). Для локального запуска пуш-уведомлений добавьте ваш `google-services.json` в директорию `coffee-shop-app/`.

4. **Сборка через Gradle CLI:**
   ```bash
   # Проверка компиляции и сборка Debug APK
   ./gradlew assembleDebug
   ```
