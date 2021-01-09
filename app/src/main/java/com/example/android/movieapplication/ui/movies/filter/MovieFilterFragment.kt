package com.example.android.movieapplication.ui.movies.filter

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
import androidx.navigation.fragment.navArgs
import com.example.android.movieapplication.R
import com.example.android.movieapplication.databinding.FragmentMovieFilterBinding
import com.example.android.movieapplication.ui.movies.moviesection.MovieSection
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieFilterFragment : Fragment() {

    private lateinit var binding: FragmentMovieFilterBinding

    private val viewModel: MovieFilterViewModel by viewModels()
    private val args: MovieFilterFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMovieFilterBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.setInitialFilter(args.section)

        initToolbar()
        setTooltip()
        setCheckChangedListener()
        setLongClickListener()

        viewModel.movieFilterModel.observe(viewLifecycleOwner) {
            disableAutoCompleteFilters()
        }

        viewModel.tvShowFilterModel.observe(viewLifecycleOwner) {
            disableAutoCompleteFilters()
        }

        return binding.root
    }

    private fun setTooltip() {
        TooltipCompat.setTooltipText(binding.textView2, "Hold on chips to disable them")
    }

    private fun initToolbar() {
        binding.toolbar.apply {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            title = "Filter Movies"
            inflateMenu(R.menu.filter_menu)
            navigationIcon?.setTint(ContextCompat.getColor(requireContext(), R.color.black_200))
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_save -> {
                        if (args.section == MovieSection.MOVIE_CUSTOM)
                            viewModel.saveMovieFilter()
                        else if (args.section == MovieSection.TV_SHOW_CUSTOM)
                            viewModel.saveTvShowFilter()
                        showFeedback()
                        true
                    }
                    else -> true
                }
            }
        }

    }

    private fun showFeedback() {
        Snackbar
            .make(
                binding.layout,
                "Filter saved. You may close this dialog.",
                Snackbar.LENGTH_SHORT
            )
            .setTextColor(
                ContextCompat.getColor(requireContext(), R.color.deep_orange_a100)
            )
            .show()
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