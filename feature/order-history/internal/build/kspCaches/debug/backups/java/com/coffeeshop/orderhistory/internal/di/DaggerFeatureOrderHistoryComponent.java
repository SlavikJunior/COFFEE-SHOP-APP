package com.coffeeshop.orderhistory.internal.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.coffeeshop.di.CoreDiComponent;
import com.coffeeshop.di.multibindings.MultiBindingFactory;
import com.coffeeshop.orderhistory.internal.data.repository.OrderHistoryRepositoryImpl;
import com.coffeeshop.orderhistory.internal.data.repository.OrderHistoryRepositoryImpl_Factory;
import com.coffeeshop.orderhistory.internal.data.service.OrderHistoryService;
import com.coffeeshop.orderhistory.internal.domain.usecase.GetOrderHistoryUseCaseImpl;
import com.coffeeshop.orderhistory.internal.domain.usecase.GetOrderHistoryUseCaseImpl_Factory;
import com.coffeeshop.orderhistory.internal.screen.OrderHistoryViewModel;
import com.coffeeshop.orderhistory.internal.screen.OrderHistoryViewModel_Factory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.InstanceFactory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import javax.annotation.processing.Generated;
import kotlinx.coroutines.CoroutineDispatcher;
import retrofit2.Retrofit;

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
public final class DaggerFeatureOrderHistoryComponent {
  private DaggerFeatureOrderHistoryComponent() {
  }

  public static FeatureOrderHistoryComponent.Builder builder() {
    return new Builder();
  }

  private static final class Builder implements FeatureOrderHistoryComponent.Builder {
    private CoreDiComponent coreDiComponent;

    private Retrofit retrofit;

    @Override
    public Builder coreDiComponent(CoreDiComponent coreDiComponent) {
      this.coreDiComponent = Preconditions.checkNotNull(coreDiComponent);
      return this;
    }

    @Override
    public Builder retrofit(Retrofit retrofit) {
      this.retrofit = Preconditions.checkNotNull(retrofit);
      return this;
    }

    @Override
    public FeatureOrderHistoryComponent build() {
      Preconditions.checkBuilderRequirement(coreDiComponent, CoreDiComponent.class);
      Preconditions.checkBuilderRequirement(retrofit, Retrofit.class);
      return new FeatureOrderHistoryComponentImpl(coreDiComponent, retrofit);
    }
  }

  private static final class FeatureOrderHistoryComponentImpl implements FeatureOrderHistoryComponent {
    private final FeatureOrderHistoryComponentImpl featureOrderHistoryComponentImpl = this;

    Provider<Retrofit> retrofitProvider;

    Provider<OrderHistoryService> provideOrderHistoryServiceProvider;

    Provider<CoroutineDispatcher> getDispatcherIOProvider;

    Provider<OrderHistoryRepositoryImpl> orderHistoryRepositoryImplProvider;

    Provider<GetOrderHistoryUseCaseImpl> getOrderHistoryUseCaseImplProvider;

    Provider<CoroutineDispatcher> getDispatcherMainProvider;

    Provider<OrderHistoryViewModel> orderHistoryViewModelProvider;

    FeatureOrderHistoryComponentImpl(CoreDiComponent coreDiComponentParam, Retrofit retrofitParam) {

      initialize(coreDiComponentParam, retrofitParam);

    }

    Map<Class<? extends ViewModel>, javax.inject.Provider<ViewModel>> mapOfClassOfAndProviderOfViewModel(
        ) {
      return Collections.<Class<? extends ViewModel>, javax.inject.Provider<ViewModel>>singletonMap(OrderHistoryViewModel.class, ((Provider) (orderHistoryViewModelProvider)));
    }

    MultiBindingFactory multiBindingFactory() {
      return new MultiBindingFactory(mapOfClassOfAndProviderOfViewModel());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final CoreDiComponent coreDiComponentParam,
        final Retrofit retrofitParam) {
      this.retrofitProvider = InstanceFactory.create(retrofitParam);
      this.provideOrderHistoryServiceProvider = DoubleCheck.provider(OrderHistoryModule_ProvideOrderHistoryServiceFactory.create(retrofitProvider));
      this.getDispatcherIOProvider = new GetDispatcherIOProvider(coreDiComponentParam);
      this.orderHistoryRepositoryImplProvider = OrderHistoryRepositoryImpl_Factory.create(provideOrderHistoryServiceProvider, getDispatcherIOProvider);
      this.getOrderHistoryUseCaseImplProvider = GetOrderHistoryUseCaseImpl_Factory.create(((Provider) (orderHistoryRepositoryImplProvider)));
      this.getDispatcherMainProvider = new GetDispatcherMainProvider(coreDiComponentParam);
      this.orderHistoryViewModelProvider = OrderHistoryViewModel_Factory.create(((Provider) (getOrderHistoryUseCaseImplProvider)), getDispatcherMainProvider);
    }

    @Override
    public ViewModelProvider.Factory getViewModelFactory() {
      return multiBindingFactory();
    }

    private static final class GetDispatcherIOProvider implements Provider<CoroutineDispatcher> {
      private final CoreDiComponent coreDiComponent;

      GetDispatcherIOProvider(CoreDiComponent coreDiComponent) {
        this.coreDiComponent = coreDiComponent;
      }

      @Override
      public CoroutineDispatcher get() {
        return Preconditions.checkNotNullFromComponent(coreDiComponent.getDispatcherIO());
      }
    }

    private static final class GetDispatcherMainProvider implements Provider<CoroutineDispatcher> {
      private final CoreDiComponent coreDiComponent;

      GetDispatcherMainProvider(CoreDiComponent coreDiComponent) {
        this.coreDiComponent = coreDiComponent;
      }

      @Override
      public CoroutineDispatcher get() {
        return Preconditions.checkNotNullFromComponent(coreDiComponent.getDispatcherMain());
      }
    }
  }
}
