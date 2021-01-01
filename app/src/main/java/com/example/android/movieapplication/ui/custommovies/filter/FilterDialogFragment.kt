package com.example.android.movieapplication.ui.custommovies.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.android.movieapplication.R
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.databinding.FragmentFilterDialogBinding
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.network.MoviesApi
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar

class FilterDialogFragment : DialogFragment() {

    private lateinit var viewModelFactory: FilterDialogViewModelFactory
    private lateinit var binding: FragmentFilterDialogBinding

    private val viewModel: FilterDialogViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.run {
            viewModelFactory = FilterDialogViewModelFactory(
                MovieDbRepository.getInstance(
                    this,
                    MoviesApi.retrofitService,
                    MovieDatabase.getInstance(this)
                ),
                application
            )
        }

        binding = FragmentFilterDialogBinding.inflate(layoutInflater, container, false)

        val toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener {
            println("close")
            dismiss()
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


        viewModel.filterModel.observe(viewLifecycleOwner) {
            it?.let {
                disableAutoCompleteFilters()
            }
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

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        dialog?.run {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            window?.setLayout(width, height)
            window?.attributes?.windowAnimations = R.style.AppTheme_Slide
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

}