package com.example.android.movieapplication.ui.movies.search

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.movieapplication.R
import com.example.android.movieapplication.databinding.FragmentMovieSearchBinding
import com.example.android.movieapplication.ui.movies.MovieListAdapter
import com.example.android.movieapplication.ui.movies.moviesection.MovieSection
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieSearchFragment : Fragment() {

    private lateinit var binding: FragmentMovieSearchBinding
    private lateinit var adapter: MovieListAdapter

    private val viewModel: MovieSearchViewModel by viewModels()
    private val args: MovieSearchFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMovieSearchBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.setSection(args.section)

        initAdapter()

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.apply {
            // Remove existing menu items
            removeItem(R.id.action_filter)
            removeItem(R.id.action_search)

            inflater.inflate(R.menu.search_menu, this)
            initSearchView(this)
        }
    }

    private fun initSearchView(menu: Menu) {
        (menu.getItem(0).actionView as SearchView).apply {
            queryHint = "Search Movies"
            isIconified = false
            setQuery(viewModel.query.value, false)
            setOnCloseListener { true }
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    hideKeyboard()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (!newText.isNullOrBlank()) {
                        viewModel.setQuery(newText)
                    }
                    return true
                }
            })
        }
    }

    private fun initAdapter() {
        adapter =
            MovieListAdapter(
                MovieListAdapter.OnClickListener { movieId: Long, imageView: ImageView ->
                    val extras = FragmentNavigatorExtras(
                        imageView to "$movieId"
                    )
                    findNavController()
                        .navigate(
                            MovieSearchFragmentDirections.actionMovieSearchFragmentToMovieDetailFragment(
                                movieId,
                                args.section
                            ),
                            extras
                        )
                })

        binding.searchedMovies.adapter = adapter
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager? =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

}