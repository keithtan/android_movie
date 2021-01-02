package com.example.android.movieapplication.ui.custommovies.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.android.movieapplication.R
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.databinding.SearchFragmentBinding
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.network.MoviesApi

class SearchFragment : Fragment() {

    private lateinit var viewModelFactory: SearchViewModelFactory
    private lateinit var binding: SearchFragmentBinding
    private lateinit var adapter: SearchAdapter
    private lateinit var extras: Navigator.Extras

    private val viewModel: SearchViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.let {
            viewModelFactory = SearchViewModelFactory(
                MovieDbRepository.getInstance(
                    it,
                    MoviesApi.retrofitService,
                    MovieDatabase.getInstance(it)
                )
            )
        }

        binding = SearchFragmentBinding.inflate(layoutInflater, container, false)


        val toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener {
            hideKeyboard()
            findNavController().popBackStack()
        }
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(requireContext(), R.color.black_200))

        binding.actionSearch.queryHint = "Search Movies"
        binding.actionSearch.isIconified = false
        binding.actionSearch.performClick()
        binding.actionSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrBlank()) {
                    viewModel.searchMovies(newText)
                }
                return true
            }
        })
        binding.actionSearch.setOnCloseListener { true }

        adapter = SearchAdapter(SearchAdapter.OnClickListener { movieId: Long, imageView: ImageView ->
            viewModel.displayMovieDetails(movieId)
            extras = FragmentNavigatorExtras(
                imageView to "$movieId"
            )
            findNavController()
                .navigate(
                    SearchFragmentDirections.actionSearchFragmentToMovieDetailFragment(
                        movieId
                    ),
                    extras
                )
        })

        binding.searchedMovies.adapter = adapter
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.movies.observe(viewLifecycleOwner) {
            it?.let {
                println(it)
                adapter.submitList(it)
            }
        }

        viewModel.navigateToSelectedMovie.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.displayMovieDetailsComplete()
            }
        }

        return binding.root
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager? =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

}