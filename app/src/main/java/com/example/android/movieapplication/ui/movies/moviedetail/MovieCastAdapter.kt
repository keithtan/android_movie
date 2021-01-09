package com.example.android.movieapplication.ui.movies.moviedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.movieapplication.databinding.ListMovieCastBinding
import com.example.android.movieapplication.network.MovieDetail.MovieCredits.MovieCast

class MovieCastAdapter(private val onClickListener: OnClickListener)
    : ListAdapter<MovieCast, MovieCastAdapter.CastViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        return CastViewHolder(ListMovieCastBinding.inflate(LayoutInflater.from(parent.context)))
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

    class CastViewHolder(private val binding: ListMovieCastBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(movieCast: MovieCast) {
            binding.movieCast = movieCast
        }

        val imageView = binding.imageView
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MovieCast>() {
        override fun areItemsTheSame(oldItem: MovieCast, newItem: MovieCast): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MovieCast, newItem: MovieCast): Boolean {
            return oldItem == newItem
        }
    }

    class OnClickListener(val clickListener: (castId: Long, imageView: ImageView) -> Unit) {
        fun onClick(castId: Long, imageView: ImageView) = clickListener(castId, imageView)
    }

}