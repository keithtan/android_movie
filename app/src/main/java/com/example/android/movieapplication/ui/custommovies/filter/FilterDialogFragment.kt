package com.example.android.movieapplication.ui.custommovies.filter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.android.movieapplication.R
import com.example.android.movieapplication.data.MovieDbRepository
import com.example.android.movieapplication.databinding.FragmentFilterDialogBinding
import com.example.android.movieapplication.db.MovieDatabase
import com.example.android.movieapplication.network.MoviesApi
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import java.util.*


class FilterDialogFragment : DialogFragment() {

    private lateinit var viewModelFactory: FilterDialogViewModelFactory
    private lateinit var binding: FragmentFilterDialogBinding

    private val viewModel: FilterDialogViewModel by viewModels { viewModelFactory }

    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.let {
            viewModelFactory = FilterDialogViewModelFactory(
                MovieDbRepository.getInstance(
                    it,
                    MoviesApi.retrofitService,
                    MovieDatabase.getInstance(it)
                )
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

                    viewModel.genres.value?.let {
                        viewModel.saveGenres(it)
                    }
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
                println(it)
                if (it.startYear != 0)
                    viewModel.startYear = it.startYear
                if (it.endYear != 0)
                    viewModel.endYear = it.endYear
                if (it.voteAverage != 0.0f)
                    viewModel.voteAverage = it.voteAverage

                binding.autoCompleteStartYear.setText(viewModel.startYear.toString(), false)
                binding.autoCompleteEndYear.setText(viewModel.endYear.toString(), false)

                setEndYearAdapter()
                setStartYearAdapter()
            }
        }


        binding.autoCompleteStartYear.setOnItemClickListener { _, view, _, _ ->
            hideKeyboard(view)
        }
        binding.autoCompleteStartYear.doOnTextChanged { text, _, _, _ ->
            if (viewModel.startYear != text.toString().toInt()) {
                viewModel.startYear = text.toString().toInt()
                setEndYearAdapter()
            }
        }


        binding.autoCompleteEndYear.doOnTextChanged { text, _, _, _ ->
            if (viewModel.endYear != text.toString().toInt()) {
                viewModel.endYear = text.toString().toInt()
                setStartYearAdapter()
            }
        }
        binding.autoCompleteEndYear.setOnItemClickListener { _, view, _, _ ->
            hideKeyboard(view)
        }

        binding.slider.addOnChangeListener { _, value, _ ->
            viewModel.voteAverage = value
        }

        setCheckChangedListener()
        setLongClickListener()

        return binding.root
    }

    private fun setStartYearAdapter() {
        val endYear = viewModel.endYear
        println("endYear: " + endYear)
        val yearsFrom = (1874..endYear).toList()
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item_year, yearsFrom)
        binding.autoCompleteStartYear.setAdapter(adapter)
    }

    private fun setEndYearAdapter() {
        val startYear = viewModel.startYear
        val yearsTo = (startYear..currentYear).toList()
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item_year, yearsTo)
        binding.autoCompleteEndYear.setAdapter(adapter)
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

    private fun hideKeyboard(view: View) {
        val imm: InputMethodManager? =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
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