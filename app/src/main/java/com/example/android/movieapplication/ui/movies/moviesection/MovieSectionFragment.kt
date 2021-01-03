package com.example.android.movieapplication.ui.movies.moviesection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.android.movieapplication.R
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.databinding.FragmentOverviewBinding
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.network.MoviesApi
import com.example.android.movieapplication.ui.ViewPagerFragmentDirections
import com.example.android.movieapplication.ui.movies.MoviePagingAdapter
import com.example.android.movieapplication.ui.movies.MoviesLoadStateAdapter
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MovieSectionFragment(private val section: MovieSection) : Fragment() {

    private lateinit var binding: FragmentOverviewBinding
    private lateinit var viewModelFactory: MovieSectionViewModelFactory
    private lateinit var adapter: MoviePagingAdapter

    private val viewModel: MovieSectionViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.let {
            viewModelFactory = MovieSectionViewModelFactory(
                MovieDbRepository.getInstance(
                    it,
                    MoviesApi.retrofitService,
                    MovieDatabase.getInstance(it)
                ),
                section
            )
        }

        binding = FragmentOverviewBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.retryButton.setOnClickListener { adapter.retry() }

        binding.movieList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val position = binding.movieList.computeVerticalScrollOffset()
                if (position > 0) binding.floatingActionButton.show()
                else binding.floatingActionButton.hide()
            }
        })

        binding.floatingActionButton.hide()

        binding.floatingActionButton.setOnClickListener {
            binding.movieList.layoutManager?.smoothScrollToPosition(binding.movieList, RecyclerView.State(), 0)
        }

        return binding.root
    }

    private var searchJob: Job? = null

    private fun search() {
        // Make sure we cancel the previous job before creating a new one
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchMovies().collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun initAdapter() {
        binding.movieList.adapter = adapter.withLoadStateHeaderAndFooter(
            header = MoviesLoadStateAdapter { adapter.retry() },
            footer = MoviesLoadStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener { loadState ->
            // Only show the list if refresh succeeds.
            binding.movieList.isVisible = loadState.source.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            // Show the retry state if initial load or refresh fails.
            binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error

            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    this.context,
                    "\uD83D\uDE28 Wooops ${it.error}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter =
            MoviePagingAdapter(
                MoviePagingAdapter.OnClickListener { movieId: Long, cardView: CardView ->

                    exitTransition = MaterialElevationScale(false).apply {
                        duration =
                            resources.getInteger(R.integer.movie_motion_duration_large).toLong()
                    }
                    reenterTransition = MaterialElevationScale(true).apply {
                        duration =
                            resources.getInteger(R.integer.movie_motion_duration_large).toLong()
                    }

                    val extras = FragmentNavigatorExtras(
                        cardView to "$movieId"
                    )
                    findNavController()
                        .navigate(
                            ViewPagerFragmentDirections.actionViewPagerFragmentToMovieDetailFragment(
                                movieId
                            ),
                            extras
                        )

                })
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        initAdapter()
        search()

    }

}