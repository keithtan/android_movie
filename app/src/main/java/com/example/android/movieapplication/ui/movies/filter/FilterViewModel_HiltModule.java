package com.example.android.movieapplication.ui.movies.filter;

import androidx.hilt.lifecycle.ViewModelAssistedFactory;
import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.codegen.OriginatingElement;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(ActivityRetainedComponent.class)
@OriginatingElement(
    topLevelClass = MovieFilterViewModel.class
)
public interface FilterViewModel_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.example.android.movieapplication.ui.movies.filter.FilterViewModel")
  ViewModelAssistedFactory<? extends ViewModel> bind(FilterViewModel_AssistedFactory factory);
}
