package com.example.android.movieapplication.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.android.movieapplication.R
import com.example.android.movieapplication.databinding.FragmentTvViewPagerBinding
import com.example.android.movieapplication.ui.movies.moviesection.MovieSection
import com.example.android.movieapplication.ui.movies.moviesection.MovieSectionFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialSharedAxis

class TvViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentTvViewPagerBinding.inflate(layoutInflater, container, false)

        val fragmentList = arrayListOf(
            MovieSectionFragment(MovieSection.LATEST),
            MovieSectionFragment(MovieSection.COMINGSOON),
            MovieSectionFragment(MovieSection.CUSTOM)
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

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setHasOptionsMenu(position == 2)
            }
        })

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.app_menu, menu)
    }

    private val currentNavigationFragment: Fragment?
        get() = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_filter -> {
            currentNavigationFragment.apply {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
                    duration = resources.getInteger(R.integer.movie_motion_duration_large).toLong()
                }
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
                    duration = resources.getInteger(R.integer.movie_motion_duration_large).toLong()
                }
            }

            findNavController()
                .navigate(
                    MoviesViewPagerFragmentDirections.actionViewPagerFragmentToFilterFragment()
                )
            true
        }
        R.id.action_search -> {
            findNavController()
                .navigate(
                    MoviesViewPagerFragmentDirections.actionViewPagerFragmentToSearchFragment()
                )
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}