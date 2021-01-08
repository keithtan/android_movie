package com.example.android.movieapplication.ui.tvshows.tvshowdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.example.android.movieapplication.databinding.FragmentTvShowDetailBinding
import com.example.android.movieapplication.network.TvShowDetail
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TvShowDetailFragment : Fragment() {

    private lateinit var binding: FragmentTvShowDetailBinding
    private lateinit var castAdapter: TvShowCastAdapter
    private lateinit var videoAdapter: TvShowVideoAdapter

    private val viewModel: TvShowDetailViewModel by viewModels()
    private val args: TvShowDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.saveMovieId(args.tvShowId)

        binding = FragmentTvShowDetailBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initCastAdapter()
        initVideoAdapter()

        viewModel.tvShowDetail.observe(viewLifecycleOwner) {
            handleNetworkError(it)
            (view?.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }
        }

        handleBackNavigation()

        return binding.root
    }

    private fun handleNetworkError(it: TvShowDetail?) {
        if (it == null) {
            binding.appbar.setExpanded(false, false)
            binding.collapseToolbar.scrimAnimationDuration = 0L
            ViewCompat.setNestedScrollingEnabled(binding.nestedScroll, false)
        }
    }

    private fun handleBackNavigation() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initVideoAdapter() {
        videoAdapter =
            TvShowVideoAdapter(
                lifecycle
            )
        binding.movieVideos.adapter = videoAdapter
    }

    private fun initCastAdapter() {
        castAdapter =
            TvShowCastAdapter(
                TvShowCastAdapter.OnClickListener { castId: Long, imageView: ImageView ->
                    val extras = FragmentNavigatorExtras(
                        imageView to "$castId"
                    )
                findNavController()
                    .navigate(
                        TvShowDetailFragmentDirections.actionTvShowDetailFragmentToTvShowCastDetailFragment(
                            castId
                        ),
                        extras
                    )
                })
        binding.movieCast.adapter = castAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedElementEnterTransition = TransitionInflater
            .from(context)
            .inflateTransition(android.R.transition.move)
        postponeEnterTransition()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

}