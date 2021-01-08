package com.example.android.movieapplication.ui.tvshows.tvshowcastdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.example.android.movieapplication.R
import com.example.android.movieapplication.databinding.FragmentTvShowCastDetailBinding
import com.example.android.movieapplication.ui.tvshows.TvShowListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TvShowCastDetailFragment : Fragment() {

    private lateinit var binding: FragmentTvShowCastDetailBinding
    private lateinit var adapter: TvShowListAdapter

    private val viewModel: TvShowCastDetailViewModel by viewModels()
    private val args: TvShowCastDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.saveCastId(args.castId)

        binding = FragmentTvShowCastDetailBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initAdapter()
        initBiographyButton()

        viewModel.tvShowCastDetail.observe(viewLifecycleOwner) {
            (view?.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }
        }

        return binding.root
    }

    private fun initBiographyButton() {
        binding.button.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Biography")
                .setMessage(viewModel.tvShowCastDetail.value?.biography)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun initAdapter() {
        adapter =
            TvShowListAdapter(
                TvShowListAdapter.OnClickListener { castId: Long, imageView: ImageView ->
                    val extras = FragmentNavigatorExtras(
                        imageView to "$castId"
                    )
                    findNavController()
                        .navigate(
                            TvShowCastDetailFragmentDirections.actionTvShowCastDetailFragmentToTvShowDetailFragment(
                                castId
                            ),
                            extras
                        )
                })
        binding.movieList.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedElementEnterTransition = TransitionInflater
            .from(context)
            .inflateTransition(R.transition.curved_motion)
        postponeEnterTransition()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

}