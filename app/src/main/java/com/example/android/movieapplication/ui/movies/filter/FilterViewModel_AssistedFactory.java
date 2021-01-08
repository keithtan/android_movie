package com.example.android.movieapplication.ui.movies.filter;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.hilt.lifecycle.ViewModelAssistedFactory;
import androidx.lifecycle.SavedStateHandle;
import com.example.android.movieapplication.data.MovieDbRepository;
import java.lang.Override;
import javax.annotation.Generated;
import javax.inject.Inject;
import javax.inject.Provider;

@Generated("androidx.hilt.AndroidXHiltProcessor")
public final class FilterViewModel_AssistedFactory implements ViewModelAssistedFactory<MovieFilterViewModel> {
  private final Provider<MovieDbRepository> repository;

  private final Provider<Application> application;

  @Inject
  FilterViewModel_AssistedFactory(Provider<MovieDbRepository> repository,
      Provider<Application> application) {
    this.repository = repository;
    this.application = application;
  }

  @Override
  @NonNull
  public MovieFilterViewModel create(SavedStateHandle arg0) {
    return new MovieFilterViewModel(repository.get(), application.get());
  }
}
