package com.example.android.movieapplication.ui.moviedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.movieapplication.databinding.CastViewItemBinding
import com.example.android.movieapplication.model.MovieCast

class MovieCastAdapter(private val onClickListener: OnClickListener)
    : ListAdapter<MovieCast, MovieCastAdapter.CastViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        return CastViewHolder(CastViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        val cast = getItem(position)
        cast?.let {
            holder.bind(it)
            holder.itemView.setOnClickListener {
                onClickListener.onClick(cast.id)
            }
        }

    }

    class CastViewHolder(private val binding: CastViewItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(movieCast: MovieCast) {
            binding.movieCast = movieCast
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MovieCast>() {
        override fun areItemsTheSame(oldItem: MovieCast, newItem: MovieCast): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MovieCast, newItem: MovieCast): Boolean {
            return oldItem == newItem
        }
    }

    class OnClickListener(val clickListener: (castId: Long) -> Unit) {
        fun onClick(castId: Long) = clickListener(castId)
    }

}