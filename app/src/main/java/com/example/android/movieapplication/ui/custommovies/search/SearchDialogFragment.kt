package com.example.android.movieapplication.ui.custommovies.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.android.movieapplication.R
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.databinding.SearchDialogFragmentBinding
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.network.MoviesApi


class SearchDialogFragment : DialogFragment() {

    private lateinit var viewModelFactory: SearchDialogViewModelFactory
    private lateinit var binding: SearchDialogFragmentBinding
    private lateinit var adapter: SearchAdapter
    private lateinit var extras: Navigator.Extras

    private val viewModel: SearchDialogViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.let {
            viewModelFactory = SearchDialogViewModelFactory(
                MovieDbRepository(
                    MoviesApi.retrofitService,
                    MovieDatabase.getInstance(it)
                )
            )
        }

        binding = SearchDialogFragmentBinding.inflate(layoutInflater, container, false)

        val toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener {
            println("close")
            dismiss()
        }
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(requireContext(), R.color.black_200))

        binding.actionSearch.queryHint = "Search Movies"
        binding.actionSearch.isIconified = false
        binding.actionSearch.performClick()
        binding.actionSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                println("search query: $query")
                closeKeyboard()
                return true
            }

            private fun closeKeyboard() {
                val imm: InputMethodManager? =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm?.toggleSoftInput(0, 0)
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                println("search text: $newText")
                if (!newText.isNullOrBlank()) {
                    viewModel.searchMovies(newText)
                }

                return true
            }
        })

        adapter = SearchAdapter(SearchAdapter.OnClickListener { movieId: Long, imageView: ImageView ->
            viewModel.displayMovieDetails(movieId)
            extras = FragmentNavigatorExtras(
                imageView to "$movieId"
            )
            findNavController()
                .navigate(
                    SearchDialogFragmentDirections.actionSearchDialogFragmentToMovieDetailFragment(
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

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.attributes.windowAnimations = R.style.AppTheme_Slide
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

}