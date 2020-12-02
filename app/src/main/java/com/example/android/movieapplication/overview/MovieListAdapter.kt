package com.example.android.movieapplication.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.movieapplication.databinding.GridViewItemBinding
import com.example.android.movieapplication.model.Movie

class MovieListAdapter(private val onClickListener: OnClickListener)
    : PagingDataAdapter<Movie, MovieListAdapter.MovieViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(GridViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        movie?.let {
            holder.bind(it)
            holder.itemView.setOnClickListener {
                onClickListener.onClick(movie.id)
            }
        }

    }

    class MovieViewHolder(private val binding: GridViewItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.movie = movie
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    class OnClickListener(val clickListener: (movieId: Long) -> Unit) {
        fun onClick(movieId: Long) = clickListener(movieId)
    }

}