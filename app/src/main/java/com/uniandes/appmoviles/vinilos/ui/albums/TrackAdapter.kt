package com.uniandes.appmoviles.vinilos.ui.albums

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uniandes.appmoviles.vinilos.databinding.ItemTrackBinding
import com.uniandes.appmoviles.vinilos.model.Track

class TrackAdapter : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    private var tracks = listOf<Track>()

    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position], position + 1, position == tracks.size - 1)
    }

    override fun getItemCount() = tracks.size

    inner class TrackViewHolder(private val binding: ItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(track: Track, index: Int, isLast: Boolean) {
            binding.trackNumber.text = String.format("%02d", index)
            binding.trackName.text = track.name
            binding.trackDuration.text = formatDuration(track.duration)
            binding.trackDivider.visibility = if (isLast) View.GONE else View.VISIBLE
        }
    }

    private fun formatDuration(durationStr: String): String {
        val totalSeconds = durationStr.toLongOrNull() ?: return durationStr
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }
}
