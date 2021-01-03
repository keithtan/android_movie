package com.example.android.movieapplication.ui.peopledetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.movieapplication.databinding.ListItemMovieBinding
import com.example.android.movieapplication.db.Movie

class MovieListAdapter(private val onClickListener: OnClickListener)
    : ListAdapter<Movie, MovieListAdapter.MovieViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(
            ListItemMovieBinding
                .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        movie?.let {
            holder.bind(it)
            holder.itemView.setOnClickListener {
                onClickListener.onClick(movie.id, holder.cardView)
            }
        }

    }

    class MovieViewHolder(private val binding: ListItemMovieBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.movie = movie
        }

        val cardView = binding.cardView
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    class OnClickListener(val clickListener: (movieId: Long, cardView: CardView) -> Unit) {
        fun onClick(movieId: Long, cardView: CardView) = clickListener(movieId, cardView)
    }

}