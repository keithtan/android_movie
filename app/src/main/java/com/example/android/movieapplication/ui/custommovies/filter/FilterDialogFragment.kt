package com.example.android.movieapplication.ui.custommovies.filter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
                MovieDbRepository(
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
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_save -> {
                    val dateFrom =
                        if (viewModel.dateFrom.isNotBlank()) "${viewModel.dateFrom}-01-01"
                        else ""
                    val dateTo =
                        if (viewModel.dateTo.isNotBlank()) "${viewModel.dateTo}-12-31"
                        else ""
                    val filter = Filter(
                        dateFrom,
                        dateTo,
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
                viewModel.dateFrom =
                    if (it.dateFrom.isNotBlank()) it.dateFrom.substring(0, 4)
                    else ""
                viewModel.dateTo =
                    if (it.dateTo.isNotBlank()) it.dateTo.substring(0, 4)
                    else ""
                viewModel.voteAverage = it.voteAverage

                updateYearFromAdapter()
                updateYearToAdapter()
            }
        }


        val yearsFrom = (1874..currentYear).toList()
        val adapterFrom = ArrayAdapter(requireContext(), R.layout.list_item_year, yearsFrom)
        binding.autoCompleteTextViewFrom.setAdapter(adapterFrom)

        binding.autoCompleteTextViewFrom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(str: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun onTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {
                viewModel.dateFrom = text.toString()
                updateYearFromAdapter()
            }

            override fun afterTextChanged(str: Editable?) {}

        })

        val yearsTo = (1874..currentYear).toList()
        val adapterTo = ArrayAdapter(requireContext(), R.layout.list_item_year, yearsTo)
        binding.autoCompleteTextViewTo.setAdapter(adapterTo)

        binding.autoCompleteTextViewTo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(str: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun onTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {
                viewModel.dateTo = text.toString()
                updateYearToAdapter()
            }

            override fun afterTextChanged(str: Editable?) {}

        })

        binding.slider.addOnChangeListener { slider, value, fromUser ->
            println(value)
            viewModel.voteAverage = value
        }


        binding.checkedChangeListener =
            CompoundButton.OnCheckedChangeListener { chip, isChecked ->
                (chip as Chip).isCheckedIconVisible = isChecked
                viewModel.updateGenre(chip.id, isChecked)
            }

        return binding.root
    }

    private fun updateYearToAdapter() {
        if (viewModel.dateTo.length >= 4) {
            println(viewModel.dateTo)
            val dateTo = viewModel.dateTo.substring(0, 4).toInt()
//                viewModel.dateTo.substring(0, 4).toInt()
            val yearsFrom = (1874..dateTo).toList()
            val adapter = ArrayAdapter(requireContext(), R.layout.list_item_year, yearsFrom)
            binding.autoCompleteTextViewFrom.setAdapter(adapter)
        }
    }

    private fun updateYearFromAdapter() {
        if (viewModel.dateFrom.length >= 4) {
            println(viewModel.dateFrom)
            val dateFrom = viewModel.dateFrom.substring(0, 4).toInt()
            val yearsTo = (dateFrom..currentYear).toList()
            val adapter = ArrayAdapter(requireContext(), R.layout.list_item_year, yearsTo)
            binding.autoCompleteTextViewTo.setAdapter(adapter)
        }
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