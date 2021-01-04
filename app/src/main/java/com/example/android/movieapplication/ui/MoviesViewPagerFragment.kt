package com.example.android.movieapplication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import com.example.android.movieapplication.databinding.FragmentMoviesViewPagerBinding
import com.example.android.movieapplication.ui.movies.custommovies.CustomMoviesFragment
import com.example.android.movieapplication.ui.movies.moviesection.MovieSection
import com.example.android.movieapplication.ui.movies.moviesection.MovieSectionFragment
import com.google.android.material.tabs.TabLayoutMediator

class MoviesViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentMoviesViewPagerBinding.inflate(layoutInflater, container, false)

        val fragmentList = arrayListOf(
            MovieSectionFragment(MovieSection.LATEST),
            MovieSectionFragment(MovieSection.COMINGSOON),
            CustomMoviesFragment()
        )

        val adapter = MoviesViewPagerAdapter(
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

        binding.viewPager.setPageTransformer(ZoomOutPageTransformer())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        (view.parent as? ViewGroup)?.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}