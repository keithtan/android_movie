package com.example.android.movieapplication.ui.moviedetail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.movieapplication.R
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.databinding.FragmentMovieDetailBinding
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.network.MoviesApi
import com.example.android.movieapplication.util.themeColor
import com.google.android.material.transition.MaterialContainerTransform

class MovieDetailFragment : Fragment() {

    private lateinit var viewModelFactory: MovieDetailViewModelFactory
    private lateinit var binding: FragmentMovieDetailBinding
    private lateinit var castAdapter: MovieCastAdapter
    private lateinit var videoAdapter: MovieVideoAdapter

    private val viewModel: MovieDetailViewModel by viewModels { viewModelFactory }
    private val args: MovieDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val application = requireNotNull(activity).application

        activity?.let {
            viewModelFactory = MovieDetailViewModelFactory(
                args.movieId,
                MovieDbRepository.getInstance(
                    it,
                    MoviesApi.retrofitService,
                    MovieDatabase.getInstance(it)
                ),
                application
            )
        }

        binding = FragmentMovieDetailBinding.inflate(layoutInflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        castAdapter = MovieCastAdapter(MovieCastAdapter.OnClickListener { personId: Long, imageView: ImageView ->
            val extras = FragmentNavigatorExtras(
                imageView to "$personId"
            )
            findNavController()
                .navigate(
                    MovieDetailFragmentDirections.actionMovieDetailFragmentToPeopleDetailFragment(
                        personId
                    ),
                    extras
                )
        })
        binding.movieCast.adapter = castAdapter

        videoAdapter = MovieVideoAdapter(lifecycle)
        binding.movieVideos.adapter = videoAdapter

        viewModel.movieCast.observe(viewLifecycleOwner) {
            castAdapter.submitList(it)
            (view?.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }
        }

        viewModel.movieVideos.observe(viewLifecycleOwner) {
            videoAdapter.submitList(it)
        }

        viewModel.movieDetail.observe(viewLifecycleOwner) {
            it?.let {
                binding.movieDetailConstraintLayout.isVisible = true
                binding.errorText.isVisible = false
            } ?: run {
                binding.movieDetailConstraintLayout.isVisible = false
                binding.errorText.isVisible = true
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            (activity as AppCompatActivity).onBackPressed()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.movie_motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }

        postponeEnterTransition()

    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

}