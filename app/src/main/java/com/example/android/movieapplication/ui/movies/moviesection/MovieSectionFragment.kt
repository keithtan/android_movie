package com.example.android.movieapplication.ui.movies.moviesection

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
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.android.movieapplication.databinding.FragmentMovieSectionBinding
import com.example.android.movieapplication.ui.MoviesViewPagerFragmentDirections
import com.example.android.movieapplication.ui.movies.MoviePagingAdapter
import com.example.android.movieapplication.ui.movies.MoviesLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieSectionFragment(private val section: MovieSection) : Fragment() {

    private lateinit var binding: FragmentMovieSectionBinding
    private lateinit var adapter: MoviePagingAdapter

    private val viewModel: MovieSectionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMovieSectionBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initRetryButton()
        initFloatingActionButton()
        handleFabAppearance()

        return binding.root
    }

    private fun initRetryButton() {
        binding.retryButton.setOnClickListener { adapter.retry() }
    }

    private fun handleFabAppearance() {
        binding.movieList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val position = binding.movieList.computeVerticalScrollOffset()
                if (position > 0) binding.floatingActionButton.show()
                else binding.floatingActionButton.hide()
            }
        })
    }

    private fun initFloatingActionButton() {
        binding.floatingActionButton.hide()
        binding.floatingActionButton.setOnClickListener {
            binding.movieList.layoutManager?.smoothScrollToPosition(
                binding.movieList,
                RecyclerView.State(),
                0
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter =
            MoviePagingAdapter(
                MoviePagingAdapter.OnClickListener { movieId: Long, imageView: ImageView ->
                    val extras = FragmentNavigatorExtras(
                        imageView to "$movieId"
                    )
                    findNavController()
                        .navigate(
                            MoviesViewPagerFragmentDirections.actionViewPagerFragmentToMovieDetailFragment(
                                movieId
                            ),
                            extras
                        )

                })
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        initAdapter()
        search()
    }

    private fun initAdapter() {
        binding.movieList.adapter = adapter.withLoadStateHeaderAndFooter(
            header = MoviesLoadStateAdapter { adapter.retry() },
            footer = MoviesLoadStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener { loadState ->
            val loadingState = loadState.source.refresh is LoadState.Loading && adapter.itemCount == 0
            val retryState = loadState.source.refresh is LoadState.Error
            // Only show the list if refresh succeeds.
            binding.movieList.isVisible = !(loadingState || retryState)
            // Show loading spinner during initial load or refresh.
            binding.progressBar.isVisible = loadingState
            // Show the retry state if initial load or refresh fails.
            binding.retryButton.isVisible = retryState

            // Show info text if there are no movies
            if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached) {
                binding.emptyText.isVisible = adapter.itemCount < 1
            }

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

    private var searchJob: Job? = null

    private fun search() {
        // Make sure we cancel the previous job before creating a new one
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchMovies(section).collectLatest {
                adapter.submitData(it)
            }
        }
    }

}