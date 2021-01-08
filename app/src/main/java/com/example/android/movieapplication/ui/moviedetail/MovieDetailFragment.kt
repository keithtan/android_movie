package com.example.android.movieapplication.ui.moviedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.example.android.movieapplication.databinding.FragmentMovieDetailBinding
import com.example.android.movieapplication.network.MovieDetail
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailFragment : Fragment() {

    private lateinit var binding: FragmentMovieDetailBinding
    private lateinit var castAdapter: MovieCastAdapter
    private lateinit var videoAdapter: MovieVideoAdapter

    private val viewModel: MovieDetailViewModel by viewModels()
    private val args: MovieDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.saveMovieId(args.movieId)

        binding = FragmentMovieDetailBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initCastAdapter()
        initVideoAdapter()

        viewModel.movieDetail.observe(viewLifecycleOwner) {
            handleNetworkError(it)
            (view?.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }
        }

        handleBackNavigation()

        return binding.root
    }

    private fun handleNetworkError(it: MovieDetail?) {
        if (it == null) {
            binding.appbar.setExpanded(false, false)
            binding.collapseToolbar.scrimAnimationDuration = 0L
            ViewCompat.setNestedScrollingEnabled(binding.nestedScroll, false)
        }
    }

    private fun handleBackNavigation() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initVideoAdapter() {
        videoAdapter = MovieVideoAdapter(lifecycle)
        binding.movieVideos.adapter = videoAdapter
    }

    private fun initCastAdapter() {
        castAdapter =
            MovieCastAdapter(MovieCastAdapter.OnClickListener { personId: Long, imageView: ImageView ->
                val extras = FragmentNavigatorExtras(
                    imageView to "$personId"
                )
                findNavController()
                    .navigate(
                        MovieDetailFragmentDirections.actionMovieDetailFragmentToMovieCastDetailFragment(
                            personId
                        ),
                        extras
                    )
            })
        binding.movieCast.adapter = castAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedElementEnterTransition = TransitionInflater
            .from(context)
            .inflateTransition(android.R.transition.move)
        postponeEnterTransition()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

}