package com.example.android.movieapplication.ui.moviedetail

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.databinding.FragmentMovieDetailBinding
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.network.MoviesApi

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

        activity?.let {
            viewModelFactory = MovieDetailViewModelFactory(
                args.movieId,
                MovieDbRepository.getInstance(
                    it,
                    MoviesApi.retrofitService,
                    MovieDatabase.getInstance(it)
                ),
                it.application
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

        viewModel.movieDetail.observe(viewLifecycleOwner) {
            (view?.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
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