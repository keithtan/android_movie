package com.example.android.movieapplication.ui.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.example.android.movieapplication.R
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.databinding.PeopleDetailFragmentBinding
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.network.MoviesApi
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PeopleDetailFragment : Fragment() {

    private lateinit var binding: PeopleDetailFragmentBinding
    private lateinit var viewModelFactory: PeopleDetailViewModelFactory
    private lateinit var adapter: MovieListAdapter
    private lateinit var extras: Navigator.Extras

    private val viewModel: PeopleDetailViewModel by viewModels { viewModelFactory }
    private val args: PeopleDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.let {
            viewModelFactory = PeopleDetailViewModelFactory(
                args.personId,
                MovieDbRepository(MoviesApi.retrofitService, MovieDatabase.getInstance(it))
            )
        }

        binding = PeopleDetailFragmentBinding.inflate(layoutInflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        adapter = MovieListAdapter(MovieListAdapter.OnClickListener { movieId: Long, imageView: ImageView ->
            viewModel.displayMovieDetails(movieId)
            extras = FragmentNavigatorExtras(
                imageView to "$movieId"
            )
            findNavController()
                .navigate(
                    PeopleDetailFragmentDirections.actionPeopleDetailFragmentToMovieDetailFragment(
                        movieId
                    ),
                    extras
                )
        })

        binding.movieList.adapter = adapter

        viewModel.peopleDetail.observe(viewLifecycleOwner) {
            println(it?.movieCredits?.movieList)
            if (it?.deathday == null) {
                binding.deathLabel.visibility = View.GONE
                binding.death.visibility = View.GONE
            }
            adapter.submitList(it?.movieCredits?.movieList)
            (view?.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }
        }

        binding.button.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Biography")
                .setMessage(viewModel.peopleDetail.value?.biography)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        viewModel.navigateToSelectedMovie.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.displayMovieDetailsComplete()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedElementEnterTransition = TransitionInflater
            .from(context)
            .inflateTransition(R.transition.curved_motion)

        postponeEnterTransition()

    }

}