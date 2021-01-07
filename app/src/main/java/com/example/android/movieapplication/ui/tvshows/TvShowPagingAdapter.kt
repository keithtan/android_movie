package com.example.android.movieapplication.ui.tvshows

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.movieapplication.databinding.ListItemTvShowBinding
import com.example.android.movieapplication.db.TvShow

class TvShowPagingAdapter(private val onClickListener: OnClickListener)
    : PagingDataAdapter<TvShow, TvShowPagingAdapter.TvShowViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvShowViewHolder {
        return TvShowViewHolder(
            ListItemTvShowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: TvShowViewHolder, position: Int) {
        val movie = getItem(position)
        movie?.let {
            holder.bind(it)
            holder.itemView.setOnClickListener {
                onClickListener.onClick(movie.id, holder.imageView)
            }
        }

    }

    class TvShowViewHolder(private val binding: ListItemTvShowBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(tvShow: TvShow) {
            binding.tvShow = tvShow
        }

        val imageView = binding.imageView
    }

    companion object DiffCallback : DiffUtil.ItemCallback<TvShow>() {
        override fun areItemsTheSame(oldItem: TvShow, newItem: TvShow): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TvShow, newItem: TvShow): Boolean {
            return oldItem == newItem
        }
    }

    class OnClickListener(val clickListener: (tvShowId: Long, imageView: ImageView) -> Unit) {
        fun onClick(tvShowId: Long, imageView: ImageView) = clickListener(tvShowId, imageView)
    }

}