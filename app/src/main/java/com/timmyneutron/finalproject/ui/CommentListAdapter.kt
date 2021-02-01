package com.timmyneutron.finalproject.ui

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.timmyneutron.finalproject.DetailActivity
import com.timmyneutron.finalproject.R
import com.timmyneutron.finalproject.model.Place
import com.timmyneutron.finalproject.model.Comment

class CommentListAdapter(private val viewModel: MainViewModel)
    : ListAdapter<Comment, CommentListAdapter.VH>(CommentDiff()) {

    inner class VH(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var bodyView = itemView.findViewById<TextView>(R.id.body)
        private var commentorView = itemView.findViewById<TextView>(R.id.commentor)
        private var upVoteArrow = itemView.findViewById<ImageButton>(R.id.upVoteArrow)
        private var downVoteArrow = itemView.findViewById<ImageButton>(R.id.downVoteArrow)
        private var commentVoteScore = itemView.findViewById<TextView>(R.id.commentVoteScore)
        private var deleteCommentButton = itemView.findViewById<ImageButton>(R.id.deleteCommentButton)

        fun bind(comment: Comment) {
            commentorView.text = "${comment.commentor} says:"
            bodyView.text = comment.body
            deleteCommentButton.visibility = View.GONE
            FirebaseAuth.getInstance().currentUser?.let {
                if (it.displayName == comment.commentor) {
                    deleteCommentButton.visibility = View.VISIBLE
                    deleteCommentButton.setOnClickListener {
                        viewModel.deleteComment(comment)
                    }
                }
            }
            upVoteArrow.setOnClickListener {
                viewModel.upVoteComment(comment)
            }
            downVoteArrow.setOnClickListener {
                viewModel.downVoteComment(comment)
            }
            commentVoteScore.text = comment.voteScore.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val placeView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_comment, parent, false)
        return VH(placeView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class CommentDiff : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.body == newItem.body
                    && oldItem.voteScore == newItem.voteScore
        }

    }
}