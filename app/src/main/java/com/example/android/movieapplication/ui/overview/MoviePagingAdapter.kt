package com.example.android.movieapplication.ui.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.movieapplication.databinding.ListViewItemBinding
import com.example.android.movieapplication.db.Movie

class MoviePagingAdapter(private val onClickListener: OnClickListener)
    : PagingDataAdapter<Movie, MoviePagingAdapter.MovieViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(ListViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        movie?.let {
            holder.bind(it)
            holder.itemView.setOnClickListener {
                onClickListener.onClick(movie.id, holder.imageView)
            }
        }

    }

    class MovieViewHolder(private val binding: ListViewItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.movie = movie
        }

        val imageView = binding.imageView
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    class OnClickListener(val clickListener: (movieId: Long, imageView: ImageView) -> Unit) {
        fun onClick(movieId: Long, imageView: ImageView) = clickListener(movieId, imageView)
    }

}