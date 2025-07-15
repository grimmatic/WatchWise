package com.vakifbank.WatchWise.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vakifbank.WatchWise.R
import com.vakifbank.WatchWise.databinding.ReviewItemBinding
import com.vakifbank.WatchWise.domain.model.MovieReview
import com.vakifbank.WatchWise.utils.toRatingString
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReviewAdapter(
    private val reviewList: List<MovieReview>,
    private val currentUserId: String?
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(private val binding: ReviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: MovieReview, isCurrentUser: Boolean) {
            val userDisplayName = review.userEmail?.substringBefore("@") ?: "Anonim"
            binding.userNameTextView.text = userDisplayName

            binding.userRatingTextView.text = review.rating?.toRatingString() ?: "0.0"

            if (review.comment.isNullOrEmpty()) {
                binding.commentTextView.visibility = View.GONE
            } else {
                binding.commentTextView.text = review.comment
                binding.commentTextView.visibility = View.VISIBLE
            }


            review.timestamp?.let { timestamp ->
                val date = Date(timestamp)
                val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                binding.timestampTextView.text = formatter.format(date)
            }


            if (isCurrentUser) {
                binding.reviewCard.setCardBackgroundColor(
                    binding.root.context.getColor(R.color.primary_purple_light)
                )
                binding.userNameTextView.setTextColor(
                    binding.root.context.getColor(R.color.primary_purple)
                )
                binding.userNameTextView.text = "$userDisplayName (Sen)"
            } else {
                binding.reviewCard.setCardBackgroundColor(
                    binding.root.context.getColor(R.color.white)
                )
                binding.userNameTextView.setTextColor(
                    binding.root.context.getColor(R.color.black)
                )
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ReviewItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviewList[position]
        val isCurrentUser = currentUserId != null && review.userId == currentUserId
        holder.bind(review, isCurrentUser)
    }

    override fun getItemCount(): Int = reviewList.size
}