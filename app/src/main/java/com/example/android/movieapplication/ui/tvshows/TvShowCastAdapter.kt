package com.example.android.movieapplication.ui.tvshows

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.movieapplication.databinding.ListTvShowCastBinding
import com.example.android.movieapplication.network.TvShowDetail.TvShowCredits.TvShowCast

class TvShowCastAdapter(private val onClickListener: OnClickListener)
    : ListAdapter<TvShowCast, TvShowCastAdapter.CastViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        return CastViewHolder(ListTvShowCastBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        val cast = getItem(position)
        cast?.let {
            holder.bind(it)
            holder.itemView.setOnClickListener {
                onClickListener.onClick(cast.id, holder.imageView)
            }
        }
    }

    class CastViewHolder(private val binding: ListTvShowCastBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(tvShowCast: TvShowCast) {
            binding.tvShowCast = tvShowCast
        }

        val imageView = binding.imageView
    }

    companion object DiffCallback : DiffUtil.ItemCallback<TvShowCast>() {
        override fun areItemsTheSame(oldItem: TvShowCast, newItem: TvShowCast): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TvShowCast, newItem: TvShowCast): Boolean {
            return oldItem == newItem
        }
    }

    class OnClickListener(val clickListener: (castId: Long, imageView: ImageView) -> Unit) {
        fun onClick(castId: Long, imageView: ImageView) = clickListener(castId, imageView)
    }

}