package com.example.android.movieapplication.ui.movies.custommovies.search

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
import com.example.android.movieapplication.R
import com.example.android.movieapplication.databinding.FragmentSearchBinding
import com.example.android.movieapplication.ui.peopledetail.MovieListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MovieListAdapter

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

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
            MovieListAdapter(MovieListAdapter.OnClickListener { movieId: Long, imageView: ImageView ->
                val extras = FragmentNavigatorExtras(
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