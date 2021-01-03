package com.example.android.movieapplication.ui.movies.custommovies.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.android.movieapplication.R
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.databinding.FragmentFilterBinding
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.network.MoviesApi
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis

class FilterFragment : Fragment() {

    private lateinit var viewModelFactory: FilterViewModelFactory
    private lateinit var binding: FragmentFilterBinding

    private val viewModel: FilterViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.run {
            viewModelFactory = FilterViewModelFactory(
                MovieDbRepository.getInstance(
                    this,
                    MoviesApi.retrofitService,
                    MovieDatabase.getInstance(this)
                ),
                application
            )
        }

        binding = FragmentFilterBinding.inflate(layoutInflater, container, false)

        val toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        toolbar.title = "Filter Movies"
        toolbar.inflateMenu(R.menu.filter_menu)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(requireContext(), R.color.black_200))
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_save -> {
                    viewModel.saveFilter()
                    Snackbar.make(binding.layout, "Filter saved. You may close this dialog.", Snackbar.LENGTH_SHORT)
                        .setTextColor(ContextCompat.getColor(requireContext(), R.color.deep_orange_a100))
                        .show()
                    true
                }
                else -> true
            }
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        TooltipCompat.setTooltipText(binding.textView2, "Hold on chips to disable them")

        viewModel.filterModel.observe(viewLifecycleOwner) {
            disableAutoCompleteFilters()
        }

        setCheckChangedListener()
        setLongClickListener()

        return binding.root
    }

    private fun disableAutoCompleteFilters() {
        binding.autoCompleteStartYear.setText(viewModel.startYear.value.toString(), false)
        binding.autoCompleteEndYear.setText(viewModel.endYear.value.toString(), false)
    }


    private fun setCheckChangedListener() {
        binding.checkedChangeListener = CompoundButton.OnCheckedChangeListener { chip, isChecked ->
            (chip as Chip).isCheckedIconVisible = isChecked
            chip.isSelected = false
            viewModel.updateIncludedGenres(chip.id, isChecked)
        }
    }

    private fun setLongClickListener() {
        binding.longClickListener = View.OnLongClickListener {
            (it as Chip).isChecked = false
            it.isSelected = !it.isSelected
            viewModel.updateExcludedGenres(it.id, it.isSelected)
            true
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.movie_motion_duration_large).toLong()
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.movie_motion_duration_large).toLong()
        }

    }

}