package com.coffeeshop.orderhistory.internal.screen;

import com.coffeeshop.orderhistory.api.domain.usecase.GetOrderHistoryUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import kotlinx.coroutines.CoroutineDispatcher;

@ScopeMetadata
@QualifierMetadata("com.coffeeshop.di.qualifiers.DispatcherMain")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class OrderHistoryViewModel_Factory implements Factory<OrderHistoryViewModel> {
  private final Provider<GetOrderHistoryUseCase> getOrderHistoryProvider;

  private final Provider<CoroutineDispatcher> mainDispatcherProvider;

  private OrderHistoryViewModel_Factory(Provider<GetOrderHistoryUseCase> getOrderHistoryProvider,
      Provider<CoroutineDispatcher> mainDispatcherProvider) {
    this.getOrderHistoryProvider = getOrderHistoryProvider;
    this.mainDispatcherProvider = mainDispatcherProvider;
  }

  @Override
  public OrderHistoryViewModel get() {
    return newInstance(getOrderHistoryProvider.get(), mainDispatcherProvider.get());
  }

  public static OrderHistoryViewModel_Factory create(
      Provider<GetOrderHistoryUseCase> getOrderHistoryProvider,
      Provider<CoroutineDispatcher> mainDispatcherProvider) {
    return new OrderHistoryViewModel_Factory(getOrderHistoryProvider, mainDispatcherProvider);
  }

  public static OrderHistoryViewModel newInstance(GetOrderHistoryUseCase getOrderHistory,
      CoroutineDispatcher mainDispatcher) {
    return new OrderHistoryViewModel(getOrderHistory, mainDispatcher);
  }
}
