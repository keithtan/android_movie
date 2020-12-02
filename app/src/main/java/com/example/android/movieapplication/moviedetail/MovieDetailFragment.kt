package com.example.android.movieapplication.moviedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.databinding.MovieDetailFragmentBinding
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.network.MoviesApi

class MovieDetailFragment : Fragment() {


    private lateinit var viewModelFactory: MovieDetailViewModelFactory
    private val viewModel: MovieDetailViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val movieId = MovieDetailFragmentArgs.fromBundle(requireArguments()).movieId
        val application = requireNotNull(activity).application

        activity?.let {
            viewModelFactory = MovieDetailViewModelFactory(
                movieId,
                MovieDbRepository(MoviesApi.retrofitService, MovieDatabase.getInstance(it)),
                application
            )
        }

        val binding = MovieDetailFragmentBinding.inflate(layoutInflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }


}