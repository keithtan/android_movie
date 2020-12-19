package com.example.android.movieapplication.ui.comingsoon

import android.graphics.drawable.Drawable
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
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.databinding.ComingSoonFragmentBinding
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.network.MoviesApi
import com.example.android.movieapplication.ui.ViewPagerFragmentDirections
import com.example.android.movieapplication.ui.overview.MovieListAdapter
import com.example.android.movieapplication.ui.overview.MoviesLoadStateAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ComingSoonFragment : Fragment() {

    private lateinit var binding: ComingSoonFragmentBinding
    private lateinit var viewModelFactory: ComingSoonViewModelFactory
    private lateinit var adapter: MovieListAdapter
    private lateinit var extras: Navigator.Extras

    private val viewModel: ComingSoonViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let {
            viewModelFactory = ComingSoonViewModelFactory(
                MovieDbRepository(
                    MoviesApi.retrofitService,
                    MovieDatabase.getInstance(it)
                )
            )
        }

        binding = ComingSoonFragmentBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val decoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        binding.movieList.addItemDecoration(decoration)

        adapter = MovieListAdapter(MovieListAdapter.OnClickListener { movieId: Long, imageView: ImageView ->
            viewModel.displayMovieDetails(movieId)
        })
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        initAdapter()
        search()

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
            viewModel.searchComingSoonMovies().collectLatest {
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
        adapter = MovieListAdapter(MovieListAdapter.OnClickListener { movieId: Long, imageView: ImageView ->
            viewModel.displayMovieDetails(movieId)
            extras = FragmentNavigatorExtras(
                imageView to "$movieId"
            )
            this.findNavController()
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