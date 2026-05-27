package com.coffeeshop.orderhistory.internal.di;

import com.coffeeshop.orderhistory.internal.data.service.OrderHistoryService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import retrofit2.Retrofit;

@ScopeMetadata("com.coffeeshop.orderhistory.internal.di.OrderHistoryScope")
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
public final class OrderHistoryModule_ProvideOrderHistoryServiceFactory implements Factory<OrderHistoryService> {
  private final Provider<Retrofit> retrofitProvider;

  private OrderHistoryModule_ProvideOrderHistoryServiceFactory(
      Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public OrderHistoryService get() {
    return provideOrderHistoryService(retrofitProvider.get());
  }

  public static OrderHistoryModule_ProvideOrderHistoryServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new OrderHistoryModule_ProvideOrderHistoryServiceFactory(retrofitProvider);
  }

  public static OrderHistoryService provideOrderHistoryService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(OrderHistoryModule.INSTANCE.provideOrderHistoryService(retrofit));
  }
}
