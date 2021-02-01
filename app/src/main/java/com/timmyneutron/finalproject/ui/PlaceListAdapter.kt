package com.timmyneutron.finalproject.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.timmyneutron.finalproject.R
import com.timmyneutron.finalproject.model.Place

class PlaceListAdapter(private val viewModel: MainViewModel)
    : ListAdapter<Place, PlaceListAdapter.VH>(PlaceDiff()) {

        inner class VH(itemView: View): RecyclerView.ViewHolder(itemView) {
            private var voteScoreView = itemView.findViewById<TextView>(R.id.placeListVoteScore)
            private var titleView = itemView.findViewById<TextView>(R.id.title)
            private var creatorView = itemView.findViewById<TextView>(R.id.creator)

            fun bind(place: Place) {
                voteScoreView.text = place.voteScore.toString()
                titleView.text = place.title
                creatorView.text = "by ${place.creator}"


                itemView.setOnClickListener {
                    viewModel.doDetail(itemView.context, place)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val placeView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_place, parent, false)
        return VH(placeView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class PlaceDiff : DiffUtil.ItemCallback<Place>() {
        override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
            return oldItem.title == newItem.title
                    && oldItem.description == newItem.description
                    && oldItem.location == newItem.location
                    && oldItem.voteScore == newItem.voteScore
        }

    }
}