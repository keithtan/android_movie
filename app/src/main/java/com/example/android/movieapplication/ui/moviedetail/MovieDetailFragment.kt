package com.example.android.movieapplication.ui.moviedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.databinding.MovieDetailFragmentBinding
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.network.MoviesApi

class MovieDetailFragment : Fragment() {

    private lateinit var viewModelFactory: MovieDetailViewModelFactory
    private lateinit var binding: MovieDetailFragmentBinding
    private lateinit var adapter: MovieCastAdapter
    private lateinit var extras: Navigator.Extras

    private val viewModel: MovieDetailViewModel by viewModels { viewModelFactory }
    private val args: MovieDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(activity).application

        activity?.let {
            viewModelFactory = MovieDetailViewModelFactory(
                args.movieId,
                MovieDbRepository(MoviesApi.retrofitService, MovieDatabase.getInstance(it)),
                application
            )
        }

        binding = MovieDetailFragmentBinding.inflate(layoutInflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        adapter = MovieCastAdapter(MovieCastAdapter.OnClickListener { personId: Long, imageView: ImageView ->
            println("cast: $personId")
            extras = FragmentNavigatorExtras(
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

        binding.movieCast.adapter = adapter

        viewModel.movieCast.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            (view?.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            (activity as AppCompatActivity).onBackPressed()
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

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}