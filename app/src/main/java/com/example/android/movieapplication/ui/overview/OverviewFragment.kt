package com.example.android.movieapplication.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.databinding.OverviewFragmentBinding
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.network.MoviesApi
import com.example.android.movieapplication.ui.ViewPagerFragmentDirections
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OverviewFragment : Fragment() {

    private lateinit var binding: OverviewFragmentBinding
    private lateinit var viewModelFactory: OverviewViewModelFactory
    private lateinit var adapter: MoviePagingAdapter
    private lateinit var extras: Navigator.Extras

    private val viewModel: OverviewViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.let {
            viewModelFactory = OverviewViewModelFactory(
                MovieDbRepository(
                    MoviesApi.retrofitService,
                    MovieDatabase.getInstance(it)
                ))
        }

        binding = OverviewFragmentBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val decoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        binding.movieList.addItemDecoration(decoration)

        binding.retryButton.setOnClickListener { adapter.retry() }

        viewModel.navigateToSelectedMovie.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.displayMovieDetailsComplete()
            }
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
        adapter = MoviePagingAdapter(MoviePagingAdapter.OnClickListener { movieId: Long, imageView: ImageView ->
            viewModel.displayMovieDetails(movieId)
            extras = FragmentNavigatorExtras(
                imageView to "$movieId"
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