package com.coffeeshop.orderhistory.internal.data.repository;

import com.coffeeshop.orderhistory.internal.data.service.OrderHistoryService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import kotlinx.coroutines.CoroutineDispatcher;

@ScopeMetadata
@QualifierMetadata("com.coffeeshop.di.qualifiers.DispatcherIO")
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
public final class OrderHistoryRepositoryImpl_Factory implements Factory<OrderHistoryRepositoryImpl> {
  private final Provider<OrderHistoryService> serviceProvider;

  private final Provider<CoroutineDispatcher> dispatcherProvider;

  private OrderHistoryRepositoryImpl_Factory(Provider<OrderHistoryService> serviceProvider,
      Provider<CoroutineDispatcher> dispatcherProvider) {
    this.serviceProvider = serviceProvider;
    this.dispatcherProvider = dispatcherProvider;
  }

  @Override
  public OrderHistoryRepositoryImpl get() {
    return newInstance(serviceProvider.get(), dispatcherProvider.get());
  }

  public static OrderHistoryRepositoryImpl_Factory create(
      Provider<OrderHistoryService> serviceProvider,
      Provider<CoroutineDispatcher> dispatcherProvider) {
    return new OrderHistoryRepositoryImpl_Factory(serviceProvider, dispatcherProvider);
  }

  public static OrderHistoryRepositoryImpl newInstance(OrderHistoryService service,
      CoroutineDispatcher dispatcher) {
    return new OrderHistoryRepositoryImpl(service, dispatcher);
  }
}
