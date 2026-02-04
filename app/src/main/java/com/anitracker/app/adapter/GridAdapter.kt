package com.anitracker.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anitracker.app.R
import com.anitracker.app.model.Media
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class GridAdapter(
    private val onItemClick: (Media) -> Unit
) : ListAdapter<Media, GridAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grid_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvScore: TextView = itemView.findViewById(R.id.tvScore)

        fun bind(media: Media) {
            tvTitle.text = media.title.preferred()

            media.averageScore?.let { score ->
                tvScore.visibility = View.VISIBLE
                tvScore.text = String.format("%.1f", score / 10.0)
            } ?: run {
                tvScore.visibility = View.GONE
            }

            Glide.with(itemView.context)
                .load(media.coverImage.best())
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(ivCover)

            itemView.setOnClickListener {
                onItemClick(media)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Media>() {
        override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean {
            return oldItem == newItem
        }
    }
}
