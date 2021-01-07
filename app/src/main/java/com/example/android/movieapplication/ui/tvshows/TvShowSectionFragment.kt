package com.example.android.movieapplication.ui.tvshows

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.android.movieapplication.databinding.FragmentTvShowSectionBinding
import com.example.android.movieapplication.ui.MoviesViewPagerFragmentDirections
import com.example.android.movieapplication.ui.movies.MoviesLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TvShowSectionFragment(private val section: TvShowSection) : Fragment() {

    private lateinit var binding: FragmentTvShowSectionBinding
    private lateinit var adapter: TvShowPagingAdapter

    private val viewModel: TvShowSectionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTvShowSectionBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initRetryButton()
        initFloatingActionButton()
        initAdapter()
        search()

        return binding.root
    }

    private fun initRetryButton() {
        binding.retryButton.setOnClickListener { adapter.retry() }
    }

    private fun initFloatingActionButton() {
        binding.floatingActionButton.hide()
        setSmoothScroll()
        handleFabAppearance()
    }

    private fun setSmoothScroll() {
        binding.floatingActionButton.setOnClickListener {
            binding.movieList.layoutManager?.smoothScrollToPosition(
                binding.movieList,
                RecyclerView.State(),
                0
            )
        }
    }

    private fun handleFabAppearance() {
        binding.movieList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                showFabOnScrollOffset()
            }

            private fun showFabOnScrollOffset() {
                val position = binding.movieList.computeVerticalScrollOffset()
                if (position > 0) binding.floatingActionButton.show()
                else binding.floatingActionButton.hide()
            }
        })
    }

    private fun initAdapter() {
        setAdapter()
        setRestorationPolicy()
        addLoadStateHeaderAndFooter()
        addLoadStateListener()
    }

    private fun setAdapter() {
        adapter =
            TvShowPagingAdapter(
                TvShowPagingAdapter.OnClickListener { movieId: Long, imageView: ImageView ->
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
    }

    private fun setRestorationPolicy() {
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private fun addLoadStateHeaderAndFooter() {
        binding.movieList.adapter = adapter.withLoadStateHeaderAndFooter(
            header = MoviesLoadStateAdapter { adapter.retry() },
            footer = MoviesLoadStateAdapter { adapter.retry() }
        )
    }

    private fun addLoadStateListener() {
        adapter.addLoadStateListener { loadState ->
            // Show loading spinner during initial load or refresh.
            viewModel.loadingState.value = loadState.source.refresh is LoadState.Loading
                    && adapter.itemCount == 0
            // Show the retry state if initial load or refresh fails.
            viewModel.retryState.value = loadState.source.refresh is LoadState.Error
            // Show empty text if there are no movies
            viewModel.emptyState.value = loadState.source.refresh is LoadState.NotLoading
                    && loadState.append.endOfPaginationReached
                    && adapter.itemCount < 1

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