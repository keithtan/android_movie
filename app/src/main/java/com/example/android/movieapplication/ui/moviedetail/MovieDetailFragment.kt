package com.example.android.movieapplication.ui.moviedetail

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionInflater
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.databinding.MovieDetailFragmentBinding
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.network.MoviesApi

class MovieDetailFragment : Fragment() {

    private lateinit var viewModelFactory: MovieDetailViewModelFactory
    private lateinit var binding: MovieDetailFragmentBinding
    private lateinit var adapter: MovieCastAdapter

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


        binding = MovieDetailFragmentBinding.inflate(layoutInflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        adapter = MovieCastAdapter(MovieCastAdapter.OnClickListener {
            println("cast: $it")
        })

        binding.movieCast.adapter = adapter

        viewModel.movieCast.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.toolbar.setNavigationOnClickListener {
            (activity as AppCompatActivity).onBackPressed()
        }

        applySharedElementTransition()

        return binding.root
    }

    private fun applySharedElementTransition() {
        postponeEnterTransition()

        binding.imageRequestListener = object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                startPostponedEnterTransition()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                startPostponedEnterTransition()
                return false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedElementEnterTransition = TransitionInflater
            .from(context)
            .inflateTransition(android.R.transition.move)
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