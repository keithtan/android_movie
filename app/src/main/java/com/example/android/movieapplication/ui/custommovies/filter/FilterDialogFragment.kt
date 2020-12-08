package com.example.android.movieapplication.ui.custommovies.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.android.movieapplication.R
import com.example.android.movieapplication.databinding.FragmentFilterDialogBinding
import com.example.android.movieapplication.db.Filter
import com.example.android.movieapplication.db.FilterDao
import com.example.android.movieapplication.db.MovieDatabase


class FilterDialogFragment : DialogFragment() {

    private lateinit var viewModelFactory: FilterDialogViewModelFactory
    private lateinit var database: FilterDao

    private val viewModel: FilterDialogViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.let {
            database = MovieDatabase.getInstance(it).filterDao()
            viewModelFactory = FilterDialogViewModelFactory(
                database
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
                    dismiss()
                    true
                }
                else -> true
            }
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


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