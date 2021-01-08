package com.example.android.movieapplication.ui.movies.filter;

import android.app.Application;
import com.example.android.movieapplication.data.MovieDbRepository;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class FilterViewModel_AssistedFactory_Factory implements Factory<FilterViewModel_AssistedFactory> {
  private final Provider<MovieDbRepository> repositoryProvider;

  private final Provider<Application> applicationProvider;

  public FilterViewModel_AssistedFactory_Factory(Provider<MovieDbRepository> repositoryProvider,
      Provider<Application> applicationProvider) {
    this.repositoryProvider = repositoryProvider;
    this.applicationProvider = applicationProvider;
  }

  @Override
  public FilterViewModel_AssistedFactory get() {
    return newInstance(repositoryProvider, applicationProvider);
  }

  public static FilterViewModel_AssistedFactory_Factory create(
      Provider<MovieDbRepository> repositoryProvider, Provider<Application> applicationProvider) {
    return new FilterViewModel_AssistedFactory_Factory(repositoryProvider, applicationProvider);
  }

  public static FilterViewModel_AssistedFactory newInstance(Provider<MovieDbRepository> repository,
      Provider<Application> application) {
    return new FilterViewModel_AssistedFactory(repository, application);
  }
}
