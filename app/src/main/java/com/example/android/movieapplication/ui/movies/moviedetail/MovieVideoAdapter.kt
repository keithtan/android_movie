package com.example.android.movieapplication.ui.movies.moviedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.movieapplication.databinding.ListMovieVideoBinding
import com.example.android.movieapplication.network.MovieDetail.MovieVideos.VideoDetail
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

class MovieVideoAdapter(val lifecycle: Lifecycle)
    : ListAdapter<VideoDetail, MovieVideoAdapter.VideoViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(ListMovieVideoBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val cast = getItem(position)
        cast?.let {
            holder.bind(it)
        }
    }

    inner class VideoViewHolder(private val binding: ListMovieVideoBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(video: VideoDetail) {
            lifecycle.addObserver(binding.youtubePlayerView)
            binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.cueVideo(video.key, 0f)
                }
            })
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<VideoDetail>() {
        override fun areItemsTheSame(oldItem: VideoDetail, newItem: VideoDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VideoDetail, newItem: VideoDetail): Boolean {
            return oldItem == newItem
        }
    }

}