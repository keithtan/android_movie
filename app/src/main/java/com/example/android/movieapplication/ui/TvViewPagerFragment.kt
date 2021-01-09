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
import com.example.android.movieapplication.ui.movies.moviesection.Section
import com.example.android.movieapplication.ui.movies.moviesection.MovieSectionFragment
import com.example.android.movieapplication.ui.movies.moviesection.TYPE
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialSharedAxis

class TvViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentTvViewPagerBinding.inflate(layoutInflater, container, false)

        val fragmentList = arrayListOf(
            MovieSectionFragment(
                Section.TvShowSection(TYPE.LATEST)
            ),
            MovieSectionFragment(
                Section.TvShowSection(TYPE.COMING_SOON)
            ),
            MovieSectionFragment(
                Section.TvShowSection(TYPE.CUSTOM)
            )
        )

        val adapter = TvShowsViewPagerAdapter(
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
                    TvViewPagerFragmentDirections.actionTvViewPagerFragmentToMovieFilterFragment(
                        Section.TvShowSection(TYPE.CUSTOM)
                    )
                )
            true
        }
        R.id.action_search -> {
            findNavController()
                .navigate(
                    TvViewPagerFragmentDirections.actionTvViewPagerFragmentToMovieSearchFragment(
                        Section.TvShowSection(TYPE.CUSTOM)
                    )
                )
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}