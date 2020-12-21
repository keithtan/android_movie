package com.example.android.movieapplication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import com.example.android.movieapplication.databinding.FragmentViewPagerBinding
import com.example.android.movieapplication.ui.comingsoon.ComingSoonFragment
import com.example.android.movieapplication.ui.custommovies.CustomMoviesFragment
import com.example.android.movieapplication.ui.overview.OverviewFragment
import com.google.android.material.tabs.TabLayoutMediator

class ViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentViewPagerBinding.inflate(layoutInflater, container, false)

        val fragmentList = arrayListOf(
            OverviewFragment(),
            ComingSoonFragment(),
            CustomMoviesFragment()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Latest"
                1 -> tab.text = "Coming Soon"
                2 -> tab.text = "Custom"
            }
        }.attach()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        (view.parent as? ViewGroup)?.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }
}