package com.example.android.movieapplication.ui.tvshows

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.movieapplication.R

class TvViewPagerFragment : Fragment() {

    private lateinit var viewModel: TvViewPagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tv_view_pager, container, false)
    }

}