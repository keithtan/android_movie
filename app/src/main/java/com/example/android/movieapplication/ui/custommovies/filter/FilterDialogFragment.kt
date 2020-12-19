package com.example.android.movieapplication.ui.custommovies.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.android.movieapplication.R
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.databinding.FragmentFilterDialogBinding
import com.example.android.movieapplication.db.Filter
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.network.MoviesApi
import com.google.android.material.chip.Chip


class FilterDialogFragment : DialogFragment() {

    private lateinit var viewModelFactory: FilterDialogViewModelFactory

    private val viewModel: FilterDialogViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.let {
            viewModelFactory = FilterDialogViewModelFactory(
                MovieDbRepository(
                    MoviesApi.retrofitService,
                    MovieDatabase.getInstance(it)
                )
            )
        }

        val binding = FragmentFilterDialogBinding.inflate(layoutInflater, container, false)

        val toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener {
            println("close")
            dismiss()
        }
        toolbar.title = "Filter Movies"
        toolbar.inflateMenu(R.menu.filter_menu)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_save -> {
                    val filter = Filter(
                        viewModel.dateFrom,
                        viewModel.dateTo,
                        viewModel.voteAverage
                    )
                    viewModel.saveFilter(filter)

                    viewModel.genres.value?.let {
                        viewModel.saveGenres(it)
                    }
                    dismiss()
                    true
                }
                else -> true
            }
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.filter.observe(this) {
            it?.let {
                viewModel.dateFrom = it.dateFrom
                viewModel.dateTo = it.dateTo
                viewModel.voteAverage = it.voteAverage
            }
        }

        binding.checkedChangeListener =
            CompoundButton.OnCheckedChangeListener { chip, isChecked ->
                (chip as Chip).isCheckedIconVisible = isChecked
                viewModel.updateGenre(chip.id, isChecked)
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