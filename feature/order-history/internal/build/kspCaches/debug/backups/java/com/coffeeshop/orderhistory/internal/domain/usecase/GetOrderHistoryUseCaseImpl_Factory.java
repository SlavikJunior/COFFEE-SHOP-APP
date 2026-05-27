package com.coffeeshop.orderhistory.internal.domain.usecase;

import com.coffeeshop.orderhistory.api.domain.repository.OrderHistoryRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
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
public final class GetOrderHistoryUseCaseImpl_Factory implements Factory<GetOrderHistoryUseCaseImpl> {
  private final Provider<OrderHistoryRepository> repositoryProvider;

  private GetOrderHistoryUseCaseImpl_Factory(Provider<OrderHistoryRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetOrderHistoryUseCaseImpl get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetOrderHistoryUseCaseImpl_Factory create(
      Provider<OrderHistoryRepository> repositoryProvider) {
    return new GetOrderHistoryUseCaseImpl_Factory(repositoryProvider);
  }

  public static GetOrderHistoryUseCaseImpl newInstance(OrderHistoryRepository repository) {
    return new GetOrderHistoryUseCaseImpl(repository);
  }
}
